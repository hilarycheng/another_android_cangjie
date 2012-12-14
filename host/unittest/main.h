#ifndef MAIN_H
#define MAIN_H

#include <QtTest>

class Main : public QObject
{
  Q_OBJECT;

public:
  Main(QObject *parent = 0);

private slots:
  void testQuick();

};

#endif
