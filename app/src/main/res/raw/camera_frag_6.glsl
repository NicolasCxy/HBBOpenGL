#extension GL_OES_EGL_image_external : require
//设置精度
precision lowp float;
//纹理坐标
varying vec2 aCoord;
//纹理对象
uniform samplerExternalOES vTexture;

const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);

//片源着色器
//颜色滤镜
void main() {

    vec4 rgb =  texture2D(vTexture, aCoord);

    float color = (rgb.r+ rgb.b + rgb.g) / 3.0;

    gl_FragColor = vec4(color, color, color, rgb.a);
}
