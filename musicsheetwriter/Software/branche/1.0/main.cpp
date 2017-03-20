#include "musicsheetwriter.h"
#include <QApplication>
#include <windows.h>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    MusicSheetWriter w;
    w.show();
    return a.exec();
}
