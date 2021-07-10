//
// Created by samira on 7/5/21.
//

#ifndef SHADER_OLD_TYPES_H
#define SHADER_OLD_TYPES_H

#include <GL/glew.h>
#include <GL/freeglut.h>
#include <math.h>

/// location in shader program
typedef GLuint LOCATION;
/// objects which are created
typedef GLuint OBJECT;
/// float which are used as input for shader uniform
typedef float SHARED_FLOAT;
/// MeshSize
typedef GLuint MeshSize;
/// vertices type
typedef struct {
    float x, y, z, s, t;
} Vertex;

typedef enum {
    ST_NONE,
    ST_HEIGHT_MAP,
    ST_WORLD_MAP
} TEXTURE_ID;

typedef enum {
    EF_NONE,
    EF_CARTOON,
    EF_GREY,
    EF_SEPIA
} EFFECT_ID;


typedef TEXTURE_ID SHARED_TEXTURE_ID;
typedef EFFECT_ID SHARED_EFFECT_ID;
typedef int SHARED_INT;


#endif //SHADER_OLD_TYPES_H
