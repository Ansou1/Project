#ifndef SCORE_H
#define SCORE_H

#include    <QString>
#include    <QtXml/QDomDocument>
#include    <QMap>
#include    "Voice.h"

class Score
{
public:
    Score(QString partition = "", QString compositeur = "" , int rythme = 1, int battement = 1, QString tempo = "");
    ~Score();

    void    setPartition(QString partition);
    void    setCompositeur(QString compositeur);
    void    setRythme(int rythme);
    void    setBattement(int battement);
    void    setTempo(QString tempo);
    void    setGmncode(QString gmncode);
    void    setVoices(QMap<int, Voice*> voices);
    QString getPartition();
    QString getCompositeur();
    int     getrythme();
    int     getBattement();
    QString getTempo();
    QString getGmncode();
    QString getInfoScore();
    QMap<int, Voice*>   getVoices();

    void        addVoice(Voice *voice);
    QDomElement writeXML(QDomDocument doc);
    void        readXML(QDomElement elem);
    QString     getmncode();

private:
    QString _partition;
    QString _compositeur;
    int     _rythme;
    int     _battement;
    QString _tempo;
    QString _infoscore;
    QString _gmncode;
    QMap<int, Voice*>   _voices;
};

#endif // SCORE_H
