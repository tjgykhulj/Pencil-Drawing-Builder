#include "tphoto.h"
#include "qpropertyanimation.h"

TPhoto::TPhoto(QWidget *parent) : QLabel(parent)
{
	setWindowFlags(Qt::FramelessWindowHint | Qt::WindowStaysOnTopHint);
}

void TPhoto::SetPixmap(QPixmap pixmap) 
{
	pix = pixmap;
	rate = 1;
	AdjustAndShow(true);
}

void TPhoto::AdjustAndShow(bool shouldMove) 
{
	QSize size(pix.size()*rate);
	this->setPixmap(pix.scaled(size, Qt::KeepAspectRatio));
	this->resize(size);
	QRect rect = QApplication::desktop()->screenGeometry();//获得分辨率
	if (shouldMove)
		this->move((rect.width() - this->width()) / 2,
				(rect.height() - this->height()) / 2);
}

void TPhoto::mousePressEvent(QMouseEvent *event) 
{
	windowPos = this->pos();
	mousePos = event->globalPos();
	dPos = mousePos - windowPos;
	this->setCursor(Qt::ClosedHandCursor);
}

void TPhoto::mouseReleaseEvent(QMouseEvent *event) 
{
	QPoint delta = event->globalPos() - mousePos;
	if (abs(delta.x()) + abs(delta.y()) < 5) {
		Hide();
	}
	this->setCursor(Qt::ArrowCursor);
}

void TPhoto::mouseMoveEvent(QMouseEvent *event) 
{
	this->move(event->globalPos() - this->dPos);
}

void TPhoto::wheelEvent(QWheelEvent *event)
{
	int numDegrees = event->delta() / 8;
	int numSteps = numDegrees / 15;
	if (numSteps < 0)
		if (pix.width()*rate < 200 || pix.height()*rate < 200)
			return;
	rate += numSteps * 0.05; 
	AdjustAndShow();
}

void TPhoto::Hide() {
	QPropertyAnimation *animation = new QPropertyAnimation(this, "windowOpacity");
	animation->setDuration(500);
	animation->setStartValue(1);
	animation->setEndValue(0);
	animation->start();
	connect(animation, SIGNAL(finished()), this, SLOT(hide()));
}
void TPhoto::Show() {
	setWindowOpacity(1);
	show();
}