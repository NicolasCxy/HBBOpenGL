#extension GL_OES_EGL_image_external : require
//设置精度
precision lowp float;
//纹理坐标
varying vec2 aCoord;
//纹理对象 - 外部数据使用  samplerExternalOES  ，GL内部数据使用Sampler2D
uniform sampler2D vTexture;

//片源着色器
//颜色滤镜
void main() {
//    gl_FragColor =  texture2D(vTexture, aCoord);
    gl_FragColor =  vec4(0,255,0,255);
}
