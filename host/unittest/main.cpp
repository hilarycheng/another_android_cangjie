#include "main.h"
#include <QtTest>

extern "C" {
#include "quick_method.h"
};

Main::Main(QObject *parent) : QObject(parent) {
}
  
void Main::testQuick()
{
  char path[128] = ".";

  QVERIFY(input_method[QUICK]->maxKey != NULL);
  QVERIFY(input_method[QUICK]->maxKey() == 2);
  input_method[QUICK]->init(path);
  QVERIFY(input_method[QUICK]->totalMatch() == 0);
  input_method[QUICK]->searchWord('a', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 15);
  input_method[QUICK]->reset();
  QVERIFY(input_method[QUICK]->totalMatch() == 0);
  input_method[QUICK]->searchWord('b', 'b', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 0);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('b', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 71);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('c', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 31);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('d', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 32);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('e', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 36);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('f', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 19);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('g', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 13);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('h', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 39);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('i', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 19);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('j', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 6);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('k', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 18);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('l', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 34);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('m', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 37);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('n', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 32);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('o', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 32);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('p', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 19);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('q', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 22);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('r', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 30);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('s', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 14);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('t', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 28);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('u', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 10);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('v', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 28);
  input_method[QUICK]->reset();
  input_method[QUICK]->searchWord('w', 'a', 0, 0, 0);
  QVERIFY(input_method[QUICK]->totalMatch() == 5);
}

int main(int argc, char **argv)
{
  QCoreApplication app(argc, argv);

  Main main;
  QTest::qExec(&main, argc, argv);

  
  return 0;
}
