#ifndef MUSICSHEETWRITER_H
#define MUSICSHEETWRITER_H

#include    <QMainWindow>
#include    "projectfactory.h"
#include    "mswtreewidget.h"
#include    "mswedittoolbar.h"
#include    <QList>
#include    <QMdiArea>

namespace Ui {
class MusicSheetWriter;
}

class MusicSheetWriter : public QMainWindow
{
    Q_OBJECT

public:
    explicit MusicSheetWriter(QWidget *parent = 0);
    ~MusicSheetWriter();

private:
    Ui::MusicSheetWriter    *ui;
    QMdiArea                *_mdiarea;
    QList<Project*>         _projectlist;
    MSWtreeWidget           *_mswtreewidget;
    QMenu                   *_filemenu;
    QAction                 *_newpjt;
    QAction                 *_openpjt;
    QAction                 *_savepjt;
    QAction                 *_saveupjt;
    QAction                 *_saveall;
    QAction                 *_imppjt;
    QAction                 *_closeprojects;
    QMenu                   *_closeproject;
    QList<QAction*>         _actcloseproject;
    QMenu                   *_editmenu;
    MSWeditToolBar          *_edittoolbar;

    void    setProjectList(QList<Project*> projectlist);

    void    createFileMenu();
    void    updateActionsFileMenu(bool upt);
    void    createActionsFileMenu();
    void    updateProjectActif(Project *pjt);
    void    updateFermerProjectMenu();
    void    addActionFermerProjectMenu(QString projectname, QString projectpath);
    void    removeActionFermerProjectMenu(QString projectpath);

private slots:
    void    newProject();
    void    openProject();
    void    saveProject();
    void    saveAsProject();
    void    importProject();
    void    closeProjects();
    void    closeProject();
    void    saveAll();
    void    checkItemChangedTreeWidget(QTreeWidgetItem * , int);
    void    on_treeWidget_clicked(const QModelIndex &index);
    void    showEditToolBar();
};

#endif // MUSICSHEETWRITER_H
