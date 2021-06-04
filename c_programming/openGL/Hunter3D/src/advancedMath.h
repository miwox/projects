#ifndef CG1_ADVANCEDMATH_H
#define CG1_ADVANCEDMATH_H

#include <math.h>
#include "types.h"

#ifndef M_PI
#define M_PI (3.14159265358979323846f)
#endif
#define M_TAU (2 * M_PI)

#define MAX(x, y) (((x) > (y)) ? (x) : (y))
#define MIN(x, y) (((x) < (y)) ? (x) : (y))
#define CLAMP(x, min, max) MAX(min, MIN(max, x))

/// Converts degree (0°, 360°) to radian (0, 2*PI)
float degToRad(float deg);

/// Converts radian (0, 2*PI) to degree (0°, 360°)
float radToDeg(float rad);

/// Calculates the length of a vector using pythagoras
float getVectorLength(float x, float y, float z);

/// Calculates the length of a Vector2 using pythagoras
float getVector2Length(Vector2 vector);

/// Calculates the length of a Vector3 using pythagoras
float getVector3Length(Vector3 vector);

/// Creates a vector with length of 1 and an angle in radian of rad
Vector2 unitVector2InRad(float rad);

/// Creates a vector with length of 1 and an angle in degree of deg
Vector2 unitVector2InDeg(float deg);

/// Changes the length of a Vector2 to a new length
Vector2 setVector2Length(Vector2 vector, float newLength);

/// Changes the length of a Vector3 to a new length
Vector3 setVector3Length(Vector3 vector, float newLength);

/// Change the vector to a given direction
IntVector2 moveVectorInDirection(IntVector2 vector2, Direction direction);

/// Get euclid distance between two vectors
float getDistanceOfVector3(Vector3 a, Vector3 b);

/// Dot product of two Vector2 vectors
float vector2DotProduct(Vector2 a, Vector2 b);

/// Dot product of two Vector3 vectors
float vector3DotProduct(Vector3 a, Vector3 b);

/// Calculates the angle in degree between two Vector2
float calcDegAngleVector2(Vector2 a, Vector2 b);

/// Calculates the angle in degree between two Vector3
float calcDegAngleVector3(Vector3 a, Vector3 b);

/// Calculate projection Vector A is project onto B
Vector3 projectionAonB(Vector3 a, Vector3 b);

/// Adds to two Vector3
Vector3 vector3Add(Vector3 a, Vector3 b);

/// Multiplies each element of an Vector3
Vector3 vector3Mul(Vector3 a, float scalar);

#endif //CG1_ADVANCEDMATH_H
