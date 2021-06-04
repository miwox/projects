#include "types.h"
#include "io.h"
#include "logic.h"
#include "advancedMath.h"
#include "level.h"
#include "scene.h"

#define IS_INPUT_PRESSING(input, key) !!(input & key)
#define IS_CAMERA_PRESSING_UP(input) !!(IS_INPUT_PRESSING(input, KEY_CAMERA_UP) && !IS_INPUT_PRESSING(input, KEY_CAMERA_DOWN))
#define IS_CAMERA_PRESSING_DOWN(input) !!(!IS_INPUT_PRESSING(input, KEY_CAMERA_UP) && IS_INPUT_PRESSING(input, KEY_CAMERA_DOWN))
#define IS_CAMERA_PRESSING_LEFT(input) !!(IS_INPUT_PRESSING(input, KEY_CAMERA_LEFT) && !IS_INPUT_PRESSING(input, KEY_CAMERA_RIGHT))
#define IS_CAMERA_PRESSING_RIGHT(input) !!(!IS_INPUT_PRESSING(input, KEY_CAMERA_LEFT) && IS_INPUT_PRESSING(input, KEY_CAMERA_RIGHT))
#define IS_CAMERA_PRESSING_ZOOM_IN(input) !!(IS_INPUT_PRESSING(input, KEY_CAMERA_ZOOM_IN) && !IS_INPUT_PRESSING(input, KEY_CAMERA_ZOOM_OUT))
#define IS_CAMERA_PRESSING_ZOOM_OUT(input) !!(!IS_INPUT_PRESSING(input, KEY_CAMERA_ZOOM_IN) && IS_INPUT_PRESSING(input, KEY_CAMERA_ZOOM_OUT))
#define IS_GRAY_KEY_FIELD(fieldType) ((fieldType) == P_KEY_GRAY)
#define IS_GOLD_KEY_FIELD(fieldType) ((fieldType) == P_KEY_GOLD)
#define IS_KEY_FIELD(fieldType) (IS_GOLD_KEY_FIELD(fieldType) || IS_GRAY_KEY_FIELD(fieldType))
#define IS_FREE_FIELD(fieldType) ((fieldType) == P_FREE)
#define IS_DOOR_FIELD(fieldType) ((fieldType) == P_DOOR)
#define IS_EXIT_FIELD(fieldType) ((fieldType) == P_EXIT)
#define IS_TRAP_FIELD(fieldType) ((fieldType) == P_TRAP)
#define IS_TREASURE_FIELD(fieldType)((fieldType) == P_TREASURE_CLOSED)
#define SET_FREE_FIELD(fieldType)((fieldType) = P_FREE)

#define IS_BOX_FIELD_WITH_MOVES_LEFT(fieldType) ((fieldType) >= P_BOX1 && (fieldType) <= P_BOX3)
#define BOX_FIELD_MOVES_LEFT(fieldType) ((fieldType) - P_BOX0)
#define MOVE_COUNT_TO_BOX_FIELD(movesLeft) (P_BOX0 + movesLeft)

static int pausedFlag = 0;
static int gameOverFlag = 0;
static int winnerFlag = 0;
static int helpFlag = 0;
static int inputAllowedAnimationFlag = 1;
static int animationFlag = 1;

const float freeCamDegRotationPerSec = 70.0f;
const float freeCamDistanceSec = 10.0f;
static Vector2 hunterPosition;
const float stepsForHunter = 1;
static float rotationKey = 0;
static int rotationSpeed = 500;

/// Process keyboard input
void processInput(float timeDeltaSec) {
    const float rotationPerSec = degToRad(freeCamDegRotationPerSec);

    if (IS_CAMERA_PRESSING_UP(gInput)) {
        gGameState->freeViewCamera.azimuthalAngle -= rotationPerSec * timeDeltaSec;
    } else if (IS_CAMERA_PRESSING_DOWN(gInput)) {
        gGameState->freeViewCamera.azimuthalAngle += rotationPerSec * timeDeltaSec;
    }

    if (IS_CAMERA_PRESSING_LEFT(gInput)) {
        gGameState->freeViewCamera.polarAngle += rotationPerSec * timeDeltaSec;
    } else if (IS_CAMERA_PRESSING_RIGHT(gInput)) {
        gGameState->freeViewCamera.polarAngle -= rotationPerSec * timeDeltaSec;
    }

    if (IS_CAMERA_PRESSING_ZOOM_IN(gInput)) {
        gGameState->freeViewCamera.distanceFromCenter += freeCamDistanceSec * timeDeltaSec;
    } else if (IS_CAMERA_PRESSING_ZOOM_OUT(gInput)) {
        gGameState->freeViewCamera.distanceFromCenter -= freeCamDistanceSec * timeDeltaSec;
    }

    gGameState->freeViewCamera.azimuthalAngle = CLAMP(gGameState->freeViewCamera.azimuthalAngle, degToRad(1),
                                                      degToRad(90));
    gGameState->freeViewCamera.distanceFromCenter = CLAMP(gGameState->freeViewCamera.distanceFromCenter, 5, 50);

    // Keep the polar angle from (0 and 360]
    while (gGameState->freeViewCamera.polarAngle >= degToRad(360)) {
        gGameState->freeViewCamera.polarAngle -= degToRad(360);
    }
    while (gGameState->freeViewCamera.polarAngle < 0) {
        gGameState->freeViewCamera.polarAngle += degToRad(360);
    }
}

/// Move hunter to the new position
void processHunterMove(float timeDeltaSec) {
    Vector2 actualPosition = getAbsoluteCoordinates(gGameState->hunterPos.x,
                                                    gGameState->hunterPos.y,
                                                    gGameState->map.size.width,
                                                    gGameState->map.size.height);
    switch (gGameState->hunterDirection) {
        case UP:
            hunterPosition.y -= timeDeltaSec * stepsForHunter;
            if (hunterPosition.y <= actualPosition.y) {
                inputAllowedAnimationFlag = 1;
                hunterPosition.y = actualPosition.y;
            };
            break;
        case DOWN:
            hunterPosition.y += timeDeltaSec * stepsForHunter;
            if (hunterPosition.y >= actualPosition.y) {
                inputAllowedAnimationFlag = 1;
                hunterPosition.y = actualPosition.y;
            };
            break;
        case LEFT:
            hunterPosition.x -= timeDeltaSec * stepsForHunter;
            if (hunterPosition.x <= actualPosition.x) {
                inputAllowedAnimationFlag = 1;
                hunterPosition.x = actualPosition.x;
            };
            break;
        case RIGHT:
            hunterPosition.x += timeDeltaSec * stepsForHunter;
            if (hunterPosition.x >= actualPosition.x) {
                inputAllowedAnimationFlag = 1;
                hunterPosition.x = actualPosition.x;
            };
            break;
    }
}

/// Process key rotation
void processRotation(float timeDelaSec) {
    rotationKey += rotationSpeed * timeDelaSec;
    while (rotationKey >= 360) {
        rotationKey -= 360;
    }
}
/// Will be called every frame with time between them
void updateGameState(float timeDeltaSec) {
    if (!isPauseActivated()) {
        processInput(timeDeltaSec);

        if (animationFlag) {
            processRotation(timeDeltaSec);
            if (!inputAllowedAnimationFlag) {
                processHunterMove(timeDeltaSec);
            }
        }
    }
}
/// Move hunter in a intended position. Also activates the animation for the hunter
void hunterMoveIntent(Direction moveDirection) {
    if (gGameState->hunterDirection != moveDirection) {
        gGameState->hunterDirection = moveDirection;
        return; //At first we rotate hunter in the moving direction (if its not the same direction), before we move.
    }
    if (animationFlag) {
        inputAllowedAnimationFlag = 0; // No input allowed while hunter is moving
    }

    FIELD_TYPE *fieldAtOldPos = &FIELD_AT_POS(gGameState->map, gGameState->hunterPos.x, gGameState->hunterPos.y);
    IntVector2 newHunterPos = moveVectorInDirection(gGameState->hunterPos, moveDirection);
    FIELD_TYPE *fieldAtNewPos = &FIELD_AT_POS(gGameState->map, newHunterPos.x, newHunterPos.y);
    // Simple check if we are able to move at the pos and maybe collect a key
    if (IS_FREE_FIELD(*fieldAtNewPos)
        || IS_KEY_FIELD(*fieldAtNewPos)
        || (IS_DOOR_FIELD(*fieldAtNewPos) && gGameState->collectedGrayKeyCount)
        || IS_TRAP_FIELD(*fieldAtNewPos)) {
        gGameState->hunterPos = newHunterPos;

        if (IS_GOLD_KEY_FIELD(*fieldAtNewPos)) {
            gGameState->collectedGoldKeyCount++;
        } else if (IS_GRAY_KEY_FIELD(*fieldAtNewPos)) {
            gGameState->collectedGrayKeyCount++;
        } else if (IS_DOOR_FIELD(*fieldAtNewPos)) {
            gGameState->collectedGrayKeyCount--;
        }

        // Check if old pos was a trap, if so active it
        if (IS_TRAP_FIELD(*fieldAtOldPos)) {
            *fieldAtOldPos = P_TRAP_ACTIVATED;
        }

        // Clear the new field (keys and stuff)
        if (!IS_TRAP_FIELD(*fieldAtNewPos)) {
            *fieldAtNewPos = P_FREE;
        }
    }

    // More complex check for boxes, because we have to check if the box is able to move
    if (IS_BOX_FIELD_WITH_MOVES_LEFT(*fieldAtNewPos)) {
        IntVector2 newBoxPos = moveVectorInDirection(newHunterPos, moveDirection);
        FIELD_TYPE *fieldAtNewBoxPos = &FIELD_AT_POS(gGameState->map, newBoxPos.x, newBoxPos.y);

        // If the box can move to its new position, we can allow our move and need to decrease the box moves
        if (IS_FREE_FIELD(*fieldAtNewBoxPos)) {
            *fieldAtNewBoxPos = MOVE_COUNT_TO_BOX_FIELD(BOX_FIELD_MOVES_LEFT(*fieldAtNewPos) - 1);
            SET_FREE_FIELD(*fieldAtNewPos);
            gGameState->hunterPos = newHunterPos;
        }
    }

    if (IS_TREASURE_FIELD(*fieldAtNewPos) && gGameState->collectedGoldKeyCount) {
        *fieldAtNewPos = P_TREASURE_OPENED;
        gGameState->collectedGoldKeyCount--;
        gGameState->exitIsOpen = 1; // activate green Exit
    }

    if (IS_EXIT_FIELD(*fieldAtNewPos) && gGameState->exitIsOpen) {
        gGameState->hunterPos = newHunterPos;
        winnerFlag = 1; // activate 'you won' and next level.
    }

    // Update the current state when animation is deactivated.
    if (!animationFlag && gGameState->hunterPos.x == newHunterPos.x && gGameState->hunterPos.y == newHunterPos.y) {
        hunterPosition = getAbsoluteCoordinates(gGameState->hunterPos.x,
                                                gGameState->hunterPos.y,
                                                gGameState->map.size.width,
                                                gGameState->map.size.height);
    }
}
/// Loads a to a given level number and sets the absolute position of hunter
void loadLevel(int levelNumber) {
    winnerFlag = 0;
    gameOverFlag = 0;
    inputAllowedAnimationFlag = 1;
    freeGeneratedLevel(gGameState);
    gGameState = generateLevel(levelNumber);
    hunterPosition = getAbsoluteCoordinates(gGameState->hunterPos.x,
                                            gGameState->hunterPos.y,
                                            gGameState->map.size.width,
                                            gGameState->map.size.height);
}

/// Loads the next level
void loadNextLevel() {
    loadLevel((gGameState->levelNumber + 1) % totalLevelCount());
}

/// Free the current level
void freeCurrentLevel(void) {
    freeGeneratedLevel(gGameState);
}

/// Get the winner flag
int hasWon() {
    return winnerFlag;
}

/// Get the winner flag
View3dCameraMode getCameraMode() {
    return gGameState->cameraMode;
}

/// Set the camera mode
void setCameraMode(View3dCameraMode camera) {
    gGameState->cameraMode = camera;
}

/// Get pause state
int isPauseActivated() {
    return pausedFlag;
}

/// Set pause state
void setPauseState(int isPaused) {
    pausedFlag = isPaused;
}

/// Decrement time and set if needed the game over flag
void onGameTimerTick(void) {
    if (!pausedFlag && !gameOverFlag && !winnerFlag) {
        gGameState->timeLeft--;
    }
    if (gGameState->timeLeft <= 0) {
        gameOverFlag = 1;
    }
}

/// Get help state
int isHelpActivated() {
    return helpFlag;
}

/// Set help state
void setHelpState(int state) {
    helpFlag = state;
}

/// Get game over flag
int isGameOver() {
    return gameOverFlag;
}

/// Get is input allowed flag
int isInputAllowedAnimation() {
    return inputAllowedAnimationFlag;
}

/// Get animation flag
int isAnimationActivated() {
    return animationFlag;
}

/// Set animation flag
void setAnimationState(int animation) {
    animationFlag = animation;
}

/// Get hunter position in absolute coordinates
Vector2 getHunterPosition() {
    return hunterPosition;
}

/// Get rotation angle of key
float getRotationKey(){
    return rotationKey;
};





