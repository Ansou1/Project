#include "MusicSheetWriter.h"
#include "ui_musicsheetwriter.h"

#include    <QDebug>

/*
 *  Création de tous les élements du logiciel
 *  connect les éléments entre eux avec les signaux et slots
*/
MusicSheetWriter::MusicSheetWriter(QWidget *parent) : QMainWindow(parent), ui(new Ui::MusicSheetWriter)
{
    ui->setupUi(this);
    _projectlist = QList<Project*>();
   _menufile = new MenuFile(menuBar()->addMenu("Fichier"));
   _menueditor = new MenuEditor(menuBar()->addMenu("Edition"));
    _mswtreewidget = new MSWtreeWidget(ui->treeWidget);
    _scoreeditor = new ScoreEditor(ui->editorview);
    ui->editorview->hide();
    this->connectSignalToSlot();
}

MusicSheetWriter::~MusicSheetWriter()
{
    delete ui;
}

/*
 *  connect les signaux envoyés par les différents elements du logiciels avec les slots correspondants
*/
void    MusicSheetWriter::connectSignalToSlot()
{
    connect(_menufile, SIGNAL(signalUpdateProjectlist(QList<Project*>)), this, SLOT(slotUpdateProjectlist(QList<Project*>)));
    connect(_menufile,SIGNAL(signalDisplayScore(Score*)), this, SLOT(slotDisplayScore(Score*)));
    connect(ui->treeWidget, SIGNAL(itemChanged(QTreeWidgetItem*,int)), this, SLOT(slotCheckItemChangedTreeWidget(QTreeWidgetItem * , int)));
    connect(_mswtreewidget, SIGNAL(signalUpdateProjectList(QList<Project*>)), this, SLOT(slotUpdateProjectlist(QList<Project*>)));
    connect(_mswtreewidget, SIGNAL(signalUpdateProjectEditorScore(Project*)), this, SLOT(slotUpdateEditorScore(Project*)));
    connect(_mswtreewidget, SIGNAL(signalDisplayScore(Score*)), this, SLOT(slotDisplayScore(Score*)));
    connect(_scoreeditor, SIGNAL(signalUpdateProject(Project*)), this, SLOT(slotUpdateProject(Project*)));
    connect(_scoreeditor, SIGNAL(signalDisplayScore(Score*)), this, SLOT(slotDisplayScore(Score*)));
}

/*
 * Lorsqu'un element change dans le QTreeWidget(le projectlist est modifié), on met à jour le projectliste de musicsheetwriter et de menufile
*/
void    MusicSheetWriter::slotCheckItemChangedTreeWidget(QTreeWidgetItem * , int)
{
    _projectlist = _mswtreewidget->getProjectList();
    _menufile->updateMenuFile(_mswtreewidget->getProjectList());
}

/*
 *  Meme principe qu'au dessus mais cette fois les  changements proviennent du menu Fichier
*/
void    MusicSheetWriter::slotUpdateProjectlist(QList<Project *> projectlist) {
    _projectlist = projectlist;
    _mswtreewidget->updateMSWtreeWidget(_projectlist);
}

/*
 *  Met à jour le projet sur lequel on travail dans l'editeur (est déclencher lorsqu'on clic sur une partition dans le QTreeWidget)
*/
void    MusicSheetWriter::slotUpdateEditorScore(Project *pjt) {
    _scoreeditor->setCurrentProject(pjt);
}

/*
 *  Met a jour la projectlist de msw et celle du QTreeWidget avec le project qui est en train d'etre modifié dans l'editeur
*/
void    MusicSheetWriter::slotUpdateProject(Project *project)
{
    foreach (Project *pjt, _projectlist) {
        if (pjt->getProjectpath() == project->getProjectpath()) {
            pjt = project;
        }
    }
    _mswtreewidget->updateMSWtreeWidget(_projectlist);
}

/*
 *  Fonction qui s'execute lorsqu'il faut modifier l'affichage de la partition et de l'editeur (est déclencher lorsqu'on clic sur une partition dans le QTreeWidget ou lorsqu'on ajoute une note depuis l'editeur)
 *  Si on a bien une partition a montrer on l'affiche avec l'editeur
*/
void    MusicSheetWriter::slotDisplayScore(Score *scr)
{
    ui->scoreview->hide();
    ui->editorview->hide();
    if (scr != NULL) {
        ui->scoreview->show();
        ui->editorview->show();
        QGuidoWidget *w = new QGuidoWidget(ui->scoreview);
        scoreView *view = new scoreView(scr, w);
        view->showscore();
    }
}

/*
 *  Fonctions set et get
*/
void    MusicSheetWriter::setProjectList(QList<Project *> projectlist) {
    _projectlist = projectlist;
}
