#ifndef MENUEDITOR_H
#define MENUEDITOR_H

#include    <QObject>
#include    <QMenu>
#include    <QAction>

class MenuEditor : public QObject
{
    Q_OBJECT

public:
    MenuEditor(QMenu *menueditor);

private:
    QMenu*  _menueditor;
};

#endif // MENUEDITOR_H
