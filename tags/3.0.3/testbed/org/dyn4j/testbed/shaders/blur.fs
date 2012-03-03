float kernel[9];

uniform sampler2D colorMap;
uniform float width;
uniform float height;
uniform float yOffset;

float step_w = 1.0/width;
float step_h = 1.0/height;

vec2 offset[9];
						 
void main(void)
{
   int i = 0;
   vec4 sum = vec4(0.0);
   
   offset[0] = vec2(-step_w, -step_h);
   offset[1] = vec2(0.0, -step_h);
   offset[2] = vec2(step_w, -step_h);
   
   offset[3] = vec2(-step_w, 0.0);
   offset[4] = vec2(0.0, 0.0);
   offset[5] = vec2(step_w, 0.0);
   
   offset[6] = vec2(-step_w, step_h);
   offset[7] = vec2(0.0, step_h);
   offset[8] = vec2(step_w, step_h);
   
   kernel[0] = 1.0/16.0; 	kernel[1] = 2.0/16.0;	kernel[2] = 1.0/16.0;
   kernel[3] = 2.0/16.0;	kernel[4] = 4.0/16.0;	kernel[5] = 2.0/16.0;
   kernel[6] = 1.0/16.0;   	kernel[7] = 2.0/16.0;	kernel[8] = 1.0/16.0;
   
   
   if(gl_TexCoord[0].t<yOffset)
   {
	   for( i=0; i<9; i++ )
	   {
			vec4 tmp = texture2D(colorMap, gl_TexCoord[0].st + offset[i]);
			sum += tmp * kernel[i];
	   }
   }
   else
   {
		sum = texture2D(colorMap, gl_TexCoord[0].xy);
   }

   gl_FragColor = sum;
}