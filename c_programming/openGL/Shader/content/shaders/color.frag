#version 330 core

/**
 * Interpolierte Textur-Koordinate des Fragments.
 */
in vec2 fTexCoord;
in vec3 fNormalNorm;
in vec4 fElevatedPos;

/**
 * Farbe des Fragments, die auf den Framebuffer geschrieben wird.
 */
out vec4 FragColor;


/**
 * Texture-Sampler, um auf die Textur zuzugreifen. Die Einstellungen
 * des Samplers (Interpolation und Randbehandlung) werden beim Erstellen
 * der Textur gesetzt.
 */
uniform sampler2D TextureHeightMap;
uniform sampler2D TextureWorldMap;
uniform int TextureId;
uniform int EffectId;
uniform int LightState;


uniform float ElapsedTime;
uniform vec3 CameraPosition;
uniform vec3 LightPosition;


/**
 * Hauptprogramm des Fragment-Shaders.
 * Diese Funktion wird für jedes Fragment ausgeführt!
 */
void main(void)
{
    const float alpha = 1;

    // ENUM: TEXTURE_ID
    const int HEIGHT_MAP = 1;
    const int WORLD_MAP = 2;

    // ENUM: EFFECT_ID
    const int CARTOON = 1;
    const int GREY = 2;
    const int SEPIA = 3;

    const float random_color_start = 0.3f;
    const int factor_r = 1;
    const int factor_g = 2;
    const int factor_b = 3;
    const float scale_RGB = 0.2f;
    /*
     * Texture work and coloring no texture
     */
    vec3 result_color = vec3(
        random_color_start + cos(ElapsedTime * factor_r) * scale_RGB,
        random_color_start + cos(ElapsedTime * factor_g) * scale_RGB,
        random_color_start + cos(ElapsedTime * factor_b) * scale_RGB
    );

    vec3 color_text_height = texture(TextureHeightMap, fTexCoord).rgb;
    vec3 color_text_world = texture(TextureWorldMap, fTexCoord).rgb;

    // Order is important: Highest value must be the lowest mix
    result_color = mix(result_color, color_text_height, TextureId / HEIGHT_MAP);
    result_color = mix(result_color, color_text_world,  TextureId / WORLD_MAP);

    /*
     * Phong
     */
    float beforePhong_r = result_color.r;
    float beforePhong_g = result_color.g;
    float beforePhong_b = result_color.b;

    vec3 light_direction  = normalize(LightPosition - fElevatedPos.xyz);
    vec3 reflection_direction = reflect(-light_direction, fNormalNorm);
    vec3 camera_direction = normalize(CameraPosition - fElevatedPos.xyz);

    const float light_ambient_factor = .3f;
    const float light_diffuse_factor = 1.2f;
    const float light_specular_factor = .5f;
    const float material_ambient_factor = .3f;
    const float material_diffuse_factor = 1.2;
    const float material_specular_factor = .2f;
    const float material_shininess_factor = 5.0f;

    vec3 material_ambient = result_color * material_ambient_factor;
    vec3 material_diffuse = result_color * material_diffuse_factor;

    vec3 color_ambient = material_ambient * light_ambient_factor;
    vec3 color_diffuse = max(dot(fNormalNorm, light_direction), 0) * material_diffuse * light_diffuse_factor;

    float color_specular_factor = pow(max(dot(camera_direction, light_direction),0), material_shininess_factor) * material_specular_factor * light_specular_factor;
    vec3 color_specular = vec3(color_specular_factor, color_specular_factor, color_specular_factor);
    vec3 phong = color_ambient + color_diffuse + color_specular;
    result_color = mix(result_color, phong, LightState);

    float afterPhong_r = result_color.r;
    float afterPhong_g = result_color.g;
    float afterPhong_b = result_color.b;

    // We could have used if because it is faster according to stackoverflow this solution is slow:
    // https://stackoverflow.com/questions/47597588/glsl-optimize-if-else#comment82165262_47598470
    float grey_cartoon =  (float(beforePhong_r + beforePhong_g + beforePhong_b) / 3);
    float intensity_cartoon = 0.6 + step(0.1, grey_cartoon) * 0.7;
    vec3 cartoon_vector = vec3(
        beforePhong_r * intensity_cartoon,
        beforePhong_g * intensity_cartoon,
        beforePhong_b * intensity_cartoon
    );

    // https://stackoverflow.com/questions/28024208/how-to-calculate-grey-scale-value-of-a-particular-pixel-in-java-is-there-any-fo
    float grey = (float(afterPhong_r + afterPhong_g + afterPhong_b) / 3);
    vec3 grey_vector = vec3(grey, grey, grey);

    vec3 sepia_vector = vec3(
        .393 * afterPhong_r + .769 * afterPhong_g + .189 * afterPhong_b,
        .343 * afterPhong_r + .786 * afterPhong_g + .168 * afterPhong_b,
        .272 * afterPhong_r + .534 * afterPhong_g + .131 * afterPhong_b
    );

    // Order is important: Highest value must be the lowest mix
    result_color = mix(result_color, cartoon_vector, EffectId / CARTOON);
    result_color = mix(result_color, grey_vector,    EffectId / GREY);
    result_color = mix(result_color, sepia_vector,   EffectId / SEPIA);

    FragColor = vec4(result_color.rgb, alpha);

}
