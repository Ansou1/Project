QT += core
QT -= gui

CONFIG += c++11

LIBS +=     -lwinmm

TARGET = MidiTest
CONFIG += console
CONFIG -= app_bundle

TEMPLATE = app

SOURCES += main.cpp \
           rtmidi.cpp \
           rtmidi_c.cpp \
           midifile.cpp

HEADERS += rtmidi.h \
           rtmidi_c.h \
           midifile.h
