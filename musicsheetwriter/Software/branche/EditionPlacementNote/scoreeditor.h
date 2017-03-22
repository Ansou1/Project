#ifndef SCOREEDITOR_H
#define SCOREEDITOR_H

#include    <QWidget>
#include    <GUIDOFactory.h>
#include    <QGuidoWidget.h>
#include    <QMessageBox>
#include    "Note.h"
#include    "Score.h"
#include    "Project.h"

namespace Ui {
class ScoreEditor;
}

class ScoreEditor : public QWidget
{
    Q_OBJECT

public:
    explicit ScoreEditor(QWidget *parent = 0);
    ~ScoreEditor();

    void    setCurrentProject(Project *pjt);
    void    setCurrentNote(Note *note);
    void    setCurrentScore(Score *score);
    Note    *getCurrentNote();
    Score   *getCurrentScore();
    Project *getCurrentProject();

    void    previewNote(Note *note);

signals:
    void    signalUpdateProject(Project *pjt);
    void    signalDisplayScore(Score *scr);

private slots:
    void slotButtonDoClicked();
    void slotButtonReClicked();
    void slotButtonMiClicked();
    void slotButtonFaClicked();
    void slotButtonSolClicked();
    void slotButtonLaClicked();
    void slotButtonSiClicked();
    void slotButtonPauseClicked();

    void slotButtonFlatClicked();
    void slotButtonDoubleFlatClicked();
    void slotButtonSharpClicked();
    void slotButtonDoubleSharpClicked();
    void slotButtonAucunClicked();

    void slotButtonNegatifClicked();
    void slotButton0Clicked();
    void slotButton1Clicked();
    void slotButton2Clicked();
    void slotButton3Clicked();

    void slotButtonQuarterClicked();
    void slotButtonHalfClicked();
    void slotButtonWholeClicked();
    void slotButtonLongaClicked();
    void slotButton8thClicked();
    void slotButton16thClicked();
    void slotButton32thClicked();
    void slotButtonDotClicked();
    void slotButtonDoubleDotClicked();
    void slotButtonHalfTripletClicked();
    void slotButtonQuarterTripletClicked();
    void slotButton8thTripletClicked();
    void slotButtonQuintupletClicked();

    void slotButtonAjouterNoteClicked();

private:
    Ui::ScoreEditor *ui;
    Note            *_currentNote;
    Score           *_currentScore;
    Project         *_currentProject;

    void    initSignalsNotes();
    void    initSignalsAccidental();
    void    initSignalsOctave();
    void    initSignalsDuration();
    bool    checkCurrentNote();
};

#endif // SCOREEDITOR_H
