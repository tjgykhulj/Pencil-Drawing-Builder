#ifndef TONE_H
#define TONE_H

#include <opencv2/opencv.hpp>
#include <opencv2/legacy/compat.hpp>
#include "image.h"

using namespace std;
using namespace cv;
//#pragma comment(linker, "/subsystem:\"windows\" /entry:\"mainCRTStartup\"")

void smooth(float a[], int n) 
{
	if (n < 5) {
		printf("too short"); 
		return;
	}
	float *s = new float[n];
	s[0] = a[0];
	for (int i = 1; i < n; i++) s[i] = s[i - 1] + a[i];
	for (int i = 0; i < 3; i++) a[i] = s[i + 2] / (i+3);
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

int main()
{
	float p1, p2, p3, p[256];
	float temp = sqrt(2 * CV_PI * 11);
	for (int i = 0; i < 256; i++) {
		p1 = 1 / 9 * exp(-(255 - i) / 9);
		p2 = (i >= 105 && i <= 225) / (225 - 105);
		p3 = exp(-(i - 90)*(i - 90) / (2.0 * 11 * 11)) / temp;
		p[i] = 52 * p1 + 37 * p2 + 11 * p3;
	}
	smooth(p, 256);
	smooth(p, 256);
	float sum = 0;
	for (int i = 0; i < 256; i++) sum += p[i];
	for (int i = 0; i < 256; i++) p[i] /= sum;
	float G[256];
	G[0] = p[0];
	for (int i = 1; i < 256; i++) G[i] = G[i - 1] + p[i];

	IplImage *pGrayImage = cvLoadImage("input.jpg", CV_LOAD_IMAGE_GRAYSCALE);
	int h = pGrayImage->height;
	int w = pGrayImage->width;
	CvHistogram *pHis = CreateGrayImageHist(&pGrayImage);
	float S[256];
	S[0] = cvQueryHistValue_1D(pHis, 0) / (h*w);
	for (int i = 1; i < 256; i++)
		S[i] = S[i - 1] + cvQueryHistValue_1D(pHis, i) / (h*w);

	int index[256];
	for (int i = 0; i < 256; i++) {
		int k = 0;
		for (int j = 1; j < 256; j++)
			if (abs(G[k] - S[i]) > abs(G[j] - S[i])) k = j;
		index[i] = k;
	}
	Image img(pGrayImage);
	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++) img[i][j] = index[img[i][j]];

	cvSaveImage("output.jpg", pGrayImage);
}

#endif