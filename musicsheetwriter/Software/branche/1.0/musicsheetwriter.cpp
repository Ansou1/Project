#include "musicsheetwriter.h"
#include "ui_musicsheetwriter.h"

#include <QDebug>

MusicSheetWriter::MusicSheetWriter(QWidget *parent) : QMainWindow(parent), ui(new Ui::MusicSheetWriter)
{
    ui->setupUi(this);
    _projectlist = QList<Project*>();
    _mdiarea = ui->mdiArea;
    _mdiarea->setHorizontalScrollBarPolicy(Qt::ScrollBarAsNeeded);
    _mdiarea->setVerticalScrollBarPolicy(Qt::ScrollBarAsNeeded);
    _filemenu = menuBar()->addMenu("Fichier");
    _editmenu = menuBar()->addMenu("Editer");
    this->createFileMenu();
    this->updateActionsFileMenu(false);
    connect(ui->treeWidget, SIGNAL(itemChanged(QTreeWidgetItem*,int)), this, SLOT(checkItemChangedTreeWidget(QTreeWidgetItem * , int)));
    _mswtreewidget = new MSWtreeWidget(ui->treeWidget);
    ui->toolBarEdit->hide();
    ui->toolBar->hide();
    connect(_editmenu, SIGNAL(aboutToShow()), this, SLOT(showEditToolBar()));
    _edittoolbar = new MSWeditToolBar(ui->toolBarEdit, ui->toolBar);
}

MusicSheetWriter::~MusicSheetWriter()
{
    delete ui;
}

void    MusicSheetWriter::setProjectList(QList<Project *> projectlist) {
    _projectlist = projectlist;
}

void    MusicSheetWriter::createFileMenu()
{
    this->createActionsFileMenu();
    _filemenu->addAction(_newpjt);
    _filemenu->addAction(_openpjt);
    _filemenu->addSeparator();
    _filemenu->addAction(_savepjt);
    _filemenu->addAction(_saveupjt);
    _filemenu->addAction(_saveall);
    _filemenu->addSeparator();
    _filemenu->addAction(_imppjt);
    _filemenu->addSeparator();
    _closeproject = new QMenu("Fermer le projet");
    _filemenu->addMenu(_closeproject);
    _filemenu->addAction(_closeprojects);
}

void    MusicSheetWriter::updateProjectActif(Project *pjt)
{
    foreach (Project *project, _projectlist)
    {
        if (project->getprojectpath() == pjt->getprojectpath()) {
            project->setisactive(true);
        } else {
            project->setisactive(false);
        }
    }
}

void    MusicSheetWriter::updateActionsFileMenu(bool upt)
{
    _savepjt->setEnabled(upt);
    _saveupjt->setEnabled(upt);
    _saveall->setEnabled(upt);
    _closeproject->setEnabled(upt);
    _closeprojects->setEnabled(upt);
  //  _editmenu->setEnabled(upt);
    if (upt == true) {
        ui->treeWidget->show();
    } else {
        ui->treeWidget->hide();
    }
}

void    MusicSheetWriter::newProject()
{
    ProjectFactory  *pct = new ProjectFactory();
    if (pct->getProject() != NULL) {
        _projectlist << pct->getProject();
        this->updateProjectActif(pct->getProject());
        _mswtreewidget->updateMSWtreeWidget(_projectlist);
        updateActionsFileMenu(true);
    }
}

void    MusicSheetWriter::openProject()
{
    QString filename = QFileDialog::getOpenFileName(this, tr("Ouvrir le projet"), "/home", "Config (*.msw)");
    if (filename != NULL) {
        Project *newpjt = new Project();
        newpjt->Deserialize(filename);
        if (!_projectlist.isEmpty()) {
            bool check = true;
            foreach (Project *pjt, _projectlist) {
                if (pjt->getprojectpath() == newpjt->getprojectpath()) {
                    QMessageBox::critical(this, "Erreur", "Le projet '" + newpjt->getprojectname() + "' est déjà ouvert");
                    check = false;
                }
            }
            if (check == true) {
                _projectlist << newpjt;
            }
        } else {
            _projectlist << newpjt;
        }
        this->updateProjectActif(newpjt);
        _mswtreewidget->updateMSWtreeWidget(_projectlist);
        this->updateActionsFileMenu(true);
    }
}

void    MusicSheetWriter::saveProject()
{
    foreach (Project *pjt, _projectlist) {
        if (pjt->getisactive() == true)
            pjt->Save();
    }
}

void    MusicSheetWriter::saveAsProject()
{
    foreach (Project *pjt, _projectlist) {
        QString dirname = QFileDialog::getExistingDirectory(this,
                                                            tr("Dossier de destination"),
                                                            "/home") + "/" + pjt->getprojectname() + "/";
        QString pathtmp = pjt->getprojectpath();
        pjt->setprojectpath(dirname);
        pjt->Save();
        pjt->setprojectpath(pathtmp);
    }
}

void    MusicSheetWriter::saveAll()
{
    foreach (Project *pjt, _projectlist) {
        pjt->Save();
    }
}

void    MusicSheetWriter::importProject()
{

}

void    MusicSheetWriter::closeProjects()
{
    foreach (Project *pjt, _projectlist) {
       pjt->checkSave();
       this->removeActionFermerProjectMenu(pjt->getprojectpath());
    }
    _projectlist.clear();
    if (_projectlist.isEmpty()) {
        this->updateActionsFileMenu(false);
    }
}

void    MusicSheetWriter::closeProject()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();
    QList<Project*>    tmp;

    foreach (Project *pjt, _projectlist)
    {
        if (pjt->getprojectpath() != projectpath) {
            tmp << pjt;
        } else {
            pjt->checkSave();
            this->removeActionFermerProjectMenu(pjt->getprojectpath());
        }
    }
    this->setProjectList(tmp);
    if (_projectlist.count() == 0) {
        this->updateActionsFileMenu(false);
    } else {
        this->updateProjectActif(tmp[0]);
    }
    _mswtreewidget->updateMSWtreeWidget(_projectlist);
}

void    MusicSheetWriter::updateFermerProjectMenu()
{
    foreach (Project *pjt, _projectlist)
    {
        this->addActionFermerProjectMenu(pjt->getprojectname(), pjt->getprojectpath());
    }
}


void    MusicSheetWriter::addActionFermerProjectMenu(QString projectname, QString projectpath)
{
    QAction *act = new QAction(projectname, this);
    act->setObjectName(projectpath);
    connect(act, SIGNAL(triggered()), this, SLOT(closeProject()));
    _closeproject->addAction(act);
    _actcloseproject << act;
}

void    MusicSheetWriter::removeActionFermerProjectMenu(QString projectpath)
{
    foreach (QAction *act, _actcloseproject) {
        if (act->objectName() == projectpath) {
            _closeproject->removeAction(act);
            _actcloseproject.removeOne(act);
        }
    }
}

void    MusicSheetWriter::createActionsFileMenu()
{
    _newpjt = new QAction(QIcon(":/images/FileMenu/new.png"), tr("&Nouveau projet"), this);
    _newpjt->setShortcuts(QKeySequence::New);
    connect(_newpjt, SIGNAL(triggered()), this, SLOT(newProject()));

    _openpjt = new QAction(QIcon(":/images/FileMenu/open.png"), tr("&Ouvrir projet"), this);
    _openpjt->setShortcuts(QKeySequence::Open);
    connect(_openpjt, SIGNAL(triggered()), this, SLOT(openProject()));

    _savepjt = new QAction(QIcon(":/images/FileMenu/save.png"), tr("&Enregistrer le projet"), this);
    _savepjt->setShortcuts(QKeySequence::Save);
    connect(_savepjt, SIGNAL(triggered()), this, SLOT(saveProject()));

    _saveupjt = new QAction(tr("&Enregistrer le projet sous"), this);
    _saveupjt->setShortcuts(QKeySequence::SaveAs);
    connect(_saveupjt, SIGNAL(triggered()), this, SLOT(saveAsProject()));

    _saveall = new QAction(tr("&Tout enregistrer"), this);
    connect(_saveall, SIGNAL(triggered()), this, SLOT(saveAll()));

    _imppjt = new QAction(tr("&Importer un projet"), this);
    connect(_imppjt, SIGNAL(triggered()), this, SLOT(importProject()));

    _closeprojects = new QAction(tr("&Fermer tous les projets"), this);
    connect(_closeprojects, SIGNAL(triggered()), this, SLOT(closeProjects()));
}

void    MusicSheetWriter::checkItemChangedTreeWidget(QTreeWidgetItem * , int)
{
    _projectlist = _mswtreewidget->getProjectList();
    _closeproject->clear();
    _actcloseproject.clear();
    this->updateFermerProjectMenu();
    if (_projectlist.count() == 0) {
        this->updateActionsFileMenu(false);
    }
}

void MusicSheetWriter::on_treeWidget_clicked(const QModelIndex &index)
{
    QString projectpath;

    if (ui->treeWidget->currentItem()->parent() != NULL) {
        projectpath = ui->treeWidget->currentItem()->parent()->whatsThis(1);
        foreach (Project *pjt, _projectlist) {
            if (pjt->getprojectpath() == projectpath) {
                scoreView *view = new scoreView(pjt->getprojectname(), pjt->getscore()->getPartition(), pjt->getscore()->getGmncode());
                _mdiarea->addSubWindow(view);
                view->show();
            }
        }
    }
}

void    MusicSheetWriter::showEditToolBar()
{
    ui->toolBarEdit->show();
}
