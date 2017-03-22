#ifndef MSWTREEWIDGET_H
#define MSWTREEWIDGET_H

#include    <QTreeWidget>
#include    <QTreeWidgetItem>
#include    <QAction>
#include    <QMenu>
#include    <QPoint>
#include    <QPrinter>
#include    <GUIDO2Midi.h>
#include    <QModelIndex>
#include    "ProjectFactory.h"
#include    "ScoreView.h"

class MSWtreeWidget : public QObject
{
    Q_OBJECT

public:
    MSWtreeWidget(QTreeWidget *treewidget);
    ~MSWtreeWidget(){}
    void                updateMSWtreeWidget(QList<Project *> projectlist);
    QList<Project *>    getProjectList();

private:
    QTreeWidget             *_treewidget;
    QTreeWidgetItem         *_itm;
    QList<Project *>        _projectlist;

    void        AddRoot();
    void        AddChild(QTreeWidgetItem *parent, QString filename, QString pathproject);
    QAction*    createActionDelScore(QTreeWidgetItem *itm);
    QAction*    createActionModScore(QTreeWidgetItem *itm);
    QAction*    createActionAddScore(QTreeWidgetItem *itm);
    QAction*    createActionActifProject(QTreeWidgetItem *itm);
    QAction*    createActionAddProject();
    QAction*    createActionModProject(QTreeWidgetItem *itm);
    QAction*    createActionExpMidi(QTreeWidgetItem *itm);
    QAction*    createActionExpPdf(QTreeWidgetItem *itm);
    QAction*    createActionExpPng(QTreeWidgetItem *itm);
    QString     createDirExport(Project *pjt, QString exp);

signals:
    void    signalUpdateProjectList(QList<Project *> projectlist);
    void    signalUpdateProjectEditorScore(Project *pjt);
    void    signalDisplayScore(Score *scr);

private slots:
    void    slotInitItemMenu(const QPoint & pos);
    void    slotDeleteScore();
    void    slotModScore();
    void    slotAddScore();
    void    slotModProject();
    void    slotAddProject();
    void    slotSetProjectActif();
    void    slotExportPDF();
    void    slotExportPNG();
    void    slotExportMIDI();
    void    slotTreewidgetClicked(const QModelIndex &index);
};

#endif // MSWTREEWIDGET_H
