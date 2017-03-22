#include "ProjectFactory.h"
#include "ui_projectfactory.h"

/*
 * Formulaire de base pour créer un projet
*/
ProjectFactory::ProjectFactory(QWidget *parent) : QDialog(parent), ui(new Ui::ProjectFactory)
{
    ui->setupUi(this);
    _newproject = NULL;
    setWindowTitle("Création nouveau projet");
    exec();
}

/*
 * Formulaire pré rempli -> appelé lorsqu'on modifie un projet
*/
ProjectFactory::ProjectFactory(QWidget *parent, Project *modproject) : QDialog(parent), ui(new Ui::ProjectFactory)
{
    ui->setupUi(this);
    _newproject = modproject;
    setWindowTitle("Modifier le projet");
    ui->Next->setText("Valider");
    ui->nameproject->setText(_newproject->getProjectname());
    ui->projectpath->setText(_newproject->getProjectpath());
    exec();
}

ProjectFactory::~ProjectFactory()
{
    delete ui;
}

/*
 * Verifie que le formulaire est bien rempli, notamment si l'emplacement choisi n'est pas déja occupé par un projet du meme nom
*/
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

/*
 *  Gère à la fois le formulaire de base et celui de modification
 *  De base :
 *      le bouton est 'suivant' et va ouvrir le formulaire de création de partition
 *  Modification:
 *      le bouton est 'validé' va juste modifier le nom ou le path du projet en fonction de ce qui a ete modifié
 *  Si la création à réussi on enregistre.
*/
void ProjectFactory::on_Next_clicked()
{
   if (this->checkFormulaire() != false)
    {
       QString dirname = ui->projectpath->text() + "/" + ui->nameproject->text() + "/";
       if (_newproject != NULL) {
        if (_newproject->getProjectname() != ui->nameproject->text() || _newproject->getProjectpath() != ui->projectpath->text()) {
            QDir(_newproject->getProjectpath()).removeRecursively();
            if (_newproject->getProjectname() != ui->nameproject->text()) {
                dirname = _newproject->getProjectpath().replace(_newproject->getProjectname(), ui->nameproject->text());
            }
            if (!QDir(dirname).exists()) {
                QDir().mkdir(dirname);
            }
            _newproject->setProjectname(ui->nameproject->text());
            _newproject->setProjectpath(dirname);
        }
       } else {
            ScoreFactory    *newscore = new ScoreFactory(0, dirname);
            if (newscore->getScore() != NULL) {
                _newproject = new Project(ui->nameproject->text(), dirname, newscore->getScore());
            }
       }
       if (_newproject != NULL) {
           _newproject->save();
           close();
       }
    }
}

/*
 *  Ouvre le QFileDialog pour choisir la destination du projet
*/
void ProjectFactory::on_Parcourir_clicked()
{
    ui->projectpath->setText(QFileDialog::getExistingDirectory(this, tr("Choisir un dossier"),
                                                          "/home",
                                                          QFileDialog::ShowDirsOnly
                                                          | QFileDialog::DontResolveSymlinks));
}


Project*    ProjectFactory::getProject() {
    return _newproject;
}
