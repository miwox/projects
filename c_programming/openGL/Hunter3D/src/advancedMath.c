#include "advancedMath.h"
#include "types.h"

/// Converts degree (0°, 360°) to radian (0, 2*PI)
float degToRad(float deg) {
    return deg * (M_PI / 180.0f);
}

/// Converts radian (0, 2*PI) to degree (0°, 360°)
float radToDeg(float rad) {
    return rad * (180.0f / M_PI);
}

/// Calculates the length of a vector using pythagoras
float getVectorLength(float x, float y, float z) {
    return sqrtf(powf(x, 2) + powf(y, 2) + powf(z, 2));
}

/// Calculates the length of a Vector2 using pythagoras
float getVector2Length(Vector2 vector) {
    return getVectorLength(vector.x, vector.y, 0);
}

/// Calculates the length of a Vector3 using pythagoras
float getVector3Length(Vector3 vector) {
    return getVectorLength(vector.x, vector.y, vector.z);
}

/// Creates a vector with length of 1 and an angle in radian of rad
Vector2 unitVector2InRad(float rad) {
    Vector2 result;

    result.x = cosf(rad);
    result.y = sinf(rad);
    return result;
}

/// Creates a vector with length of 1 and an angle in degree of deg
Vector2 unitVector2InDeg(float deg) {
    return unitVector2InRad(degToRad(deg));
}

/// Changes the length of a vector to a new length
Vector2 setVector2Length(Vector2 vector, float newLength) {
    float oldLength = getVector2Length(vector);

    float scaling = newLength / oldLength;

    Vector2 newVector = vector;
    newVector.x *= scaling;
    newVector.y *= scaling;

    return newVector;
}

/// Changes the length of a Vector3 to a new length
Vector3 setVector3Length(Vector3 vector, float newLength) {
    float oldLength = getVector3Length(vector);

    float scaling = newLength / oldLength;

    Vector3 newVector = vector;
    newVector.x *= scaling;
    newVector.y *= scaling;
    newVector.z *= scaling;

    return newVector;
}

/// Dot product of two Vector2 vectors
float vector2DotProduct(Vector2 a, Vector2 b) {
    return (a.x * b.x) + (a.y * b.y);
}

/// Dot product of two Vector3 vectors
float vector3DotProduct(Vector3 a, Vector3 b) {
    return (a.x * b.x) + (a.y * b.y) + (a.z * b.z);
}

/// Calculates the angle in degree between two Vector2
float calcDegAngleVector2(Vector2 a, Vector2 b) {
    float scalar = vector2DotProduct(a, b);
    float lengthA = getVector2Length(a);
    float lengthB = getVector2Length(b);
    return radToDeg(acos(scalar / (lengthA * lengthB)));
}

/// Calculates the angle in degree between two Vector3
float calcDegAngleVector3(Vector3 a, Vector3 b) {
    float scalar = vector3DotProduct(a, b);
    float lengthA = getVector3Length(a);
    float lengthB = getVector3Length(b);

    return radToDeg(acos(scalar / (lengthA * lengthB)));
}

/// Calculate projection Vector A is project onto B
Vector3 projectionAonB(Vector3 a, Vector3 b) {
    // http://www.math-grain.de/download/m1/vektoren/skalar/projektion-1.pdf
    Vector3 result = b;

    float scalar = vector3DotProduct(a, b);
    float length = getVector3Length(b);
    float coff = scalar / powf(length, 2);

    result.x *= coff;
    result.y *= coff;
    result.z *= coff;

    return result;

}
/// Change the vector to a given direction
IntVector2 moveVectorInDirection(IntVector2 vector2, Direction direction) {
    IntVector2 result = vector2;

    switch (direction) {
        case LEFT:
            result.x--;
            break;
        case RIGHT:
            result.x++;
            break;
        case UP:
            result.y--;
            break;
        case DOWN:
            result.y++;
            break;
        default:
            // invalid move
            return result;
    }
    return result;
}

/// Adds to two Vector3
Vector3 vector3Add(Vector3 a, Vector3 b) {
    Vector3 result = { a.x+b.x, a.y+b.y, a.z+b.z };
    return result;
}

/// Multiplies each element of an Vector3
Vector3 vector3Mul(Vector3 a, float scalar) {
    Vector3 result = { a.x * scalar, a.y * scalar, a.z * scalar };
    return result;
}
/// Get euclid distance between two vectors
float getDistanceOfVector3(Vector3 a, Vector3 b){
    float x1x2 = powf(a.x-b.x, 2.0f);
    float y1y2 = powf(a.y-b.y, 2.0f);
    float z1z2 = powf(a.z-b.z, 2.0f);
    return sqrtf(x1x2 + y1y2 + z1z2);
}
