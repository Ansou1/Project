#ifndef VOICE_H
#define VOICE_H

#include    <QMap>
#include    <QtXml/QDomDocument>
#include    "Note.h"

class Voice
{
public:
    Voice(QString key);

    QMap<int, Note*>    getNotes();
    QString             getKey();
    void                setNotes(QMap<int, Note*> notes);
    void                setKey(QString key);

    void        addNote(Note *note);
    void        deleteNote(int pos);
    void        readXML(QDomElement elem);
    QDomElement writeXML(QDomDocument doc, int pos);

private:
    QString             _key;
    QMap<int, Note*>    _notes;
};

#endif // VOICE_H
