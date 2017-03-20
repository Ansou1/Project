#ifndef SCORE_H
#define SCORE_H

#include    <QString>
#include    "scoreview.h"
#include    <QtXml/QDomDocument>

class Score
{
public:
    Score(QString partition, QString compositeur, int rythme, int battement, QString tempo);
    Score();
    ~Score();

    void    setPartition(QString partition);
    void    setCompositeur(QString compositeur);
    void    setRythme(int rythme);
    void    setBattement(int battement);
    void    setTempo(QString tempo);
    void    setGmncode(QString gmncode);
    QString getPartition();
    QString getCompositeur();
    int     getrythme();
    int     getBattement();
    QString getTempo();
    QString getGmncode();

    QDomElement writeXML(QDomDocument doc);
    void        readXML(QDomElement elem);

private:
    QString _partition;
    QString _compositeur;
    int     _rythme;
    int     _battement;
    QString _tempo;
    QString _gmncode;
};

#endif // SCORE_H
