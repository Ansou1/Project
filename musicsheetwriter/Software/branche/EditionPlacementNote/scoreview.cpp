#include "ScoreView.h"

/*
 *  Création de la partition à l'aide de GuidoFactory
 *  On ouvre GuidoFactory, puis une partition
 *  On genere les elements musicaux
 *  On recupere le ARHandler puis on le set
*/
scoreView::scoreView(Score *score, QGuidoWidget *w)
{
    _widget = w;
    _widget->setWindowTitle(score->getPartition());
    GuidoFactoryOpen(&_factory);
    GuidoFactoryOpenMusic(_factory);
    generateMusicElement(score);
    ARHandler ar = GuidoFactoryCloseMusic(_factory);
    GuidoFactoryClose(_factory);
    _widget->setARHandler(ar);
}

scoreView::~scoreView()
{

}

/*
 *  Genere le nom du compositeur à l'aide de la guidofactory
*/
void    scoreView::generateCompositor(Score *score)
{
    GuidoFactoryOpenTag (_factory, "composer", 0);
    GuidoFactoryAddTagParameterString (_factory, score->getCompositeur().toStdString().c_str());
    GuidoFactoryCloseTag (_factory);
}

/*
 *  Genere le nom de la partition à l'aide de la guidofactory
*/
void    scoreView::generateTitle(Score *score)
{
    GuidoFactoryOpenTag (_factory, "title", 0);
    GuidoFactoryAddTagParameterString (_factory, score->getPartition().toStdString().c_str());
    GuidoFactoryCloseTag (_factory);
}

/*
 * Genere le meter de la portée
*/
void    scoreView::generateMeter(Score *score)
{
    GuidoFactoryOpenTag (_factory, "meter", 0);
    QString meter = QString::number(score->getrythme()) + "/" + QString::number(score->getBattement());
    GuidoFactoryAddTagParameterString (_factory, meter.toStdString().c_str());
    GuidoFactoryCloseTag (_factory);
}

/*
 *  Genere la clé de la portée
*/
void    scoreView::generateKey(QString key)
{
    GuidoFactoryOpenTag(_factory, "clef", 0);
    GuidoFactoryAddTagParameterString(_factory, key.toStdString().c_str());
    GuidoFactoryCloseTag (_factory);
}

/*
 *  Generer la partition
 *  Boucle sur toutes les portées de la partition
 *  Si c'est la 1ere on y met le nom du compositeur, le titre de la partition et son meter
 *  On boucle sur toutes les notes de la portée et on créer l'event note avec tous les attributs de la note
*/
void    scoreView::generateMusicElement(Score *score)
{
    foreach(int i, score->getVoices().keys()) {
        GuidoFactoryOpenVoice(_factory);
        generateKey(score->getVoices()[i]->getKey());
        if (i == 1) {
            generateCompositor(score);
            generateTitle(score);
            generateMeter(score);
        }
        QMap<int, Note*> notes = score->getVoices()[i]->getNotes();
        foreach(int j, notes.keys()) {
            GuidoFactoryOpenEvent (_factory, notes[j]->getNotename().toStdString().c_str());
            GuidoFactorySetEventAccidentals(_factory, notes[j]->getAccidental());
            GuidoFactorySetDuration(_factory,notes[j]->getEnumduration(),notes[j]->getDenomduration());
            GuidoFactorySetOctave(_factory, notes[j]->getOctave());
            GuidoFactoryCloseEvent (_factory);
        }
        GuidoFactoryCloseVoice(_factory);
    }
}

/*
 *  Affiche la partition généré par GuidoFactory
*/
void    scoreView::showscore() {
    _widget->resize(920, 570);
    _widget->show();
}


