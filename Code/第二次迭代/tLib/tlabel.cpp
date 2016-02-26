#include "tlabel.h"
#include "qpainter.h"

TLabel::TLabel(QWidget *parent) :QLabel(parent) {
	setMouseTracking(true);
	connect(this, SIGNAL(show(QPixmap)), parent, SLOT(ShowImage(QPixmap)));
	this->setPixmap(QPixmap(500, 500));
	this->adjustSize();
}

void TLabel::SetPixmap(QPixmap pix) {
	this->pix = pix;
	update();
}

void TLabel::SavePixmap(QString fileName) {
	pix.save(fileName);
}

void TLabel::mousePressEvent(QMouseEvent *e)
{
	this->setCursor(Qt::ClosedHandCursor);
}
void TLabel::mouseMoveEvent(QMouseEvent *e) {
	this->setCursor(Qt::OpenHandCursor);
}

void TLabel::mouseReleaseEvent(QMouseEvent *e) {
	this->setCursor(Qt::ArrowCursor);
	if (e->button() == Qt::LeftButton)
		emit show(pix);
}

void TLabel::paintEvent(QPaintEvent *e) 
{
	QPainter painter(this);
	QPixmap temp = pix.scaled(500, 500, Qt::AspectRatioMode::KeepAspectRatio);
	painter.drawPixmap((500-temp.width())/2, (500-temp.height())/2, temp);
}