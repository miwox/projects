#include <stdlib.h>
#include "io.h"
#include "scene.h"
#include "logic.h"

#ifdef __APPLE__

#include <GLUT/glut.h>

#else
#include <GL/glut.h>
#include <stdio.h>

#endif

int gLastRenderTime = 0;
float lastInputTime = 0;
const float MIN_TIME_BETWEEN_INPUTS = 450;


/// Will be called when the window is resized
static void onReshape(int newWidth, int newHeight) {
    UNUSED(newHeight);
    UNUSED(newWidth);
}


/// Check if the user is spamming a key
int isInputAllowed(void) {
    float now = glutGet(GLUT_ELAPSED_TIME);
    if ((lastInputTime + MIN_TIME_BETWEEN_INPUTS) > now) {
        return 0;
    }
    lastInputTime = now;
    return 1;
}

/// Sets a 3D viewport and draws a 3D scene, handles camera position
static void
set3DViewport(float x, float y, float width, float height, LookAt* lookAt) {
    double aspect = (double) width / height;
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glViewport(x, y, width, height);
    gluPerspective(70.0, aspect, 0.1, 70.0);

    gluLookAt(lookAt->cameraPosition.x, lookAt->cameraPosition.y, lookAt->cameraPosition.z,
              lookAt->lookingDirection.x, lookAt->lookingDirection.y, lookAt->lookingDirection.z,
              0.0, 1.0, 0.0);

    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();

    drawScene3D(gGameState);
}
/// Sets a 2D viewport and draws a 2D scene
static void
set2DViewport(float x, float y, float width, float height, GameState gameState) { //TODO const wurde entfernt
    double aspect = (double) width / height;
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glViewport(x, y, width, height);

    if (aspect <= 1) {
        glOrtho(-1, 1,
                -1 / aspect, 1 / aspect, -1.0, 100);
    } else {
        glOrtho(-1.0f * aspect, 1.0f * aspect, -1.0f, 1.0f,
                -1.0f, 100.0f);
    }
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();

    drawScene2D(gameState);
}

/// Display callback. Will called every frame and is part of the main loop
static void onDisplay(void) {

    int width = glutGet(GLUT_WINDOW_WIDTH);
    int height = glutGet(GLUT_WINDOW_HEIGHT);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    if (gGameState->cameraMode == VIEW3D_FREE_VIEW) {
        Vector3  cameraPosition = getCameraPosition(gGameState);
        LookAt lookAt = {
                .cameraPosition = cameraPosition,
                .lookingDirection = {0, 0, 0},
                .upDirection = {0, 1, 0}
        };

        set3DViewport((float) width/2 , 0, width/2, height, &lookAt);
    } else { //Activate ego shooter  view
        Vector2 hunterPosition = getHunterPosition();
        Vector2 lookingDirection = getHunterPosition();
        setLookingDirection(&lookingDirection, gGameState->hunterDirection, 1.0f);
        const float distanceHead = 0.5f;
        LookAt lookAt = {
                .cameraPosition = {hunterPosition.x, distanceHead, hunterPosition.y},
                .lookingDirection = {lookingDirection.x, 0.5, lookingDirection.y},
                .upDirection = {0, 1, 0}
        };
        set3DViewport((float) width /2, 0, width / 2, height, &lookAt);
    }
    set2DViewport(0, 0, (float) width/2, (float) height, gGameState);
    glutSwapBuffers();
}
/// Will be called when a key get pressed
void onKeyDown(unsigned char charKey, int mouseX, int mouseY) {
    UNUSED(mouseX);
    UNUSED(mouseY);

    switch (charKey) {
        case '1':
            loadLevel(0);
            break;
        case '2':
            loadLevel(1);
            break;
        case '3':
            loadLevel(2);
            break;

        case 'A':
        case 'a':
            gInput |= KEY_CAMERA_LEFT;
            break;
        case 'D':
        case 'd':
            gInput |= KEY_CAMERA_RIGHT;
            break;
        case 'W':
        case 'w':
            gInput |= KEY_CAMERA_UP;
            break;
        case 'S':
        case 's':
            gInput |= KEY_CAMERA_DOWN;
            break;

        case 'Q':
        case 'q':
            gInput |= KEY_CAMERA_ZOOM_OUT;
            break;
        case 'E':
        case 'e':
            gInput |= KEY_CAMERA_ZOOM_IN;
            break;

        case 'P':
        case 'p':
            setPauseState(!isPauseActivated());
            break;
        case 'C':
        case 'c':
            setCameraMode(!getCameraMode());
            break;

        case 'R':
        case 'r':
            loadLevel(gGameState->levelNumber);
        break;
        case 'T':
        case 't': /* TODO: Enable/Disable animation */ break;
        case 'H':
        case 'h':
            setHelpState(!isHelpActivated());
            break;

        case 27 /*GLUT_KEY_ESC*/:
            glutDestroyWindow(glutGetWindow());
            freeDisplayList();
            freeCurrentLevel();
            exit(EXIT_SUCCESS);

        default:; // ignore all other keys
    }
}
/// Will be called when a press key go up
void onKeyUp(unsigned char charKey, int mouseX, int mouseY) {
    UNUSED(mouseX);
    UNUSED(mouseY);

    switch (charKey) {
        case 'A':
        case 'a':
            gInput &= ~KEY_CAMERA_LEFT;
            break;
        case 'D':
        case 'd':
            gInput &= ~KEY_CAMERA_RIGHT;
            break;
        case 'W':
        case 'w':
            gInput &= ~KEY_CAMERA_UP;
            break;
        case 'S':
        case 's':
            gInput &= ~KEY_CAMERA_DOWN;
            break;

        case 'Q':
        case 'q':
            gInput &= ~KEY_CAMERA_ZOOM_OUT;
            break;
        case 'E':
        case 'e':
            gInput &= ~KEY_CAMERA_ZOOM_IN;
            break;
        default:; // ignore all other keys
    }
    /// Draw the scene again and swap the buffers
    glutPostRedisplay();

}

void onSpecialKeyDown(int specialKey, int mouseX, int mouseY) {
    UNUSED(mouseX);
    UNUSED(mouseY);

    // Dont move hunter when pause is activated.
    if(isPauseActivated()){
        return;
    }

    if (hasWon()) {
        loadNextLevel();
        return;
    }

    if (isGameOver()){
        return;
    }

    // Is animation still running?
    if(!isInputAllowedAnimation() && isAnimationActivated()){
        return;
    }

    // Is input allowed? (Prevent input spamming) / Will also set the new input time
    if (!isInputAllowed()) {
        return;
    }

    switch (specialKey) {
        // Right player
        case GLUT_KEY_LEFT:
            hunterMoveIntent(LEFT);
            break;
        case GLUT_KEY_RIGHT:
            hunterMoveIntent(RIGHT);
            break;
        case GLUT_KEY_UP:
            hunterMoveIntent(UP);
            break;
        case GLUT_KEY_DOWN:
            hunterMoveIntent(DOWN);
            break;

            // Global keys
        case GLUT_KEY_F1:
            toggleWireframeMode();
            break;
        case GLUT_KEY_F2:
            setNormalMode(!isNormalModeActivated());
            break;
        case GLUT_KEY_F3:
            gGameState->lightRenderingIsEnabled ^= 1;
            break;
        case GLUT_KEY_F4:
            gGameState->lightParallelIsEnabled ^= 1;
            break;
        case GLUT_KEY_F5:
            gGameState->spotLightIsEnabled ^= 1;
            break;
        case GLUT_KEY_F6:
            setAnimationState(!isAnimationActivated());
            break;
        default:; // ignore all other keys
    }
    glutPostRedisplay ();

}

void onSpecialKeyUp(int specialKey, int mouseX, int mouseY) {
    UNUSED(mouseX);
    UNUSED(mouseY);

    switch (specialKey) {
        default:; // ignore all other keys
    }
}

void registerKeyboardCallbacks(void) {
    // prevents callback spamming if key is held down
    glutIgnoreKeyRepeat(1);

    // Normal keys like W,A,S,D
    glutKeyboardFunc(onKeyDown);
    glutKeyboardUpFunc(onKeyUp);

    // Special keys like ESC and Arrows Keys
    glutSpecialFunc(onSpecialKeyDown);
    glutSpecialUpFunc(onSpecialKeyUp);
}

/// Will be called for each frame (60 times a sec or so). Will mark scene as dirty. And request a new animation frame
void onAnimationFrame(int value) {
    UNUSED(value);
    glutPostRedisplay(); // Mark scene as dirty. Glut will trigger onDisplay
    requestAnimationFrame();
}

/// Queues a new animation frame and will call onAnimationFrame as soon as the frame should be rendered
void requestAnimationFrame(void) {
    const int anticipatedFps = 60;
    int timeNow = glutGet(GLUT_ELAPSED_TIME);
    float timeDeltaSec = ((float) (timeNow - gLastRenderTime)) / 1000.0f;
    gLastRenderTime = timeNow;
    updateGameState(timeDeltaSec);
    glutTimerFunc(1000 / anticipatedFps, onAnimationFrame, 0);
}

static void queueOncePerSecondTimer(void);

/// Get called every 1000ms to count the timer down
static void onOncePerSecond(int unusedValue) {
    UNUSED(unusedValue);
    onGameTimerTick();
    queueOncePerSecondTimer(); // Requeue the loop;
}


static void queueOncePerSecondTimer(void) {
    const int timerIntervalMs = 1000;
    glutTimerFunc(timerIntervalMs, onOncePerSecond, 0);
}



/// Registers all necessary callbacks (keyboard, reshape, display)
void registerAllIoCallbacks(void) {
    registerKeyboardCallbacks();

    glutReshapeFunc(onReshape);
    glutDisplayFunc(onDisplay);
    queueOncePerSecondTimer();

}

