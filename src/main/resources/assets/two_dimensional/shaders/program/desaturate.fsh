#version 330 compatibility

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform mat4 InverseTransformMatrix;
uniform mat4 ModelViewMat;
uniform vec3 CameraPos;

uniform vec3 PlaneOffset;
uniform float PlaneSlope;
uniform vec3 PlaneNormal;

in vec2 texCoord;

// almost the same as in plane
float sdf(vec3 point) {
    float x = (PlaneSlope * PlaneOffset.x - PlaneOffset.z + point.x / PlaneSlope + point.z) / (PlaneSlope + 1. / PlaneSlope);
    float z = PlaneSlope * (x - PlaneOffset.x) + PlaneOffset.z;

    vec3 to_point = vec3(point.x - x, 0, point.z - z);

    // we can assume it's always positive as negative would be culled
    return length(to_point) * sign(dot(to_point, PlaneNormal));
}

void main() {
    vec4 tex = texture(DiffuseSampler, texCoord);
    gl_FragColor = tex;

    float sceneDepth = texture(DepthSampler, texCoord).x;

    if (sceneDepth == 1) {
        return;
    }

    vec3 ndc = vec3(texCoord.xy, sceneDepth) * 2.0 - 1.0;
    vec4 homPos = InverseTransformMatrix * vec4(ndc, 1.0);
    vec3 viewPos = homPos.xyz / homPos.w;

    vec3 worldPos = (inverse(ModelViewMat) * vec4(viewPos, 1.)).xyz + CameraPos;
    float dist = sdf(worldPos);

    gl_FragColor /= 1 + 0.1 * smoothstep(0.40, 0.60, dist);
}
