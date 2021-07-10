#ifndef SHADER_OLD_VERTEX_WORK_H
#define SHADER_OLD_VERTEX_WORK_H

#include "types.h"

#define MESH_SIZE_TO_VERTICES(X) ((X + 1) * (X + 1))
#define NUMBER_OF_VERTICES_TRIANGLE (6)
#define MESH_SIZE_TO_TRIANGLE_INDICES(X) ((X)*(X)*NUMBER_OF_VERTICES_TRIANGLE)
#define VERTEX_ARRAY_INDEX(X, Y, NUMBER_OF_VERTEX_ROW) ( (X) * (NUMBER_OF_VERTEX_ROW) + (Y) )
#define SCENE_HEIGHT_WIDTH 1

/// build vertices
Vertex* createBasicVertices(MeshSize size);

/// build indices for drawing
GLuint* createIndicesTriangles(MeshSize size);


#endif //SHADER_OLD_VERTEX_WORK_H
