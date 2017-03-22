#-------------------------------------------------
#
# Project created by QtCreator 2016-02-03T10:25:04
#
#-------------------------------------------------

QT       += core gui xml

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets printsupport

TARGET = MusicSheetWriter
TEMPLATE = app


SOURCES +=\
    ProjectFactory.cpp \
    Main.cpp \
    MenuEditor.cpp \
    MenuFile.cpp \
    MSWtreeWidget.cpp \
    MusicSheetWriter.cpp \
    Note.cpp \
    Project.cpp \
    Score.cpp \
    ScoreEditor.cpp \
    ScoreFactory.cpp \
    ScoreView.cpp \
    Voice.cpp

HEADERS  += \
    ProjectFactory.h \
    MenuEditor.h \
    MenuFile.h \
    MSWtreeWidget.h \
    MusicSheetWriter.h \
    Note.h \
    Project.h \
    Score.h \
    ScoreEditor.h \
    ScoreFactory.h \
    ScoreView.h \
    Voice.h

FORMS    += musicsheetwriter.ui \
    projectfactory.ui \
    scorefactory.ui \
    scoreeditor.ui

RESOURCES += \
    ressources.qrc

win32: LIBS += -L$$PWD/GuidoEngine/ -lGUIDOEngine64D
INCLUDEPATH += $$PWD/GuidoEngine/include
DEPENDPATH += $$PWD/GuidoEngine/include

win32: LIBS += -L$$PWD/GuidoEngine/midisharelight/ -lmidisharelight64
INCLUDEPATH += $$PWD/GuidoEngine/midisharelight/include
DEPENDPATH += $$PWD/GuidoEngine/midisharelight/include

win32: LIBS += -L$$PWD/GuidoQt/ -lGuidoQt
INCLUDEPATH += $$PWD/GuidoQt/include
DEPENDPATH += $$PWD/GuidoQt/include
