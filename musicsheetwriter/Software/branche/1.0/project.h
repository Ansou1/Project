#ifndef PROJECT_H
#define PROJECT_H

#include    "score.h"
#include    <QList>
#include    <QDir>
#include    <QFile>
#include    <QMessageBox>
#include    <QTextStream>
#include    <QtXml/QDomDocument>
#include    <QBuffer>
#include    <QByteArray>

class Project : public QObject
{
    Q_OBJECT

public:
    Project(QString projectname, QString projectpath, Score *score);
    Project();
    ~Project();

    void            setprojectname(QString projectname);
    void            setprojectpath(QString projectpath);
    void            setscore(Score* score);
    void            setisactive(bool active);
    QString         getprojectname();
    QString         getprojectpath();
    Score*          getscore();
    bool            getisactive();

    void        Save();
    void        checkSave();
    void        Serialized();
    QDomElement SerializedScorePng(QDomDocument doc);
    void        Deserialize(QString path);
    QDomElement writeXML(QDomDocument doc);
    void        readXML(QDomElement elem);

private:
    QString _projectname;
    QString _projectpath;
    Score*  _score;
    bool    _isactive;
};

#endif // PROJECT_H
