#ifndef SCOREVIEW_H
#define SCOREVIEW_H

#include    <QGuidoWidget.h>
#include    "GuidoFactory.h"
#include    "Score.h"

class scoreView
{
public:
    scoreView(Score *score, QGuidoWidget *w);
    ~scoreView();

    void    showscore();

private:
    ARFactoryHandler    _factory;
    QGuidoWidget        *_widget;

    void    generateTitle(Score *score);
    void    generateCompositor(Score *score);
    void    generateMeter(Score *score);
    void    generateKey(QString key);
    void    generateMusicElement(Score *score);

};

#endif // SCOREVIEW_H
