#include "mswtreewidget.h"

#include <QDebug>

MSWtreeWidget::MSWtreeWidget(QTreeWidget *treewidget)
{
    _treewidget = treewidget;
    _treewidget->setContextMenuPolicy(Qt::CustomContextMenu);
    _projectlist = QList<Project *>();
    connect(_treewidget,SIGNAL(customContextMenuRequested(const QPoint&)),SLOT(initItemMenu(const QPoint&)));
}

MSWtreeWidget::~MSWtreeWidget()
{

}

QList<Project *>    MSWtreeWidget::getProjectList() {
    return _projectlist;
}

QAction*    MSWtreeWidget::createActionDelScore(QTreeWidgetItem *itm)
{
    QAction *delscore = new QAction("Supprimer la partition", this);
    delscore->setObjectName(itm->whatsThis(1));
    connect(delscore, SIGNAL(triggered()), this, SLOT(deleteScore()));
    return delscore;
}

QAction*    MSWtreeWidget::createActionModScore(QTreeWidgetItem *itm)
{
    QAction *modscore = new QAction("Modifier la partition", this);
    modscore->setObjectName(itm->whatsThis(1));
    connect(modscore, SIGNAL(triggered()), this, SLOT(modScore()));
    return modscore;
}

QAction*    MSWtreeWidget::createActionAddScore(QTreeWidgetItem *itm)
{
    QAction *addscore = new QAction("Ajouter une partition", this);
    addscore->setObjectName(itm->whatsThis(1));
    connect(addscore, SIGNAL(triggered()), this, SLOT(addScore()));
    return addscore;
}

QAction*    MSWtreeWidget::createActionActifProject(QTreeWidgetItem *itm)
{
    QAction *actifproject = new QAction("Définir " + itm->text(0) + " comme projet actif", this);
    actifproject->setObjectName(itm->whatsThis(1));
    connect(actifproject, SIGNAL(triggered()), this, SLOT(setProjectActif()));
    return actifproject;
}

QAction*    MSWtreeWidget::createActionAddProject()
{
    QAction *addproject = new QAction("Nouveau Projet", this);
    connect(addproject, SIGNAL(triggered()), this, SLOT(addProject()));
    return addproject;
}

QAction*    MSWtreeWidget::createActionModProject(QTreeWidgetItem *itm)
{
    QAction *modproject = new QAction("Modifier le projet", this);
    modproject->setObjectName(itm->whatsThis(1));
    connect(modproject, SIGNAL(triggered()), this, SLOT(modProject()));
    return modproject;
}

QAction*    MSWtreeWidget::createActionExpMidi(QTreeWidgetItem *itm)
{
    QAction *expmidi = new QAction("MIDI", this);
    expmidi->setObjectName(itm->whatsThis(1));
    connect(expmidi, SIGNAL(triggered()), this, SLOT(exportMIDI()));
    return expmidi;
}

QAction*    MSWtreeWidget::createActionExpPdf(QTreeWidgetItem *itm)
{
    QAction *exppdf = new QAction("PDF", this);
    exppdf->setObjectName(itm->whatsThis(1));
    connect(exppdf, SIGNAL(triggered()), this, SLOT(exportPDF()));
    return exppdf;
}

QAction*    MSWtreeWidget::createActionExpPng(QTreeWidgetItem *itm)
{
    QAction *exppng = new QAction("PNG", this);
    exppng->setObjectName(itm->whatsThis(1));
    connect(exppng, SIGNAL(triggered()), this, SLOT(exportPNG()));
    return exppng;
}

void    MSWtreeWidget::initItemMenu(const QPoint &pos)
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

void    MSWtreeWidget::AddRoot()
{
    _treewidget->clear();
    foreach (Project *pjt, _projectlist)
    {
        _itm = new QTreeWidgetItem(_treewidget);
        _itm->setText(0, pjt->getprojectname());
        _itm->setWhatsThis(1, pjt->getprojectpath());
        if (pjt->getisactive() == true) {
            QFont font;
            font.setBold(true);
            _itm->setFont(0, font);
        }
        _treewidget->addTopLevelItem(_itm);
        if (pjt->getscore() != NULL) {
            AddChild(_itm, pjt->getscore()->getPartition(), pjt->getprojectpath());
        }
    }
}

void    MSWtreeWidget::AddChild(QTreeWidgetItem *parent, QString filename, QString pathproject)
{
    QTreeWidgetItem *itm = new QTreeWidgetItem();
    itm->setText(0,filename);
    itm->setWhatsThis(1, pathproject);
    parent->addChild(itm);
}

void    MSWtreeWidget::deleteScore()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist) {
        if (pjt->getprojectpath() == projectpath) {
            QFile::remove(obj->objectName() + pjt->getscore()->getPartition() + ".gmn");
            pjt->setscore(NULL);
            this->updateMSWtreeWidget(_projectlist);
            pjt->Save();
        }
    }
}

void    MSWtreeWidget::modScore()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist) {
        if (pjt->getprojectpath() == projectpath) {
            QFile::remove(obj->objectName() + pjt->getscore()->getPartition() + ".gmn");
            ScoreFactory *modscore = new ScoreFactory(0, pjt->getprojectpath(), pjt->getscore());
            pjt->setscore(modscore->getScore());
            this->updateMSWtreeWidget(_projectlist);
            pjt->Save();
        }
    }
}


void    MSWtreeWidget::addScore()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist)
    {
        if (pjt->getprojectpath() == projectpath) {
            if (pjt->getscore() == NULL) {
                ScoreFactory *score = new ScoreFactory(0,pjt->getprojectpath());
                pjt->setscore(score->getScore());
                pjt->Save();
                this->updateMSWtreeWidget(_projectlist);
            }
       }
    }
}

void    MSWtreeWidget::addProject()
{
    ProjectFactory  *pct = new ProjectFactory();
    if (pct->getProject() != NULL) {
        _projectlist << pct->getProject();
        this->updateMSWtreeWidget(_projectlist);
    }
}

void    MSWtreeWidget::modProject()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist)
    {
        if (pjt->getprojectpath() == projectpath) {
            ProjectFactory  *pjtf = new ProjectFactory(0, pjt);
            pjt = pjtf->getProject();
            this->updateMSWtreeWidget(_projectlist);
        }
    }
}

void    MSWtreeWidget::setProjectActif()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist) {
        if (pjt->getprojectpath() == projectpath) {
            pjt->setisactive(true);
        } else {
            pjt->setisactive(false);
        }
        this->updateMSWtreeWidget(_projectlist);
    }
}

QString    MSWtreeWidget::createDirExport(Project *pjt, QString exp)
{
    QString dirname = QFileDialog::getExistingDirectory(0,
                                                        tr("Dossier de destination"),
                                                        "/home") + "/" + pjt->getprojectname() + exp;
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

void    MSWtreeWidget::exportPDF()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist) {
        if (pjt->getprojectpath() == projectpath) {
            QString dirname;
            if ((dirname = createDirExport(pjt, "_PDF/")) != "") {
                QGuidoPainter::startGuidoEngine();
                int pageIndex = 1;
                QGuidoPainter *ptr = QGuidoPainter::createGuidoPainter();
                ptr->setGMNCode(pjt->getscore()->getGmncode());
                QSizeF s =  ptr->pageSizeMM( pageIndex );
                QImage image( s.toSize() * 10, QImage::Format_ARGB32 );
                image.fill( QColor(Qt::white).rgb() );
                QPrinter printer(QPrinter::ScreenResolution);
                printer.setOutputFormat(QPrinter::PdfFormat);
                printer.setColorMode(QPrinter::Color);
                printer.setOutputFileName(dirname + pjt->getscore()->getPartition() + ".pdf");
                printer.setPageSize(QPrinter::A4);
                printer.setOrientation(QPrinter::Portrait);
                QPainter p(&printer);
                ptr->draw( &p , pageIndex , image.rect() );
                QGuidoPainter::stopGuidoEngine();
            }
        }
    }
}

void    MSWtreeWidget::exportPNG()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist) {
        if (pjt->getprojectpath() == projectpath) {
            QString dirname;
            if ((dirname = createDirExport(pjt, "_PNG/")) != "") {
                QGuidoPainter::startGuidoEngine();
                int pageIndex = 1;
                QGuidoPainter *ptr = QGuidoPainter::createGuidoPainter();
                ptr->setGMNCode(pjt->getscore()->getGmncode());
                QSizeF s = ptr->pageSizeMM( pageIndex );
                QImage image( s.toSize() * 10 , QImage::Format_ARGB32 );
                image.fill( QColor(Qt::white).rgb() );
                QPainter painter( &image );
                ptr->draw( &painter , pageIndex , image.rect() );
                image.save( dirname + pjt->getscore()->getPartition() + ".png" );
                QGuidoPainter::stopGuidoEngine();
            }
        }
    }
}

void    MSWtreeWidget::exportMIDI()
{
    QObject *obj = sender();
    QString projectpath = obj->objectName();

    foreach (Project *pjt, _projectlist) {
        if (pjt->getprojectpath() == projectpath) {
            QString dirname;
            if ((dirname = createDirExport(pjt, "_MIDI/")) != "") {
                QGuidoPainter::startGuidoEngine();
                QString outfile = dirname + pjt->getscore()->getPartition() + ".mid";
                QGuidoPainter *ptr = QGuidoPainter::createGuidoPainter();
                ptr->setGMNCode(pjt->getscore()->getGmncode());
                CARHandler ar = ptr->getARHandler();
                GuidoAR2MIDIFile((const ARHandler)ar, outfile.toStdString().c_str(), 0);
                QGuidoPainter::stopGuidoEngine();
            }
        }
    }
}


