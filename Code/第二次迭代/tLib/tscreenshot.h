#ifndef TSCREENSHOT_H
#define TSCREENSHOT_H

#include "qlabel.h"
#include "qevent.h"
#include "qmouseeventtransition.h"
#include "qrubberband.h"
#include "qaction.h"
#include "qdesktopwidget.h"
#include "qapplication.h"
#include "qfiledialog.h"

class TScreenShot : public QLabel
{
	Q_OBJECT
public:
	TScreenShot(QWidget *parent = 0);
protected:
	void mousePressEvent(QMouseEvent*);
	void mouseReleaseEvent(QMouseEvent*);
	void mouseMoveEvent(QMouseEvent*);
signals:
	void showImage(QPixmap);
public slots :
	void GrapWindowScreen();

private:
	QRubberBand *rubberBand;
	bool leftMousePress;
	QPoint first, second;
	QPixmap fullScreenPixmap;
	QPixmap shotScreenPixmap;
};

#endif TSCREENSHOT_H