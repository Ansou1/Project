#include "Note.h"

Note::Note(QString notename)
{
    _notename = notename;
    _accidental = 0;
    _octave = 1;
    _enumduration = 1;
    _denomduration = 4;
}

/*
 *  Fonction qui retourne le gmn de la note en fonction de ses attributs
*/
QString Note::gmnNote()
{
    QString note;

    note = _notename;
    if (_accidental == -1) {
        note = note + "&";
    } else if (_accidental == -2) {
        note = note + "&&";
    } else if (_accidental == 1) {
        note = note + "#";
    } else if (_accidental == 2) {
        note = note + "##";
    }
    note = note + QString::number(_octave);
    if (_enumduration == 3) {
        note = note + ".";
    } else if (_enumduration == 7) {
        note = note + "..";
    } else {
        note = note + "/" + QString::number(_denomduration);
    }
    return note;
}

/*
 *  Voir le principe dans Project::writeXML
*/
QDomElement Note::writeXML(QDomDocument doc, int pos)
{
    QDomElement note = doc.createElement("note");

    QDomElement position = doc.createElement("position");
    position.setAttribute("type", "int");
    note.appendChild(position);
    QDomText positiontext = doc.createTextNode(QString::number(pos));
    position.appendChild(positiontext);

    QDomElement notename = doc.createElement("notename");
    notename.setAttribute("type", "QString");
    note.appendChild(notename);
    QDomText notenametext = doc.createTextNode(_notename);
    notename.appendChild(notenametext);

    QDomElement accidental = doc.createElement("accidental");
    accidental.setAttribute("type", "int");
    note.appendChild(accidental);
    QDomText accidentaltext = doc.createTextNode(QString::number(_accidental));
    accidental.appendChild(accidentaltext);

    QDomElement octave = doc.createElement("octave");
    octave.setAttribute("type", "int");
    note.appendChild(octave);
    QDomText octavetext = doc.createTextNode(QString::number(_octave));
    octave.appendChild(octavetext);

    QDomElement enumduration = doc.createElement("enumduration");
    enumduration.setAttribute("type", "int");
    note.appendChild(enumduration);
    QDomText enumdurationtext = doc.createTextNode(QString::number(_enumduration));
    enumduration.appendChild(enumdurationtext);

    QDomElement denomduration = doc.createElement("denomduration");
    denomduration.setAttribute("type", "int");
    note.appendChild(denomduration);
    QDomText denomdurationtext = doc.createTextNode(QString::number(_denomduration));
    denomduration.appendChild(denomdurationtext);

    return note;
}

/*
 *  Voir le principe dans Project::readXML
*/
void    Note::readXML(QDomElement elem)
{
    if (elem.tagName() == "notename") {
        _notename = elem.text();
    } else if (elem.tagName() == "accidental") {
        _accidental = elem.text().toInt(false, 10);
    } else if (elem.tagName() == "octave") {
        _octave = elem.text().toInt(false, 10);
    } else if (elem.tagName() == "enumduration") {
        _enumduration = elem.text().toInt(false, 10);
    } else if (elem.tagName() == "denomduration") {
        _denomduration = elem.text().toInt(false, 10);
    }
}

/*
 *  Fonctions set et get
*/

QString Note::getNotename() {
    return _notename;
}
int Note::getAccidental() {
    return _accidental;
}

int Note::getOctave() {
    return _octave;
}

int Note::getEnumduration() {
    return _enumduration;
}

int Note::getDenomduration() {
    return _denomduration;
}

void    Note::setNotename(QString notename) {
    _notename = notename;
}

void    Note::setAccidental(int accidental) {
    _accidental = accidental;
}

void    Note::setOctave(int octave) {
    _octave = octave;
}

void    Note::setEnumduration(int enumduration) {
    _enumduration = enumduration;
}

void    Note::setDenomduration(int denomduration) {
    _denomduration = denomduration;
}

