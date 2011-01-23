uniform sampler2D tex2;
varying vec2 vTexCoord;
 
const float blurSize = 1.0/512.0;
 
void main(void)
{
   vec4 sum = vec4(0.0);
 
   sum += texture2D(tex2, vec2(vTexCoord.x, vTexCoord.y - 4.0*blurSize)) * 0.05;
   sum += texture2D(tex2, vec2(vTexCoord.x, vTexCoord.y - 3.0*blurSize)) * 0.09;
   sum += texture2D(tex2, vec2(vTexCoord.x, vTexCoord.y - 2.0*blurSize)) * 0.12;
   sum += texture2D(tex2, vec2(vTexCoord.x, vTexCoord.y - blurSize)) * 0.15;
   sum += texture2D(tex2, vec2(vTexCoord.x, vTexCoord.y)) * 0.16;
   sum += texture2D(tex2, vec2(vTexCoord.x, vTexCoord.y + blurSize)) * 0.15;
   sum += texture2D(tex2, vec2(vTexCoord.x, vTexCoord.y + 2.0*blurSize)) * 0.12;
   sum += texture2D(tex2, vec2(vTexCoord.x, vTexCoord.y + 3.0*blurSize)) * 0.09;
   sum += texture2D(tex2, vec2(vTexCoord.x, vTexCoord.y + 4.0*blurSize)) * 0.05;
 
   gl_FragColor = sum;
}