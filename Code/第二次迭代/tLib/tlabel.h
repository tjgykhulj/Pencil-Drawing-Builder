#ifndef TLABEL_H
#define TLABEL_H

#include "qlabel.h"
#include "qdialog.h"
#include "qlayout.h"
#include "qevent.h"

class TLabel : public QLabel
{
	Q_OBJECT

public:
	TLabel(QWidget *parent = 0);
	void SetPixmap(QPixmap);
	void SavePixmap(QString);
signals:
	void show(QPixmap);
protected:
	void paintEvent(QPaintEvent *);
	void mousePressEvent(QMouseEvent*);
	void mouseMoveEvent(QMouseEvent*);
	void mouseReleaseEvent(QMouseEvent*);
private:
	QPixmap pix;
};

#endif TLABEL_H