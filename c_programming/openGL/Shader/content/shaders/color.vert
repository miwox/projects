#version 330 core

/** 
 * Position des Vertex. 
 * Wird von der Host-Anwendung über den Attribut-Pointer 0 
 * bereitgestellt. 
 */
layout (location = 0) in vec4 vPosition;

layout (location = 1) in vec2 vTexCoord;

uniform sampler2D TextureHeightMap;

out vec2 fTexCoord;
out vec3 fNormalNorm;
out vec4 fElevatedPos;

uniform mat4 Projection;
uniform mat4 ModelView;
uniform float Elevation;
uniform float MeshSize;

vec4 calcBallCoordinate(vec4 coordinate, vec2 hightmapCoord)
{
    vec4 result;
    const float radius = 1.0;
    const float field_size = 1;
    const float delta_increase = 0.1f;
    const int two_pi_rad = 360;
    const int one_pi_rad = 180;
    float azimuthalAngle = radians(((coordinate.x + (field_size / 2.0f)) / field_size) * two_pi_rad);
    float polarAngle = radians(((coordinate.z + (field_size / 2.0f)) / field_size) * one_pi_rad);

    // Getting height from heightMap
    vec4 height_texture_vec = texture(TextureHeightMap, hightmapCoord);
    float height = (height_texture_vec).r * delta_increase;
    float altitude = height * Elevation + radius;

    result.x = sin(polarAngle) * cos(azimuthalAngle) * altitude;
    result.y = sin(polarAngle) * sin(azimuthalAngle) * altitude;
    result.z = cos(polarAngle) * altitude;
    result.w = coordinate.w;

    return result;
}

/**
 * Hauptprogramm des Vertex-Shaders.
 * Diese Funktion wird für jeden Vertex ausgeführt!
 */
void main(void)
{
    vec4 elevatedPosition = calcBallCoordinate(vPosition, vTexCoord);
    float distanceVertices = 1/MeshSize;

    float current_x = vPosition.x;
    float current_y = vPosition.y;
    float current_z = vPosition.z;
    float current_w = vPosition.w;
    float current_s = vTexCoord.s;
    float current_t = vTexCoord.t;

    /*
     * calc
     */

    vec4 left_neighbor4  = vec4(current_x - distanceVertices, current_y, current_z, current_w);
    vec4 right_neighbor4 = vec4(current_x + distanceVertices, current_y, current_z, current_w);
    vec4 up_neighbor4    = vec4(current_x, current_y, current_z - distanceVertices, current_w);
    vec4 down_neighbor4  = vec4(current_x, current_y, current_z + distanceVertices, current_w);

    vec2 left_neighbor2  = vec2(current_s - distanceVertices, current_t);
    vec2 right_neighbor2 = vec2(current_s + distanceVertices, current_t);
    vec2 up_neighbor2    = vec2(current_s, current_t + distanceVertices);
    vec2 down_neighbor2 =  vec2(current_s, current_t - distanceVertices);

    vec4 result_left = calcBallCoordinate(left_neighbor4, left_neighbor2);
    vec4 result_right = calcBallCoordinate(right_neighbor4, right_neighbor2);
    vec4 result_up = calcBallCoordinate(up_neighbor4, up_neighbor2);
    vec4 result_down = calcBallCoordinate(down_neighbor4, down_neighbor2);

    vec4 result_horizontal = result_right - result_left;
    vec4 result_vertical = result_up - result_down;
    vec3 vNormalNorm = normalize(cross(result_horizontal.xyz, result_vertical.xyz));

    /*
     * export
     */

    fTexCoord = vTexCoord;
    fNormalNorm = vNormalNorm;
    fElevatedPos = elevatedPosition;

    gl_Position = Projection * ModelView * elevatedPosition;
}
