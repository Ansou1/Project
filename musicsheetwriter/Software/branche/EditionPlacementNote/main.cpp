
#include "MusicSheetWriter.h"
#include <QApplication>
#include <windows.h>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    QGuidoPainter::startGuidoEngine();
    MusicSheetWriter w;
    w.show();
    int result = a.exec();
    QGuidoPainter::stopGuidoEngine();
    return result;
}
