#include "tscreenShot.h"

TScreenShot::TScreenShot(QWidget *parent) : QLabel(parent)
{
	leftMousePress = false;

	rubberBand = new QRubberBand(QRubberBand::Rectangle, this);
}

void TScreenShot::mousePressEvent(QMouseEvent* event)
{
	if (event->button() == Qt::LeftButton) {
		leftMousePress = true;
		first = event->pos();
		rubberBand->setGeometry(QRect(first, QSize()));
		rubberBand->show();
	} else
		if (event->button() == Qt::RightButton) {
			this->hide();
		}
}

void TScreenShot::mouseReleaseEvent(QMouseEvent *event)
{
	if (event->button() == Qt::LeftButton) {
		leftMousePress = false;
		second = event->pos();

		QRect rect = QRect(first, second).normalized();
		rubberBand->hide();
		this->hide();
		if (rect.width() > 50 && rect.height() > 50)
			emit showImage(fullScreenPixmap.grabWidget(this, rect));
	}
}
void TScreenShot::mouseMoveEvent(QMouseEvent *event)
{
	if (leftMousePress)
		rubberBand->setGeometry(QRect(first, event->pos()).normalized());
}

void TScreenShot::GrapWindowScreen() {
	fullScreenPixmap = QPixmap::grabWindow(QApplication::desktop()->winId());
	this->setPixmap(fullScreenPixmap);
	this->showFullScreen();
}