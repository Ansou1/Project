#ifndef MSWTREEWIDGET_H
#define MSWTREEWIDGET_H

#include    "project.h"
#include    "projectfactory.h"
#include    <QTreeWidget>
#include    <QTreeWidgetItem>
#include    <QAction>
#include    <QMenu>
#include    <QPoint>
#include    <QPrinter>
#include    <GUIDO2Midi.h>

class MSWtreeWidget : public QObject
{
    Q_OBJECT

public:
    MSWtreeWidget(QTreeWidget *treewidget);
    ~MSWtreeWidget();

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

private slots:
    void    initItemMenu(const QPoint & pos);
    void    deleteScore();
    void    modScore();
    void    addScore();
    void    modProject();
    void    addProject();
    void    setProjectActif();
    void    exportPDF();
    void    exportPNG();
    void    exportMIDI();
};

#endif // MSWTREEWIDGET_H
