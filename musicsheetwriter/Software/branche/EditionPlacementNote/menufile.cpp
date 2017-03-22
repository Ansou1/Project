#include "Menufile.h"
#include    <QDebug>

/*
 * Création du menu Fichier
*/
MenuFile::MenuFile(QMenu *menufile)
{
    _menufile = menufile;
    _projectlist = QList<Project*>();
    this->createFileMenu();
    this->updateActionsFileMenu(false);
}

/*
 * Création de chaque action du menu Fichier
 * Pour chaque action, lorsqu'on clic dessus, on execute la fonction qui lui est rattachée.
*/
void    MenuFile::createActionsFileMenu()
{
    _newpjt = new QAction(QIcon(":/images/FileMenu/new.png"), tr("&Nouveau projet"), _menufile);
    _newpjt->setShortcuts(QKeySequence::New);
    connect(_newpjt, SIGNAL(triggered()), this, SLOT(slotNewProject()));

    _openpjt = new QAction(QIcon(":/images/FileMenu/open.png"), tr("&Ouvrir projet"), _menufile);
    _openpjt->setShortcuts(QKeySequence::Open);
    connect(_openpjt, SIGNAL(triggered()), this, SLOT(slotOpenProject()));

    _savepjt = new QAction(QIcon(":/images/FileMenu/save.png"), tr("&Enregistrer le projet"), _menufile);
    _savepjt->setShortcuts(QKeySequence::Save);
    connect(_savepjt, SIGNAL(triggered()), this, SLOT(slotSaveProject()));

    _saveupjt = new QAction(tr("&Enregistrer le projet sous"), _menufile);
    _saveupjt->setShortcuts(QKeySequence::SaveAs);
    connect(_saveupjt, SIGNAL(triggered()), this, SLOT(slotSaveAsProject()));

    _saveall = new QAction(tr("&Tout enregistrer"), _menufile);
    connect(_saveall, SIGNAL(triggered()), this, SLOT(slotSaveAll()));

    _imppjt = new QAction(tr("&Importer un projet"), _menufile);
  //  connect(_imppjt, SIGNAL(triggered()), this, SLOT(importProject()));

    _closeprojects = new QAction(tr("&Fermer tous les projets"), _menufile);
    connect(_closeprojects, SIGNAL(triggered()), this, SLOT(slotCloseProjects()));
}

/*
 * Ajoute chaque action au menu fichier
 * Créer le menu Fermer le projet qui va contenir les noms des projets ouverts, ce menu est ajouter au menu Fichier
*/
void    MenuFile::createFileMenu()
{
    this->createActionsFileMenu();
    _menufile->addAction(_newpjt);
    _menufile->addAction(_openpjt);
    _menufile->addSeparator();
    _menufile->addAction(_savepjt);
    _menufile->addAction(_saveupjt);
    _menufile->addAction(_saveall);
    _menufile->addSeparator();
    _menufile->addAction(_imppjt);
    _menufile->addSeparator();
    _closeproject = new QMenu("Fermer le projet");
    _menufile->addMenu(_closeproject);
    _menufile->addAction(_closeprojects);
}

/*
 *  Fonction qui s'execute lorsqu'on clic sur Nouveau projet
 *  Ouvre la fenetre de création de projet
 *  Si la création est réussite, on ajoute à la liste des projets ouverts, on passe le projet en actif
 *  On emet un signal pour que les autres elements, notamment le qtreewidget, puisse recuperer la nouvelle liste de projet ouvert mis à jour
 *  On update les actions du menu Fichier
*/
void    MenuFile::slotNewProject() {
    ProjectFactory  *pjt = new ProjectFactory();
      if (pjt->getProject() != NULL) {
        _projectlist << pjt->getProject();
        this->updateProjectActif(pjt->getProject());
        emit signalUpdateProjectlist(_projectlist);
        this->updateActionsFileMenu(true);
    }
}

/*
 *  Fonction qui s'execute lorsqu'on clic sur Ouvrir projet
 *  Ouvre un QFileDialog pour permettre à l'utilisateur d'aller recuperer le .msw de son projet
 *  On créer ensuite un nouveau projet et on fait appel à la fonction deserialize pour recuperer toutes les infos du .msw
 *  Si on a bien recuperer le projet on verifie que celui ci n'est pas deja ouvert, si c'est pas le cas on l'ajoute a notre liste de projet ouvert et on passe le projet en actif
 *  On emet un signal pour que les autres elements, notamment le qtreewidget, puisse recuperer la nouvelle liste de projet ouvert mis à jour
 *  On update les actions du menu Fichier
*/
void    MenuFile::slotOpenProject() {
    QString filename = QFileDialog::getOpenFileName(0, tr("Ouvrir le projet"), "/home", "Config (*.msw)");
    if (filename != NULL) {
        Project *newpjt = new Project();
        newpjt->deserialize(filename);
        if (newpjt->getProjectname() != NULL) {
            if (!_projectlist.isEmpty()) {
                bool check = true;
                foreach (Project *pjt, _projectlist) {
                    if (pjt->getProjectpath() == newpjt->getProjectpath()) {
                        QMessageBox::critical(0, "Erreur", "Le projet '" + newpjt->getProjectname() + "' est déjà ouvert");
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
            emit signalUpdateProjectlist(_projectlist);
            this->updateActionsFileMenu(true);
        }
    }
}

/*
 *  Fonction qui s'execute lorsqu'on clic sur Enregistrer le projet
 *  On cherche dans notre liste de projet ouvert, lequel est actif et on l'enregistre
*/
void    MenuFile::slotSaveProject()
{
    foreach (Project *pjt, _projectlist) {
        if (pjt->getIsactive() == true)
            pjt->save();
    }
}

/*
 *  Fonction qui s'execute lorsqu'on clic sur Enregistrer le projet sous
 *  Meme principe que pour Enregistrer le projet, on ajoute juste l'etape ou on choisi la destination
*/
void    MenuFile::slotSaveAsProject() {
    foreach (Project *pjt, _projectlist) {
        if (pjt->getIsactive() == true) {
            QString dirname = QFileDialog::getExistingDirectory(0, tr("Dossier de destination"), "/home") + "/" + pjt->getProjectname() + "/";
            QString pathtmp = pjt->getProjectpath();
            pjt->setProjectpath(dirname);
            pjt->save();
            pjt->setProjectpath(pathtmp);
        }
    }
}

/*
 *  Fonction qui s'execute lorsqu'on clic sur Enregistrer tout
 *  Pour chaque projet dans notre liste de projet ouvert, on appel la fonction save.
*/
void    MenuFile::slotSaveAll() {
    foreach (Project *pjt, _projectlist) {
        pjt->save();
    }
}

/*
 *  Fonction qui s'execute lorsqu'on clic sur Fermer tous les projets
 *  Pour chaque projet dans notre liste de projet ouvert, on demande si on souhaite sauvegarder avant fermeture
 *  On vide la liste
 *  On met à jour les actions du menu fichier
 *  On envoi un signal avec une Score = Null pour effacer l'affichage de la partition ainsi que de l'editeur
 *  On emet un signal pour que les autres elements, notamment le qtreewidget, puisse recuperer la nouvelle liste de projet ouvert mis à jour
*/
void    MenuFile::slotCloseProjects()
{
    foreach (Project *pjt, _projectlist) {
       pjt->checkSave();
       this->removeActionFermerProjectMenu(pjt->getProjectpath());
    }
    _projectlist.clear();
    if (_projectlist.isEmpty()) {
        this->updateActionsFileMenu(false);
        emit signalDisplayScore(NULL);
    }
    emit signalUpdateProjectlist(_projectlist);
}

/*
 *  Fonction qui s'execute lorsqu'on clic sur le nom du projet dans le menu Fermer le projet
 *  On recupere l'info envoyer par l'action, ici le path du projet sur lequel on a appuié
 *  On boucle sur les projets ouverts et lorsqu'on tombe sur le bon on le sort de la liste
 *  A noter que la verification se fait par rapport au path et non le nom car on peut avoir 2 noms identiques à des paths differents mais pas 2 paths identiques ayant les memes noms de projets
 *  Si l'element supprimer est le dernier de la liste on update les actions du menu fichier et on envoi un signal avec une Score = Null pour effacer l'affichage de la partition ainsi que de l'editeur
 *  On emet un signal pour que les autres elements, notamment le qtreewidget, puisse recuperer la nouvelle liste de projet ouvert mis à jour
*/
void    MenuFile::slotCloseProject()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();
    QList<Project*>    tmp;

    foreach (Project *pjt, _projectlist)
    {
        if (pjt->getProjectpath() != projectpath) {
            tmp << pjt;
        } else {
            pjt->checkSave();
            this->removeActionFermerProjectMenu(pjt->getProjectpath());
        }
    }
    this->setProjectlist(tmp);
    if (_projectlist.count() == 0) {
        this->updateActionsFileMenu(false);
        emit signalDisplayScore(NULL);
    } else {
        this->updateProjectActif(tmp[0]);
    }
    emit signalUpdateProjectlist(_projectlist);
}

/*
 * Fonction qui ajoute les actions au menu Fermer le projet
*/
void    MenuFile::updateFermerProjectMenu()
{
    foreach (Project *pjt, _projectlist)
    {
        this->addActionFermerProjectMenu(pjt->getProjectname(), pjt->getProjectpath());
    }
}

/*
 *  Pour chaque action on lui attribut le nom de projet
 *  ObjectName prend pour valeur le path du projet, c'est ObjectName qui est recuperer dans slotCloseProject
 *  On connecte l'action au slot slotCloseProject
 *  On ajoute au menu Fermer le projet ainsi qu'a notre liste d'actions qui concerne les actions liés au menu Fermer le projet
*/
void    MenuFile::addActionFermerProjectMenu(QString projectname, QString projectpath)
{
    QAction *act = new QAction(projectname, this);
    act->setObjectName(projectpath);
    connect(act, SIGNAL(triggered()), this, SLOT(slotCloseProject()));
    _closeproject->addAction(act);
    _actcloseproject << act;
}

/*
 *  Lorsqu'on ferme un projet, il faut egalement supprimer l'action de fermeture
 *  Boucle sur les actions et on supprime celle qui correspond
 *  Comparaison faite sur le path (voir MenuFile::slotCloseProject() pour explication)
*/
void    MenuFile::removeActionFermerProjectMenu(QString projectpath)
{
    foreach (QAction *act, _actcloseproject) {
        if (act->objectName() == projectpath) {
            _closeproject->removeAction(act);
            _actcloseproject.removeOne(act);
        }
    }
}

/*
 *  Fonction qui passe le projet en parametre en actif et tous les autres en non actif
*/
void    MenuFile::updateProjectActif(Project *pjt)
{
    foreach (Project *project, _projectlist)
    {
        if (project->getProjectpath() == pjt->getProjectpath()) {
            project->setIsactive(true);
        } else {
            project->setIsactive(false);
        }
    }
}
/*
 *  Fonction qui met à jour le projectlist de la classe
 *  Cela permet de mettre a jour le menu Fermer le projet, notamment lorsqu'on modifie le nom d'un projet
*/
void    MenuFile::updateMenuFile(QList<Project *> projectlist) {
    _projectlist = projectlist;
    _closeproject->clear();
    _actcloseproject.clear();
    this->updateFermerProjectMenu();
    if (_projectlist.count() == 0) {
        this->updateActionsFileMenu(false);
        emit signalDisplayScore(NULL);
    }
}

/*
 *  Fonction qui autorise ou non l'utilisation de certaines actions qui necessite qu'une partition soit ouverte
*/
void    MenuFile::updateActionsFileMenu(bool upt)
{
    _savepjt->setEnabled(upt);
    _saveupjt->setEnabled(upt);
    _saveall->setEnabled(upt);
    _closeproject->setEnabled(upt);
    _closeprojects->setEnabled(upt);
}

/*
 * set et get de la classe
*/

void    MenuFile::setMenufile(QMenu *menufile) {
    _menufile = menufile;
}

void    MenuFile::setProjectlist(QList<Project *> projectlist) {
    _projectlist = projectlist;
}

QMenu*  MenuFile::getMenufile() {
    return _menufile;
}

QList<Project *>    MenuFile::getProjectlist() {
    return  _projectlist;
}
