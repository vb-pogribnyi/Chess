#include <jni.h>
#include <string>
#include <sstream>
#include "bitboard.h"
#include "endgame.h"
#include "position.h"
#include "psqt.h"
#include "search.h"
#include "syzygy/tbprobe.h"
#include "thread.h"
#include "tt.h"
#include "uci.h"

using namespace Stockfish;

Position pos;
std::string token, cmd;
StateListPtr states(new std::deque<StateInfo>(1));
const char* StartFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";



extern "C" JNIEXPORT jstring JNICALL
Java_com_example_chess_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_chess_MainActivity_fishInit(JNIEnv *env, jobject thiz) {
    UCI::init(Options);
    Tune::init();
    PSQT::init();
    Bitboards::init();
    Position::init();
    Bitbases::init();
    Endgames::init();
    Threads.set(size_t(Options["Threads"]));
    Search::clear(); // After threads are up
    Eval::NNUE::init();
    pos.set(StartFEN, false, &states->back(), Threads.main());

    return env->NewStringUTF(engine_info().c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_chess_MainActivity_fishGo(JNIEnv *env, jobject thiz, jstring history) {
    std::istringstream is(std::string("depth 10"));
    UCI::go(pos, is, states);

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}