#include "scorefactory.h"
#include "ui_scorefactory.h"

#include <QDebug>

ScoreFactory::ScoreFactory(QWidget *parent, QString dirname) : QDialog(parent), ui(new Ui::ScoreFactory)
{
    _dirname = dirname;
    _newscore = NULL;
    ui->setupUi(this);
    setWindowTitle("Création nouvelle partition");
    exec();
}

ScoreFactory::ScoreFactory(QWidget *parent, QString dirname, Score *score) : QDialog(parent), ui(new Ui::ScoreFactory)
{
    _dirname = dirname;
    _newscore = score;
    ui->setupUi(this);
    setWindowTitle("Modifer la partition");
    ui->Partition->setText(_newscore->getPartition());
    ui->Compositeur->setText(_newscore->getCompositeur());
    ui->Ryhtme->setValue(_newscore->getrythme());
    ui->Battements->setValue(_newscore->getBattement());
    ui->Tempo->setCurrentText(_newscore->getTempo());
    exec();
}

ScoreFactory::~ScoreFactory()
{
    delete ui;
}

Score   *ScoreFactory::getScore() {
    return _newscore;
}

bool ScoreFactory::checkFormulaire()
{
    if (ui->Partition->text() == NULL) {
        QMessageBox::critical(this, "Erreur", "Le champs Nom de la partition est requis");
        return false;
    } else if (ui->Compositeur->text() == NULL) {
        QMessageBox::critical(this, "Erreur", "Le champs Nom du compositeur est requis");
        return false;
    }
    QString sheetpath = _dirname + ui->Partition->text() + ".gmn";
    qDebug() << sheetpath;
    QFile file(sheetpath);
    if (file.exists()) {
        QMessageBox::critical(this, "Erreur", "Une partition portant ce nom existe déjà. Veuillez choisir  un nouveau nom");
        return false;
    }
    return true;
}

void ScoreFactory::on_Valider_clicked()
{
    if (this->checkFormulaire() != false)
    {
        _newscore = new Score(ui->Partition->text(), ui->Compositeur->text(), ui->Ryhtme->value(), ui->Battements->value(), ui->Tempo->currentText());
        close();
    }
}
