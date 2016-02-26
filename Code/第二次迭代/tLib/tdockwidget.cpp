#include "tdockwidget.h"

TDockWidget::TDockWidget(const QString &title, QWidget *parent) :QDockWidget(title, parent) {
	main = new QWidget;
	layout = new QVBoxLayout;

	main->setLayout(layout);
	this->setWidget(main);
	this->setFeatures(QDockWidget::DockWidgetFloatable | QDockWidget::DockWidgetMovable);
}

void TDockWidget::addWidget(QWidget *obj) {
	layout->addWidget(obj);
}