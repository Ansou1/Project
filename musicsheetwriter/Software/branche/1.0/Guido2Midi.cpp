
#ifndef WIN32
#include <libgen.h>
#endif
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <stdlib.h>

#include "GUIDOParse.h"
#include "GUIDOEngine.h"
#define MIDIEXPORT
#include "GUIDO2Midi.h"
#include "SVGSystem.h"
#include "SVGDevice.h"
#include "SVGFont.h"

using namespace std;

int guidoFile2MidiFile(string &infile, string& outfile){
	GuidoErrCode err;
	SVGSystem sys;
	VGDevice *dev = sys.CreateDisplayDevice();
	GuidoInitDesc id = { dev, 0, 0, 0 };
	err = GuidoInit(&id);
	if (err != guidoNoErr)
        error(err);
	
	ARHandler arh;

    GuidoParser *parser = GuidoOpenParser();

    std::ifstream ifs(infile.c_str(), ios::in);
    if (!ifs)
        return 0;

    std::stringstream streamBuffer;
    streamBuffer << ifs.rdbuf();
    ifs.close();

    arh = GuidoString2AR(parser, streamBuffer.str().c_str());
	if (!arh) {
        int line, col;
		error(GuidoParserGetErrorCode (parser, line, col, 0));
	}

/*
	GuidoAR2MIDIFile operates using an ARHandler
	However, for an unknown reason, it fails to convert scores with chords when the
	AR handler has not been converted to GR. This is probably due to the AR to AR 
	transforms that take place before the AR to GR conversion.
	This is the reason of the GuidoAR2GR call below.
	Should be corrected in a future version.
	D.F. June 12 2013
*/
	GRHandler grh;
	err = GuidoAR2GR( arh, 0, &grh);
	if (err != guidoNoErr) error (err);

	cout << "converting " << infile << " to " << outfile << endl;
    err = GuidoAR2MIDIFile(arh, outfile.c_str(), 0);
	GuidoFreeGR (grh);
	GuidoFreeAR (arh);

	if (err != guidoNoErr)
        error(err);

    GuidoCloseParser(parser);
}

//______________________________________________________________________________
static void error (GuidoErrCode err)
{
	cerr << "error #" << err << ": " << GuidoGetErrorString (err) << endl;
	exit(1);
}
