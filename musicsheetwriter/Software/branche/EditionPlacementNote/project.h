#ifndef PROJECT_H
#define PROJECT_H

#include    <QList>
#include    <QDir>
#include    <QFile>
#include    <QMessageBox>
#include    <QTextStream>
#include    <QtXml/QDomDocument>
#include    <QBuffer>
#include    <QByteArray>
#include    <QGuidoPainter.h>
#include    "Score.h"

class Project : public QObject
{
    Q_OBJECT

public:
    Project(QString projectname = "", QString projectpath = "", Score *score = NULL);
    ~Project();

    void    setProjectname(QString projectname);
    void    setProjectpath(QString projectpath);
    void    setScore(Score* score);
    void    setIsactive(bool active);
    QString getProjectname();
    QString getProjectpath();
    Score*  getScore();
    bool    getIsactive();

    void        save();
    void        checkSave();
    void        serialized();
    QDomElement serializedScorePng(QDomDocument doc);
    void        deserialize(QString path);
    QDomElement writeXML(QDomDocument doc);
    void        readXML(QDomElement elem);

private:
    QString _projectname;
    QString _projectpath;
    Score*  _score;
    bool    _isactive;
};

#endif // PROJECT_H
