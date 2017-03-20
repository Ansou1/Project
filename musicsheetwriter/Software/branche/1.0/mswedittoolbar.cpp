#include "mswedittoolbar.h"

MSWeditToolBar::MSWeditToolBar(QToolBar *toolbar, QToolBar *toolbaredit)
{
    _toolbar = toolbar;
    _toolbaredit = toolbaredit;
    this->createToolBar();
}

void    MSWeditToolBar::createToolBar()
{
    this->createActionsToolBar();
    _toolbar->addAction(_toolbarnotes);
    _toolbar->addAction(_toolbarnuances);
    _toolbar->addAction(_toolbarmesures);
}

void    MSWeditToolBar::createActionsToolBar()
{
    _toolbarnotes = new QAction("Notes", this);
    connect(_toolbarnotes, SIGNAL(triggered()), this, SLOT(createToolBarNotes()));
    _toolbarnuances = new QAction("Nuances", this);
    connect(_toolbarnuances, SIGNAL(triggered()), this, SLOT(createToolBarNuances()));
    _toolbarmesures = new QAction("Mesures", this);
    connect(_toolbarmesures, SIGNAL(triggered()), this, SLOT(createToolBarMesures()));
}

void    MSWeditToolBar::createToolBarNotes()
{
    _toolbaredit->clear();
    this->generateToolBarNotes(_toolbaredit);
    _toolbaredit->show();
}

void    MSWeditToolBar::createToolBarNuances()
{
    _toolbaredit->clear();
    this->generateToolBarNuances(_toolbaredit);
    _toolbaredit->show();
}

void    MSWeditToolBar::createToolBarMesures()
{
    _toolbaredit->clear();
    this->generateToolBarMesures(_toolbaredit);
    _toolbaredit->show();
}

void   MSWeditToolBar::generateToolBarNotes(QToolBar *toolbar)
{
  /*  foreach (QAction *act, toolbar->actions()) {
        toolbar->removeAction(act);
    }*/
    QAction *act = new QAction("blanche", this);
    toolbar->addAction(act);
    act = new QAction("noire", this);
    toolbar->addAction(act);
    act = new QAction("croche", this);
    toolbar->addAction(act);
    act = new QAction("double croche", this);
    toolbar->addAction(act);
    act = new QAction("dièse", this);
    toolbar->addAction(act);
    act = new QAction("bémole", this);
    toolbar->addAction(act);
}

void   MSWeditToolBar::generateToolBarNuances(QToolBar *toolbar)
{
    QAction *act = new QAction("pppp", this);
    toolbar->addAction(act);
    act = new QAction("pp", this);
    toolbar->addAction(act);
    act = new QAction("p", this);
    toolbar->addAction(act);
    act = new QAction("mp", this);
    toolbar->addAction(act);
    act = new QAction("mf", this);
    toolbar->addAction(act);
    act = new QAction("mf", this);
    toolbar->addAction(act);
}

void   MSWeditToolBar::generateToolBarMesures(QToolBar *toolbar)
{
    QAction *act = new QAction("ajouter mesure", this);
    toolbar->addAction(act);
    act = new QAction("supprimer mesure", this);
    toolbar->addAction(act);
 }
