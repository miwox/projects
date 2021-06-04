/**
 * author: miwox
 */
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#ifdef __APPLE__

#include <GLUT/glut.h>

#else
#include <GL/glut.h>
#include <time.h>

#endif

#include "io.h"
#include "scene.h"
#include "logic.h"

/// Will be called when GLUT thinks the window must be redrawn
void onDisplay(void) {
    glClear (GL_COLOR_BUFFER_BIT);
    glMatrixMode (GL_MODELVIEW);
    glLoadIdentity ();
    drawScene();
    glutSwapBuffers ();
}

/// Will be called when Reshape

static void
setProjection(GLdouble
              aspect) {
    glMatrixMode(GL_PROJECTION);

    glLoadIdentity();

    if (aspect <= 1) {
        gluOrtho2D(-1.0, 1.0,
                   -1.0 / aspect, 1.0 / aspect);
    } else {
        gluOrtho2D(-1.0 * aspect, 1.0 * aspect,
                   -1.0, 1.0);
    }
}

/// Will be called when the window is resized
void onReshape(int newWidth, int newHeight) {
    glViewport(0, 0, newWidth, newHeight);
    setProjection((GLdouble) newWidth / (GLdouble) newHeight);
}

/// Will be called for each frame (60 times a sec or so) + sometimes on onDisplay
void onAnimationFrame(int timeLast) {
    int timeNow = glutGet(GLUT_ELAPSED_TIME);
    float timeDelta = ((float) (timeNow - timeLast)) / 1000.0f; // Delta in ms
    updateGameState(timeDelta);

    const int anticipatedFps = 60;
    glutTimerFunc (1000 / anticipatedFps, onAnimationFrame, timeNow);
    glutPostRedisplay();
}

/// Queues a new animation frame and will call onAnimationFrame as soon as the frame should be rendered
void requestAnimationFrame(void) {
    const int anticipatedFps = 60;
    glutTimerFunc(1000 / anticipatedFps, onAnimationFrame, glutGet(GLUT_ELAPSED_TIME));
}
///  Main initialises and starts OpenGL, scene and logic.
int main(int argc, char **argv) {
    srand(time(0)); // Initializing/seeding random

    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB);
    glutInitWindowSize(700, 700);
    glutInitWindowPosition(0, 0);

    if (!glutCreateWindow("Pong with rotating paddles")) {
        printf("Error: Failed to init window\n");
        return EXIT_FAILURE;
    }

    registerCallbacks();
    resetIo();
    resetGameState();
    requestAnimationFrame();
    glutReshapeFunc(onReshape);
    glutDisplayFunc(onDisplay);

    glutMainLoop();

    return EXIT_SUCCESS;
}
