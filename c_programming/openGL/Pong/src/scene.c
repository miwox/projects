#include "scene.h"
#include "logic.h"
#include "io.h"
#include "advancedMath.h"
#ifdef __APPLE__
#include <GLUT/glut.h>
#include <stdlib.h>
#else
#include <GL/glut.h>
#endif

#define DEFAULT_CIRCLE_SEGMENT_COUNT 350

static GLubyte COLORS[COLOR_MAX][3] = {
    {255,255,255},  // WHITE
    {255,0,  0},    // RED
    {0,  255,0},    // GREEN
    {0,  0,  255},  // BLUE
    {255,255,0},    // YELLOW
    {255,0,  255},  // ORANGE
    {0,  255,255},  // CYAN
};

/// Calls glVertex2f for a given vector
void glVertexVector2(Vector2 vec2) {
    glVertex2f(vec2.x, vec2.y);
}

/*
 * Our coordinate system goes from 0 to 1.
 * Advantages: Barriers have a width of 1 not 2
 */

/// Draws a identity rectangle (each side is 1 long)
void drawIdentityRectangle() {
    glBegin(GL_QUADS);
    {
        glVertex2f(0.5f, 0.5f);
        glVertex2f(-0.5f, 0.5f);
        glVertex2f(-0.5f, -0.5f);
        glVertex2f(0.5f, -0.5f);
    }
    glEnd();
}

/// Draws a rectangle at a given center pos with given width and height
void drawRectangle(float centerX, float centerY, float width, float height, float rotation) {
    glPushMatrix();
    {
        glTranslatef((centerX * 2) - 1, (centerY * 2) - 1, 0);
        glRotatef(rotation, 0, 0, 1);
        glScalef(width * 2, height * 2, 0);
        drawIdentityRectangle();
    }
    glPopMatrix();
}

/// Draws an star of radius 1
void drawIdentityCircle(int numSegments) {
    glBegin(GL_POLYGON);
    for (int seg = 0; seg < numSegments; seg++) {
        float rad = ((float)seg) / ((float)numSegments) * M_TAU;
        Vector2 point = unitVectorInRad(rad);
        glVertex2f(point.x, point.y);
    }
    glEnd();
}

/// Draws a circle at a given center position and radius
void drawCircle(float centerX, float centerY, float radius, int numSegments) {
    glPushMatrix();
    {
        glTranslatef((centerX * 2) - 1, (centerY * 2) - 1, 0);
        glScalef(radius * 2, radius * 2, 0);
        drawIdentityCircle(numSegments);
    }
    glPopMatrix();
}

/// Draws a given paddle. If controlled by AI it gets a different color
void drawPaddle(const PaddleInfo* paddle, int isControlledByAi) {
    static const GLfloat aiColor[3] = {0.9f,0,0};
    static const GLfloat humanColor[3] = {1,1,1};

    glColor3fv(isControlledByAi ? aiColor : humanColor);

    drawRectangle(paddle->pos.x, paddle->pos.y, paddle->size.width, paddle->size.height, paddle->rotation);
}

/// Draws the left and right paddle
void drawPaddles(void) {
    drawPaddle(&gGameState.paddleLeft, IS_LEFT_SIDE_CONTROLLED_BY_AI);
    drawPaddle(&gGameState.paddleRight, IS_RIGHT_SIDE_CONTROLLED_BY_AI);
}

/// Draws the top and bottom barrier
void drawBarriers(void) {
    // Top barrier
    glColor3f(1.0f, 1.0f, 1.0f);
    drawRectangle(0.5f, 1-(gGameState.topBarrierHeight / 2), 1, gGameState.topBarrierHeight, 0);

    // Bottom barrier
    glColor3f(1.0f, 1.0f, 1.0f);
    drawRectangle(0.5f, gGameState.bottomBarrierHeight / 2, 1, gGameState.bottomBarrierHeight, 0);
}

/// Draws a triangle with
void drawIdentityTriangle(){
    glBegin(GL_TRIANGLES);
    {
        glVertex2f(-0.5f, -(sinf(degToRad(60)))/3);
        glVertex2f(0.5f, -(sinf(degToRad(60)))/3);
        glVertex2f(0.0f, (2 * sinf(degToRad(60)))/3);
    }
    glEnd();
}

/// Draws a triangle at a given position
void drawTriangle(float centerX, float centerY, float circleRadius, float rotation) {
    // https://de.wikipedia.org/wiki/Gleichseitiges_Dreieck#Berechnung_und_Konstruktion
    float scale = circleRadius * sqrtf(3) * 2;

    glPushMatrix();
    {
        glTranslatef((centerX*2)-1, (centerY*2)-1, 0);
        glRotatef(rotation,0,0,1);
        glScalef(scale, scale, 0);

        drawIdentityTriangle();
    }
    glPopMatrix();
}

/// Draws an star of radius 1
void drawIdentityStar(void) {
    const float middleLength = 0.5f;
    const float outerLength = 1.0f;

    const unsigned int cornerCount = 8;

    // calculating this ahead so we dont need to recalculate it in the loop
    const unsigned int totalSegmentCount = cornerCount * 2;
    const float radPerSegment = M_TAU / ((float) totalSegmentCount);

    glBegin(GL_LINE_LOOP);
    for (unsigned int i = 0; i < totalSegmentCount; i++) {
        float segmentRad = ((float) i) * radPerSegment;
        float length = i % 2 == 0 ? middleLength : outerLength;
        Vector2 segmentVec = setVectorLength(unitVectorInRad(segmentRad), length);

        glVertexVector2(segmentVec);
        glVertexVector2(ZeroVector);
        glVertexVector2(segmentVec); // Technically we draw from 0,0 to segmentVec line twice
    }
    glEnd();
}

/// Draws a star at a given position
void drawStar(float centerX, float centerY, float radius, float rotation) {
    glPushMatrix();
    {
        glTranslatef((centerX*2) - 1, (centerY*2) - 1, 0);
        glRotatef(rotation,0,0,1);
        glScalef(radius * 2,radius * 2, 0);

        drawIdentityStar();
    }
    glPopMatrix();
}

/// Draws a given ball
void drawBall(const BallInfo* ball) {
        glColor3ubv(COLORS[ball->color]); // Set ball color
    switch(ball->type) {
        case TRIANGLE:
            drawTriangle(ball->pos.x, ball->pos.y, ball->radius, ball->rotation);
            break;
        case STAR:
            drawStar(ball->pos.x, ball->pos.y, ball->radius, ball->rotation);
            break;
        case CIRCLE:
        default:
            drawCircle(ball->pos.x, ball->pos.y, ball->radius, DEFAULT_CIRCLE_SEGMENT_COUNT);
    }
}

/// Returns the length of a given text
float textWidth(const char* text, void* font) {
    unsigned int totalPixelWidth = 0;
    for (const char* cPtr = text; *cPtr; cPtr++) {
        totalPixelWidth += glutBitmapWidth(font, *cPtr);
    }
    return (float)totalPixelWidth / (float)glutGet(GLUT_WINDOW_WIDTH);
}

/// Draws text on the screen; going right from the start pos
void drawText(float startX, float startY, const char* text, void* font) {
    glRasterPos2d(startX, startY);

    for (const char* cPtr = text; *cPtr; cPtr++) {
        glutBitmapCharacter(font, *cPtr); // Automatically updates the position
    }
}

/// Draws text on the screen; but centers the content
void drawCenteredText(float centerX, float centerY, const char* text, void* font) {
    drawText(centerX - textWidth(text, font), centerY, text, font);
}

/// Draws a debug scene to test shapes
void drawDebugScene(void) {

    glColor3ub(50, 50, 50);
    drawRectangle(0.25f, 0.75f, 0.5f, 0.5f,0);

    glColor3ub(20, 20, 20);
    drawRectangle(0.25f, 0.25f, 0.5f, 0.5f,0);

    glColor3ub(50, 50, 50);
    drawRectangle(0.75f, 0.25f, 0.5f, 0.5f,0);

    glColor3ub(20, 20, 20);
    drawRectangle(0.75f, 0.75f, 0.5f, 0.5f,0);

    static float rotation = 0.1f;
    rotation += 1;

    static float radius = 0.1f;
    static int radiusDirection = 1;
    const float radiusStep = 0.004f;

    radius += radiusStep * (float)radiusDirection;
    if (radius >= 0.55f || radius <= 0.1f) {
        radiusDirection *= -1;
    }

    Vector2 center = {0.6f, 0.3f};

    {
        glColor3ubv(COLORS[BLUE]);
        drawCircle(center.x, center.y, radius, DEFAULT_CIRCLE_SEGMENT_COUNT);

        glColor3ubv(COLORS[GREEN]);
        drawTriangle(center.x, center.y, radius, rotation);

        glColor3ubv(COLORS[CYAN]);
        drawRectangle(center.x, center.y, radius * sqrtf(2), radius * sqrtf(2), rotation);

        glColor3ubv(COLORS[ORANGE]);
        drawStar(center.x, center.y, radius, rotation);
    }

    glColor3ubv(COLORS[RED]);
    drawCircle(center.x, center.y, 0.01f, DEFAULT_CIRCLE_SEGMENT_COUNT);

    glFlush();
}

/// Draws the whole game state on the screen
void drawScene(void) {
    glClear(GL_COLOR_BUFFER_BIT);

    if (DRAW_DEBUG) {
        drawDebugScene();
        return;
    }

    drawBall(&gGameState.ball);

    drawPaddles();
    drawBarriers();

    if (IS_PAUSED) {
        drawCenteredText(0.0f, 0.0f, "Paused", GLUT_BITMAP_HELVETICA_18);
    }

}
