#ifndef UEB03_SCENE_H
#define UEB03_SCENE_H

#include "types.h"

/// Draw the scene 3d with all rendering lights
void drawScene3D(const GameState gameState);

/// Draw the scene 2D
void drawScene2D(const GameState gameState);

/// No negative numbers for distance!
/// Adds (direction Vector * distance) to lookingDirection Vector
void setLookingDirection(Vector2 *lookingDirection, Direction direction, float distance);

/// Calculates the camera position
Vector3 getCameraPosition(GameState gameState);

/// Get normal mode state
int isNormalModeActivated();

/// Set the normal mode
void setNormalMode(int normal);

/// Get absolute coordinates for logical x,y, map mapWidth and mapHeight.
Vector2 getAbsoluteCoordinates(int x, int y, int mapWidth, int mapHeight);

/// Initialise the scene
int initScene(void);

/// Initialise the display lists
void initDisplayLists(void);

/// Free display lsit
void freeDisplayList(void);

/// Toggles wireframe
void toggleWireframeMode(void);


#endif
