#ifndef UEB01_IO_H
#define UEB01_IO_H

#include "structs.h"

/// Global keyboard input
KeyboardInput gInput;

/// Marcos that will check if the left/right side should be controlled by an AI
#define IS_LEFT_SIDE_CONTROLLED_BY_AI !!(gInput.global & GLOBAL_KEY_TOGGLE_AI_LEFT)
#define IS_RIGHT_SIDE_CONTROLLED_BY_AI !!(gInput.global & GLOBAL_KEY_TOGGLE_AI_RIGHT)
#define DRAW_DEBUG !!(gInput.global & GLOBAL_KEY_TOGGLE_DEBUG_DRAW)
#define IS_PAUSED !!((gInput.global & GLOBAL_KEY_TOGGLE_PAUSE) || (DRAW_DEBUG))

void resetIo(void);
void registerCallbacks(void);

#endif //UEB01_IO_H
