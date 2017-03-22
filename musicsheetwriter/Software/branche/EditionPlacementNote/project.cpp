#include "Project.h"

Project::Project(QString projectname, QString projectpath, Score *score)
{
    _projectname = projectname;
    _projectpath = projectpath;
    _score = score;
    _isactive = false;
}

Project::~Project()
{

}

/*
 *  A noter que cette fonction ne s'execute que sur le projet actif
 *  Fonction de sauvegarde du projet
 *  On créer le dossier si celui-ci n'existe pas
 *  Si il ya une partition on créer son gmn (on supprime a chaque sauvegarde le fichier pour le réécrire)
 *  On ecrit dans le fichier le code gmn de la partition
 *  On sérialise les infos du projet
*/
void    Project::save()
{
    if (!QDir(_projectpath).exists()) {
        QDir().mkdir(_projectpath);
    }
    if (_score != NULL) {
        QString path = _projectpath + _score->getPartition() + ".gmn";
        QFile::remove(path);
        QFile file(path);
        file.open(QIODevice::WriteOnly | QIODevice::Text);
        QTextStream out(&file);
        QString gmncode;
        QMap<int, Voice*> voicestmp = _score->getVoices();
        foreach (int i, voicestmp.keys()) {
            if (i != 1) {
                gmncode += ",[";
            } else {
                gmncode = "{[" + _score->getInfoScore() + " ";
            }
            gmncode += "\\clef<\"" + voicestmp[i]->getKey() + "\"> \\meter<\"" + QString::number(_score->getrythme()) + "/" + QString::number(_score->getBattement()) + "\">";
            QMap<int, Note*> notestmp = voicestmp[i]->getNotes();
            foreach (int j, notestmp.keys()) {
                gmncode += " " + notestmp[j]->gmnNote();
            }
            gmncode += "]";
        }
        gmncode += "}";
        out << gmncode;
        _score->setGmncode(gmncode);
        file.close();
    }
    this->serialized();
}

/*
 *  Fonction qui demande si on souhaite enregister le projet -> est appelé lors de la fermeture du projet
*/
void    Project::checkSave()
{
    QMessageBox::StandardButton reply;

    reply = QMessageBox::question(0, "Ferme le projet", "Souhaitez-vous enregistrer le projet " + _projectname + " avant la fermeture ?", QMessageBox::Yes|QMessageBox::No);
    if (reply == QMessageBox::Yes) {
        this->save();
    }
}

/*
 *  Fonction qui ecrit dans le .msw tous les attributs du projet au format XML
 *  Pour chaque element qu'on souahite ajouter, on créer un QDomElement et on lui donne le nom souhaité
 *  Si on souhaite ajouter un attribut comme le type il suffit d'appeler setAttribut("type", "letypequonveut")
 *  On peut egalement ajouter du text entre les crochets à l'aide de QDomText
*/
QDomElement Project::writeXML(QDomDocument doc)
{
    QDomElement project = doc.createElement("project");
    project.setAttribute("type","Project");

    QDomElement projectname = doc.createElement("projectname");
    projectname.setAttribute("type","QString");
    project.appendChild(projectname);
    QDomText nametext = doc.createTextNode(_projectname);
    projectname.appendChild(nametext);

    QDomElement projectpath = doc.createElement("projectpath");
    projectpath.setAttribute("type","QString");
    project.appendChild(projectpath);
    QDomText pathtext = doc.createTextNode(_projectpath);
    projectpath.appendChild(pathtext);

    if (_score != NULL) {
        QDomElement score = _score->writeXML(doc);
        score.setAttribute("type","Score");
        project.appendChild(score);
    }

    return project;
}

/*
 *  Fonction qui reçoit un QDomElement qui correspond à un des elements créer dans le writeXML
 *  Ici il suffit juste de faire correspondre le nom de l'element récupéré avec l'attribut qui lui correspond
*/
void        Project::readXML(QDomElement elem)
{
    if (elem.tagName() == "projectname") {
        _projectname = elem.text();
    } else if (elem.tagName() == "projectpath") {
        _projectpath = elem.text();
    } else if (elem.tagName() == "score") {
        Score *tmp = new Score();
        QDomElement elemscore = elem.firstChildElement();
        while (!elemscore.isNull()) {
            tmp->readXML(elemscore);
            elemscore = elemscore.nextSiblingElement();
        }
        _score = tmp;
    }
}

/*
 *  Serialise les infos de l'image de la partition
 *  Ici on créer une image png temporaire de la partition
 *  On recupere les infos que l'on souhaite et on detruit l'image
*/
QDomElement    Project::serializedScorePng(QDomDocument doc)
{
    QString filename = _projectpath + _projectname + ".png";
    int pageIndex = 1;
    QGuidoPainter *ptr = QGuidoPainter::createGuidoPainter();
    ptr->setGMNCode(_score->getGmncode());
    QSizeF s = ptr->pageSizeMM( pageIndex );
    QImage image( s.toSize() * 10 , QImage::Format_ARGB32 );
    image.fill( QColor(Qt::white).rgb() );
    QPainter painter( &image );
    ptr->draw( &painter , pageIndex , image.rect() );
    image.save( filename );

    QImage imagepng(filename);
    QByteArray byteArray;
    QBuffer buffer(&byteArray);
    buffer.open(QIODevice::WriteOnly);
    imagepng.save(&buffer, "png");

    QDomElement picture = doc.createElement("Picture");
    picture.setAttribute("type", "png");

    QDomElement width = doc.createElement("width");
    width.setAttribute("type", "int");
    picture.appendChild(width);
    QDomText widthtext = doc.createTextNode(QString::number(imagepng.size().rwidth()));
    width.appendChild(widthtext);

    QDomElement height = doc.createElement("height");
    height.setAttribute("type", "int");
    picture.appendChild(height);
    QDomText heighttext = doc.createTextNode(QString::number(imagepng.size().rheight()));
    height.appendChild(heighttext);

    QDomElement picturedata = doc.createElement("PictureData");
    picturedata.setAttribute("type", "QString");
    picture.appendChild(picturedata);
    QDomText data = doc.createTextNode(byteArray.toBase64());
    picturedata.appendChild(data);

    QFile::remove(filename);

    return picture;
}

/*
 *  Fonction qui serialize les attributs du projet ainsi que l'image de la partition
*/
void    Project::serialized()
{
    QDomDocument doc;
    QString filename = _projectpath + _projectname + ".msw";
    QDomNode xmlNode = doc.createProcessingInstruction("xml","version=\"1.0\" encoding=\"UTF-8\"");
    doc.insertBefore(xmlNode, doc.firstChild());

    QDomElement project = writeXML(doc);
    doc.appendChild(project);

    if (_score != NULL) {
        QDomElement pngdata = serializedScorePng(doc);
        doc.appendChild(pngdata);
    }

    QFile file( filename );
    if (!file.open(QIODevice::WriteOnly)) {
        QMessageBox::critical(0, "Sauvegarde échouée", "Impossible d'ouvrir le fichier " + filename );
        return;
    }
    QTextStream ts(&file);
    int indent = 2;
    doc.save(ts, indent);
}

/*
 *  Fonction qui recupère tous les attributs du projet
*/
void    Project::deserialize(QString path)
{
    QFile file(path);
    if (!file.open(QIODevice::ReadOnly)) {
        QMessageBox::critical(0, "Ouverture échouée", "Impossible d'ouvrir le fichier " + path);
        return;
    }
    QDomDocument doc;
    doc.setContent(&file, false);
    QDomElement racine = doc.documentElement();
    racine = racine.firstChildElement();
    while (!racine.isNull())
    {
        this->readXML(racine);
        racine = racine.nextSiblingElement();
    }
}

/*
 *  Fonctions set et get
*/
void    Project::setProjectname(QString projectname) {
    _projectname = projectname;
}

void    Project::setProjectpath(QString projectpath) {
    _projectpath = projectpath;
}

void    Project::setScore(Score* score) {
    _score = score;
}

void    Project::setIsactive(bool active) {
    _isactive = active;
}

QString Project::getProjectname() {
    return _projectname;
}

QString Project::getProjectpath() {
    return _projectpath;
}

Score *Project::getScore() {
    return _score;
}

bool    Project::getIsactive() {
    return _isactive;
}

