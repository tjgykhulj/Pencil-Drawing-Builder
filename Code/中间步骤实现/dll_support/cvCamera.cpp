// cvCamera.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"
#include "cvCamera.h"
#include <opencv2\opencv.hpp>

static CvCapture *cam = NULL;

// This is the constructor of a class that has been exported.
// see cvCemera.h for the class definition
CvFrame::CvFrame(int h, int w, char* src)
	:height(h), width(w)
{
	data = src;
}

void CVCAMERA_API::cvOpenCamera() {
	cam = cvCreateCameraCapture(0);
}

void CVCAMERA_API::cvCloseCamera() {
	cvReleaseCapture(&cam);
}


CvFrame CVCAMERA_API::cvReadFrame() {
	IplImage *img = cvQueryFrame(cam);
	return CvFrame(img->height, img->width, img->imageData);
}