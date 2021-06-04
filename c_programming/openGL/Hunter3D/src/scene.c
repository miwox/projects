#include "scene.h"
#include "types.h"
#include "advancedMath.h"
#include <stdlib.h>
#include <stdio.h>
#include "logic.h"

#ifdef __APPLE__

#include <GLUT/glut.h>
#include <printf.h>

#else
#include <GL/glut.h>
#endif

typedef void (*TransparentDrawingFunction)(void);

/// Save the drawing function
typedef struct {
    TransparentDrawingFunction drawingFunc;
    float distanceToCamera;
    IntVector2 logicalPosition;
} DrawingInfo;

/// Linkedlist
typedef struct ListNode *List;

/// Listnode
struct ListNode {
    DrawingInfo draw;
    List next;
};

/// Cons list element.
List cons(List l, DrawingInfo elem) {
    List res = malloc(sizeof(struct ListNode));
    res->draw = elem;
    res->next = l;
    return res;
}

/// Sorted Insert. First low distance.
List sortedInsert(List l, DrawingInfo elem) {
    if (l == NULL || elem.distanceToCamera >= l->draw.distanceToCamera) {
        return cons(l, elem);
    }
    l->next = sortedInsert(l->next, elem);
    return l;
}

/// Delete first Element
List deleteFirstElement(List l) {
    List res = l->next;
    free(l);
    return res;
}

/// Delete complete list.
void deleteAll(List l) {
    while (l != NULL) {
        l = deleteFirstElement(l);
    }
}

/// 3-Vector
typedef GLfloat CGVector3f[3];

/// 3-Vector with extra information
typedef GLfloat CGPoint4f[4];


/// Flag that indicates if the wireframe mode is active.
GLboolean isWireframe = GL_FALSE;
static int isNormalMode = 0;

/// If is lagging please down this sub divisions
const int SUB_DIV_SQUARE = 30;
const int SUB_DIV_TRIANGLE = 3;
const int SUB_DIV_CYLINDER = 50;

/// Maps COLOR enum to byte array (RGB)
static GLubyte COLORS[COLOR_MAX][3] = {
        {255, 255, 255},  // WHITE
        {255, 0,   0},    // RED
        {0,   136, 0},    // GREEN
        {0,   0,   255},  // BLUE
        {255, 255, 0},    // YELLOW
        {255, 165, 0},    // ORANGE
        {0,   255, 255},  // CYAN
        {105, 105, 105},  // GRAY
        {139, 90,  43},     // BROWN
        {0,   0,   0},    // BLACK
};

/// Material properties
static Material COLORS_MATERIAL[COLOR_MAX] =
        {
                {// WHITE
                        .ambient =      {0.9f, 0.9f, 0.9f, 1.0f},
                        .diffuse =      {0.9f, 0.9f, 0.9f, 1.0f},
                        .specular =     {0.9f, 0.9f, 0.9f, 1.0f},
                        .shininess =    {9}
                },

                {// RED
                        .ambient = {0.93f, 0.19f, 0.19f, 1.0f},
                        .diffuse = {0.93f, 0.19f, 0.19f, 1.0f},
                        .specular = {0.6f, 0.24f, 0.19f, 1.0f},
                        .shininess= {20}
                },

                {// GREEN
                        .ambient = {0.0f, 0.3f, 0.06f, 1.0f},
                        .diffuse = {0.0f, 0.3f, 0.06f, 1.0f},
                        .specular = {0.24f, 0.5f, 0.19f, 1.0f},
                        .shininess = {65.0f}
                },
                {// BLUE
                        .ambient = {0.19f, 0.19f, 0.93f, 1.0f},
                        .diffuse = {0.19f, 0.19f, 0.93f, 1.0f},
                        .specular = {0.24f, 0.5f, 0.5f, 1.0f},
                        .shininess = {65.0f}
                },
                {// YELLOW
                        .ambient = {1.0f, 1.0f, 0, 1.0f},
                        .diffuse = {0.93f, 0.93f, 0.19f, 1.0f},
                        .specular = {0.5f, 0.5f, 0.5f, 1.0f},
                        .shininess = {65.0f}
                },
                {// ORANGE
                        .ambient = {1, 0.65f, 0, 1.0f},
                        .diffuse = {1, 0.65f, 0, 1.0f},
                        .specular = {0.5f, 0.5f, 0.5f, 1.0f},
                        .shininess = {65.0f}
                },

                {// CYAN
                        .ambient = {0.19f, 0.93f, 0.93f, 1.0f},
                        .diffuse = {0.19f, 0.93f, 0.93f, 1.0f},
                        .specular = {0.5f, 0.5f, 0.5f, 1.0f},
                        .shininess = {65.0f}
                },

                {// GREY
                        .ambient = {0.41f, 0.41f, 0.41f, 1.0f},
                        .diffuse = {0.41f, 0.41f, 0.41f, 1.0f},
                        .specular = {0.5f, 0.5f, 0.5f, 1.0f},
                        .shininess = {65.0f}
                },

                {// BROWN
                        .ambient = {0.241f, 0.071f, 0.071f, 1.0f},
                        .diffuse = {0.141f, 0.171f, 0.071f, 1.0f},
                        .specular = {0.141f, 0.071f, 0.071f, 1.0f},
                        .shininess =  {65.00f}
                },

                {// BLACK
                        .ambient = {0.1f, 0.1f, 0.1f, 1.0f},
                        .diffuse = {0.1f, 0.1f, 0.1f, 1.0f},
                        .specular = {0.5f, 0.5f, 0.5f, 1.0f},
                        .shininess = {65.0f}
                }
        };

/// Help texts
static const char *HELP_TEXT[] = {
        "UP, DOWN, LEFT, RIGHT: Move hunter",
        "Q,E: Zoom-in or out",
        "P: Pause",
        "C: Ego mode",
        "F1: Toggle wireframe",
        "F2: Dea-Activate normals",
        "F3: Dea-Activate lighting",
        "F4: Dea-Activate parallel lighting",
        "F5: Dea-Activate spotlight",
        "F6: Dea-Activate animation",
};

static GLuint displayListID = 0;
static GLuint displayListNormalsID = 0;

typedef void (*FieldDrawFunction)(void);

/// Get the camera position
Vector3 getCameraPosition(GameState gameState) {
    const CenteredCamera *freeCam = &gameState->freeViewCamera;
    float xCamera = freeCam->distanceFromCenter * sinf(freeCam->azimuthalAngle) * cosf(freeCam->polarAngle);
    float yCamera = freeCam->distanceFromCenter * cosf(freeCam->azimuthalAngle);
    float zCamera = freeCam->distanceFromCenter * sinf(freeCam->azimuthalAngle) * sinf(freeCam->polarAngle);
    Vector3 cameraPosition = {xCamera, yCamera, zCamera};
    return cameraPosition;
}

/// Get normal mode state
int isNormalModeActivated() {
    return isNormalMode;
}

/// Set the normal mode
void setNormalMode(int normal) {
    isNormalMode = normal;
}

/// Allocate alpha color array
GLubyte *allocColorAlphaArray(const GLubyte *color, GLubyte alpha) {
    GLubyte *transparent = malloc(4 * sizeof(*color) + sizeof(alpha));
    transparent[0] = color[0];
    transparent[1] = color[1];
    transparent[2] = color[2];
    transparent[3] = alpha;
    return transparent;
}

/// Make material transparent to an alpha value
Material makeTransparent(const Material mat, float alpha) {
    Material res = mat;
    res.diffuse[3] = alpha;
    res.ambient[3] = alpha;
    res.specular[3] = alpha;
    return res;
}

/// Set material properties for drawing object.
static void setMaterial(Material material) {
    glMaterialfv(GL_FRONT, GL_AMBIENT, material.ambient);
    glMaterialfv(GL_FRONT, GL_DIFFUSE, material.diffuse);
    glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular);
    glMaterialfv(GL_FRONT, GL_SHININESS, material.shininess);
}

/// Returns the length of a given text
float textWidth(const char *text, void *font) {
    unsigned int totalPixelWidth = 0;
    for (const char *cPtr = text; *cPtr; cPtr++) {
        totalPixelWidth += glutBitmapWidth(font, *cPtr);
    }
    return (float) totalPixelWidth / (float) glutGet(GLUT_WINDOW_WIDTH);
}

/// Draws text on the screen; going right from the start pos
void drawText(float startX, float startY, const char *text, void *font) {
    glRasterPos2d(startX, startY);

    for (const char *cPtr = text; *cPtr; cPtr++) {
        glutBitmapCharacter(font, *cPtr); // Automatically updates the position
    }
}

/// Draws text on the screen; but centers the content
void drawCenteredText(float centerX, float centerY, const char *text, void *font) {
    drawText(centerX - textWidth(text, font), centerY, text, font);
}

/// No negative numbers for distance!
/// Adds (direction Vector * distance) to lookingDirection Vector
void setLookingDirection(Vector2 *lookingDirection, Direction direction, float distance) {
    switch (direction) {
        case UP:
            lookingDirection->y -= distance;
            break;
        case DOWN:
            lookingDirection->y += distance;
            break;
        case RIGHT:
            lookingDirection->x += distance;
            break;
        case LEFT:
            lookingDirection->x -= distance;
            break;
        default:
            break;
    }
}

/// No negative numbers for distance!
void setLightDirection(Vector2 *lookingDirection, Direction direction) {
    switch (direction) {
        case UP:
            lookingDirection->y = -1;
            break;
        case DOWN:
            lookingDirection->y = 1;
            break;
        case RIGHT:
            lookingDirection->x = 1;
            break;
        case LEFT:
            lookingDirection->x = -1;
            break;
        default:
            break;
    }
}

/// Set direction angle of hunter
void directionAngleForRotation(float *directionAngle, Direction hunterDirection) {
    switch (hunterDirection) {
        case DOWN:
            *directionAngle = 0;
            break;
        case RIGHT:
            *directionAngle = 90;
            break;
        case UP:
            *directionAngle = 180;
            break;
        case LEFT:
            *directionAngle = 270;
            break;
        default:
            break;
    }
}

/// Get the absolute coordinates from logical coordinates.
Vector2 getAbsoluteCoordinates(int x, int y, int mapWidth, int mapHeight) {
    const float margin = 0.05f;
    float xAbsolute = -((float) mapWidth / 2) + (float) x + (float) x * margin;
    float yAbsolute = -((float) mapHeight / 2) + (float) y + (float) y * margin;
    Vector2 position = {xAbsolute, yAbsolute};
    return position;
}

/// Draws a line of length 1
static void
drawLineHorizontal() {
    glBegin(GL_LINES);
    {
        glNormal3f(0.0f, 0.0f, 1.0f);
        glScalef(1, 1, 1);
        glVertex3f(-0.5f, 0, 0);
        glVertex3f(0.5f, 0, 0);
    }
    glEnd();
}

/// Draws a line of length 1
static void
drawLineVertical(void) {
    glPushMatrix();
    {
        glRotatef(90, 0, 0, 1);
        drawLineHorizontal();
    }
    glPopMatrix();
}

/// Draw a sphere with radius 0.5
static void
drawSphere(void) {
    const int subDivs = 500;
    GLUquadricObj *sphere = gluNewQuadric();
    gluQuadricNormals(sphere, GL_FLAT);

    glPushMatrix();
    {
        gluSphere(sphere, 0.5, subDivs, subDivs);
    }
    glPopMatrix();
    gluDeleteQuadric(sphere);
}

/// Draws normal
void drawNormal() {
    glBegin(GL_LINES);
    {
        glVertex3f(0, 0, 0);
        glVertex3f(0, 0, 1);
    }
    glEnd();
}

/// Draws a square with sub squares
static void
drawSquare(void) {
    int x, y;

    for (y = 0; y < SUB_DIV_SQUARE + 1; y++) {
        if (isNormalModeActivated()) {
            drawNormal();
        }
        glNormal3f(0.0f, 0.0f, 1.0f);
        glBegin(GL_QUAD_STRIP);
        for (x = 0; x <= SUB_DIV_SQUARE + 1; x++) {
            glVertex3f(-0.5f + (float) x / ((float) SUB_DIV_SQUARE + 1.0f),
                       0.5f - (float) y / ((float) SUB_DIV_SQUARE + 1.0f),
                       0.0f);

            glVertex3f(-0.5f + (float) x / ((float) SUB_DIV_SQUARE + 1.0f),
                       0.5f - ((float) y + 1) / ((float) SUB_DIV_SQUARE + 1.0f),
                       0.0f);
        }
        glEnd();
    }
}

/// Draws a triangle with little triangles.
static void drawTriangle(void) {

    const int levelsOfTriangle = pow(2, SUB_DIV_TRIANGLE);
    const float lengthOfEachTriangle = 1.0f / (float) levelsOfTriangle;
    const float heightOfEachTriAngle = sinf(degToRad(60)) * lengthOfEachTriangle;
    const float startPointX = -0.5f;
    const float startPointY = -(sinf(degToRad(60))) / 3;

    //Start position. Middle point at origin.
    float xCoord = startPointX;
    float yCoord = startPointY;
    int currentLevel = 0;
    int toggleDirection = 1;

    for (int y = levelsOfTriangle; y > 0; y--) {
        for (int x = 0; x < (y * 2 - 1); x++) {

            if (isNormalModeActivated()) {
                drawNormal();
            }

            glNormal3f(0, 0, 1);
            glBegin(GL_TRIANGLE_STRIP);
            {
                glVertex2f(xCoord, yCoord);
                if (toggleDirection == 1) { //Direction important
                    glVertex2f(xCoord + lengthOfEachTriangle, yCoord);
                    glVertex2f(xCoord + lengthOfEachTriangle / 2, yCoord + heightOfEachTriAngle);
                } else {
                    glVertex2f(xCoord + lengthOfEachTriangle / 2, yCoord - heightOfEachTriAngle);
                    glVertex2f(xCoord + lengthOfEachTriangle, yCoord);
                }
            }
            glEnd();
            xCoord += lengthOfEachTriangle / 2;
            yCoord = yCoord + heightOfEachTriAngle * (float) toggleDirection;
            toggleDirection *= -1;
        }
        currentLevel++;

        // toggle direction of the coordinates of triangles.
        toggleDirection = 1;
        xCoord = -0.5f + lengthOfEachTriangle / 2 * (float) currentLevel;
    }

}

/// Draws a cube side length 1
static void
drawCube(void) {

    // front
    glPushMatrix();
    {
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawSquare();
    }
    glPopMatrix();

    // right
    glPushMatrix();
    {
        glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawSquare();
    }
    glPopMatrix();

    // back
    glPushMatrix();
    {
        glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawSquare();
    }
    glPopMatrix();

    // left
    glPushMatrix();
    {
        glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawSquare();
    }
    glPopMatrix();

    // top
    glPushMatrix();
    {
        glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawSquare();
    }
    glPopMatrix();

    // bottom
    glPushMatrix();
    {
        glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawSquare();
    }
    glPopMatrix();
}

/// Draw wall side
void drawWallSide() {

    // Draw green wall side
    glPushMatrix();
    {
        glTranslatef(0, 0, -0.001f); // prevent z-fighting
        glColor3ubv(COLORS[GREEN]);
        setMaterial(COLORS_MATERIAL[GREEN]);
        drawSquare();
    }
    glPopMatrix();

    setMaterial(COLORS_MATERIAL[WHITE]);
    glColor3ubv(COLORS[WHITE]);

    const float marginTopBottom = 0.25f;
    const unsigned int linesHorizontal = 3;
    float linePositionY = -0.5f; //We start at the bottom draw horizontal lines

    for (int i = 0; i < linesHorizontal; i++) {
        linePositionY += marginTopBottom;
        glPushMatrix();
        {
            glTranslatef(0, linePositionY, 0);
            drawLineHorizontal();
        }
        glPopMatrix();
    }

    const float blockSize = 1.0f / 3.0f;
    const unsigned int linesVertical = 4;
    float centerPositionVertical = -0.5f + marginTopBottom / 2;

    for (int y = 0; y < linesVertical; y++) {//Draw vertical lines.
        float xCoord = -0.5f + (y % 2 ? blockSize : blockSize / 2);
        for (int x = 0; x < (y % 2 ? 2 : 3); x++) { // every second line is offsettet by one (brick pattern)
            glPushMatrix();
            {
                glTranslatef(xCoord, centerPositionVertical, 0);
                glScalef(0, marginTopBottom, 0);
                drawLineVertical();
            }
            glPopMatrix();
            xCoord += blockSize;
        }
        centerPositionVertical += marginTopBottom;
    }

}

/// Draw a wall with his sides.
static void
drawWall() {

    //Front
    glPushMatrix();
    {
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawWallSide();
    }
    glPopMatrix();

    //Right
    glPushMatrix();
    {
        glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawWallSide();
    }
    glPopMatrix();

    //Back
    glPushMatrix();
    {
        glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawWallSide();
    }
    glPopMatrix();

    //Left
    glPushMatrix();
    {
        glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawWallSide();
    }
    glPopMatrix();

    //Top
    glPushMatrix();
    {
        glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawWallSide();
    }
    glPopMatrix();

    //Bottom
    glPushMatrix();
    {
        glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawWallSide();
    }
    glPopMatrix();
}

/// Draw empty free field to move on
static void
drawField(void) {
    setMaterial(COLORS_MATERIAL[YELLOW]);
    glColor3ubv(COLORS[YELLOW]);
    glPushMatrix();
    {
        glTranslatef(0, -0.5, 0);
        glRotatef(-90, 1, 0, 0);
        drawSquare();
    }
    glPopMatrix();
}

/// Draw the front and back
static void
drawDoorFrontAndBack() {

    const float doorWidth = 1 / 3.0f;
    const float marginLeft = -0.5f + doorWidth;
    const float marginRight = 0.5f - doorWidth;

    //DOOR
    setMaterial(COLORS_MATERIAL[BROWN]);
    glColor3ubv(COLORS[BROWN]);
    glPushMatrix();
    {
        glScalef(doorWidth, 1, 1);
        drawSquare();
    }
    glPopMatrix();

    // LEFT
    setMaterial(COLORS_MATERIAL[BLACK]);
    glColor3ubv(COLORS[BLACK]);
    glPushMatrix();
    {
        glTranslatef(marginLeft, 0, 0);
        drawLineVertical();
    }
    glPopMatrix();

    //RIGHT
    glPushMatrix();
    {
        glTranslatef(marginRight, 0, 0);
        drawLineVertical();
    }
    glPopMatrix();

    glColor3ubv(COLORS[BLACK]);

    //MIDDLE
    glPushMatrix();
    {
        glScalef(doorWidth, 0, 0);
        drawLineHorizontal();
    }
    glPopMatrix();
}

/// Draw a door
static void
drawDoor() {
    const float doorWidth = .5f;
    setMaterial(COLORS_MATERIAL[BROWN]);
    glColor3ubv(COLORS[BROWN]);

    //Front side
    glPushMatrix();
    {
        glTranslatef(0, 0, doorWidth / 2);
        drawDoorFrontAndBack();

    }
    glPopMatrix();

    //Back side
    glPushMatrix();
    {
        glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        glTranslatef(0, 0, doorWidth / 2);
        drawDoorFrontAndBack();
    }
    glPopMatrix();


    setMaterial(COLORS_MATERIAL[BROWN]);
    glColor3ubv(COLORS[BROWN]);
    const float doorWidth2 = 1 / 3.0f;

    //Right Side
    glPushMatrix();
    {
        glRotatef(90, 0, 1, 0);
        glTranslatef(0, 0, doorWidth2 / 2);
        glScalef(doorWidth, 1, 0);
        drawSquare();
    }
    glPopMatrix();

    //Left side
    glPushMatrix();
    {
        glRotatef(270, 0, 1, 0);
        glTranslatef(0, 0, doorWidth2 / 2);
        glScalef(doorWidth, 1, 0);
        drawSquare();
    }
    glPopMatrix();

    //Bottom
    glPushMatrix();
    {
        glRotatef(90, 1, 0, 0);
        glTranslatef(0, 0, 0.5);
        glScalef(doorWidth2, doorWidth, 0);
        drawSquare();
    }
    glPopMatrix();

    //Top
    glPushMatrix();
    {
        glRotatef(270, 1, 0, 0);
        glTranslatef(0, 0, 0.5);
        glScalef(doorWidth2, doorWidth, 0);
        drawSquare();
    }
    glPopMatrix();

}

/// Draw a regular tetrahedron with triangles. Each side is 1 and height is sin(60). Starts at y=0.
/// We cannot simply connect the dots since we need to make subDivisions
static void drawRegularTetrahedron(void) {
    const float angle = radToDeg(acosf((float) 1 / 3));

    // Buttom
    glPushMatrix();
    {
        glRotatef(60, 0, 1, 0);
        glRotatef(90, 1, 0, 0);
        drawTriangle();
    }
    glPopMatrix();

    const float rotationAngle = angle - 90;
    const float yOld = -sinf(degToRad(60)) * 1 / 3;

    // calculate new translation after rotation.
    // Drehmatrix - Formel.
    const float yNew = cosf(degToRad(rotationAngle)) * yOld;
    const float zNew = sinf(degToRad(rotationAngle)) * yOld;

    //Front side pyramide
    glPushMatrix();
    {
        glTranslatef(0, -yNew, sinf(degToRad(60)) * 1 / 3 - zNew);
        glRotatef(rotationAngle, 1, 0, 0); // 90 - acos 1/3
        drawTriangle();
    }
    glPopMatrix();

    //leftside
    glPushMatrix();
    {
        glRotatef(120, 0, 1, 0); // 90 - acos 1/3
        glTranslatef(0, -yNew, sinf(degToRad(60)) * 1 / 3 - zNew);
        glRotatef(rotationAngle, 1, 0, 0); // 90 - acos 1/3
        drawTriangle();
    }
    glPopMatrix();

    //right side
    glPushMatrix();
    {
        glRotatef(240, 0, 1, 0); // 90 - acos 1/3
        glTranslatef(0, -yNew, sinf(degToRad(60)) * 1 / 3 - zNew);
        glRotatef(rotationAngle, 1, 0, 0); // 90 - acos 1/3
        drawTriangle();
    }
    glPopMatrix();
}

/// Draw a cylinder with radius 0.5
static void drawCylinder(void) {
    GLUquadricObj *cylinder = gluNewQuadric();
    GLUquadricObj *circle = gluNewQuadric();

    gluQuadricNormals(cylinder, GL_FLAT);
    gluQuadricNormals(circle, GL_FLAT);

    glPushMatrix();
    {
        glRotatef(90, 1, 0, 0);
        glTranslatef(0, 0, -0.5);
        //Back cover
        glPushMatrix();
        {
            glRotatef(180, 0, 1, 0);
            gluDisk(circle, 0, 0.5, SUB_DIV_CYLINDER, SUB_DIV_CYLINDER);
        }
        glPopMatrix();

        //Fron cover
        glPushMatrix();
        {
            glTranslatef(0, 0, 1);
            gluDisk(circle, 0, 0.5, SUB_DIV_CYLINDER, SUB_DIV_CYLINDER);
        }
        glPopMatrix();

        //Cyclder
        glPushMatrix();
        {
            gluCylinder(cylinder, 0.5f, 0.5f, 1.0f, SUB_DIV_CYLINDER, SUB_DIV_CYLINDER);
        }
        glPopMatrix();
        gluDeleteQuadric(cylinder);
        gluDeleteQuadric(circle);
    }
    glPopMatrix();
}

/// Draw a torus with 0.5 outer radius and inner radius: outer radius / 2
static void
drawTorus(void) {
    GLUquadricObj *torus = gluNewQuadric();
    const int sides = 100;
    const int rings = 100;
    gluQuadricNormals(torus, GL_FLAT);

    glPushMatrix();
    {
        glutSolidTorus(0.25, 0.5, sides, rings);
    }
    glPopMatrix();
    gluDeleteQuadric(torus);
}

/// Draw key, material properties will be not set here
static void
drawKey(void) {

    const float lengthOfBodyKey = 0.8f;
    const float lengthOfHeadKey = 1 - lengthOfBodyKey;
    const float widthOfHeadKey = 0.3f;
    const float widthOfBodyKey = widthOfHeadKey / 2.0f;
    const float widthOfFootKey = widthOfBodyKey;

    const float border = 0.5f;


    //TODO freiwillig: lass uns hier eine animation einbauen, sodass die Schlüssel sich um die y - Achse rotieren.

    // Draw body of key
    glPushMatrix();
    {
        glTranslatef(0, (-border + lengthOfBodyKey / 2), 0);
        glScalef(widthOfBodyKey, lengthOfBodyKey, widthOfBodyKey);
        drawCylinder();
    }
    glPopMatrix();

    // Draw head of key
    glPushMatrix();
    {
        glTranslatef(0, (border - lengthOfHeadKey / 2), 0);
        glScalef(widthOfHeadKey, lengthOfHeadKey, widthOfHeadKey);
        drawTorus();
    }
    glPopMatrix();

    // Draw foot of key

    glPushMatrix();
    {
        glTranslatef(0 - widthOfBodyKey / 2 - widthOfFootKey / 2, (-border + lengthOfHeadKey / 2), 0);
        glScalef(widthOfFootKey, lengthOfHeadKey, widthOfBodyKey);
        glRotatef(90, 0, 0, 1.0f);
        drawCylinder();
    }
    glPopMatrix();

}

/// Draw grey key
static void
drawGreyKey(void) {
    glColor3ubv(COLORS[GRAY]);
    setMaterial(COLORS_MATERIAL[GRAY]);
    drawKey();
}

/// Draw golden key
static void
drawGoldenKey(void) {
    glColor3ubv(COLORS[ORANGE]);
    setMaterial(COLORS_MATERIAL[ORANGE]);
    drawKey();
}

/// Draw trap deactivated
static void
drawTrapDeactivated(void) {
    glColor3ubv(COLORS[GRAY]);
    setMaterial(COLORS_MATERIAL[GRAY]);
    glPushMatrix();
    {
        glTranslatef(0, -0.5, 0);
        glRotatef(-90, 1, 0, 0);
        drawSquare();

    }
    glPopMatrix();
}

/// Draw trap activated
static void
drawTrapActivated(void) {
    const float numberOfArrowEachLine = 5;
    const float arrowHeadHeightWidth = 1 / numberOfArrowEachLine;
    const float arrowBodyHeight = 1 - arrowHeadHeightWidth;
    const float arrowBodyWidth = arrowHeadHeightWidth / 3.0f;
    float xCoord = -0.5 + arrowHeadHeightWidth / 2;

    glColor3ubv(COLORS[BLACK]);
    setMaterial(COLORS_MATERIAL[BLACK]);

    for (int i = 0; i < numberOfArrowEachLine; i++) {

        glPushMatrix();
        {
            glTranslatef(xCoord, 0, 0);

            //Arrow head
            glPushMatrix();
            {
                glTranslatef(0, 0.5 - arrowHeadHeightWidth, 0);
                glScalef(arrowHeadHeightWidth, arrowHeadHeightWidth, arrowHeadHeightWidth);
                drawRegularTetrahedron();
            }
            glPopMatrix();


            //Arrow body
            glPushMatrix();
            {
                glTranslatef(0, -(0.5f - arrowBodyHeight / 2), 0);
                glScalef(arrowBodyWidth, arrowBodyHeight, arrowBodyWidth);
                drawCylinder();
            }
            glPopMatrix();


        }
        glPopMatrix();
        xCoord = xCoord + arrowHeadHeightWidth;
    }
}

/// Draw exit
static void drawExit(void) {
    const float heightArrow = 0.5f;
    const float widthBody = heightArrow / 2;
    const float lengthBody = heightArrow;

    // draw tetrahedron
    glTranslatef(0.5f - heightArrow, 0, 0);
    glPushMatrix();
    {
        glScalef(sinf(degToRad(60)) * heightArrow, sinf(degToRad(60)) * heightArrow, heightArrow);
        glRotatef(270, 0, 0, 1); // Rotate so it points to the side and NOT up
        glRotatef(90, 0, 1, 0); // Rotating in order to make it look aligned to the ground
        drawRegularTetrahedron();
    }
    glPopMatrix();

    // draw cylinder
    glPushMatrix();
    {
        glTranslatef(-lengthBody / 2, 0, 0);
        glRotatef(270, 0, 0, 1);
        glScalef(widthBody, lengthBody, widthBody),
        drawCylinder();
    }
    glPopMatrix();
}

/// Draw nothing
static void
drawNothing(void) {

}

/// Draw closed chest front is z axis
void drawChestClosed(void) {

    const float widthBottom = 0.35f;
    const float depthBottom = widthBottom;
    const float heightBottom = 0.50f;

    setMaterial(COLORS_MATERIAL[BROWN]);
    glColor3ubv(COLORS[BROWN]);
    glPushMatrix();
    {
        glTranslatef(0, -0.5 + heightBottom / 2, 0);
        glScalef(widthBottom, heightBottom, depthBottom);
        drawCube();
    }
    glPopMatrix();

    const float widthTop = 0.35f;
    const float depthTop = widthTop;
    const float heightTop = 0.25f;

    setMaterial(COLORS_MATERIAL[RED]);
    glColor3ubv(COLORS[RED]);
    glPushMatrix();
    {
        glTranslatef(0, heightTop / 2, 0);
        glScalef(widthTop, heightTop, depthTop);
        drawCube();
    }
    glPopMatrix();

    const float lockRadius = widthBottom / 5.0f;

    setMaterial(COLORS_MATERIAL[ORANGE]);
    glColor3ubv(COLORS[ORANGE]);
    glPushMatrix();
    {
        glTranslatef(0, 0, depthTop / 2 + lockRadius / 2);
        glScalef(lockRadius, lockRadius, lockRadius);
        drawSphere();
    }
    glPopMatrix();
}

/// Draw opened chest front is z axis
static void
drawChestOpened(void) {
    const float widthBottom = 0.35f;
    const float depthBottom = widthBottom;
    const float heightBottom = 0.50f;

    setMaterial(COLORS_MATERIAL[BROWN]);
    glColor3ubv(COLORS[BROWN]);
    glPushMatrix();
    {
        glTranslatef(0, -0.5 + heightBottom / 2, 0);
        glScalef(widthBottom, heightBottom, depthBottom);
        drawCube();
    }
    glPopMatrix();

    const float widthTop = 0.35f;
    const float depthTop = widthTop;
    const float heightTop = 0.25f;

    setMaterial(COLORS_MATERIAL[RED]);
    glColor3ubv(COLORS[RED]);
    glPushMatrix();
    {
        glTranslatef(0, widthTop / 2, 0);
        glRotatef(-90, 1, 0, 0);
        glTranslatef(0, heightTop / 2, 0);
        glScalef(widthTop, heightTop, depthTop);
        drawCube();
    }
    glPopMatrix();
}

/// Draw eyes of hunter front is z axis
static void drawEye() {

    const float diameterEye = 0.90f;
    const float diameterPupil = 0.65f * diameterEye;


    glPushMatrix();    // Pupil
    {
        setMaterial(COLORS_MATERIAL[BLACK]);
        glColor3ubv(COLORS[BLACK]);
        glTranslatef(0, 0, 0.5 - diameterPupil / 2);
        glScalef(diameterPupil, diameterPupil, diameterPupil);
        drawSphere();
    }
    glPopMatrix();

    glPushMatrix(); // Eye
    {
        setMaterial(COLORS_MATERIAL[WHITE]);
        glColor3ubv(COLORS[WHITE]);
        glScalef(diameterEye, diameterEye, diameterEye);
        drawSphere();
    }
    glPopMatrix();

}

/// Draw hunter front is z axis
static void drawHunter(void) {
    const float diameterHead = 0.3f;
    const float diameterEyes = diameterHead / 3.0f;
    const float neckHeight = diameterHead / 5.0f;
    const float neckDiameter = neckHeight;
    const float bodyHeight = 0.4f;
    const float bodyWidth = 0.4f;
    const float bodyDepth = 0.3f;
    const float widthArms = 0.3f;
    const float heightArms = 0.1f;
    const float depthArms = 0.2f;
    const float heightLegs = 1 - diameterHead - neckHeight - bodyHeight;
    const float depthLegs = depthArms;
    const float widthLegs = heightArms;

    const float marginBodyAndParty = bodyHeight / 4.0f;

    setMaterial(COLORS_MATERIAL[BLACK]);
    glColor3ubv(COLORS[BLACK]);


    glPushMatrix();
    {
        glTranslatef(0, 0.5 - diameterHead / 2, 0);

        glPushMatrix();
        {
            glScalef(diameterHead, diameterHead, diameterHead);
            drawSphere();
        }
        glPopMatrix();

        //Draw right eye
        glPushMatrix();
        {
            glRotatef(30, 0, 1, 0);
            glTranslatef(0, 0, diameterHead / 2);
            glScalef(diameterEyes, diameterEyes, diameterEyes);
            drawEye();
        }
        glPopMatrix();

        //Draw left eye
        glPushMatrix();
        {
            glRotatef(-30, 0, 1, 0);
            glTranslatef(0, 0, diameterHead / 2);
            glScalef(diameterEyes, diameterEyes, diameterEyes);
            drawEye();
        }
        glPopMatrix();
    }
    glPopMatrix();

    //Draw neck
    setMaterial(COLORS_MATERIAL[BLACK]);
    glColor3ubv(COLORS[BLACK]);
    glPushMatrix();
    {
        glTranslatef(0, 0.5 - diameterHead - neckHeight / 2, 0);
        glScalef(neckDiameter, neckHeight, neckDiameter);
        drawCylinder();
    }
    glPopMatrix();

    //Draw body

    setMaterial(COLORS_MATERIAL[BLACK]);
    glColor3ubv(COLORS[BLACK]);

    glPushMatrix();
    {
        glTranslatef(0, 0.5 - diameterHead - neckHeight - bodyHeight / 2, 0);
        glScalef(bodyWidth, bodyHeight, bodyDepth);
        drawCube();
    }
    glPopMatrix();


    setMaterial(COLORS_MATERIAL[BLACK]);
    glColor3ubv(COLORS[BLACK]);

// Right arm
    glPushMatrix();
    {
        glTranslatef(bodyWidth / 2 + widthArms / 2, 0.5 - diameterHead - neckHeight - marginBodyAndParty, 0);
        glScalef(widthArms, heightArms, depthArms);
        drawCube();
    }
    glPopMatrix();
// Left arm
    glPushMatrix();
    {
        glTranslatef(-bodyWidth / 2 - widthArms / 2, 0.5 - diameterHead - neckHeight - marginBodyAndParty, 0);
        glScalef(widthArms, heightArms, depthArms);
        drawCube();
    }
    glPopMatrix();

    //Right Leg
    glPushMatrix();
    {
        glTranslatef(marginBodyAndParty + widthLegs / 2, 0.5 - diameterHead - neckHeight - bodyHeight - heightLegs / 2,
                     0);
        glScalef(widthLegs, heightLegs, depthLegs);
        drawCube();
    }
    glPopMatrix();

    //Left leg
    glPushMatrix();
    {
        glTranslatef(-marginBodyAndParty - widthLegs / 2, 0.5 - diameterHead - neckHeight - bodyHeight - heightLegs / 2,
                     0);
        glScalef(widthLegs, heightLegs, depthLegs);
        drawCube();
    }
    glPopMatrix();


}

/// Draws a magic chest side
static void
drawMagicSide(int number, COLOR color) {

    // Draw rome number
    setMaterial(COLORS_MATERIAL[BLACK]);
    glColor3ubv(COLORS[BLACK]);
    const float length = 0.5f;
    const float width = length / 5;
    float x = (((float) (-number)) + 1) * width;
    for (int i = 0; i < number; i++) {
        glPushMatrix();
        {
            glTranslatef(x, 0, 0.002);
            glScalef(width, length, 0);
            drawSquare();
        }
        glPopMatrix();
        x += 2 * width;
    }

    // Draw front side
    GLubyte *transparent = allocColorAlphaArray(COLORS[color], 200);
    glColor4ubv(transparent);

    free(transparent);
    transparent = NULL;
    Material trans = makeTransparent(COLORS_MATERIAL[color], 0.5);
    setMaterial(trans);
    drawSquare();

    // Draw circle of front side.
    GLUquadricObj *quad = gluNewQuadric();
    gluQuadricDrawStyle(quad, GLU_FILL);
    transparent = allocColorAlphaArray(COLORS[WHITE], 255);
    glColor4ubv(transparent);
    free(transparent);
    transparent = NULL;
    trans = makeTransparent(COLORS_MATERIAL[WHITE], 0.5);
    setMaterial(trans);
    glPushMatrix();
    {
        glTranslatef(0, 0, 0.001);
        gluDisk(quad, 0, 0.5, 30, 30);
    }
    glPopMatrix();
    gluDeleteQuadric(quad);

}

/// Draws a magic chest with given no.
static void
drawMagicChestNumber(int number, COLOR color) {
    // front
    glPushMatrix();
    {
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawMagicSide(number, color);
    }
    glPopMatrix();

    // right
    glPushMatrix();
    {
        glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawMagicSide(number, color);
    }
    glPopMatrix();

    // back
    glPushMatrix();
    {
        glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawMagicSide(number, color);
    }
    glPopMatrix();

    // left
    glPushMatrix();
    {
        glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawMagicSide(number, color);
    }
    glPopMatrix();

    // top
    glPushMatrix();
    {
        glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawMagicSide(number, color);
    }
    glPopMatrix();

    // bottom
    glPushMatrix();
    {
        glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        glTranslatef(0.0f, 0.0f, 0.5f);
        drawMagicSide(number, color);
    }
    glPopMatrix();

}

/// Draws a magic chest with no. 0
static void
drawMagicChestZero() {
    drawMagicChestNumber(0, RED);
}

/// Draws a magic chest with no. 3
static void
drawMagicChestOne() {
    drawMagicChestNumber(1, GRAY);
}

/// Draws a magic chest with no. 3
static void
drawMagicChestTwo() {
    drawMagicChestNumber(2, GRAY);
}

/// Draws a magic chest with no. 3
static void
drawMagicChestThree() {
    drawMagicChestNumber(3, GRAY);
}

/// Draw pointer function for displaylist
const static FieldDrawFunction fieldTypeDrawFunctions[P_MAX] = {
        &drawNothing,
        &drawField,
        &drawWall,
        &drawMagicChestZero, ///P_BOX0 is transparent and non static. TODO draw the magic chest
        &drawMagicChestOne,
        &drawMagicChestTwo,
        &drawMagicChestThree,
        &drawTrapDeactivated,
        &drawTrapActivated,
        &drawExit,
        &drawGreyKey,
        &drawGoldenKey,
        &drawChestClosed,
        &drawChestOpened,
        &drawDoor,
        &drawHunter,
};

/// Draw the transparent objects only for 3D map
void drawTransparent(Map map) {

    // Add transparent object in a sorted linked List. Only
    List list = NULL;
    for (int zCoord = 0; zCoord < map.size.height; zCoord++) {
        for (int xCoord = 0; xCoord < map.size.width; xCoord++) {
            FIELD_TYPE field = FIELD_AT_POS(map, xCoord, zCoord);
            if (field == P_BOX0 || field == P_BOX1 || field == P_BOX2 || field == P_BOX3) {
                Vector3 camera = getCameraPosition(gGameState);
                Vector2 position = getAbsoluteCoordinates(xCoord, zCoord, map.size.width, map.size.height);
                Vector3 absolutePosition = {position.x, 0, position.y};
                float distance = getDistanceOfVector3(camera, absolutePosition);

                DrawingInfo element = {
                        .drawingFunc = fieldTypeDrawFunctions[field],
                        .distanceToCamera = distance,
                        .logicalPosition = {
                                .x = xCoord,
                                .y = zCoord
                        }
                };
                list = sortedInsert(list, element);
            }
        }
    }


    // Draw the transparent object. Iterate the list
    List iterator = list;
    while (iterator != NULL) {
        DrawingInfo* element = &(iterator->draw);
        Vector2 position = getAbsoluteCoordinates(element->logicalPosition.x, element->logicalPosition.y, map.size.width,
                                                  map.size.height);
        glPushMatrix();
        {
            glTranslatef(position.x, 0, position.y);
            element->drawingFunc();
        }
        glPopMatrix();
        iterator = iterator->next;
    }
    deleteAll(list);
}

/// Draw the game field in 2D or 3D
void drawGameField(Map map, GLboolean is3D) {
    // if we are in 2D we do not use the normals.
    GLuint selectDisplayList = isNormalMode && is3D ? displayListNormalsID : displayListID;

    for (int zCoord = 0; zCoord < map.size.height; zCoord++) {
        for (int xCoord = 0; xCoord < map.size.width; xCoord++) {
            FIELD_TYPE field = FIELD_AT_POS(map, xCoord, zCoord);
            Vector2 position = getAbsoluteCoordinates(xCoord, zCoord, map.size.width, map.size.height);
            glPushMatrix(); //call the display list.
            {
                glTranslatef(position.x, 0, position.y);
                if (field != P_TRAP && field != P_BLACK && field != P_WALL) {
                    glCallList(selectDisplayList + P_FREE);
                }
                // if we are in 2D we call the display list to draw also the transparent objects, and the rotated objects normal.
                if (!is3D ||
                    (field != P_BOX0 && field != P_BOX1 &&
                     field != P_BOX2 && field != P_BOX3 &&
                     field != P_KEY_GRAY && field != P_KEY_GOLD)
                        ) {

                    if (field == P_EXIT)
                    {
                        if (gGameState->exitIsOpen) {
                            setMaterial(COLORS_MATERIAL[GREEN]);
                            glColor3ubv(COLORS[GREEN]);
                        } else {
                            setMaterial(COLORS_MATERIAL[RED]);
                            glColor3ubv(COLORS[RED]);
                        }
                    }

                    glCallList(selectDisplayList + field);
                }
                // Dont draw rotated keys when we are in 3D
                if (is3D && (field == P_KEY_GRAY || field == P_KEY_GOLD)) {
                    glPushMatrix();
                    {
                        glRotatef(getRotationKey(), 0, 1, 0);
                        glCallList(selectDisplayList + field);
                    }
                    glPopMatrix();
                }
            }
            glPopMatrix();
        }
    }

    // We dont need transparent objects when we're in 2D, we call the display list.
    // The transparent objects in 2D are already drawn when were are here.
    if (is3D) {
        drawTransparent(map);
    }

}

/// Draw hunter from logical coordinates
void drawHunterAtPos(Direction hunterDirection, GLboolean is3D) {
    float directionAngle = 0.0f;
    int selectDisplayID = is3D && isNormalMode ? displayListNormalsID : displayListID;
    directionAngleForRotation(&directionAngle, hunterDirection);
    Vector2 absolutePosition = getHunterPosition();

    glPushMatrix();
    {
        // Hunter direction to move, we have to rotate Hunter!
        glTranslatef(absolutePosition.x, 0, absolutePosition.y);
        glRotatef(directionAngle, 0, 1, 0);
        glCallList(selectDisplayID + P_HUNTER);
    }
    glPopMatrix();
};

/// Draw the scene 3d with all rendering lights
void drawScene3D(const GameState gameState) {
    // Global light
    const CGPoint4f lightPos0 = {-1.0f, 1.0f, 1.0f, 0.0f};

    //activate light rendering
    if (gameState->lightRenderingIsEnabled) {
        glEnable(GL_LIGHTING);

        //activate parallel light source
        if (gameState->lightParallelIsEnabled) {
            //activate parallel light
            glLightfv(GL_LIGHT0, GL_POSITION, lightPos0);
            glEnable(GL_LIGHT0);
        }

        //activate hunters spotlight
        if (gameState->cameraMode == VIEW3D_FIRST_PERSON && gameState->spotLightIsEnabled) {
            Vector2 hunterPosition = getAbsoluteCoordinates(gameState->hunterPos.x, gameState->hunterPos.y,
                                                            gameState->map.size.width,
                                                            gameState->map.size.height);

            Vector2 lightPosition = {.x = 0, .y = 0};
            setLightDirection(&lightPosition, gameState->hunterDirection);

            const float distanceHead = 0.5f;

            CGPoint4f lightPos1 = {hunterPosition.x, distanceHead, hunterPosition.y, 1.0f};
            CGVector3f lightDirection1 = {lightPosition.x, -0.25f, lightPosition.y};

            glLightfv(GL_LIGHT1, GL_POSITION, lightPos1);
            glLightfv(GL_LIGHT1, GL_SPOT_DIRECTION, lightDirection1);
            glEnable(GL_LIGHT1);
        }
    }

    drawGameField(gameState->map, GL_TRUE);
    drawHunterAtPos(gameState->hunterDirection, GL_TRUE);

    glDisable(GL_LIGHT0);
    glDisable(GL_LIGHT0);
    glDisable(GL_LIGHT1);
    glDisable(GL_LIGHTING);
}

/// Draw Inventory, when key is collected
void drawInventory(int collectedGrayKeyCount, int collectedGoldKeyCount) {
    const float marginX = 0.2f;
    const float marginY = 0.1f;
    const float width = 0.1f;

    char inventoryGrayKeyAmountText[sizeof("x9")];
    char inventoryGoldKeyAmountText[sizeof("x9")];
    sprintf(inventoryGrayKeyAmountText, "x%d", collectedGrayKeyCount);
    sprintf(inventoryGoldKeyAmountText, "x%d", collectedGoldKeyCount);
    glPushMatrix();
    {
        glTranslatef(-1 + marginX, -1 + marginY, 0);
        glScalef(width, width, 0);
        glCallList(displayListID + P_KEY_GRAY);

    }
    glPopMatrix();

    glColor3ubv(COLORS[WHITE]);
    drawText(-1 + marginX + width, -1 + marginY, inventoryGrayKeyAmountText, GLUT_BITMAP_HELVETICA_10);

    glPushMatrix();
    {
        glTranslatef(-1 + marginX + 2 * width, -1 + marginY, 0);
        glScalef(width, width, 0);
        glCallList(displayListID + P_KEY_GOLD);

    }
    glPopMatrix();
    glutPostRedisplay();
    glColor3ubv(COLORS[WHITE]);
    drawText(-1 + marginX + 3 * width, -1 + marginY, inventoryGoldKeyAmountText, GLUT_BITMAP_HELVETICA_10);
}
/// Draw timer menu
void drawHud(GameState state) {
    const float marginX = 0.5f;
    const float marginY = 0.2f;
    char timeStr[32]; // len('Time:') + some buffer
    sprintf(timeStr, "Time: %d", state->timeLeft);

    drawText(1 - marginX, -1 + marginY, timeStr, GLUT_BITMAP_HELVETICA_18);
}

/// Draw help text to constant
void drawHelpText() {
    glColor3ubv(COLORS[WHITE]);
    const float xOffSet = -1 + 0.3;
    float y = 0.8;
    for (int i = 0; i < sizeof(HELP_TEXT) / sizeof(HELP_TEXT[0]); i++) {
        drawText(xOffSet, y, HELP_TEXT[i], GLUT_BITMAP_HELVETICA_18);
        y -= 0.2;
    }
}

/// Draw the scene 2d
void drawScene2D(const GameState gameState) {

    if (!isHelpActivated()) {
        glPushMatrix();
        {
            glTranslatef(0, 0.1, 0);
            glScalef((float) 0.125, (float) 0.125, 1);
            glRotatef(90, 1, 0, 0);
            drawGameField(gameState->map, GL_FALSE);
            drawHunterAtPos(gameState->hunterDirection, GL_FALSE);
        }
        glPopMatrix();

        if (gameState->collectedGrayKeyCount || gameState->collectedGoldKeyCount) {
            drawInventory(gameState->collectedGrayKeyCount, gameState->collectedGoldKeyCount);
        }

        if (isPauseActivated()) {
            glColor3ubv(COLORS[BLACK]);
            drawCenteredText(0.0f, 0.0f, "Paused", GLUT_BITMAP_HELVETICA_18);
        }

        if (hasWon()) {
            glColor3ubv(COLORS[BLACK]);
            drawCenteredText(0.0f, 0.0f, "YOU WON", GLUT_BITMAP_HELVETICA_18);
            glColor3ubv(COLORS[WHITE]);
        }

        if (isGameOver()) {
            glColor3ubv(COLORS[RED]);
            drawCenteredText(-0.20f, -0.80f, "GAME-OVER", GLUT_BITMAP_HELVETICA_18);
            glColor3ubv(COLORS[WHITE]);
        }

    } else {
        drawHelpText();
    }

    glColor3ubv(COLORS[WHITE]);
    drawHud(gameState);
}

/// Initialise the lights
static void initLight(void) {
    /* Farbe der ersten Lichtquelle */
    CGPoint4f lightColor0[3] =
            {{0.1f, 0.1f, 0.1f, 1.0f},
             {1.0f, 1.0f, 1.0f, 1.0f},
             {1.5f, 1.5f, 1.5f,
                                1.0f}
            };

    /* Farbe der zweiten Lichtquelle */
    CGPoint4f lightColor1[3] =
            {{0.0f, 0.0f, 0.0f, 1.0f},
             {2.0f, 2.0f, 2.0f, 1.0f},
             {3.0f, 3.0f, 3.0f,
                                1.0f}
            };

    //angle
    GLdouble lightCutoff1 = 50.0f;
    //spread
    GLdouble lightExponent1 = 50.0f;

    glLightfv(GL_LIGHT0, GL_AMBIENT, lightColor0[0]);
    glLightfv(GL_LIGHT0, GL_DIFFUSE, lightColor0[1]);
    glLightfv(GL_LIGHT0, GL_SPECULAR, lightColor0[2]);

    glLightfv(GL_LIGHT1, GL_AMBIENT, lightColor1[0]);
    glLightfv(GL_LIGHT1, GL_DIFFUSE, lightColor1[1]);
    glLightfv(GL_LIGHT1, GL_SPECULAR, lightColor1[2]);


    glLightf(GL_LIGHT1, GL_SPOT_CUTOFF, (GLfloat) lightCutoff1);
    glLightf(GL_LIGHT1, GL_SPOT_EXPONENT, (GLfloat) lightExponent1);

    glLightf(GL_LIGHT1, GL_CONSTANT_ATTENUATION, 0.4);
    glLightf(GL_LIGHT1, GL_LINEAR_ATTENUATION, 0.4);
    glLightf(GL_LIGHT1, GL_QUADRATIC_ATTENUATION, 0.4);
}

/// Initialise the scene
int initScene(void) {
    /* Setzen der Farbattribute */
    /* Hintergrundfarbe */
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    /* Zeichenfarbe */
    glColor3f(1.0f, 1.0f, 1.0f);
    // glShadeModel(GL_SMOOTH);


    /* Z-Buffer-Test aktivieren */
    glEnable(GL_DEPTH_TEST);

    /* Polygonrueckseiten nicht anzeigen */
    glCullFace(GL_BACK);
    glEnable(GL_CULL_FACE);
    initLight();


    /* Normalen nach Transformationen wieder auf die
     * Einheitslaenge bringen */
    glEnable(GL_NORMALIZE);

    glEnable(GL_LINE_SMOOTH);
    //TODO maybe turn off.

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    ///Set line width once.
    glLineWidth(1.0f);

    return (glGetError() == GL_NO_ERROR);
}

/// Initialise the display lists
void initDisplayLists() {
    displayListID = glGenLists(P_MAX);
    displayListNormalsID = glGenLists(P_MAX);
    for (int i = 0; i < P_MAX; i++) {
        glNewList(displayListID + i, GL_COMPILE);
        fieldTypeDrawFunctions[i]();
        glEndList();
    }
    setNormalMode(1);
    for (int i = 0; i < P_MAX; i++) {
        glNewList(displayListNormalsID + i, GL_COMPILE);
        fieldTypeDrawFunctions[i]();
        glEndList();
    }

    setNormalMode(0);

}

/// Free display lsit
void freeDisplayList(void) {
    glDeleteLists(displayListID, P_MAX);
}

/// Toggles wireframe
void toggleWireframeMode(void) {
    isWireframe = !isWireframe;

    GLenum glMode = isWireframe ? GL_LINE : GL_FILL;
    glPolygonMode(GL_FRONT_AND_BACK, glMode);
}

