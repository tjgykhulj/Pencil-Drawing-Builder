#include <opencv2/opencv.hpp>
#include "image.h"

using namespace std;
using namespace cv;
#pragma comment(linker, "/subsystem:\"windows\" /entry:\"mainCRTStartup\"")

const char *pGrayTitle = "Gray";
IplImage *pGrayImage;

Mat rotateImage(Mat img, double degree)
{

	Point2f center = Point2f((img.cols - 1) / 2.0, (img.rows - 1) / 2.0);
	Mat M = getRotationMatrix2D(center, degree, 1);
	Mat result;
	warpAffine(img, result, M, img.size(), CV_INTER_LINEAR);
	//+CV_WARP_FILL_OUTLIERS);
	return result;
}

void conv2(const Mat &img, const Mat &kernel, Mat& dest) {
	Point anchor(kernel.cols - kernel.cols / 2 - 1, kernel.rows - kernel.rows / 2 - 1);
	filter2D(img, dest, img.depth(), kernel, anchor);
}

int main()
{
	// 加载原图    
	pGrayImage = cvLoadImage("1.jpg", CV_LOAD_IMAGE_GRAYSCALE);

	//Sobel边缘检测
	Image gray(pGrayImage);
	for (int i = 0; i < gray.getH() - 2; i++)
		for (int j = 0; j < gray.getW() - 2; j++)
		{
			int cx = (2 * gray[i + 2][j + 1] + gray[i + 2][j] + gray[i + 2][j + 2])
				- (2 * gray[i][j + 1] + gray[i][j] + gray[i][j + 2]);
			int cy = (2 * gray[i + 1][j + 2] + gray[i][j + 2] + gray[i + 2][j + 2])
				- (2 * gray[i + 1][j] + gray[i][j] + gray[i + 2][j]);

			gray[i][j] = sqrt(cx*cx + cy*cy);
		}
	Mat sobel;
	Mat(pGrayImage).convertTo(sobel, CV_32FC1);
	sobel /= 256;

	int dirNum = 18;
	//计算L0
	int sz = 5;
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
	S = (1 - S / Max) * 200;
	Mat ans;
	S.convertTo(ans, CV_8UC1);
	cvShowImage("4", &IplImage(ans));
	cvWaitKey();
	cvDestroyAllWindows();
	cvReleaseImage(&pGrayImage);
	return 0;
}