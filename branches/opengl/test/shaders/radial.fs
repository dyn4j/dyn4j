uniform sampler2D tex;
 
varying vec2 uv;
 
const float sampleDist = 1.0;
const float sampleStrength = 2.2; 
 
void main(void)
{
    vec2 dir = 0.5 - uv; 
 
    float dist = sqrt(dir.x*dir.x + dir.y*dir.y); 
 
    dir = dir/dist; 
 
    vec4 color = texture2D(tex,uv); 
 
    vec4 sum = color;
    
    sum += texture2D( tex, uv + dir * -0.08 * sampleDist );
    sum += texture2D( tex, uv + dir * -0.05 * sampleDist );
    sum += texture2D( tex, uv + dir * -0.03 * sampleDist );
    sum += texture2D( tex, uv + dir * -0.02 * sampleDist );
    sum += texture2D( tex, uv + dir * -0.01 * sampleDist );
    sum += texture2D( tex, uv + dir *  0.01 * sampleDist );
    sum += texture2D( tex, uv + dir *  0.02 * sampleDist );
    sum += texture2D( tex, uv + dir *  0.03 * sampleDist );
    sum += texture2D( tex, uv + dir *  0.05 * sampleDist );
    sum += texture2D( tex, uv + dir *  0.08 * sampleDist );
 
    sum *= 1.0/11.0;
 
    float t = dist * sampleStrength;
    t = clamp( t ,0.0,1.0);
 
    gl_FragColor = mix( color, sum, t );
} 