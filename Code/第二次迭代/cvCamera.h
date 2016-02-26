// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the CVCAMERA_EXPORTS
// symbol defined on the command line. This symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// CVCAMERA_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.
#ifdef CVCAMERA_EXPORTS
#define CVCAMERA_API __declspec(dllexport)
#else
#define CVCAMERA_API __declspec(dllimport)
#endif

// This class is exported from the cvCemera.dll
struct CVCAMERA_API CvFrame {
	CvFrame(int, int, char*);
	int height, width;
	char *data;
};

CVCAMERA_API void cvOpenCamera();
CVCAMERA_API void cvCloseCamera();
CVCAMERA_API CvFrame cvReadFrame();