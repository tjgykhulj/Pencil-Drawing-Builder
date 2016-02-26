#ifndef TPHOTO_H
#define TPHOTO_H

#include "qlabel.h"
#include "qevent.h"
#include "qapplication.h"
#include "qdesktopwidget.h"

class TPhoto : public QLabel {
	Q_OBJECT

public:
	TPhoto(QWidget *parent = 0);
	void SetPixmap(QPixmap);
	void Show();
	void Hide();

protected:
	void mousePressEvent(QMouseEvent*);
	void mouseReleaseEvent(QMouseEvent*);
	void mouseMoveEvent(QMouseEvent*);
	void wheelEvent(QWheelEvent*);
private:
	void AdjustAndShow(bool shouldMove = false);
	QPoint dPos, windowPos, mousePos;
	QPixmap pix;
	double rate;
};

#endif TPHOTO_H