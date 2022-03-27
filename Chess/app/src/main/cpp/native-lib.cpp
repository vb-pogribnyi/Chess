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
#include <unistd.h>

using namespace Stockfish;

Position pos;
std::string token, cmd;
StateListPtr states(new std::deque<StateInfo>(1));
const char* StartFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
std::stringstream buffer;



extern "C" JNIEXPORT jstring JNICALL
Java_com_example_chess_Fish_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_chess_Fish_fishInit(JNIEnv *env, jobject thiz) {
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
    std::streambuf * old = std::cout.rdbuf(buffer.rdbuf());

    return env->NewStringUTF(engine_info().c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_chess_Fish_go(JNIEnv *env, jobject thiz, jint jdepth) {
    std::istringstream is(std::string("depth 10"));
    UCI::go(pos, is, states);
    Threads.main()->wait_for_search_finished();
    Threads.wait_for_search_finished();
    auto bestThread = Threads.get_best_thread();
    std::string result = UCI::move(bestThread->rootMoves[0].pv[0], pos.is_chess960());

    return env->NewStringUTF(result.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_chess_Fish_perft(JNIEnv *env, jobject thiz, jstring jhistory) {
    std::string history = std::string(env->GetStringUTFChars(jhistory, 0));
    std::string result = "";
    std::istringstream ispos(std::string("startpos moves " + history));
    UCI::setpos(pos, ispos, states);
    for (const auto& m : MoveList<LEGAL>(pos))
    {
        result += UCI::move(m, pos.is_chess960()) + ' ';
    }
    return env->NewStringUTF(result.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_chess_Fish_stop(JNIEnv *env, jobject thiz) {
    Threads.stop = true;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_chess_Fish_fen(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(pos.fen().c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_chess_Fish_setpos(JNIEnv *env, jobject thiz, jstring jhistory) {
    std::string history = std::string(env->GetStringUTFChars(jhistory, 0));
//    std::string history = "e2e4";
    std::istringstream ispos(std::string("startpos moves " + history));
    UCI::setpos(pos, ispos, states);
}