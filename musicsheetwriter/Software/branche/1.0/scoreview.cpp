#include "scoreview.h"
#include "project.h"

scoreView::scoreView(QString projectname, QString scorename, QString gmncode)
{
    _gmncode = gmncode;
    _scorename = scorename;
    _projectname = projectname;
    setWindowTitle(_projectname + " : " + _scorename);
    setAttribute(Qt::WA_DeleteOnClose);
    this->generatePainter();
    setPixmap(QPixmap::fromImage(this->drawPainter()).scaled(500, 400, Qt::KeepAspectRatio, Qt::SmoothTransformation));
}

scoreView::~scoreView()
{

}

QString    scoreView::getgmncode(){
    return _gmncode;
}

QString     scoreView::getscorename() {
    return _scorename;
}

void    scoreView::setgmncode(QString gmncode) {
    _gmncode = gmncode;
}


void    scoreView::generatePainter()
{
    QGuidoPainter::startGuidoEngine();
    _painter = QGuidoPainter::createGuidoPainter();
    _painter->setGMNCode(_gmncode);
    QGuidoPainter::stopGuidoEngine();
}

QImage    scoreView::drawPainter()
{
    int pageIndex = 1;

    QGuidoPainter::startGuidoEngine();
    QSizeF s = _painter->pageSizeMM( pageIndex );
    QImage image( s.toSize() * 10 , QImage::Format_ARGB32 );
    image.fill( QColor(Qt::white).rgb() );
    QPainter painter( &image );
    _painter->draw( &painter , pageIndex , image.rect() );
    QGuidoPainter::stopGuidoEngine();
    return image;
}
