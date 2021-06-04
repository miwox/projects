#ifndef UEB03_LOGIC_H
#define UEB03_LOGIC_H

#include "types.h"

GameState gGameState;

/// Load a to a given level number
void loadLevel(int levelNumber);

/// Move hunter to intended direction
void hunterMoveIntent(Direction intendedDirection);

/// Free current level
void freeCurrentLevel(void);

/// Load next level
void loadNextLevel();

/// Get is input allowed flag
int isInputAllowedAnimation();

/// Get hunter position in absolute coordinates
Vector2 getHunterPosition();

/// Get the activate camera mode
View3dCameraMode getCameraMode();

/// Set the activate camera mode
void setCameraMode(View3dCameraMode camera);

/// Updates the global gameState (physics calculations, score, win condition and stuff)
void updateGameState(float timeDeltaSec);

/// Set pause state
void setPauseState(int isPaused);

/// Get pause state
int isPauseActivated();

/// Decrement time and set if needed the game over flag
void onGameTimerTick(void);

/// Get help state
int isHelpActivated();

/// Set help state
void setHelpState(int state);

/// Get won state
int hasWon();

/// Get game over flag
int isGameOver();

/// Get animation flag
int isAnimationActivated();

/// Set animation flag
void setAnimationState(int animation);

/// Get rotation angle of key
float getRotationKey();

#endif
