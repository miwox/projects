#ifndef UEB01_LOGIC_H
#define UEB01_LOGIC_H

#include "structs.h"

/// Global keyboard input
GameState gGameState;

void resetGameState(void);

/// Updates a gameState
void updateGameState(float timeDelta);

#endif //UEB01_LOGIC_H
