#include <QtWidgets/QApplication>
#include "window.h"

int main(int argc, char *argv[])
{
	QApplication a(argc, argv);    
	Window *w = new Window;
	w->show();
	//������������Ļ����
	w->move((QApplication::desktop()->width() - w->width()) / 2,
		(QApplication::desktop()->height() - w->height()) / 2);
	int r = a.exec();
	return r;
}