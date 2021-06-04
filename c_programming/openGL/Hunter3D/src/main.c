#ifdef __APPLE__
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#endif
#include "io.h"
#include "scene.h"
#include "logic.h"
#include <stdlib.h>

/**
 *  Hunter3D
 *  by miwox
 * @param argc Commando
 * @param argv Commando
 * @return SUCCESS
 */
int main(int argc, char** argv)
{
    UNUSED(argc);
    UNUSED(argv);

    loadLevel(0);

    glutInit(&argc, argv);

    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
    glutInitWindowSize(1000, 500);
    glutInitWindowPosition(0, 0);
    glutCreateWindow("Hunter in 3D");

    initScene();
    initDisplayLists();

    registerAllIoCallbacks();
    requestAnimationFrame();

    glutMainLoop();

    return EXIT_SUCCESS;
}
