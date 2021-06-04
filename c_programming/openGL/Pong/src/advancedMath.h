#ifndef UEB01_ADVANCEDMATH_H
#define UEB01_ADVANCEDMATH_H

#include <math.h>
#include "structs.h"

#define M_TAU (2 * M_PI)

#define MAX(x, y) (((x) > (y)) ? (x) : (y))
#define MIN(x, y) (((x) < (y)) ? (x) : (y))
#define CLAMP(x, min, max) MAX(min, MIN(max, x))

/// Converts degree (0°, 360°) to radian (0, 2*PI)
float degToRad(float deg);

/// Converts radian (0, 2*PI) to degree (0°, 360°)
float radToDeg(float rad);

/// Calculates the length of a vector using pythagoras
float vectorLength(float x, float y);

/// Calculates the length of a Vector2 using pythagoras
float vector2Length(Vector2 vector);

/// Creates a vector with length of 1 and an angle in radian of rad
Vector2 unitVectorInRad(float rad);

/// Creates a vector with length of 1 and an angle in degree of deg
Vector2 unitVectorInDeg(float deg);

/// Changes the length of a vector to a new length
Vector2 setVectorLength(Vector2 vector, float newLength);

#endif //UEB01_ADVANCEDMATH_H
