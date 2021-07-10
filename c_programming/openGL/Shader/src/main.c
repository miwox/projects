#include <stdio.h>
#include <stddef.h>
#include <stdlib.h>
#include "types.h"
#include "vertexWork.h"
#include "utility.h"

#define IMITATE_INDEX_START ((void *) 0)
#define ELEVATION_MIN 0
#define ELEVATION_MAX 20
#define STB_IMAGE_STATIC
#define STB_IMAGE_IMPLEMENTATION
#include <stb/stb_image.h>

#define RADIUS 4
#define ROTATED_X(T)((RADIUS) * sinf((T)))
#define ROTATED_Z(T)((RADIUS) * cosf((T)))

#define MESH_SIDE_LENGTH 8
/// 2^n always
#define MESH_SIZE pow(2,MESH_SIDE_LENGTH)

#define ROTATION_SPEED 0.2f
#define X_POS 0
#define Y_POS 1
#define Z_POS 2
#define OFFSET_Y_CAMERA 0
#define OFFSET_Y_LIGHT RADIUS


/// buffer save vertices array
static OBJECT g_arrayVerticesBuffer;
/// buffer save indices array
static OBJECT g_arrayIndicesBuffer;
/// vertex array object, save attribute pointer
static OBJECT g_vertexArrayObject;
/// program object to render
static OBJECT g_program;
/// texture object
static OBJECT g_heightMap;
/// texture object
static OBJECT g_worldMap;
/// location of elevation
static LOCATION g_locationElevation;
/// location of shader var ModelView
static LOCATION g_locationModelViewMatrix;
/// location shader var Texture in color.frag
static LOCATION g_locationTexHeightMap;
/// location shader var Texture in color.frag
static LOCATION g_locationTexWorldMap;
/// location of which Texture is currently used
static LOCATION g_locationTextureId;
/// location of which Texture is currently used
static LOCATION g_locationEffectId;
/// location of mesh size
static LOCATION g_locationMeshSize;
/// location of mesh size
static LOCATION g_locationElapsedTime;
/// location camera position
static LOCATION g_locationCameraPosition;
/// location light position
static LOCATION g_locationLightPosition;
/// location light state
static LOCATION g_locationLightingState;
/// shared int for lighting
static SHARED_INT  g_lightingState = 1;
/// shared raising used as insput for shader
static SHARED_FLOAT g_elevation;
/// shared texture id for fragment shader
static SHARED_TEXTURE_ID g_textureId = ST_WORLD_MAP;
/// shared texture id for fragment shader
static SHARED_EFFECT_ID g_effectId = EF_NONE;
/// wireframe
static GLboolean isWireframe = GL_FALSE;
/// global vertices
static Vertex* gVertices;
/// global indices
static GLuint* gIndices;
/// variable for user
// TODO build in animation state
static int isAnimated = 1;
int g_lastRenderTime = 0;

float animationProgress = 0;

/// draw the scene
static void drawScene(void) {
    float viewMatrix[16];
    GLfloat* cameraPosition = malloc(3 * sizeof(GLfloat));
    cameraPosition[X_POS] = ROTATED_X(animationProgress);
    cameraPosition[Y_POS] = OFFSET_Y_CAMERA;
    cameraPosition[Z_POS] = ROTATED_Z(animationProgress);

    GLfloat* lightPosition = malloc(3 * sizeof(GLfloat));
    lightPosition[X_POS] = ROTATED_X(animationProgress);
    lightPosition[Y_POS] = OFFSET_Y_LIGHT;
    lightPosition[Z_POS] = ROTATED_Z(animationProgress);

    lookAt(cameraPosition[X_POS], cameraPosition[Y_POS], cameraPosition[Z_POS], 0, 0, 0, 0, 1, 0, viewMatrix);

    /* Aktivieren des Programms. Ab jetzt ist die Fixed-Function-Pipeline
     * inaktiv und die Shader des Programms aktiv. */
    glUseProgram(g_program);

    /* Übermitteln der View-Matrix an den Shader.
     * OpenGL arbeitet intern mit Matrizen in column-major-layout. D.h.
     * nicht die Reihen, sondern die Spalten liegen hintereinander im
     * Speicher. Die Funktionen zur Erzeugen von Matrizen in diesem
     * Programm berücksichtigen dies. Deswegen müssen die Matrizen nicht
     * transponiert werden und es kann GL_FALSE übergeben werden. Beim
     * Schreiben eigener Matrix-Funktionen oder beim Verwenden von
     * Mathematik-Bibliotheken muss dies jedoch berücksichtigt werden. */
    glUniformMatrix4fv((GLint) g_locationModelViewMatrix, 1, GL_FALSE, viewMatrix);

    /// share data with shader
    glUniform1f((GLint) g_locationElevation, g_elevation);
    glUniform1i((GLint) g_locationTextureId, g_textureId);
    glUniform1i((GLint) g_locationEffectId, g_effectId);
    glUniform1i((GLint) g_locationLightingState,  g_lightingState);


    glUniform1f((GLint) g_locationMeshSize, (float) MESH_SIZE);
    glUniform1f((GLint) g_locationElapsedTime, animationProgress);
    glUniform3fv((GLint) g_locationCameraPosition, 1, cameraPosition);
    glUniform3fv((GLint) g_locationLightPosition, 1, lightPosition);




    /* Übergeben der Textur an den Shader.
     * Texturen werden nicht direkt an den Shader übergeben, sondern
     * zuerst an eine Textureinheit gebunden. Anschließend wird dem
     * Programm nur der Index der Textureinheit übergeben. */

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, g_heightMap);
    glUniform1i((GLint) g_locationTexHeightMap, 0);

    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, g_worldMap);
    glUniform1i((GLint) g_locationTexWorldMap, 1);

    glBindVertexArray(g_vertexArrayObject);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, g_arrayIndicesBuffer);
    glDrawElements(GL_TRIANGLES, MESH_SIZE_TO_TRIANGLE_INDICES(MESH_SIZE), GL_UNSIGNED_INT, IMITATE_INDEX_START);

    glBindVertexArray(0);
    glUseProgram(0);
    free(cameraPosition);
    free(lightPosition);
}

/// init the scene
static void
initScene(void) {
    {
        // generate buffer object names: generates 1 buffer object names in buffers &g_arrayVerticesBuffer
        glGenBuffers(1, &g_arrayVerticesBuffer);
        // bind a named buffer object: bind g_arrayVerticesBuffer to target GL_ARRAY_BUFFER (Vertex attributes)
        glBindBuffer(GL_ARRAY_BUFFER, g_arrayVerticesBuffer);
        // void glBufferData, creates and initializes a buffer object's data store currently bound to target

        glBufferData(GL_ARRAY_BUFFER, sizeof(Vertex) * MESH_SIZE_TO_VERTICES(MESH_SIZE), gVertices, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glGenBuffers(1, &g_arrayIndicesBuffer);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, g_arrayIndicesBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(GLuint) * MESH_SIZE_TO_TRIANGLE_INDICES(MESH_SIZE), gIndices,
                     GL_STATIC_DRAW);




        /* Erstellen eines Vertex-Array-Objektes (VAO).
         * Damit die OpenGL-Implementierung weiß welche Daten der Pipeline
         * bereitgestellt werden müssen, werden Attribut-Pointer gesetzt.
         * Um mehrere Datenquellen (unterschiedliche Meshes) zu verwalten
         * können die Attribut-Pointer in VAOs gruppiert werden.
         *
         * Die Erzeugung von VAOs geschieht prinzipiell genauso wie bei
         * Buffern oder anderen OpenGL-Objekten.
         * */

        /* Erstellen eines Buffer-Objektes.
         * In modernem OpenGL werden alle Vertex-Daten in Buffer-Objekten
         * gespeichert. Dabei handelt es sich um Speicherbereiche die von
         * der OpenGL-Implementierung verwaltet werden und typischerweise
         * auf der Grafikkarte angelegt werden.
         *
         * Mit der Funktion glGenBuffers können Namen für Buffer-Objekte
         * erzeugt werden. Mit glBindBuffer werden diese anschließend
         * erzeugt, ohne jedoch einen Speicherbereich für die Nutzdaten
         * anzulegen. Dies geschieht nachfolgend mit einem Aufruf der
         * Funktion glBufferData.
         *
         * Um mit Buffer-Objekten zu arbeiten, mussen diese an Targets
         * gebunden werden. Hier wird das Target GL_ARRAY_BUFFER verwendet.
         *
         * Der OpenGL-Implementierung wird zusätzlich ein Hinweis mitgegeben,
         * wie die Daten eingesetzt werden. Hier wird GL_STATIC_DRAW
         * übergeben. OpenGL kann diesen Hinweis nutzen, um Optimierungen
         * vorzunehmen.
         */

        // generate 1 vertex array object name
        glGenVertexArrays(1, &g_vertexArrayObject);

        // binds the vertex array object with name array
        glBindVertexArray(g_vertexArrayObject);

        /* Die Pointer werden immer in den Buffer gesetzt, der am
        * GL_ARRAY_BUFFER-Target gebunden ist! */

        glBindBuffer(GL_ARRAY_BUFFER, g_arrayVerticesBuffer);


        /* Im Vertex-Shader existieren folgende Zeilen:
         * > layout (location = 0) in vec4 vPosition;
         * > layout (location = 1) in vec2 vTexCoord;
         *
         * Beim Aufruf einen Draw-Command, müssen diesen beiden Attributen
         * Daten bereitgestellt werden. Diese sollen aus dem oben erstellten
         * Buffer gelesen werden. Dafür müssen zwei Attribut-Pointer aktiviert
         * und eingerichtet werden. */

        const GLuint positionLocation = 0;
        const GLuint texCoordLocation = 1;

        // Enable or disable a generic vertex attribute array
        glEnableVertexAttribArray(positionLocation);

        // specify the location and data format of the array of generic vertex attributes
        glVertexAttribPointer(
                positionLocation,                  // location in shader
                3,                                 // dimension
                GL_FLOAT,                          // data type in buffer
                GL_FALSE,                          // no normalize
                sizeof(Vertex),                    // offset to next vertex
                (void*) offsetof(Vertex, x));      // offset to first vertex

        // attribute pointer for texture
        glEnableVertexAttribArray(texCoordLocation);
        glVertexAttribPointer(
                texCoordLocation,                  // location in shader
                2,                                 // dimension
                GL_FLOAT,                          // data type in buffer
                GL_FALSE,                          // no normalize
                sizeof(Vertex),                    // offset to next vertex
                (void*) offsetof(Vertex, s));      // offset to first vertex

        // to find errors better unbind the vertex array
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    {
        // build shader program
        g_program = createProgram("../content/shaders/color.vert", "../content/shaders/color.frag");

        //  request location in shader to share data with shader, very hard operation. Use it wise.
        //  returns an integer that represents
        //  the location of a specific uniform variable within a program object.
        g_locationModelViewMatrix = glGetUniformLocation(g_program, "ModelView");
        g_locationElevation = glGetUniformLocation(g_program, "Elevation");
        g_locationTexHeightMap = glGetUniformLocation(g_program, "TextureHeightMap");
        g_locationTexWorldMap = glGetUniformLocation(g_program, "TextureWorldMap");
        g_locationTextureId = glGetUniformLocation(g_program, "TextureId");
        g_locationEffectId = glGetUniformLocation(g_program, "EffectId");
        g_locationMeshSize = glGetUniformLocation(g_program, "MeshSize");
        g_locationElapsedTime = glGetUniformLocation(g_program, "ElapsedTime");
        g_locationLightPosition = glGetUniformLocation(g_program, "LightPosition");
        g_locationCameraPosition = glGetUniformLocation(g_program, "CameraPosition");
        g_locationLightingState = glGetUniformLocation(g_program, "LightState");

        // DEBUG
        printf("ModelView has 'location': %i\n", g_locationModelViewMatrix);
        printf("EffectId has 'location': %i\n", g_effectId);
        printf("Elevation has 'location': %i\n", g_locationElevation);
        printf("TextureHeightMap has 'location': %i\n", g_locationTexHeightMap);
        printf("TextureWorldMap has 'location': %i\n", g_locationTexWorldMap);
        printf("TextureId has 'location': %i\n", g_locationTextureId);
        printf("MeshSize has 'location': %i\n", g_locationMeshSize);
        printf("ElapsedTime has 'location': %i\n", g_locationElapsedTime);
        printf("LightPosition has 'location': %i\n", g_locationLightPosition);
        printf("CameraPosition has 'location': %i\n", g_locationCameraPosition);


    }

    {
        float projectionMatrix[16];

        perspective(60, (float) glutGet(GLUT_WINDOW_WIDTH) / glutGet(GLUT_WINDOW_HEIGHT), 0.01f, 100.0f,
                    projectionMatrix);

        glUseProgram(g_program);
        // modifies the value of a uniform variable or a uniform variable array.
        // The location of the uniform variable to be modified is specified by location,
        // which should be a value returned by glGetUniformLocation.
        // uniform variable is in shader
        // projection matrix will not change, so modifying it once is enough.
        glUniformMatrix4fv(glGetUniformLocation(g_program, "Projection"), 1, GL_FALSE, projectionMatrix);
        glUseProgram(0);
    }

    {
        // load texture
        int width, height, comp;
        GLubyte* data = stbi_load("../content/textures/height_map.jpg", &width, &height, &comp, 4);

        // load height map texture
        glGenTextures(1, &g_heightMap);
        glBindTexture(GL_TEXTURE_2D, g_heightMap);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
        stbi_image_free(data);

        // load world map texture
        GLubyte* data_2 = stbi_load("../content/textures/world_map.png", &width, &height, &comp, 4);
        glGenTextures(1, &g_worldMap);
        glBindTexture(GL_TEXTURE_2D, g_worldMap);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data_2);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
        stbi_image_free(data_2);
    }
}

/// init openGl
static int
initOpenGL(void) {
    glClearColor(0, 0, 0, 0);
    glClearDepth(1);
    glEnable(GL_DEPTH_TEST);
    return 1;
}

void requestAnimationFrame(void);

/// Will be called for each frame (60 times a sec or so). Will mark scene as dirty. And request a new animation frame
void onAnimationFrame(int value) {
    ((void)value);
    glutPostRedisplay(); // Mark scene as dirty. Glut will trigger onDisplay
    requestAnimationFrame();
}

void updateAnimation(float deltaInSec) {
    if (isAnimated) {
        animationProgress += deltaInSec * ROTATION_SPEED;
    }
}

/// Queues a new animation frame and will call onAnimationFrame as soon as the frame should be rendered
void requestAnimationFrame(void) {
    const int anticipatedFps = 60;
    int timeNow = glutGet(GLUT_ELAPSED_TIME);
    float timeDeltaSec = ((float) (timeNow - g_lastRenderTime)) / 1000.0f;
    g_lastRenderTime = timeNow;
    updateAnimation(timeDeltaSec);
    glutTimerFunc(1000 / anticipatedFps, onAnimationFrame, 0);
}

/// draw callback
static void
cbDisplay(void) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    drawScene();
    glutSwapBuffers();
}


void toggleWireframeMode(void);

static void freeState() {
    free(gIndices);
    free(gVertices);
}

/// cb handle keyboard
static void
cbKeyboard(unsigned char key, int x, int y) {
    (void) x;
    (void) y;
#define ESC 27

#define TOGGLE(field, test, default) field = field == test ? default : test;

    switch (key) {
        case 'q':
        case 'Q':
        case ESC:
            freeState();
            exit(0);
            break;
        case '+':
            g_elevation += 0.1f;
            g_elevation = CLAMP(g_elevation, ELEVATION_MIN, ELEVATION_MAX);
            break;
        case '-':
            g_elevation -= 0.1f;
            g_elevation = CLAMP(g_elevation, ELEVATION_MIN, ELEVATION_MAX);
            break;

        case 'p':
        case 'P':
            isAnimated = !isAnimated;
            break;
        case 'W':
        case 'w':
            toggleWireframeMode();
            break;
        case 'h':
        case 'H':
            printf("Help:\n");
            printf("    L: Toggle light\n");
            printf("Texture:\n");
            printf("    0: No texture\n");
            printf("    1: show heightmap as texture\n");
            printf("    2: show worldmap as texture\n");
            printf("Effect:\n");
            printf("    3: Disable effects\n");
            printf("    4: Toggle cartoon effect\n");
            printf("    5: Toggle gray effect\n");
            printf("    6: Toggle sepia effect\n");
            break;

        case 'L':
        case 'l':
            g_lightingState = !g_lightingState;
            break;

        case '0':
            g_textureId = ST_NONE;
            break;
        case '1':
            TOGGLE(g_textureId, ST_HEIGHT_MAP, ST_NONE);
            break;
        case '2':
            TOGGLE(g_textureId, ST_WORLD_MAP, ST_NONE);
            break;
        case '3':
            g_effectId = EF_NONE;
            break;
        case '4':
            TOGGLE(g_effectId, EF_CARTOON, EF_NONE);
            break;
        case '5':
            TOGGLE(g_effectId, EF_GREY, EF_NONE);
            break;
        case '6' :
            TOGGLE(g_effectId, EF_SEPIA, EF_NONE);
            break;

        default:
            printf("For Help press H,h\n");
            break;
    }

#undef TOGGLE
}

/// register callbacks
static void
registerCallbacks(void) {
    glutDisplayFunc(cbDisplay);
    glutIgnoreKeyRepeat(1);
    glutKeyboardFunc(cbKeyboard);

    requestAnimationFrame();
}


/// create window with given size and title
static int
createWindow(char* title, int width, int height) {
    int windowID = 0;
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
    glutInitWindowSize(width, height);
    glutInitWindowPosition(0, 0);

    glutInitContextVersion(3, 3);
    glutInitContextProfile(GLUT_CORE_PROFILE);
    windowID = glutCreateWindow(title);
    return windowID;
}

/// toggles wireframe
void toggleWireframeMode(void) {
    isWireframe = !isWireframe;
    GLenum glMode = isWireframe ? GL_LINE : GL_FILL;
    glPolygonMode(GL_FRONT_AND_BACK, glMode);
}

/// init the mesh of the field
void initMesh() {
    gVertices = createBasicVertices(MESH_SIZE);
    gIndices = createIndicesTriangles(MESH_SIZE);
}

/// main program
int
main(int argc, char** argv) {
    GLenum error;
    glutInit(&argc, argv);
    if (!createWindow("I love Shader", 1000, 1000)) {
        fprintf(stderr, "Error 5****!");
        exit(1);
    }

    glewExperimental = GL_TRUE;
    error = glewInit();
    if (error != GLEW_OK) {
        fprintf(stderr, "Error 5****!");
        exit(1);
    }

    initOpenGL();
    registerCallbacks();
    initMesh();
    initScene();
    glutMainLoop();

    return 0;
}
