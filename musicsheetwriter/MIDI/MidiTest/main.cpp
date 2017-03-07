/*
 * #include <QCoreApplication>

int main(int argc, char *argv[])
{
    //QCoreApplication a(argc, argv);

    //return a.exec();
}
*/

/*
enum NoteName
    {
        A,  // la
        B,  // si
        C,  // do
        D,  // re
        E,  // mi
        F,  // fa
        G   // sol
    };

NoteName    _noteName;
    int         _accidental;
    int         _octave;

    Pour Do# ==> Notename = Do, Accidental = 1


int midiDecTime2normalTime(int[] n) {
  int l=n.length;    int t=0;
  for (int i=0 ; i<l-1 ; i++) {
    t += (n[i]-128) * Math.pow(2,7 * (l-i-1)) ;
  }
  t += n[l-1];
  return t;
}
*/

#include <iostream>
#include <cstdlib>
#include <chrono>
#include "RtMidi.h"
#include "midifile.h"

// ici je créer une gloabe temps qui me servira de référence
// prendre la valeur de la première note, puis prendre la valeur de la noteoff et ainsi récupérer la valeur attendre la suivante
//

auto saveOn = std::chrono::system_clock::now();
auto saveOff = std::chrono::system_clock::now();

bool chooseMidiPort( RtMidiIn *rtmidi )
{
  std::string portName;
  unsigned int i = 0, nPorts = rtmidi->getPortCount();
  if ( nPorts == 0 ) {
    std::cout << "No input ports available!" << std::endl;
    return false;
  }

  if ( nPorts == 1 ) {
    std::cout << "\nOpening " << rtmidi->getPortName() << std::endl;
  }
  else {
    for ( i=0; i<nPorts; i++ ) {
      portName = rtmidi->getPortName(i);
      std::cout << "  Input port #" << i << ": " << portName << '\n';
    }

    do {
      std::cout << "\nChoose a port number: ";
      std::cin >> i;
    } while ( i >= nPorts );
  }

  rtmidi->openPort( i );

  return true;
}

void mycallback( double deltatime, std::vector< unsigned char > *message, void *userData)
{
  MIDIfile *file = reinterpret_cast<MIDIfile*>(userData);
  static const char* const noteNames2[] = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
  static const char* const noteNames[] = { "Do", "Do#", "Re", "Re#", "Mi", "Fa", "Fa#", "Sol", "Sol#", "La", "La#", "Si" };
  int accidental = 0;

  unsigned int nBytes = message->size();
  for ( unsigned int i=0; i<nBytes; i++ )
    std::cout << "Byte " << i << " = " << (int)message->at(i) << " = " << message->at(i) << ", ";
  if ( nBytes > 0 ){
    std::cout << "stamp = " << deltatime << std::endl;
    typedef std::chrono::milliseconds ms;
    typedef std::chrono::duration<float> fsec;
    ms d = std::chrono::duration_cast<ms>((fsec)deltatime);
    std::cout << d.count() << "ms\n";
  }
  std::cout << "Note name" << std::endl;
  std::cout << noteNames[((int)message->at(1) > 128) ? ((int)message->at(1) - 128) % 12 : (int)message->at(1) % 12] << std::endl;//a check ici via la doc pour le 127 ou 128
  std::cout << noteNames2[((int)message->at(1) > 128) ? ((int)message->at(1) - 128) % 12 : (int)message->at(1) % 12] << std::endl;
  std::cout << "Octave" << std::endl;
  int res = ((int)message->at(1) > 128) ? ((int)message->at(1) - 128) / 12 : (int)message->at(1) / 12;// récupération de l'octave
  std::cout<< res << std::endl;
  std::cout << "Accidental" << std::endl;
  int result = ((int)message->at(1) > 128) ? ((int)message->at(1) - 128) % 12 : (int)message->at(1) % 12;
  if (result == 1 || result == 3 || result == 6 || result == 8 || result == 10)
    {accidental = 1;std::cout << accidental << std::endl;}

  /*
   * ici je créer un objet note que je remplis de la façon suivante:
   * _noteName = noteNames2[((int)message->at(1) > 128) ? ((int)message->at(1) - 128) % 12 : (int)message->at(1) % 12];
   * _accidental = accidental;
   * _octave = ((int)message->at(1) > 128) ? ((int)message->at(1) - 128) / 12 : (int)message->at(1) / 12;
  */

  /*
   * Pour créer un fichier depuis une partion (plus besoin peut être gérer par la lib guido, export midi)
   *
   * je recoit la note du type objet note
   *
   * je fais donc:
   *
   * int note = _octave * 12 + _noteName + ((_accidental == 1) ? (1) : (0))
   *
   * (*file)[0].KeyOn(1, note, 75); // j'ai pas de valeur de la pression dans la classse note, je mais donc une valeur par défaut
   * (*file)[0].KeyOff(1, -1, 75); // j'ai pas de valeur de la pression dans la classse note, je mais donc une valeur par défaut
   * (*file)[0].AddDelay(360);
  */

  if ((int)message->at(2) != 0)
  {
      std::cout << "KeyOn => " << (int)message->at(1) << std::endl;
      (*file)[0].KeyOn(1, (int)message->at(1), (int)message->at(2));
      (*file)[0].AddDelay(100);
      auto noteOn = std::chrono::system_clock::now();
      saveOn = noteOn;
  }
  else
  {
      std::cout << "KeyOff => " << (int)message->at(1) << std::endl;
      (*file)[0].KeyOff(1, (int)message->at(1), (int)message->at(2));
      (*file)[0].AddDelay(360);
      auto noteOff = std::chrono::system_clock::now();
      saveOff = noteOff;
  }
}

int main()
{
  RtMidiIn *midiin = new RtMidiIn();

  // Check available ports.
  if ( chooseMidiPort( midiin ) == false )
  {
      std::cout << "No ports available!\n";
      delete midiin;
      return 0;
  }

  try
  {
    // Set our callback function.  This should be done immediately after
    // opening the port to avoid having incoming messages written to the
    // queue.

//    std::vector<unsigned char> *message;
//    void *test1;
//    double test = 0.8888;

    MIDIfile file;
    file.AddLoopStart();

    midiin->setCallback( &mycallback, &file );

    //midiin->RtMidiCallback(test, message, test1);

    // Don't ignore sysex, timing, or active sensing messages.
    midiin->ignoreTypes( true, true, true);
    std::cout << "\nReading MIDI input ... press <enter> to quit.\n";
    char input;
    std::cin.get(input);

    //file[0].KeyOn(1, 70, 75);
    //file[0].KeyOff(1, -1, 75);
    //file[0].AddDelay(360);

    file.AddLoopEnd();
    file.Finish();
    std::cout << file.size() << std::endl;
    FILE* fp = std::fopen("test_beta.mid", "wb");
    std::fwrite(&file.at(0), 1, file.size(), fp);
    std::fclose(fp);
  }
  catch( RtMidiError &error )
  {
    error.printMessage();
  }

  return 0;
}

/*
#include <iostream>
#include <cstdlib>
#include <signal.h>
#include "RtMidi.h"

// Platform-dependent sleep routines.
#if defined(__WINDOWS_MM__)
  #include <windows.h>
  #define SLEEP( milliseconds ) Sleep( (DWORD) milliseconds )
#else // Unix variants
  #include <unistd.h>
  #define SLEEP( milliseconds ) usleep( (unsigned long) (milliseconds * 1000.0) )
#endif

bool done;
//static void finish( int /*ignore*/ //){ done = true; }
/*
void usage( void ) {
  // Error function in case of incorrect command-line
  // argument specifications.
  std::cout << "\nusage: qmidiin <port>\n";
  std::cout << "    where port = the device to use (default = 0).\n\n";
  exit( 0 );
}
int main( int argc, char *argv[] )
{
  RtMidiIn *midiin = 0;
  std::vector<unsigned char> message;
  int nBytes, i;
  double stamp;

  // Minimal command-line check.
  if ( argc > 2 ) usage();

  // RtMidiIn constructor
  try {
    midiin = new RtMidiIn();
  }
  catch ( RtMidiError &error ) {
    error.printMessage();
    exit( EXIT_FAILURE );
  }

  // Check available ports vs. specified.
  unsigned int port = 0;
  unsigned int nPorts = midiin->getPortCount();
  if ( argc == 2 ) port = (unsigned int) atoi( argv[1] );
  if ( port >= nPorts ) {
    delete midiin;
    std::cout << "Invalid port specifier!\n";
    usage();
  }

  try {
    midiin->openPort( port );
  }
  catch ( RtMidiError &error ) {
    error.printMessage();
    delete midiin;
  }

  // Don't ignore sysex, timing, or active sensing messages.
  midiin->ignoreTypes( true, true, true );

  // Install an interrupt handler function.
  done = false;
  (void) signal(SIGINT, finish);

  // Periodically check input queue.
  std::cout << "Reading MIDI from port ... quit with Ctrl-C.\n";
  while ( !done ) {
    stamp = midiin->getMessage( &message );
    nBytes = message.size();
    for ( i=0; i<nBytes; i++ )
      std::cout << "Byte " << i << " = " << (int)message[i] << ", ";
    if ( nBytes > 0 )
      std::cout << "stamp = " << stamp << std::endl;

    // Sleep for 10 milliseconds.
    SLEEP( 10 );
  }

  return 0;
}
*/
