#include "io.h"
#include "logic.h"
#ifdef __APPLE__
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#endif
#include <string.h>

void onKeyDown(unsigned char charKey, int mouseX, int mouseY) {
    UNUSED(mouseX);
    UNUSED(mouseY);

    switch (charKey) {
        // Left player
        case 'A': case 'a': gInput.left |= PADDLE_ROTATE_COUNTER_CLOCKWISE; break;
        case 'D': case 'd': gInput.left |= PADDLE_ROTATE_CLOCKWISE; break;
        case 'W': case 'w': gInput.left |= PADDLE_UP; break;
        case 'S': case 's': gInput.left |= PADDLE_DOWN; break;
        case 'P': case 'p': gInput.global ^= GLOBAL_KEY_TOGGLE_PAUSE; break;

        // Ball
        case 'B': case 'b': gGameState.ball.type = (gGameState.ball.type + 1) % BALL_TYPE_MAX; break;

        case 27 /*GLUT_KEY_ESC*/: glutDestroyWindow(glutGetWindow()); break;

        default:; // ignore all other keys
    }
}

void onKeyUp(unsigned char charKey, int mouseX, int mouseY) {
    UNUSED(mouseX);
    UNUSED(mouseY);

    switch (charKey) {
        // Left player
        case 'A': case 'a': gInput.left &= ~PADDLE_ROTATE_COUNTER_CLOCKWISE; break;
        case 'D': case 'd': gInput.left &= ~PADDLE_ROTATE_CLOCKWISE; break;
        case 'W': case 'w': gInput.left &= ~PADDLE_UP; break;
        case 'S': case 's': gInput.left &= ~PADDLE_DOWN; break;

        default:; // ignore all other keys
    }
}

void onSpecialKeyDown(int specialKey, int mouseX, int mouseY) {
    UNUSED(mouseX);
    UNUSED(mouseY);

    switch (specialKey) {
        // Right player
        case GLUT_KEY_LEFT: gInput.right |= PADDLE_ROTATE_COUNTER_CLOCKWISE; break;
        case GLUT_KEY_RIGHT: gInput.right |= PADDLE_ROTATE_CLOCKWISE; break;
        case GLUT_KEY_UP: gInput.right |= PADDLE_UP; break;
        case GLUT_KEY_DOWN: gInput.right |= PADDLE_DOWN; break;

        // Global keys
        case GLUT_KEY_F1: gInput.global ^= GLOBAL_KEY_TOGGLE_AI_LEFT; break;
        case GLUT_KEY_F2: gInput.global ^= GLOBAL_KEY_TOGGLE_AI_RIGHT; break;
        case GLUT_KEY_F3: gInput.global ^= GLOBAL_KEY_TOGGLE_DEBUG_DRAW; break;

        default:; // ignore all other keys
    }
}

void onSpecialKeyUp(int specialKey, int mouseX, int mouseY) {
    UNUSED(mouseX);
    UNUSED(mouseY);

    switch (specialKey) {
        // Right player
        case GLUT_KEY_LEFT: gInput.right &= ~PADDLE_ROTATE_COUNTER_CLOCKWISE; break;
        case GLUT_KEY_RIGHT: gInput.right &= ~PADDLE_ROTATE_CLOCKWISE; break;
        case GLUT_KEY_UP: gInput.right &= ~PADDLE_UP; break;
        case GLUT_KEY_DOWN: gInput.right &= ~PADDLE_DOWN; break;

        default:; // ignore global toggle (and all other keys)
    }
}

void registerKeyboardCallbacks() {
    // prevents callback spamming if key is held down
    glutIgnoreKeyRepeat(1);

    // Normal keys like W,A,S,D
    glutKeyboardFunc(onKeyDown);
    glutKeyboardUpFunc(onKeyUp);

    // Special keys like ESC and Arrows Keys
    glutSpecialFunc(onSpecialKeyDown);
    glutSpecialUpFunc(onSpecialKeyUp);
}

void resetIo(void) {
    memset(&gInput, 0, sizeof(gInput));
    gInput.global |= GLOBAL_KEY_TOGGLE_AI_RIGHT;
}

void registerCallbacks(void) {
    registerKeyboardCallbacks();
}
