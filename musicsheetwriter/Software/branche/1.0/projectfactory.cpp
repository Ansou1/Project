#include "projectfactory.h"
#include "ui_projectfactory.h"

#include <QDebug>

ProjectFactory::ProjectFactory(QWidget *parent) : QDialog(parent), ui(new Ui::ProjectFactory)
{
    ui->setupUi(this);
    _newproject = NULL;
    setWindowTitle("Création nouveau projet");
    exec();
}

ProjectFactory::ProjectFactory(QWidget *parent, Project *modproject) : QDialog(parent), ui(new Ui::ProjectFactory)
{
    ui->setupUi(this);
    _newproject = modproject;
    setWindowTitle("Modifier le projet");
    ui->Next->setText("Valider");
    ui->nameproject->setText(_newproject->getprojectname());
    ui->projectpath->setText(_newproject->getprojectpath());
    exec();
}

ProjectFactory::~ProjectFactory()
{
    delete ui;
}

Project*    ProjectFactory::getProject() {
    return _newproject;
}

bool    ProjectFactory::checkFormulaire()
{
    if (ui->nameproject->text() == NULL) {
        QMessageBox::critical(this, "Erreur", "Le champs Nom du projet est requis");
        return false;
    } else if (ui->projectpath->text() == NULL) {
        QMessageBox::critical(this, "Erreur", "Le champs Créer dans est requis");
        return false;
    }
    QString dirname = ui->projectpath->text() + "/" + ui->nameproject->text();
    if (QDir(dirname).exists()) {
        QMessageBox::critical(this, "Erreur", "Un projet portant le même nom existe déjà à l'emplacement indiqué");
        return false;
    }
    return  true;
}

void ProjectFactory::on_Next_clicked()
{
   if (this->checkFormulaire() != false)
    {
       QString dirname = ui->projectpath->text() + "/" + ui->nameproject->text() + "/";
       if (_newproject != NULL) {
        if (_newproject->getprojectname() != ui->nameproject->text() || _newproject->getprojectpath() != ui->projectpath->text()) {
            QDir(_newproject->getprojectpath()).removeRecursively();
            if (_newproject->getprojectname() != ui->nameproject->text()) {
                dirname = _newproject->getprojectpath().replace(_newproject->getprojectname(), ui->nameproject->text());
            }
            if (!QDir(dirname).exists()) {
                QDir().mkdir(dirname);
            }
            _newproject->setprojectname(ui->nameproject->text());
            _newproject->setprojectpath(dirname);
        }
       } else {
            ScoreFactory    *newscore = new ScoreFactory(0, dirname);
            if (newscore->getScore() != NULL) {
                _newproject = new Project(ui->nameproject->text(), dirname, newscore->getScore());
            }
       }
       if (_newproject != NULL) {
           _newproject->Save();
           close();
       }
    }
}

void ProjectFactory::on_Parcourir_clicked()
{
    ui->projectpath->setText(QFileDialog::getExistingDirectory(this, tr("Choisir un dossier"),
                                                          "/home",
                                                          QFileDialog::ShowDirsOnly
                                                          | QFileDialog::DontResolveSymlinks));
}
