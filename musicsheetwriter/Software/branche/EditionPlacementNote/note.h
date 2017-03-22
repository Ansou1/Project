#ifndef NOTE_H
#define NOTE_H

#include    <QString>
#include    <QtXml/QDomDocument>

class Note
{
public:
    Note(QString notename);
    ~Note();

    QString getNotename();
    int     getAccidental();
    int     getOctave();
    int     getEnumduration();
    int     getDenomduration();
    void    setNotename(QString notename);
    void    setAccidental(int accidental);
    void    setOctave(int octave);
    void    setEnumduration(int enumduration);
    void    setDenomduration(int denomduration);

    QDomElement writeXML(QDomDocument doc, int pos);
    void        readXML(QDomElement elem);
    QString     gmnNote();

private:
    QString _notename;
    int     _accidental;
    int     _octave;
    int     _enumduration;
    int     _denomduration;
};

#endif // NOTE_H
