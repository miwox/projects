#ifndef HUNTER3D_LEVEL_H
#define HUNTER3D_LEVEL_H
#include "types.h"

unsigned int totalLevelCount();

GameState generateLevel(LevelNumber levelNumber);
void freeGeneratedLevel(GameState gameState);

#endif //HUNTER3D_LEVEL_H
