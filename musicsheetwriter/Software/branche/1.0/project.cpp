#include "project.h"

#include <QDebug>

Project::Project(QString projectname, QString projectpath, Score *score)
{
    _projectname = projectname;
    _projectpath = projectpath;
    _score = score;
    _isactive = false;
}

Project::Project()
{
    _projectname = "";
    _projectpath = "";
    _score = NULL;
    _isactive = false;
}

Project::~Project()
{

}

void    Project::setprojectname(QString projectname) {
    _projectname = projectname;
}

void    Project::setprojectpath(QString projectpath) {
    _projectpath = projectpath;
}

void    Project::setscore(Score* score) {
    _score = score;
}

void    Project::setisactive(bool active) {
    _isactive = active;
}

QString Project::getprojectname() {
    return _projectname;
}

QString Project::getprojectpath() {
    return _projectpath;
}

Score *Project::getscore() {
    return _score;
}

bool    Project::getisactive() {
    return _isactive;
}

void    Project::Save()
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
        out << "code gmn";
        file.close();
    }
    this->Serialized();
}

void    Project::checkSave()
{
    QMessageBox::StandardButton reply;

    reply = QMessageBox::question(0, "Ferme le projet", "Souhaitez-vous enregistrer le projet " + _projectname + " avant la fermeture ?", QMessageBox::Yes|QMessageBox::No);
    if (reply == QMessageBox::Yes) {
        this->Save();
    }
}

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

    QDomElement score = _score->writeXML(doc);
    score.setAttribute("type","Score");
    project.appendChild(score);

    return project;
}

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

QDomElement    Project::SerializedScorePng(QDomDocument doc)
{
    QString filename = _projectpath + _projectname + ".png";
    QGuidoPainter::startGuidoEngine();
    int pageIndex = 1;
    QGuidoPainter *ptr = QGuidoPainter::createGuidoPainter();
    ptr->setGMNCode(_score->getGmncode());
    QSizeF s = ptr->pageSizeMM( pageIndex );
    QImage image( s.toSize() * 10 , QImage::Format_ARGB32 );
    image.fill( QColor(Qt::white).rgb() );
    QPainter painter( &image );
    ptr->draw( &painter , pageIndex , image.rect() );
    image.save( filename );
    QGuidoPainter::stopGuidoEngine();

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

void    Project::Serialized()
{
    QDomDocument doc;
    QString filename = _projectpath + _projectname + ".msw";
    QDomNode xmlNode = doc.createProcessingInstruction("xml","version=\"1.0\" encoding=\"UTF-8\"");
    doc.insertBefore(xmlNode, doc.firstChild());

    QDomElement project = writeXML(doc);
    doc.appendChild(project);

    QDomElement pngdata = SerializedScorePng(doc);
    doc.appendChild(pngdata);

    QFile file( filename );
    if (!file.open(QIODevice::WriteOnly)) {
        QMessageBox::critical(0, "Sauvegarde échouée", "Impossible d'ouvrir le fichier " + filename );
        return;
    }
    QTextStream ts(&file);
    int indent = 2;
    doc.save(ts, indent);
}

void    Project::Deserialize(QString path)
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
