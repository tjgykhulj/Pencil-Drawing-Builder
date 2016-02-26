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
//ͼƬ����
	void SetImage(QPixmap);
	void LoadImage(QString);

	Ui::WindowClass ui;
//����ͷ����
	QTimer *camTimer;
	QPushButton *camOpen, *camGet, *camCancel;
//���桢��ȡͼƬ
	QPushButton *save, *select;
//��ͼ����
	TScreenShot *shotScreen;
	QPushButton *shot, *shotM;
//��Ƭ����ʾ�����
	QRadioButton *sobel;
	QRadioButton *grad;
	QPushButton *convert;
	TLabel *src;
	TLabel *dst;
	TPhoto *showPix;
public slots:
//����ͷ����
	void CameraGetFrame();
	void CameraChangeStatus();
	void CameraOpen();
	void CameraClose();
//�ļ�����
	void LoadImageFile();
	void SaveImageFile();
//��ͼ
	void MinAndGrapWindowScreen();
	void ShowScreenImage(QPixmap);
//ͼƬ����
	void CalcImage();
	void ShowImage(QPixmap);
//����
	void SetOptSobel();
	void SetOptGrad();
//�˳�
	void Quit();
};

#endif // WINDOW_H
