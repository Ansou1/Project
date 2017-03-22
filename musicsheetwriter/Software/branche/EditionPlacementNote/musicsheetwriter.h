#ifndef MUSICSHEETWRITER_H
#define MUSICSHEETWRITER_H

#include    <QMainWindow>
#include    <QList>
#include    "ProjectFactory.h"
#include    "MSWtreeWidget.h"
#include    "ScoreView.h"
#include    "MenuFile.h"
#include    "MenuEditor.h"
#include    "ScoreEditor.h"

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
    QList<Project*>         _projectlist;
    MSWtreeWidget           *_mswtreewidget;
    MenuFile                *_menufile;
    MenuEditor              *_menueditor;
    ScoreEditor             *_scoreeditor;

    void    setProjectList(QList<Project*> projectlist);
    void    previewNote(Note *note);
    void    connectSignalToSlot();

private slots:
    void    slotUpdateProjectlist(QList<Project *> projectlist);
    void    slotUpdateEditorScore(Project *pjt);
    void    slotCheckItemChangedTreeWidget(QTreeWidgetItem * , int);
    void    slotUpdateProject(Project *scr);
    void    slotDisplayScore(Score *scr);
};

#endif // MUSICSHEETWRITER_H
