#include <opencv2\opencv.hpp>
#include <cmath>
#include "method.h"
#include "image.h"
#pragma comment(linker, "/subsystem:\"windows\" /entry:\"mainCRTStartup\"")

IplImage* getStroke(const char *filename)
{
	// 加载原图    
	IplImage *pStrokeImage = cvLoadImage(filename, CV_LOAD_IMAGE_GRAYSCALE);
	Image gray(pStrokeImage);
	//Sobel算子 边缘检测
	for (int i = 0; i < gray.getH() - 2; i++)
		for (int j = 0; j < gray.getW() - 2; j++)
		{
			int cx = (2 * gray[i + 2][j + 1] + gray[i + 2][j] + gray[i + 2][j + 2])
				- (2 * gray[i][j + 1] + gray[i][j] + gray[i][j + 2]);
			int cy = (2 * gray[i + 1][j + 2] + gray[i][j + 2] + gray[i + 2][j + 2])
				- (2 * gray[i + 1][j] + gray[i][j] + gray[i + 2][j]);

			/*梯度算子 边缘检测
			cx = gray[i+1][j] - gray[i][j];
			cy = gray[i][j+1] - gray[i][j];
			*/
			gray[i][j] = sqrt(cx*cx + cy*cy);
		}
	Mat sobel;
	Mat(pStrokeImage).convertTo(sobel, CV_32FC1);
	sobel /= 256;

	//选取dirNum个方向检测
	int dirNum = 8;
	//计算L0
	int sz = 5;
	int len = sz * 2 + 1;
	Mat L(len, len, CV_32FC1, Scalar::all(0));
	for (int i = 0; i < len; i++) L.at<float>(sz, i) = 1.0/len;

	//计算Li与Gi
	Mat *l = new Mat[dirNum];
	Mat *G = new Mat[dirNum];
	for (int i = 0; i < dirNum; i++)
	{
		l[i] = rotateImage(L, i * 180.0 / dirNum);
		conv2(sobel, l[i], G[i]);
	}

	//初始化并计算Ci
	Mat *C = new Mat[dirNum];
	for (int i = 0; i < dirNum; i++)
		C[i] = Mat(gray.getH(), gray.getW(), CV_32FC1, Scalar::all(0));

	for (int i = 0; i < gray.getH(); i++)
		for (int j = 0; j < gray.getW(); j++) {
			int key = 0;
			for (int k = 1; k < dirNum; k++)
				if (G[key].at<float>(i, j) < G[k].at<float>(i, j))
					key = k;
			C[key].at<float>(i, j) = sobel.at<float>(i, j);
		}

	//计算S
	Mat temp;
	Mat S(gray.getH(), gray.getW(), CV_32FC1, Scalar::all(0));
	for (int i = 0; i < dirNum; i++) {
		conv2(C[i], l[i], temp);
		S += temp;
	}
	float Max = 0;
	for (int i = 0; i < S.rows; i++)
		for (int j = 0; j < S.cols; j++) Max = max(Max, S.at<float>(i, j));
	S = (1 - S / Max) * 240;

	for (int i = 0; i < S.rows; i++)
		for (int j = 0; j < S.cols; j++) gray[i][j] = S.at<float>(i, j);
	return pStrokeImage;
}

IplImage* getTone(const char *filename)
{
	//实现论文中提到的正常铅笔画应有的直方图
	double p1, p2, p3, p[256];
	double temp = sqrt(2 * CV_PI * 10);
	for (int i = 0; i < 256; i++) {
		p1 = 1 / 9.0 * exp(-(256 - i) / 9.0);
		p2 = (i >= 105 && i <= 225) / (225 - 105.0);
		p3 = exp(-(i - 80)*(i - 80) / (2.0 * 10 * 10)) / temp;
		p[i] = 0.52 * p1 + 0.37 * p2 + 0.11 * p3;
	}
	smooth(p, 256);
	smooth(p, 256);
	double sum = 0;
	for (int i = 0; i < 256; i++) sum += p[i];
	for (int i = 0; i < 256; i++) p[i] /= sum;
	double G[256];
	G[0] = p[0];
	for (int i = 1; i < 256; i++) G[i] = G[i - 1] + p[i];

	//计算原图的直方图
	IplImage *pToneImage = cvLoadImage(filename, CV_LOAD_IMAGE_GRAYSCALE);
	Image img(pToneImage);
	int h = pToneImage->height;
	int w = pToneImage->width;

	CvHistogram *pHis = CreateGrayImageHist(&pToneImage);
	double S[256];
	S[0] = cvQueryHistValue_1D(pHis, 0) / (h*w);
	for (int i = 1; i < 256; i++)
		S[i] = S[i - 1] + cvQueryHistValue_1D(pHis, i) / (h*w);

	//进行直方图匹配
	int index[256];
	for (int i = 0; i < 256; i++) {
		int k = 0;
		for (int j = 1; j < 256; j++)
			if (abs(G[k] - S[i]) > abs(G[j] - S[i])) k = j;
		index[i] = k;
	}

	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++) img[i][j] = index[img[i][j]];

	//Pencil Texture Rendering
	IplImage *pRender = cvLoadImage("Tonal Texture.png", CV_LOAD_IMAGE_GRAYSCALE);
	Image pR(pRender);
	double **b = new double*[h];
	for (int i = 0; i < h; i++) 
	{
		b[i] = new double[w];
		for (int j = 0; j < w; j++)
		{
			double H = pR[i % pR.getH()][j % pR.getW()] / 256.0;
			double J = img[i][j] / 256.0;
			double bx = (i) ? b[i - 1][j] : 0;
			double by = (j) ? b[i][j - 1] : 0;
			double A = 0.2 * 2 + log(H)*log(H);
			double B = -2*(0.2*(bx + by) + log(H)*log(J));
			b[i][j] = -B / (2*A);
			img[i][j] = pow(H, b[i][j]) * 256;
		}
	}
	return pToneImage;
}

int main()
{
	const char *filename = "1.jpg";
	IplImage *pStroke = getStroke(filename);
	IplImage *pTone = getTone(filename);
	Image stroke(pStroke);
	Image tone(pTone);

	cvShowImage("stroke", pStroke);
	cvShowImage("tone", pTone);
	//合并stroke与tone两张图
	for (int i = 0; i < stroke.getH(); i++)
		for (int j = 0; j < stroke.getW(); j++)
			stroke[i][j] = (uchar) sqrt(stroke[i][j] * tone[i][j]);
	
	cvShowImage("result", pStroke);
	cvWaitKey();
	cvDestroyAllWindows();
	cvReleaseImage(&pStroke);
	cvReleaseImage(&pTone);
}