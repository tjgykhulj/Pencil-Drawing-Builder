#ifndef METHOD_H
#define METHOD_H

#include <opencv2\opencv.hpp>
#include <opencv2/legacy/compat.hpp>
using namespace cv;

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

void smooth(double a[], int n)
{
	if (n < 5) {
		printf("too short");
		return;
	}
	double *s = new double[n];
	s[0] = a[0];
	for (int i = 1; i < n; i++) s[i] = s[i - 1] + a[i];
	for (int i = 0; i < 3; i++) a[i] = s[i + 2] / (i + 3);
	for (int i = 3; i < n; i++)
	{
		int r = min(i + 2, n - 1);
		a[i] = (s[r] - s[i - 3]) / (r - i + 3);
	}
}

CvHistogram* CreateGrayImageHist(IplImage **img)
{
	int nHistSize = 256;
	float fRange[] = { 0, 255 };  //»Ò¶È¼¶µÄ·¶Î§    
	float *pfRanges[] = { fRange };
	CvHistogram *pcvHistogram = cvCreateHist(1, &nHistSize, CV_HIST_ARRAY, pfRanges);
	cvCalcHist(img, pcvHistogram);
	return pcvHistogram;
}

#endif