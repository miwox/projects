#include "advancedMath.h"

/// Converts degree (0°, 360°) to radian (0, 2*PI)
float degToRad(float deg) {
    return deg * (M_PI / 180.0f);
}

/// Converts radian (0, 2*PI) to degree (0°, 360°)
float radToDeg(float rad) {
    return rad * (180.0f / M_PI);
}

/// Calculates the length of a vector using pythagoras
float vectorLength(float x, float y) {
    return sqrtf(powf(x, 2) + powf(y, 2));
}

/// Calculates the length of a Vector2 using pythagoras
float vector2Length(Vector2 vector) {
    return vectorLength(vector.x, vector.y);
}

/// Creates a vector with length of 1 and an angle in radian of rad
Vector2 unitVectorInRad(float rad) {
    Vector2 result;

    result.x = cosf(rad);
    result.y = sinf(rad);

    return result;
}

/// Creates a vector with length of 1 and an angle in degree of deg
Vector2 unitVectorInDeg(float deg) {
    return unitVectorInRad(degToRad(deg));
}

/// Changes the length of a vector to a new length
Vector2 setVectorLength(Vector2 vector, float newLength) {
    float oldLength = vector2Length(vector);

    float scaling = newLength / oldLength;

    Vector2 newVector = vector;
    newVector.x *= scaling;
    newVector.y *= scaling;

    return newVector;
}
