#ifndef SCOREFACTORY_H
#define SCOREFACTORY_H

#include    <QDialog>
#include    <QFile>
#include    <QMessageBox>
#include    "score.h"

namespace Ui {
class ScoreFactory;
}

class ScoreFactory : public QDialog
{
    Q_OBJECT

public:
    ScoreFactory(QWidget *parent = 0, QString dirname = "");
    ScoreFactory(QWidget *parent, QString dirname, Score *score);

    ~ScoreFactory();

    Score   *getScore();

private slots:
    void on_Valider_clicked();

private:
    Ui::ScoreFactory    *ui;
    QString             _dirname;
    Score               *_newscore;

    bool    checkFormulaire();
};

#endif // SCOREFACTORY_H
