#ifndef PROJECTFACTORY_H
#define PROJECTFACTORY_H

#include <QDialog>
#include <QDir>
#include <QMessageBox>
#include <QFileDialog>
#include "ScoreFactory.h"
#include "Project.h"

namespace Ui {
class ProjectFactory;
}

class ProjectFactory : public QDialog
{
    Q_OBJECT

public:
    ProjectFactory(QWidget *parent = 0);
    ProjectFactory(QWidget *parent, Project *modproject);
    ~ProjectFactory();

    Project*    getProject();

private slots:
    void on_Next_clicked();
    void on_Parcourir_clicked();

private:
    Ui::ProjectFactory  *ui;
    Project             *_newproject;

    bool    checkFormulaire();
};

#endif // PROJECTFACTORY_H
