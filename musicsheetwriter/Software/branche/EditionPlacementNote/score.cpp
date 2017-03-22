#include "Score.h"

Score::Score(QString partition, QString compositeur, int rythme, int battement, QString tempo)
{
    _partition = partition;
    _compositeur = compositeur;
    _rythme = rythme;
    _battement = battement;
    _tempo = tempo;
    _infoscore = "\\composer<\"" + _compositeur + "\"> \\title<\"" + _partition + "\">";
    _gmncode = "";
    _voices.insert(1, new Voice("g"));
}

Score::~Score()
{

}

/*
 *  Ajoute une note à la map de notes
*/
void    Score::addVoice(Voice *voice)
{
    _voices.insert(_voices.count() + 1, voice);
}

/*
 * Voir le principe dans writeXML
*/
QDomElement Score::writeXML(QDomDocument doc)
{
    QDomElement score = doc.createElement("score");

    QDomElement scorename = doc.createElement("scorename");
    scorename.setAttribute("type","QString");
    score.appendChild(scorename);
    QDomText nametext = doc.createTextNode(_partition);
    scorename.appendChild(nametext);

    QDomElement compo = doc.createElement("composer");
    compo.setAttribute("type","QString");
    score.appendChild(compo);
    QDomText namecompo = doc.createTextNode(_compositeur);
    compo.appendChild(namecompo);

    QDomElement rythme = doc.createElement("rhythm");
    rythme.setAttribute("type","int");
    score.appendChild(rythme);
    QDomText rythmetext = doc.createTextNode(QString::number(_rythme));
    rythme.appendChild(rythmetext);

    QDomElement battement = doc.createElement("beat");
    battement.setAttribute("type","int");
    score.appendChild(battement);
    QDomText battementtext = doc.createTextNode(QString::number(_battement));
    battement.appendChild(battementtext);

    QDomElement tempo = doc.createElement("tempo");
    tempo.setAttribute("type","QString");
    score.appendChild(tempo);
    QDomText tempotext = doc.createTextNode(_tempo);
    tempo.appendChild(tempotext);

    QDomElement gmncode = doc.createElement("gmncode");
    gmncode.setAttribute("type","QString");
    score.appendChild(gmncode);
    QDomText gmncodetext = doc.createTextNode(_gmncode);
    gmncode.appendChild(gmncodetext);

    foreach(int i, _voices.keys()) {
        QDomElement voice = _voices[i]->writeXML(doc, i);
        voice.setAttribute("type","Voice");
        score.appendChild(voice);
    }

    return score;
}

/*
 * voir le principe dans Project::readXML
*/
void    Score::readXML(QDomElement elem)
{
    if (elem.tagName() == "scorename") {
        _partition = elem.text();
    } else if (elem.tagName() == "composer") {
        _compositeur = elem.text();
    } else if (elem.tagName() == "rhythm") {
        _rythme = elem.text().toInt(false, 10);
    } else if (elem.tagName() == "beat") {
        _battement = elem.text().toInt(false, 10);
    } else if (elem.tagName() == "tempo") {
        _tempo = elem.text();
   /* }/* else if (elem.tagName() == "gmncode") {
        _gmncode = elem.text();*/
    } else if (elem.tagName() == "voice") {
        QDomElement elemscore = elem.firstChildElement();
        while (!elemscore.isNull()) {
            _voices[1]->readXML(elemscore);
            elemscore = elemscore.nextSiblingElement();
        }
    }
}

/*
 *  Fonctions set et get
*/
void    Score::setPartition(QString partition){
    _partition = partition;
}

void    Score::setCompositeur(QString compositeur){
    _compositeur = compositeur;
}

void    Score::setRythme(int rythme){
    _rythme = rythme;
}

void    Score::setBattement(int battement){
    _battement = battement;
}

void    Score::setTempo(QString tempo){
    _tempo = tempo;
}

void    Score::setGmncode(QString gmncode) {
    _gmncode = gmncode;
}

void    Score::setVoices(QMap<int, Voice *> voices) {
    _voices = voices;
}

QString Score::getPartition(){
    return _partition;
}

QString Score::getCompositeur(){
    return _compositeur;
}

int Score::getrythme(){
    return _rythme;
}

int Score::getBattement(){
    return _battement;
}

QString Score::getTempo(){
    return _tempo;
}

QString Score::getGmncode() {
    return _gmncode;
}

QString Score::getInfoScore() {
    return _infoscore;
}

QMap<int, Voice*>   Score::getVoices() {
    return _voices;
}
