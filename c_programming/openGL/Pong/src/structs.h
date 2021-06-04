#ifndef UEB01_STRUCTS_H
#define UEB01_STRUCTS_H

#define UNUSED(x) (void)(x)

typedef struct {
    float x, y;
} Vector2;

const static Vector2 ZeroVector = { 0, 0 };

typedef struct {
    float width, height;
} Size2;

typedef struct {
    /// Indicates where center of the paddle is,
    /// x float from [(0+width/2) to (1-width/2)], where 0 is the leftmost position
    /// y float from [((0+height/2)+topBarrierHeight) to ((1-height/2)-bottomBarrierHeight)], where 1 is the upmost position
    Vector2 pos;

    /// Indicates how big the paddle is
    /// width float from [0 to 1], where 1 means its the full (window) size
    /// height float from [0 to 1], where 1 means its the full (window) size
    Size2 size;

    /// Counter clockwise rotation of the paddle in degree, Float [0, 360), where 0 means that the paddle is in the straight upright
    float rotation;
} PaddleInfo;

typedef enum {
    CIRCLE,
    TRIANGLE,
    STAR,
    BALL_TYPE_MAX,
} BALL_TYPE;

typedef enum {
    WHITE,
    RED,
    GREEN,
    BLUE,
    YELLOW,
    ORANGE,
    CYAN,
    COLOR_MAX
} COLOR;

typedef struct {
    Vector2 pos;
    Vector2 momentum; // normed movement vector
    float speed;

    /// float from [0 to 0.5] where 0.5 means the ball is fully touching the sides of the screen
    float radius;

    /// Counter clockwise rotation of the ball from (0 to 360]
    float rotation;

    /// The amount that the ball hit a paddle, starts with 0
    int hits; // Needed to change the color
    BALL_TYPE type;
    COLOR color;
} BallInfo;

typedef struct {
    unsigned int scoreLeftPlayer;
    unsigned int scoreRightPlayer;

    // Heights of the barriers
    float topBarrierHeight;
    float bottomBarrierHeight;

    PaddleInfo paddleLeft;
    PaddleInfo paddleRight;

    BallInfo ball;
} GameState;

/// All the keys that are player independent
typedef enum {
    GLOBAL_KEY_TOGGLE_AI_LEFT = (1 << 0),
    GLOBAL_KEY_TOGGLE_AI_RIGHT = (1 << 1),
    GLOBAL_KEY_TOGGLE_DEBUG_DRAW = (1 << 2),
    GLOBAL_KEY_TOGGLE_PAUSE = (1 << 3),
} GLOBAL_INPUT;

/// All the keys that are player dependent
typedef enum {
    PADDLE_UP = (1 << 0),
    PADDLE_DOWN = (1 << 1),

    PADDLE_ROTATE_CLOCKWISE = (1 << 2),
    PADDLE_ROTATE_COUNTER_CLOCKWISE = (1 << 3),
} PLAYER_INPUT;


typedef struct {
    GLOBAL_INPUT global; // Will be toggled by io
    PLAYER_INPUT left;
    PLAYER_INPUT right;
} KeyboardInput;

typedef enum {
    LEFT_SIDE,
    RIGHT_SIDE,
} SIDE;

#define SIDE_TO_NAME(side) (((side) == LEFT_SIDE) ? "left" : "right")

#endif //UEB01_STRUCTS_H
