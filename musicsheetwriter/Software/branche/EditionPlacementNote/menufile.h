#ifndef MENUFILE_H
#define MENUFILE_H

#include    <QObject>
#include    <QMenu>
#include    <QAction>
#include    <QList>
#include    "ProjectFactory.h"

class MenuFile : public QObject
{
    Q_OBJECT

public:
    MenuFile(QMenu *menufile);
    ~MenuFile(){}
    void                setMenufile(QMenu *menufile);
    void                setProjectlist(QList<Project *> projectlist);
    QMenu*              getMenufile();
    QList<Project *>    getProjectlist();
    void                updateMenuFile(QList<Project *> projectlist);

private:
    QMenu               *_menufile;
    QMenu               *_closeproject;
    QAction             *_newpjt;
    QAction             *_openpjt;
    QAction             *_savepjt;
    QAction             *_saveupjt;
    QAction             *_saveall;
    QAction             *_imppjt;
    QAction             *_closeprojects;
    QList<Project *>    _projectlist;
    QList<QAction*>     _actcloseproject;

    void    createActionsFileMenu();
    void    createFileMenu();
    void    updateProjectActif(Project *pjt);
    void    removeActionFermerProjectMenu(QString projectpath);
    void    addActionFermerProjectMenu(QString projectname, QString projectpath);
    void    updateFermerProjectMenu();
    void    updateActionsFileMenu(bool upt);

signals:
    void    signalUpdateProjectlist(QList<Project *> projectlist);
    void    signalDisplayScore(Score *scr);

private slots:
    void    slotNewProject();
    void    slotOpenProject();
    void    slotSaveProject();
    void    slotSaveAsProject();
    void    slotSaveAll();
    void    slotCloseProjects();
    void    slotCloseProject();

};

#endif // MENUFILE_H
