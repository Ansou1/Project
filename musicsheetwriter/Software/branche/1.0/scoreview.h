#ifndef SCOREVIEW_H
#define SCOREVIEW_H

#include    <QLabel>
#include    <QGuidoPainter.h>
#include    <QImage>
#include    <QPainter>

class scoreView :   public QLabel
{
    Q_OBJECT

public:
    scoreView(QString projectname, QString scorename, QString gmncode);
    ~scoreView();

    void    setgmncode(QString gmncode);
    void    setscoreviewlist(QList<scoreView*> scoreviewlist);
    QString getgmncode();
    QString getscorename();

private:
    QString         _gmncode;
    QString         _projectname;
    QString         _scorename;
    QGuidoPainter   *_painter;

    void    generatePainter();
    QImage  drawPainter();
};

#endif // SCOREVIEW_H
