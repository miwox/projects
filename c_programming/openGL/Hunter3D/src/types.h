#ifndef UEB03_TYPES_H
#define UEB03_TYPES_H

#define UNUSED(x) (void)(x)

typedef struct {
    int x, y;
} IntVector2;

typedef struct {
    float x, y, z;
} Vector3;

typedef struct {
    float pitch; // oben unten
    float yaw;   // links rechts
    float roll;  // nicht nutzen!
} Rotation3;

typedef struct {
    float x, y;
} Vector2;

typedef struct {
    // https://i.pinimg.com/736x/04/52/24/0452247b2f84cb86841d2398a9bcc6d9.jpg
    /// Angles are in radians
    float azimuthalAngle;   // up, down
    float polarAngle;       // left, right
    float distanceFromCenter;
} CenteredCamera;

typedef struct {
    unsigned int width, height;
} IntSize2;

/// Values of material.
typedef struct {
    float ambient[4];
    float diffuse[4];
    float specular[4];
    float shininess[1];
} Material;

typedef enum {
    WHITE,
    RED,
    GREEN,
    BLUE,
    YELLOW,
    ORANGE,
    CYAN,
    GRAY,
    BROWN,
    BLACK,
    COLOR_MAX,
} COLOR;

#define FIELD_INDEX_AT_POS(map, x, y) ((x) + ((y) * (map).size.width))
#define FIELD_AT_POS(map, x, y) ((map).fields[FIELD_INDEX_AT_POS(map, x, y)])

/*
typedef struct {
    int isEnabled;
    Vector3 position;
    Rotation3 rotation;
    COLOR color;
    // TODO TYPE(SPOT, GLOBAL, DIRECTION)
} LightSource;
#define LIGHT_SOURCE_COUNT 3
*/

typedef enum {
    VIEW3D_FREE_VIEW,
    VIEW3D_FIRST_PERSON,
} View3dCameraMode;

typedef enum {
    LEFT,
    RIGHT,
    UP,
    DOWN,
} Direction;

typedef enum {
    P_BLACK,
    P_FREE,
    P_WALL,
    P_BOX0, // Box with no moves
    P_BOX1, // Box with 1 move
    P_BOX2, // Box with 2 moves
    P_BOX3, // Box with 3 moves
    P_TRAP,
    P_TRAP_ACTIVATED,
    P_EXIT,
    P_KEY_GRAY,
    P_KEY_GOLD,
    P_TREASURE_CLOSED,
    P_TREASURE_OPENED,
    P_DOOR,
    P_HUNTER,
    P_MAX,
} FIELD_TYPE;

typedef FIELD_TYPE* GameField;

#define FIELD_INDEX_AT_POS(map, x, y) ((x) + ((y) * (map).size.width))
#define FIELD_AT_POS(map, x, y) ((map).fields[FIELD_INDEX_AT_POS(map, x, y)])

typedef struct {
    IntSize2 size;
    GameField fields; // All tiles in the game. To access see FIELD_AT_POS
} Map;

typedef unsigned int LevelNumber;
typedef struct {
    float stepProgress; // From 0 to 1. Controls all animations in the scene (push progress, hunter walk animation)

    Map map;

    View3dCameraMode cameraMode;
    CenteredCamera freeViewCamera;
    //LightSource lightSource[LIGHT_SOURCE_COUNT];
    IntVector2 hunterPos;
    Direction hunterDirection;
    int renderHunterHead;

    unsigned int collectedGoldKeyCount;
    unsigned int collectedGrayKeyCount;

    int timeLeft;
    LevelNumber levelNumber;

    int exitIsOpen; // 0 when the exit is closed
    int lightRenderingIsEnabled;
    int lightParallelIsEnabled;
    int spotLightIsEnabled;
} *GameState;

/// All the keys that can change the state
typedef enum {
    // Camera keys
    KEY_CAMERA_ZOOM_IN      = (1 << 0),
    KEY_CAMERA_ZOOM_OUT     = (1 << 1),
    KEY_CAMERA_LEFT         = (1 << 2),
    KEY_CAMERA_RIGHT        = (1 << 3),
    KEY_CAMERA_UP           = (1 << 4),
    KEY_CAMERA_DOWN         = (1 << 5),

    // Hunter/Wireframe/Pause intention input will be directly inserted into logic.h, since they dont depend on FPS
} KeyboardInput;

typedef enum {
    // DrawScene Debug
    DRAW_WALL = 1, // equals the pressed button.
    DRAW_DOOR,
    DRAW_GOLDEN_KEY,
    DRAW_GRAY_KEY,
    DRAW_TRAP_ACTIVATED,
    DRAW_EXIT,
    DRAW_MAX

   /* KEY_CAMERA_LEFT         = (1 << 2),
    KEY_CAMERA_RIGHT        = (1 << 3),
    KEY_CAMERA_UP           = (1 << 4),
    KEY_CAMERA_DOWN         = (1 << 5),
    */

} DebugInput;

typedef struct {
Vector3 cameraPosition;
Vector3 lookingDirection;
Vector3 upDirection;
} LookAt;

#endif
