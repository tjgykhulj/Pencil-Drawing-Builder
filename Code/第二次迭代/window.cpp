#include "window.h"
#include "qlayout.h"
#include "qsplitter.h"
#include "qfiledialog.h"
#include "qtimer.h"
#include "qfile.h"
#include "qtextstream.h"
#include "qmessagebox.h"
#include "qpropertyanimation.h"

#include "cvPencil.h"
#include "cvCamera.h"
#pragma comment(lib,"cvPencil.lib")
#pragma comment(lib,"cvCamera.lib")


Window::Window(QWidget *parent)
	: QMainWindow(parent)
{
	ui.setupUi(this);
	QPropertyAnimation *animation = new QPropertyAnimation(this, "windowOpacity");
	animation->setDuration(500);
	animation->setStartValue(0);
	animation->setEndValue(1);
	animation->start();

	//��Ƭ��ʾ����
	showPix = new TPhoto;
	src = new TLabel(this);
	dst = new TLabel(this);
	//��������
	sobel = new QRadioButton("sobel", this);
	grad = new QRadioButton("grad", this);
	sobel->setChecked(true);
	//��������ť
	convert = new QPushButton("Convert", this);

	connect(convert, SIGNAL(clicked()), this, SLOT(CalcImage()));
	connect(sobel, SIGNAL(clicked()), this, SLOT(SetOptSobel()));
	connect(grad, SIGNAL(clicked()), this, SLOT(SetOptGrad()));

	/*********************���Ĳ���*********************/
	QVBoxLayout *layoutMid = new QVBoxLayout;
	layoutMid->addStretch();
	layoutMid->addWidget(sobel);
	layoutMid->addWidget(grad);
	layoutMid->addWidget(convert);
	layoutMid->addStretch();

	QHBoxLayout *layout = new QHBoxLayout;
	layout->addWidget(src);
	layout->addLayout(layoutMid);
	layout->addWidget(dst);

	QWidget *widget = new QWidget(this);
	widget->setLayout(layout);
	setCentralWidget(widget);
	/**************************************************/

	//��ʹ����Ƭ
	LoadImage(T_EXAMPLE);
	CalcImage();

	//�ļ�����ť
	select	= new QPushButton("Select", this);
	save = new QPushButton("Save", this);
	connect(select, SIGNAL(clicked()), this, SLOT(LoadImageFile()));
	connect(save, SIGNAL(clicked()), this, SLOT(SaveImageFile()));
	//����ͷ����
	camTimer = new QTimer(this);
	camOpen	= new QPushButton("Open", this);
	camGet	= new QPushButton("Pause/Continue", this);
	camCancel = new QPushButton("Cancel", this);
	connect(camOpen, SIGNAL(clicked()), this, SLOT(CameraOpen()));
	connect(camGet, SIGNAL(clicked()), this, SLOT(CameraChangeStatus()));
	connect(camCancel, SIGNAL(clicked()), this, SLOT(CameraClose()));
	camGet->setEnabled(false);
	camCancel->setEnabled(false);

	//��ͼ��ť
	shotScreen = new TScreenShot();
	shot = new QPushButton("Shot Screen");
	shotM = new QPushButton("Min and Shot Screen");
	
	connect(shot, SIGNAL(clicked()), shotScreen, SLOT(GrapWindowScreen()));
	connect(shotM, SIGNAL(clicked()), this, SLOT(MinAndGrapWindowScreen()));
	connect(shotScreen, SIGNAL(showImage(QPixmap)), this, SLOT(ShowScreenImage(QPixmap)));

	//����dock���ڣ�����Ӧ��ť������ȥ
	TDockWidget *dock1 = new TDockWidget(tr("File"), this);
	dock1->addWidget(select);
	dock1->addWidget(save);
	this->addDockWidget(Qt::LeftDockWidgetArea, dock1);

	TDockWidget *dock2 = new TDockWidget(tr("Camera"), this);
	dock2->addWidget(camOpen);
	dock2->addWidget(camGet);
	dock2->addWidget(camCancel);
	this->addDockWidget(Qt::LeftDockWidgetArea, dock2);

	TDockWidget *dock3 = new TDockWidget(tr("Shot"), this);
	dock3->addWidget(shot);
	dock3->addWidget(shotM);
	this->addDockWidget(Qt::LeftDockWidgetArea, dock3);
}

/***************************ͼ����******************************/
//����ͼƬ���ı�����TLabel
void Window::SetImage(QPixmap pixmap)
{
	src->SetPixmap(pixmap);
	dst->SetPixmap(QPixmap("image/background.png"));
}
//����һ��ͼ�����·������T_MIDDLE�����串�ǵ����е�T_MIDDLE
#define max_len 800
void Window::LoadImage(QString filename)
{
	QPixmap pix = QPixmap(filename);
	//ͼƬ����ʱ��������С����
	if (pix.height() > max_len || pix.width() > max_len) {
		pix = pix.scaled(max_len, max_len, Qt::AspectRatioMode::KeepAspectRatio);
	}
	if (filename != T_MIDDLE)
		pix.save(T_MIDDLE);

	this->SetImage(QPixmap(T_MIDDLE));
}
//����ǰT_MIDDLE�������
void Window::CalcImage()
{
	Image result = PencilDrawing(T_MIDDLE);
	QImage img((uchar*)result.data, result.width, result.height, QImage::Format_Indexed8);
	dst->SetPixmap(QPixmap::fromImage(img));
}
//���TLabelʱ��ͼƬչʾ
void Window::ShowImage(QPixmap pix) {
	showPix->SetPixmap(pix);
	showPix->Show();
}

/******�����ڹر�ʱ�ص����д���*******/
void Window::closeEvent(QCloseEvent * event)
{
	QPropertyAnimation *animation = new QPropertyAnimation(this, "windowOpacity");
	animation->setDuration(500);
	animation->setStartValue(1);
	animation->setEndValue(0);
	animation->start();
	connect(animation, SIGNAL(finished()), this, SLOT(Quit()));
	event->ignore();
}

void Window::Quit() {
	cvCloseCamera();
	qApp->quit();
}

/*****************************Camera SLOTS*************************************/
//��cvCamera��ȡһ֡
void Window::CameraGetFrame() 
{
	CvFrame frame = cvReadFrame();
	QImage img = QImage((uchar *)frame.data, frame.width, frame.height, QImage::Format_RGB888).rgbSwapped();
	this->SetImage(QPixmap::fromImage(img));
}
//ת��Camera����ͣ������״̬
void Window::CameraChangeStatus()
{
	if (camTimer->isActive()) {
		src->SavePixmap(T_MIDDLE);
		camTimer->stop();
		convert->setEnabled(true);
	} else {
		camTimer->start(50);
		convert->setEnabled(false);
	}
}
//��Camera
void Window::CameraOpen() 
{
	if (!cvOpenCamera()) {
		cvCloseCamera();
		QMessageBox::warning(this,
			"Can't Find Camera", 
			"Please check your camera and drive", " OK ");
		return;
	}
	camOpen->setEnabled(false);
	camGet->setEnabled(true); 
	camCancel->setEnabled(true);
	convert->setEnabled(false);
	camTimer->start(50);
	connect(camTimer, SIGNAL(timeout()), this, SLOT(CameraGetFrame()));
}

void Window::CameraClose() 
{
	camOpen->setEnabled(true);
	camGet->setEnabled(false);
	camCancel->setEnabled(false);
	convert->setEnabled(true);
	if (camTimer->isActive()) {
		camTimer->stop();
		src->SavePixmap(T_MIDDLE);
	}
	disconnect(camTimer, SIGNAL(timeout()), this, SLOT(CameraGetFrame()));
	cvCloseCamera();
}

/*****************************File SLOTS*************************************/
void Window::LoadImageFile() {
	/*�����ļ���*/
	QString fileName = QFileDialog::getOpenFileName(this, "Select", "",
		"Images (*.png *.bmp *.jpg *.tif *.GIF )");

	if (fileName != "")
		LoadImage(fileName);
}

void Window::SaveImageFile() {
	/*�����ļ���*/
	QString fileName = QFileDialog::getSaveFileName(this, "Save", "result.png",
		"Images (*.png)");
	dst->SavePixmap(fileName);
}

/*****************************ShotScreen SLOTS*************************************/
void Window::ShowScreenImage(QPixmap img) {
	this->SetImage(img);
	src->SavePixmap(T_MIDDLE);
}

void Window::MinAndGrapWindowScreen() {
	this->showMinimized();
	shotScreen->GrapWindowScreen();
}

/*****************************Opt SLOTS*************************************/
void Window::SetOptSobel() {
	useSobel();
}

void Window::SetOptGrad()	{
	useGrad();
}