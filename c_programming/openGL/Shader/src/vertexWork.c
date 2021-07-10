#include "vertexWork.h"

Vertex* createBasicVertices(MeshSize size) {
    Vertex* result = malloc(sizeof(Vertex) * MESH_SIZE_TO_VERTICES(size));
    const unsigned int numberOfVertexRow = size + 1;
    const float distanceVertices = (float)SCENE_HEIGHT_WIDTH / (float)size;
    const float offset = -(float)size / 2 * distanceVertices;
    const float textOffSet =  (float)1/(float)(size);


    for (int x = 0; x < numberOfVertexRow; x++) {
        for (int y = 0; y < numberOfVertexRow; y++) {
            Vertex* vertex = &(result[VERTEX_ARRAY_INDEX(x, y, numberOfVertexRow)]);
            vertex->x = offset + (float)x * distanceVertices;
            vertex->y  = 0;
            vertex->z = offset + (float)y * distanceVertices;
            vertex->s = textOffSet * (float)x;
            vertex->t = textOffSet * (float)y;
        }
    }
    return result;
}

/// create indices to draw gl quads
GLuint* createIndicesTriangles(MeshSize size) {
    GLuint* indices = calloc(MESH_SIZE_TO_TRIANGLE_INDICES(size), sizeof(GLuint));
    int index = 0;
    const unsigned int MESH_SIZE = size;
    for (int i = 0; i < pow(size, 2); i++) {
        // top half
        indices[VERTEX_ARRAY_INDEX(i, 0, NUMBER_OF_VERTICES_TRIANGLE)] = index;
        indices[VERTEX_ARRAY_INDEX(i, 1, NUMBER_OF_VERTICES_TRIANGLE)] = index + MESH_SIZE + 2;
        indices[VERTEX_ARRAY_INDEX(i, 2, NUMBER_OF_VERTICES_TRIANGLE)] = index + MESH_SIZE + 1;

        // bottom half
        indices[VERTEX_ARRAY_INDEX(i, 3, NUMBER_OF_VERTICES_TRIANGLE)] = index;
        indices[VERTEX_ARRAY_INDEX(i, 4, NUMBER_OF_VERTICES_TRIANGLE)] = index + 1;
        indices[VERTEX_ARRAY_INDEX(i, 5, NUMBER_OF_VERTICES_TRIANGLE)] = index + MESH_SIZE + 2;

        index++;
        if ((i + 1) % size == 0) {
            index++; // new row
        }
    }
    return indices;
}
