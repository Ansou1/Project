#include "MSWtreeWidget.h"

#include <QDebug>

/*
 *  Création du QTreeWidget
 *  On indique qu'on souhaite pouvoir avoir un menu contextuel(1er connect) on connecte à la fonction de création du menu contextuel
 *  On connect l'action du click sur un element du QTreeWidget avec le slot slotTreewidgetClicked
 *  On chaque le QTreeWidget si la liste des projets ouverts est vide
*/
MSWtreeWidget::MSWtreeWidget(QTreeWidget *treewidget)
{
    _treewidget = treewidget;
    _treewidget->setContextMenuPolicy(Qt::CustomContextMenu);
    _projectlist = QList<Project *>();
    connect(_treewidget,SIGNAL(customContextMenuRequested(const QPoint&)),SLOT(slotInitItemMenu(const QPoint&)));
    connect(_treewidget, SIGNAL(clicked(QModelIndex)), SLOT(slotTreewidgetClicked(QModelIndex)));
    if (_projectlist.isEmpty()) {
        _treewidget->hide();
    }
}

/*
 *  Action Supprimer partition -> executer depuis le clic droit sur le nom de la partiton dans le QTreeWidget
 *  Prend en parametre l'item sur lequel on a fait clic droit
 *  On attribut a ObjectName le path du projet auquel correpond la partition
 *  On connecte l'action à sa fonction
*/
QAction*    MSWtreeWidget::createActionDelScore(QTreeWidgetItem *itm)
{
    QAction *delscore = new QAction("Supprimer la partition", this);
    delscore->setObjectName(itm->whatsThis(1));
    connect(delscore, SIGNAL(triggered()), this, SLOT(slotDeleteScore()));
    return delscore;
}

/*
 * Action Modifier partition -> executer depuis le clic droit sur le nom de la partition dans le QTreeWidget
 * Meme principe qu'au dessus
*/
QAction*    MSWtreeWidget::createActionModScore(QTreeWidgetItem *itm)
{
    QAction *modscore = new QAction("Modifier la partition", this);
    modscore->setObjectName(itm->whatsThis(1));
    connect(modscore, SIGNAL(triggered()), this, SLOT(slotModScore()));
    return modscore;
}

/*
 * Action Ajouter partition -> executer depuis le clic droit sur le nom du projet dans le QTreeWidget
 * Meme principe qu'au dessus
*/
QAction*    MSWtreeWidget::createActionAddScore(QTreeWidgetItem *itm)
{
    QAction *addscore = new QAction("Ajouter une partition", this);
    addscore->setObjectName(itm->whatsThis(1));
    connect(addscore, SIGNAL(triggered()), this, SLOT(slotAddScore()));
    return addscore;
}


/*
 * Action Pour passer le projet en actif -> executer depuis le clic droit sur le nom du projet dans le QTreeWidget
 * Meme principe qu'au dessus
*/
QAction*    MSWtreeWidget::createActionActifProject(QTreeWidgetItem *itm)
{
    QAction *actifproject = new QAction("Définir " + itm->text(0) + " comme projet actif", this);
    actifproject->setObjectName(itm->whatsThis(1));
    connect(actifproject, SIGNAL(triggered()), this, SLOT(slotSetProjectActif()));
    return actifproject;
}


/*
 * Action Ajouter un projet -> executer depuis le clic droit dans le QTreeWidget
 * Meme principe qu'au dessus
*/
QAction*    MSWtreeWidget::createActionAddProject()
{
    QAction *addproject = new QAction("Nouveau Projet", this);
    connect(addproject, SIGNAL(triggered()), this, SLOT(slotAddProject()));
    return addproject;
}


/*
 * Action Modifier un projet -> executer depuis le clic droit sur le nom du projet dans le QTreeWidget
 * Meme principe qu'au dessus
*/
QAction*    MSWtreeWidget::createActionModProject(QTreeWidgetItem *itm)
{
    QAction *modproject = new QAction("Modifier le projet", this);
    modproject->setObjectName(itm->whatsThis(1));
    connect(modproject, SIGNAL(triggered()), this, SLOT(slotModProject()));
    return modproject;
}

/*
 * Action Exporter en midi -> executer depuis le clic droit sur le nom du projet dans le QTreeWidget
 * Meme principe qu'au dessus
*/
QAction*    MSWtreeWidget::createActionExpMidi(QTreeWidgetItem *itm)
{
    QAction *expmidi = new QAction("MIDI", this);
    expmidi->setObjectName(itm->whatsThis(1));
    connect(expmidi, SIGNAL(triggered()), this, SLOT(slotExportMIDI()));
    return expmidi;
}

/*
 * Action Exporter en Pdf -> executer depuis le clic droit sur le nom du projet dans le QTreeWidget
 * Meme principe qu'au dessus
*/
QAction*    MSWtreeWidget::createActionExpPdf(QTreeWidgetItem *itm)
{
    QAction *exppdf = new QAction("PDF", this);
    exppdf->setObjectName(itm->whatsThis(1));
    connect(exppdf, SIGNAL(triggered()), this, SLOT(slotExportPDF()));
    return exppdf;
}

/*
 * Action Exporter en png -> executer depuis le clic droit sur le nom du projet dans le QTreeWidget
 * Meme principe qu'au dessus
*/
QAction*    MSWtreeWidget::createActionExpPng(QTreeWidgetItem *itm)
{
    QAction *exppng = new QAction("PNG", this);
    exppng->setObjectName(itm->whatsThis(1));
    connect(exppng, SIGNAL(triggered()), this, SLOT(slotExportPNG()));
    return exppng;
}

/*
 *  Pour chaque element du QTreeWidget, on créé son menu contextuel qui correspond
 *  On verifie qu'on clic bien sur un element du Qtreewidget
 *  Si c'est le cas on verifie si on clic sur un parent(nom du projet) ou un enfant(la partition) et on affiche en fonction le menu contextuel qui le correspond
 *  Si on clic en dehor d'un element, on affiche un menu contextuel different des 2 précédents
*/
void    MSWtreeWidget::slotInitItemMenu(const QPoint &pos)
{
    QTreeWidgetItem *itm = _treewidget->itemAt( pos );

    if (itm != NULL) {
        if (itm->parent() != NULL) {
            QMenu menu(_treewidget);
            menu.addAction(this->createActionModScore(itm));
            menu.addAction(this->createActionDelScore(itm));
            menu.exec( _treewidget->mapToGlobal(pos) );
        }else {
            QMenu menu(_treewidget);
            menu.addAction(this->createActionActifProject(itm));
            menu.addAction(this->createActionAddScore(itm));
            menu.addAction(this->createActionModProject(itm));
            QMenu *menuexp = menu.addMenu("Exporter");
            menuexp->addAction(this->createActionExpPng(itm));
            menuexp->addAction(this->createActionExpPdf(itm));
            menuexp->addAction(this->createActionExpMidi(itm));
            menu.exec( _treewidget->mapToGlobal(pos) );
       }
    } else {
        QMenu menu(_treewidget);
        menu.addAction(this->createActionAddProject());
        menu.exec( _treewidget->mapToGlobal(pos) );
    }
}

/*
 *  Fonction qui s'execute lorsqu'on clic sur un element du QTreeWidget
 *  On verifie si on a cliqué sur une partition (seule les partitions ont un parent qui correspond au projet)
 *  On cherche le projet auquel est rattaché la partition sur laquelle on a cliqué (la comparaison de se fait par rapport au path (voir MenuFile::slotCloseProject() pour explication)
 *  On envoi à l'aide des signaux à l'editeur et a l'affichage de partition, le projet qui correspond.
*/
void MSWtreeWidget::slotTreewidgetClicked(const QModelIndex &index)
{
    QString projectpath;

    (void) index;
    if (_treewidget->currentItem()->parent() != NULL) {
        projectpath = _treewidget->currentItem()->parent()->whatsThis(1);
        foreach (Project *pjt, _projectlist) {
            if (pjt->getProjectpath() == projectpath) {
                emit signalUpdateProjectEditorScore(pjt);
                emit signalDisplayScore(pjt->getScore());
            }
        }
    }
}

/*
 *  Met à jour la liste des projets ouverts dans le QTreeWidget
*/
void    MSWtreeWidget::updateMSWtreeWidget(QList<Project *> projectlist)
{
    _projectlist = projectlist;
    if (_projectlist.count() == 0) {
        _treewidget->hide();
    } else {
        this->AddRoot();
        _treewidget->show();
    }
}

/*
 *  Fonction qui créer un parent et l'enfant qui lui correspond dans le QTreeWidget
 *  On vide le QTreeWidget puis on boucle sur les projets ouverts
 *  Pour chaque, on créer un itm dans text on y met le nom et dans whatsthis le path du projet (utile pour toutes les fonctions de création d'action plus haut)
 *  Si le projet est actif on affiche son nom en gras
 *  Si le projet a une partition au ajoute un enfant au parent
*/
void    MSWtreeWidget::AddRoot()
{
    _treewidget->clear();
    foreach (Project *pjt, _projectlist)
    {
        _itm = new QTreeWidgetItem(_treewidget);
        _itm->setText(0, pjt->getProjectname());
        _itm->setWhatsThis(1, pjt->getProjectpath());
        if (pjt->getIsactive() == true) {
            QFont font;
            font.setBold(true);
            _itm->setFont(0, font);
        }
        _treewidget->addTopLevelItem(_itm);
        if (pjt->getScore() != NULL) {
            AddChild(_itm, pjt->getScore()->getPartition(), pjt->getProjectpath());
        }
    }
}

/*
 *  Meme principe que pour le parent, sauf qu'ici on ajoute l'item au parent à la place du QTreeWidget
*/
void    MSWtreeWidget::AddChild(QTreeWidgetItem *parent, QString filename, QString pathproject)
{
    QTreeWidgetItem *itm = new QTreeWidgetItem();
    itm->setText(0,filename);
    itm->setWhatsThis(1, pathproject);
    parent->addChild(itm);
}

/*
 *  Fonction pour supprimer une partition
 *  On recupere la path du projet auquel est rattaché la partition
 *  On cherche le projet dans la liste des projets ouverts
 *  On supprime sa partition
 *  On envoi un signal pour mettre a jour le projetlist des autres elements du logiciel ainsi que pour mettre à jour l'affichage
*/
void    MSWtreeWidget::slotDeleteScore()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist) {
        if (pjt->getProjectpath() == projectpath) {
            QFile::remove(obj->objectName() + pjt->getScore()->getPartition() + ".gmn");
            pjt->setScore(NULL);
            emit signalUpdateProjectList(_projectlist);
            emit signalDisplayScore(pjt->getScore());
            pjt->save();
        }
    }
}

/*
 * Meme principe qu'au dessus sauf qu'on modifie la partition en faisant appel au formulaire de création de partition avec les infos déja prérempli .
*/
void    MSWtreeWidget::slotModScore()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist) {
        if (pjt->getProjectpath() == projectpath) {
            QFile::remove(obj->objectName() + pjt->getScore()->getPartition() + ".gmn");
            ScoreFactory *modscore = new ScoreFactory(0, pjt->getProjectpath(), pjt->getScore());
            pjt->setScore(modscore->getScore());
            emit signalUpdateProjectList(_projectlist);
            emit signalDisplayScore(pjt->getScore());
            pjt->save();
        }
    }
}

/*
 *  Ajoute une partition, ne fonction que si le projet n'a pas de partition
*/
void    MSWtreeWidget::slotAddScore()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist)
    {
        if (pjt->getProjectpath() == projectpath) {
            if (pjt->getScore() == NULL) {
                ScoreFactory *score = new ScoreFactory(0,pjt->getProjectpath());
                pjt->setScore(score->getScore());
                pjt->save();
                emit signalUpdateProjectList(_projectlist);
            }
       }
    }
}

/*
 * Ajoute un projet et envoi un signal pour mettre a jour le projectlist des autres elements
*/
void    MSWtreeWidget::slotAddProject()
{
    ProjectFactory  *pct = new ProjectFactory();
    if (pct->getProject() != NULL) {
        _projectlist << pct->getProject();
        emit signalUpdateProjectList(_projectlist);
    }
}

/*
 *  Modifie un projet en faisant appel au formulaire de création de projet pré rempli avec les infos du projet à modifier
*/
void    MSWtreeWidget::slotModProject()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist)
    {
        if (pjt->getProjectpath() == projectpath) {
            ProjectFactory  *pjtf = new ProjectFactory(0, pjt);
            pjt = pjtf->getProject();
            emit signalUpdateProjectList(_projectlist);
        }
    }
}

/*
 *  Passe le projet en actif
*/
void    MSWtreeWidget::slotSetProjectActif()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist) {
        if (pjt->getProjectpath() == projectpath) {
            pjt->setIsactive(true);
        } else {
            pjt->setIsactive(false);
        }
        emit signalUpdateProjectList(_projectlist);
    }
}

/*
 *  Fonction qui créer le dossier de destination de l'export
 *  le nom est sour la form : nomprojet_typedelexport
*/

QString    MSWtreeWidget::createDirExport(Project *pjt, QString exp)
{
    QString dirname = QFileDialog::getExistingDirectory(0,
                                                        tr("Dossier de destination"),
                                                        "/home") + "/" + pjt->getProjectname() + exp;
   if (dirname != NULL && QDir(dirname).exists()) {
       QMessageBox::StandardButton reply;
       reply = QMessageBox::question(0,
                                     "Dossier de destination",
                                     "Un dossier portant le même nom existe à l'emplacement indiqué. Souhaitez-vous le remplacer ?",
                                     QMessageBox::Yes|QMessageBox::No);
       if (reply == QMessageBox::No) {
        return (dirname = "");
       }
   } else {
       QDir().mkdir(dirname);
   }
   return (dirname);
}

/*
 *  Export PDF qui fait appel aux fonctions de la lib guido
*/
void    MSWtreeWidget::slotExportPDF()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist) {
        if (pjt->getProjectpath() == projectpath) {
            QString dirname;
            if ((dirname = createDirExport(pjt, "_PDF/")) != "") {
                int pageIndex = 1;
                QGuidoPainter *ptr = QGuidoPainter::createGuidoPainter();
                ptr->setGMNCode(pjt->getScore()->getGmncode());
                QSizeF s =  ptr->pageSizeMM( pageIndex );
                QImage image( s.toSize() * 10, QImage::Format_ARGB32 );
                image.fill( QColor(Qt::white).rgb() );
                QPrinter printer(QPrinter::ScreenResolution);
                printer.setOutputFormat(QPrinter::PdfFormat);
                printer.setColorMode(QPrinter::Color);
                printer.setOutputFileName(dirname + pjt->getScore()->getPartition() + ".pdf");
                printer.setPageSize(QPrinter::A4);
                printer.setOrientation(QPrinter::Portrait);
                QPainter p(&printer);
                ptr->draw( &p , pageIndex , image.rect() );
            }
        }
    }
}

/*
 *  Export PNG qui fait appel aux fonctions de la lib guido
*/
void    MSWtreeWidget::slotExportPNG()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist) {
        if (pjt->getProjectpath() == projectpath) {
            QString dirname;
            if ((dirname = createDirExport(pjt, "_PNG/")) != "") {
                int pageIndex = 1;
                QGuidoPainter *ptr = QGuidoPainter::createGuidoPainter();
                ptr->setGMNCode(pjt->getScore()->getGmncode());
                QSizeF s = ptr->pageSizeMM( pageIndex );
                QImage image( s.toSize() * 10 , QImage::Format_ARGB32 );
                image.fill( QColor(Qt::white).rgb() );
                QPainter painter( &image );
                ptr->draw( &painter , pageIndex , image.rect() );
                image.save( dirname + pjt->getScore()->getPartition() + ".png" );
            }
        }
    }
}

/*
 *  Export Midi qui fait appel aux fonctions de la lib guido
*/
void    MSWtreeWidget::slotExportMIDI()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist) {
        if (pjt->getProjectpath() == projectpath) {
            QString dirname;
            if ((dirname = createDirExport(pjt, "_MIDI/")) != "") {
                QString outfile = dirname + pjt->getScore()->getPartition() + ".mid";
                QGuidoPainter *ptr = QGuidoPainter::createGuidoPainter();
                ptr->setGMNCode(pjt->getScore()->getGmncode());
                CARHandler ar = ptr->getARHandler();
                GuidoAR2MIDIFile((const ARHandler)ar, outfile.toStdString().c_str(), 0);
            }
        }
    }
}

/*
 * fonctions set et get
*/

QList<Project *>    MSWtreeWidget::getProjectList() {
    return _projectlist;
}
