#ifndef UEB03_IO_H
#define UEB03_IO_H

#include "types.h"

/// Global keyboard input
KeyboardInput gInput;

///Debug Input
DebugInput debugInput;


/// Registers all necessary callbacks (keyboard, reshape, display)
void registerAllIoCallbacks(void);

/// Queues a new animation frame and will call onAnimationFrame as soon as the frame should be rendered
void requestAnimationFrame(void);

#endif // UEB03_IO_H
