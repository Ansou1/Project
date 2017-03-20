#ifndef MSWEDITTOOLBAR_H
#define MSWEDITTOOLBAR_H

#include    <QToolBar>
#include    <QAction>

class MSWeditToolBar : public QObject
{
    Q_OBJECT

public:
    MSWeditToolBar(QToolBar *toolbar, QToolBar *toolbaredit);

private:
    QToolBar    *_toolbar;
    QToolBar    *_toolbaredit;
    QAction     *_toolbarnotes;
    QAction     *_toolbarnuances;
    QAction     *_toolbarmesures;
    QToolBar    *_barnotes;


    void    createToolBar();
    void    createActionsToolBar();
    void    generateToolBarNotes(QToolBar *toolbar);
    void    generateToolBarNuances(QToolBar *toolbar);
    void    generateToolBarMesures(QToolBar *toolbar);

private slots:
    void    createToolBarNotes();
    void    createToolBarNuances();
    void    createToolBarMesures();
};

#endif // MSWEDITTOOLBAR_H
