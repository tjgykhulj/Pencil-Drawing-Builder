// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the CVPENCIL_EXPORTS
// symbol defined on the command line. This symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// CVPENCIL_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.
#ifdef CVPENCIL_EXPORTS
#define CVPENCIL_API __declspec(dllexport)
#else
#define CVPENCIL_API __declspec(dllimport)
#endif
#include <stdio.h>

typedef unsigned char uchar;

struct CVPENCIL_API Image{
	Image(int h, int w, int ws, char* img) :
		height(h), width(w), widthStep(ws), data(img)
	{
	}
	uchar *operator[](int index) { return (uchar*) data+index*widthStep; }
	char *data;
	int height, width;
public:
	int widthStep;
};

CVPENCIL_API Image PencilDrawing(const char *filename);
CVPENCIL_API void useSobel();
CVPENCIL_API void useGrad();