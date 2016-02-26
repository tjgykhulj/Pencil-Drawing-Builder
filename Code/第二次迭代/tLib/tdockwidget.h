#ifndef TDOCKWIDGET_H
#define TDOCKWIDGET_H

#include "qdockwidget.h"
#include "qlayout.h"

class TDockWidget : public QDockWidget
{
	Q_OBJECT

public:
	TDockWidget(const QString &, QWidget *parent = 0);
	void addWidget(QWidget*);
private:
	QWidget *main;
	QLayout *layout;
};

#endif TDOCKWIDGET_H