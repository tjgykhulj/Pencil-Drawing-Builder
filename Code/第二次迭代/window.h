#ifndef WINDOW_H
#define WINDOW_H

#define T_EXAMPLE	"image/example.jpg"
#define T_MIDDLE	"image/src.png"

#include <QtWidgets/QMainWindow>
#include <qwidget.h>
#include <qlabel.h>
#include <qpushbutton.h>
#include <qradiobutton.h>
#include "tlib.h"
#include "ui_window.h"

class Window : public QMainWindow
{
	Q_OBJECT

public:
	Window(QWidget *parent = 0);

protected:
	void closeEvent(QCloseEvent * event);
private:
//图片处理
	void SetImage(QPixmap);
	void LoadImage(QString);

	Ui::WindowClass ui;
//摄像头功能
	QTimer *camTimer;
	QPushButton *camOpen, *camGet, *camCancel;
//保存、读取图片
	QPushButton *save, *select;
//截图功能
	TScreenShot *shotScreen;
	QPushButton *shot, *shotM;
//照片的显示与计算
	QRadioButton *sobel;
	QRadioButton *grad;
	QPushButton *convert;
	TLabel *src;
	TLabel *dst;
	TPhoto *showPix;
public slots:
//摄像头部分
	void CameraGetFrame();
	void CameraChangeStatus();
	void CameraOpen();
	void CameraClose();
//文件处理
	void LoadImageFile();
	void SaveImageFile();
//截图
	void MinAndGrapWindowScreen();
	void ShowScreenImage(QPixmap);
//图片处理
	void CalcImage();
	void ShowImage(QPixmap);
//参数
	void SetOptSobel();
	void SetOptGrad();
//退出
	void Quit();
};

#endif // WINDOW_H
