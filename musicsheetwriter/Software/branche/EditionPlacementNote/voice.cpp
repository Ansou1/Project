#include "Voice.h"

Voice::Voice(QString key)
{
    _key = key;
}

/*
 *  Ajoute une note à la portée
*/
void    Voice::addNote(Note *note)
{
    _notes.insert(_notes.count() + 1, note);
}

/*
 *  Supprime la note à la position passé en parametre
*/
void    Voice::deleteNote(int pos)
{
    for (auto it = _notes.begin(); it != _notes.end();) {
        if (it.key() == pos) {
            it = _notes.erase(it);
        } else {
            ++it;
        }
    }
}
/*
 * Voir le principe dans Project::writeXML
*/

QDomElement Voice::writeXML(QDomDocument doc, int pos)
{
    QDomElement voice = doc.createElement("voice");

    QDomElement position = doc.createElement("position");
    position.setAttribute("type", "int");
    voice.appendChild(position);
    QDomText positiontext = doc.createTextNode(QString::number(pos));
    position.appendChild(positiontext);

    QDomElement key = doc.createElement("key");
    key.setAttribute("type", "QString");
    voice.appendChild(key);
    QDomText keytext = doc.createTextNode(_key);
    key.appendChild(keytext);

    foreach(int i, _notes.keys()) {
        QDomElement note = _notes[i]->writeXML(doc, i);
        note.setAttribute("type","Note");
        voice.appendChild(note);
    }

    return voice;
}

/*
 *  Voir le principe dans Project::readXML
*/
void    Voice::readXML(QDomElement elem){
    if (elem.tagName() == "key") {
        _key = elem.text();
    } else if (elem.tagName() == "note") {
        Note *tmp = new Note("");
        QDomElement elemscore = elem.firstChildElement();
        while (!elemscore.isNull()) {
            tmp->readXML(elemscore);
            elemscore = elemscore.nextSiblingElement();
        }
        addNote(tmp);
    }
}

/*
 *  Fonctions get et set
*/
QMap<int, Note*>    Voice::getNotes() {
    return _notes;
}

QString Voice::getKey() {
    return _key;
}

void    Voice::setNotes(QMap<int, Note *> notes) {
    _notes = notes;
}

void    Voice::setKey(QString key) {
    _key = key;
}
