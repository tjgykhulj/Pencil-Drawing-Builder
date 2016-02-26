#ifndef METHOD_H
#define METHOD_H

#include <opencv2\opencv.hpp>
using namespace cv;

Mat rotateImage(Mat img, double degree)
{
	Point2f center = Point2f((img.cols-1) / 2.0, (img.rows-1) / 2.0);
	Mat M = getRotationMatrix2D(center, degree, 1);
	Mat result;
	warpAffine(img, result, M, img.size(), CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS);
	return result;
}

void conv2(const Mat &img, const Mat &kernel, Mat& dest) {
	Point anchor(kernel.cols - kernel.cols / 2 - 1, kernel.rows - kernel.rows / 2 - 1);
	filter2D(img, dest, img.depth(), kernel, anchor);
}

void smooth(double a[], int n)
{
	if (n < 3) {
		printf("too short");
		return;
	}
	double *s = new double[n];
	s[0] = a[0];
	for (int i = 1; i < n; i++) s[i] = s[i - 1] + a[i];
	a[0] = s[1]/2;
	a[1] = s[2]/3;
	for (int i = 2; i < n-1; i++) a[i] = (s[i + 1] - s[i - 2]) / 3;
}

//求和、归一化
double* sumAndNormalize(double *src, int len)
{
	double *dest = new double[len];
	dest[0] = src[0];
	for (int i = 1; i < len; i++) dest[i] = dest[i - 1] + src[i];
	for (int i = 0; i < len; i++) dest[i] /= dest[len - 1];
	return dest;
}
//灰度直方图
double* grayImageHist(IplImage *img)
{
	int size = 256;
	double *ans = new double[size];
	memset(ans, 0, size * sizeof(double));

	for (int i = 0; i < img->height; i++)
	{
		uchar *value = (uchar *)img->imageData + i * img->widthStep;
		for (int j = 0; j < img->width; j++)
			ans[value[j]] += 1;
	}
	return ans;
}
#endif