// cvPencil.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"
#include "cvPencil.h"
#include "method.h"
#include <opencv2\opencv.hpp>
#include <cmath>
using namespace cv;

// This is an example of an exported variable
static string opt = "sobel";

IplImage* getStroke(const char *filename);
IplImage* getTone(const char *filename);
Image getImage(IplImage *src);

CVPENCIL_API void useSobel()	{ opt = "sobel"; }
CVPENCIL_API void useGrad()		{ opt = "grad"; }

CVPENCIL_API Image PencilDrawing(const char *filename)
{
	IplImage *pStroke = getStroke(filename);
	IplImage *pTone = getTone(filename);
	IplImage *pResult = cvLoadImage(filename, CV_LOAD_IMAGE_GRAYSCALE);
	Image ans = getImage(pResult);

	for (int i = 0; i < ans.height; i++)
		for (int j = 0; j < ans.width; j++)
			ans[i][j] = (uchar)sqrt(CV_IMAGE_ELEM(pStroke, uchar, i, j) * CV_IMAGE_ELEM(pTone, uchar, i, j));

	cvReleaseImage(&pStroke);
	cvReleaseImage(&pTone);
	return ans;
}


IplImage* getStroke(const char *filename)
{
	// 加载原图    
	IplImage *pStrokeImage = cvLoadImage(filename, CV_LOAD_IMAGE_GRAYSCALE);

	Image gray = getImage(pStrokeImage);
	//sobel边缘检测
	if (opt == "sobel")
		for (int i = 0; i < gray.height - 2; i++)
			for (int j = 0; j < gray.width - 2; j++)
			{
				int cx = (2 * gray[i + 2][j + 1] + gray[i + 2][j] + gray[i + 2][j + 2])
					- (2 * gray[i][j + 1] + gray[i][j] + gray[i][j + 2]);
				int cy = (2 * gray[i + 1][j + 2] + gray[i][j + 2] + gray[i + 2][j + 2])
					- (2 * gray[i + 1][j] + gray[i][j] + gray[i + 2][j]);

				gray[i][j] = sqrt(cx*cx + cy*cy);
			}
	else
		for (int i = 0; i < gray.height - 1; i++)
			for (int j = 0; j < gray.width - 1; j++)
			{
				int cx = gray[i + 1][j] - gray[i][j];
				int cy = gray[i][j + 1] - gray[i][j];
				gray[i][j] = sqrt(cx*cx + cy*cy);
			}


	Mat sobel;
	Mat(pStrokeImage).convertTo(sobel, CV_32FC1);
	sobel /= 256;

	int dirNum = 8;
	//计算L0
	int sz = max(5, pStrokeImage->height/100);
	int len = sz * 2 + 1;
	Mat L(len, len, CV_32FC1, Scalar::all(0));
	for (int i = 0; i < len; i++) L.at<float>(sz, i) = 1;

	//计算Li与Gi
	Mat *l = new Mat[dirNum];
	Mat *G = new Mat[dirNum];
	for (int i = 0; i < dirNum; i++)
	{
		l[i] = rotateImage(L, i * 180.0 / dirNum) / len;
		conv2(sobel, l[i], G[i]);
	}

	//初始化并计算Ci
	Mat *C = new Mat[dirNum];
	for (int i = 0; i < dirNum; i++)
		C[i] = Mat(gray.height, gray.width, CV_32FC1, Scalar::all(0));

	for (int i = 0; i < gray.height; i++)
		for (int j = 0; j < gray.width; j++) {
			int key = 0;
			for (int k = 1; k < dirNum; k++)
				if (G[key].at<float>(i, j) < G[k].at<float>(i, j))
					key = k;
			C[key].at<float>(i, j) = sobel.at<float>(i, j);
		}

	//计算S
	Mat temp;
	Mat S(gray.height, gray.width, CV_32FC1, Scalar::all(0));
	for (int i = 0; i < dirNum; i++) {
		conv2(C[i], l[i], temp);
		S += temp;
	}
	float Max = 0;
	for (int i = 0; i < S.rows; i++)
		for (int j = 0; j < S.cols; j++) Max = max(Max, S.at<float>(i, j));
	S = (1 - S / Max) * 255;
	Mat ans;

	S.convertTo(ans, CV_8UC1);
	for (int i = 0; i < ans.rows; i++)
		for (int j = 0; j < ans.cols; j++) gray[i][j] = ans.at<uchar>(i, j);

	return pStrokeImage;
}

IplImage* getTone(const char *filename)
{
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
	double *G = sumAndNormalize(p, 256);

	IplImage *pToneImage = cvLoadImage(filename, CV_LOAD_IMAGE_GRAYSCALE);
	Image img = getImage(pToneImage);
	int h = pToneImage->height;
	int w = pToneImage->width;

	double *pHis = grayImageHist(pToneImage);
	double *S = sumAndNormalize(pHis, 256);

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
	IplImage *pRender = cvLoadImage("image/Tonal Texture.png", CV_LOAD_IMAGE_GRAYSCALE);
	Image pR = getImage(pRender);
	double **b = new double*[h];
	for (int i = 0; i < h; i++)
	{
		b[i] = new double[w];
		for (int j = 0; j < w; j++)
		{
			double H = pR[i % pR.height][j % pR.width] / 256.0;
			double J = img[i][j] / 256.0;
			double bx = (i) ? b[i - 1][j] : 0;
			double by = (j) ? b[i][j - 1] : 0;
			double A = 0.2 * 2 + log(H)*log(H);
			double B = -2 * (0.2*(bx + by) + log(H)*log(J));
			b[i][j] = -B / (2 * A);
			img[i][j] = pow(H, b[i][j]) * 256;
		}
	}
	return pToneImage;
}

Image getImage(IplImage *src)
{
	return Image(src->height, src->width, src->widthStep, src->imageData);
}