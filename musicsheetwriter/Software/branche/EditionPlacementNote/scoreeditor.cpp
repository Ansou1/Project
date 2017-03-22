#include "ScoreEditor.h"
#include "ui_scoreeditor.h"

/*
 * connect tous les boutons à leurs slots
*/
ScoreEditor::ScoreEditor(QWidget *parent) : QWidget(parent), ui(new Ui::ScoreEditor)
{
    ui->setupUi(this);
    _currentNote = new Note("");
    this->initSignalsNotes();
    this->initSignalsAccidental();
    this->initSignalsOctave();
    this->initSignalsDuration();
    ui->buttonAjouterNote->setEnabled(false);
    connect(ui->buttonAjouterNote, SIGNAL(clicked(bool)), SLOT(slotButtonAjouterNoteClicked()));

    previewNote(_currentNote);
}

ScoreEditor::~ScoreEditor()
{
    delete ui;
}

/*
 *  connecte tous les boutons de notes à leurs slots
*/
void    ScoreEditor::initSignalsNotes()
{
    connect(ui->buttonDo, SIGNAL(clicked(bool)), SLOT(slotButtonDoClicked()));
    connect(ui->buttonRe, SIGNAL(clicked(bool)), SLOT(slotButtonReClicked()));
    connect(ui->buttonMi, SIGNAL(clicked(bool)), SLOT(slotButtonMiClicked()));
    connect(ui->buttonFa, SIGNAL(clicked(bool)), SLOT(slotButtonFaClicked()));
    connect(ui->buttonSol, SIGNAL(clicked(bool)), SLOT(slotButtonSolClicked()));
    connect(ui->buttonLa, SIGNAL(clicked(bool)), SLOT(slotButtonLaClicked()));
    connect(ui->buttonSi, SIGNAL(clicked(bool)), SLOT(slotButtonSiClicked()));
    connect(ui->buttonPause, SIGNAL(clicked(bool)), SLOT(slotButtonPauseClicked()));
}

/*
 *  connecte tous les boutons accidental à leurs slots
*/
void    ScoreEditor::initSignalsAccidental()
{
    connect(ui->buttonFlat, SIGNAL(clicked(bool)), SLOT(slotButtonFlatClicked()));
    connect(ui->buttonDoubleFlat, SIGNAL(clicked(bool)), SLOT(slotButtonDoubleFlatClicked()));
    connect(ui->buttonSharp, SIGNAL(clicked(bool)), SLOT(slotButtonSharpClicked()));
    connect(ui->buttonDoubleSharp, SIGNAL(clicked(bool)), SLOT(slotButtonDoubleSharpClicked()));
    connect(ui->buttonAucun, SIGNAL(clicked(bool)), SLOT(slotButtonAucunClicked()));
}

/*
 *  connecte tous les boutons octave à leurs slots
*/
void    ScoreEditor::initSignalsOctave()
{
    connect(ui->buttonNegatif, SIGNAL(clicked(bool)), SLOT(slotButtonNegatifClicked()));
    connect(ui->button0, SIGNAL(clicked(bool)), SLOT(slotButton0Clicked()));
    connect(ui->button1, SIGNAL(clicked(bool)), SLOT(slotButton1Clicked()));
    connect(ui->button2, SIGNAL(clicked(bool)), SLOT(slotButton2Clicked()));
    connect(ui->button3, SIGNAL(clicked(bool)), SLOT(slotButton3Clicked()));
}

/*
 *  connecte tous les boutons duration à leurs slots
*/
void    ScoreEditor::initSignalsDuration()
{
    connect(ui->buttonQuarter, SIGNAL(clicked(bool)), SLOT(slotButtonQuarterClicked()));
    connect(ui->buttonHalf, SIGNAL(clicked(bool)), SLOT(slotButtonHalfClicked()));
    connect(ui->buttonWhole, SIGNAL(clicked(bool)), SLOT(slotButtonWholeClicked()));
    connect(ui->buttonLonga, SIGNAL(clicked(bool)), SLOT(slotButtonLongaClicked()));
    connect(ui->button8th, SIGNAL(clicked(bool)), SLOT(slotButton8thClicked()));
    connect(ui->button16th, SIGNAL(clicked(bool)), SLOT(slotButton16thClicked()));
    connect(ui->button32th, SIGNAL(clicked(bool)), SLOT(slotButton32thClicked()));
    connect(ui->buttonDot, SIGNAL(clicked(bool)), SLOT(slotButtonDotClicked()));
    connect(ui->buttonDoubleDot, SIGNAL(clicked(bool)), SLOT(slotButtonDoubleDotClicked()));
    connect(ui->buttonHalfTriplet, SIGNAL(clicked(bool)), SLOT(slotButtonHalfTripletClicked()));
    connect(ui->buttonQuarterTriplet, SIGNAL(clicked(bool)), SLOT(slotButtonQuarterTripletClicked()));
    connect(ui->button8thTriplet, SIGNAL(clicked(bool)), SLOT(slotButton8thTripletClicked()));
    connect(ui->buttonQuintuplet, SIGNAL(clicked(bool)), SLOT(slotButtonQuintupletClicked()));
}

/*
 *  Affiche une preview de la note
 *  On créer une nouvelle QGuidoWidget
 *  On ouvre la guidofactory
 *  On ouvre une nouvelle partition
 *  On ouvre une nouvelle portée
 *  On y ajoute le tag clé
 *  Si on a une note à affiché on créer un event qui correpond à la note et on set les differents attribut de la note
 *  On ferme la porté ainsi que la partition qui retourne un ARHandler qui est utilisé par QGuidoWidget pour afficher la partition
*/
void    ScoreEditor::previewNote(Note *note)
{
    QGuidoWidget *w = new QGuidoWidget(ui->previewnote);
    w->resize(200, 150);
    ARFactoryHandler f;
    GuidoFactoryOpen(&f);
    GuidoFactoryOpenMusic(f);
    GuidoFactoryOpenVoice(f);
    GuidoFactoryOpenTag(f, "clef", 0);
    GuidoFactoryAddTagParameterString(f, "g");
    GuidoFactoryCloseTag (f);
    if (note->getNotename() != "") {
        GuidoFactoryOpenEvent (f, note->getNotename().toStdString().c_str());
        GuidoFactorySetEventAccidentals(f, note->getAccidental());
        GuidoFactorySetDuration(f,note->getEnumduration(),note->getDenomduration());
        GuidoFactorySetOctave(f, note->getOctave());
        GuidoFactoryCloseEvent (f);
    }
    GuidoFactoryCloseVoice(f);
    ARHandler ar = GuidoFactoryCloseMusic(f);
    w->setARHandler(ar);
    w->show();
}

/*
 *  Toutes les actions rattachées aux boutons de note
 *  A chaque note cliqué on set la _currentNote de l'editeur avec celle cliqué
 *  On affiche la preview de la note
*/

void ScoreEditor::slotButtonDoClicked()
{
    Note *note = new Note("do");
    _currentNote = note;
    ui->buttonAjouterNote->setEnabled(true);
    this->previewNote(note);
}

void ScoreEditor::slotButtonReClicked()
{
    Note *note = new Note("re");
    _currentNote = note;
    ui->buttonAjouterNote->setEnabled(true);
    this->previewNote(note);

}

void ScoreEditor::slotButtonMiClicked()
{
    Note *note = new Note("mi");
    _currentNote = note;
    ui->buttonAjouterNote->setEnabled(true);
    this->previewNote(note);

}

void ScoreEditor::slotButtonFaClicked()
{
    Note *note = new Note("fa");
    _currentNote = note;
    ui->buttonAjouterNote->setEnabled(true);
    this->previewNote(note);

}

void ScoreEditor::slotButtonSolClicked()
{
    Note *note = new Note("sol");
    _currentNote = note;
    ui->buttonAjouterNote->setEnabled(true);
    this->previewNote(note);

}

void ScoreEditor::slotButtonLaClicked()
{
    Note *note = new Note("la");
    _currentNote = note;
    ui->buttonAjouterNote->setEnabled(true);
    this->previewNote(note);

}

void ScoreEditor::slotButtonSiClicked()
{
    Note *note = new Note("si");
    _currentNote = note;
    ui->buttonAjouterNote->setEnabled(true);
    this->previewNote(note);
}

/*
 * Verifie qu'une note à bien été selectionné afin de pouvoir lui attribué les attributs accidental, octave et duration
*/
bool    ScoreEditor::checkCurrentNote()
{
    if (_currentNote->getNotename() == "") {
         QMessageBox::critical(0, "Erreur", "Aucune note selectionnée");
         return false;
    }
    return true;
}

void ScoreEditor::slotButtonPauseClicked()
{
}

/*
 *  Fonctions qui set l'accidental de la note
*/
void ScoreEditor::slotButtonFlatClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setAccidental(-1);
        this->previewNote(_currentNote);
    }
}

void ScoreEditor::slotButtonDoubleFlatClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setAccidental(-2);
        this->previewNote(_currentNote);
    }
}

void ScoreEditor::slotButtonSharpClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setAccidental(1);
        this->previewNote(_currentNote);
    }
}

void ScoreEditor::slotButtonDoubleSharpClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setAccidental(2);
        this->previewNote(_currentNote);
    }
}

void ScoreEditor::slotButtonAucunClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setAccidental(0);
        this->previewNote(_currentNote);
    }
}

/*
 *  Fonctions qui set l'octave de la note
*/
void ScoreEditor::slotButtonNegatifClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setOctave(-1);
        this->previewNote(_currentNote);
    }
}

void ScoreEditor::slotButton0Clicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setOctave(0);
        this->previewNote(_currentNote);
    }
}

void ScoreEditor::slotButton1Clicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setOctave(1);
        this->previewNote(_currentNote);
    }
}

void ScoreEditor::slotButton2Clicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setOctave(2);
        this->previewNote(_currentNote);
    }
}

void ScoreEditor::slotButton3Clicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setOctave(3);
        this->previewNote(_currentNote);
    }
}

/*
 *  Fonctions  qui set la durée de la note
*/
void ScoreEditor::slotButtonQuarterClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setEnumduration(1);
        _currentNote->setDenomduration(4);
        previewNote(_currentNote);
    }
}

void ScoreEditor::slotButtonHalfClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setEnumduration(1);
        _currentNote->setDenomduration(2);
        previewNote(_currentNote);
    }
}

void ScoreEditor::slotButtonWholeClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setEnumduration(1);
        _currentNote->setDenomduration(1);
        previewNote(_currentNote);
    }
}

void ScoreEditor::slotButtonLongaClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setEnumduration(2);
        _currentNote->setDenomduration(1);
        previewNote(_currentNote);
    }
}

void ScoreEditor::slotButton8thClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setEnumduration(1);
        _currentNote->setDenomduration(8);
        previewNote(_currentNote);
    }
}

void ScoreEditor::slotButton16thClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setEnumduration(1);
        _currentNote->setDenomduration(16);
        previewNote(_currentNote);
    }
}

void ScoreEditor::slotButton32thClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setEnumduration(1);
        _currentNote->setDenomduration(32);
        previewNote(_currentNote);
    }
}

void ScoreEditor::slotButtonDotClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setEnumduration(3);
        _currentNote->setDenomduration(8);
        previewNote(_currentNote);
    }
}

void ScoreEditor::slotButtonDoubleDotClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setEnumduration(7);
        _currentNote->setDenomduration(16);
        previewNote(_currentNote);
    }
}

void ScoreEditor::slotButtonHalfTripletClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setEnumduration(1);
        _currentNote->setDenomduration(3);
        previewNote(_currentNote);
    }
}

void ScoreEditor::slotButtonQuarterTripletClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setEnumduration(1);
        _currentNote->setDenomduration(6);
        previewNote(_currentNote);
    }
}

void ScoreEditor::slotButton8thTripletClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setEnumduration(1);
        _currentNote->setDenomduration(12);
        previewNote(_currentNote);
    }
}

void ScoreEditor::slotButtonQuintupletClicked()
{
    if (checkCurrentNote() == true) {
        _currentNote->setEnumduration(1);
        _currentNote->setDenomduration(5);
        previewNote(_currentNote);
    }
}

/*
 *  Fonction qui ajoute la note à la partition
 *  On recupere la map de voices de la partition à modifier
 *  On y ajoute la note
 *  On reset l'editeur
 *  On envoi un signal pour mettre à jours le projectlist dans musicsheetwriter, mswtreewidget et menufile ainsi qu'un signal pour modifier l'affichage
*/
void ScoreEditor::slotButtonAjouterNoteClicked()
{
    QMap<int, Voice*> voices = _currentProject->getScore()->getVoices();
    foreach (int i, voices.keys()) {
        if (i == 1) {
            voices[i]->addNote(_currentNote);
        }
    }
    _currentProject->getScore()->setVoices(voices);
    _currentNote = new Note("");
    ui->buttonAjouterNote->setEnabled(false);
    previewNote(_currentNote);
    emit signalUpdateProject(_currentProject);
    emit signalDisplayScore(_currentProject->getScore());
}


/*
 * Fonctions set et get
*/
void    ScoreEditor::setCurrentNote(Note *note) {
    _currentNote = note;
}

void    ScoreEditor::setCurrentScore(Score *score) {
    _currentScore = score;
}

void    ScoreEditor::setCurrentProject(Project *pjt) {
    _currentProject = pjt;
}

Note*   ScoreEditor::getCurrentNote() {
    return _currentNote;
}

Score*  ScoreEditor::getCurrentScore() {
    return _currentScore;
}

Project*    ScoreEditor::getCurrentProject() {
    return _currentProject;
}
