#version 330

in vec2 fragTexCoord;
out vec4 fragColor;

uniform sampler2D textureSampler;
uniform int useTransparency;

void main() {
    vec4 textureColor = texture(textureSampler, fragTexCoord);
    
    // If using transparency and the alpha is very low, discard the fragment completely
    if (useTransparency == 1 && textureColor.a < 0.01) {
        discard;
    }
    
    fragColor = textureColor;
}