#-------------------------------------------------
#
# Project created by QtCreator 2016-02-03T10:25:04
#
#-------------------------------------------------

QT       += core gui xml

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets printsupport

TARGET = MusicSheetWriter
TEMPLATE = app


SOURCES += main.cpp\
        musicsheetwriter.cpp \
    projectfactory.cpp \
    scorefactory.cpp \
    score.cpp \
    project.cpp \
    mswtreewidget.cpp \
    scoreview.cpp \
    mswedittoolbar.cpp

HEADERS  += musicsheetwriter.h \
    projectfactory.h \
    scorefactory.h \
    score.h \
    project.h \
    mswtreewidget.h \
    scoreview.h \
    mswedittoolbar.h

FORMS    += musicsheetwriter.ui \
    projectfactory.ui \
    scorefactory.ui

RESOURCES += \
    ressources.qrc

LIBS += -L$$PWD/GuidoEngine/ -lGUIDOEngine64d
INCLUDEPATH += $$PWD/GuidoEngine/include
DEPENDPATH += $$PWD/GuidoEngine


LIBS += -L$$PWD/GuidoQt/ -lGuidoQt
INCLUDEPATH += $$PWD/GuidoQt/include
DEPENDPATH += $$PWD/GuidoQt

LIBS += -L$$PWD/GuidoEngine/midisharelight/ -lmidisharelight64
INCLUDEPATH += $$PWD/GuidoEngine/midisharelight/include
DEPENDPATH += $$PWD/GuidoEngine/midisharelight

LIBS += -lwinmm
