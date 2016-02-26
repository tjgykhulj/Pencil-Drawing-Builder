#ifndef IMAGE_H
#define IMAGE_H

#include <iostream>
#include <opencv2\opencv.hpp>
//·½±ã¶ÁÈ¡Í¼Æ¬
class Image {
public:
	Image(IplImage *image) {
		h = image->height;
		w = image->width;
		data = new uchar*[h];
		for (int i = 0; i < h; i++) {
			data[i] = (uchar *)image->imageData + i * image->widthStep;
		}
	}
	uchar* operator[](int w) { return data[w]; }
	int getH() { return h; }
	int getW()  { return w; }
private:
	int h, w;
	uchar **data;
};

#endif