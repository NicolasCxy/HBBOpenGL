//设置精度
precision lowp float;

varying vec2 textureCoordinate;

uniform sampler2D vTexture;//0图层
//片元      纹理坐标系  2
void main(){

    vec4 rgba = texture2D(vTexture, textureCoordinate);
    float color = (rgba.r + rgba.g + rgba.b) / 3.0;
    gl_FragColor = vec4(color,color,color,rgba.a);
}