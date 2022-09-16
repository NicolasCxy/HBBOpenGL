#extension GL_OES_EGL_image_external : require
//设置精度
precision lowp float;
//纹理坐标
varying vec2 aCoord;
//纹理对象
uniform samplerExternalOES vTexture;

const vec2 MosicSize = vec2(0.05, 0.05);

//马赛克
void main() {

    /**
    * 思路：
        1. 将图像分配多个小块，每个块只取左上角的值
        2. 当前值 除 块大小 再乘块大小就能求出所属块的左上角的值。
    */
    vec2 XYMosic = vec2(floor (aCoord.x / MosicSize.x) * MosicSize.x, floor(aCoord.y / MosicSize.y) * MosicSize.y);


//    vec2 XYMosaic = vec2(floor(aCoord.x/MosoicSize.x)*MosoicSize.x, floor(aCoord.y/MosoicSize.y)*MosoicSize.y);
    
    
    vec4 rgb =  texture2D(vTexture, XYMosic);

    gl_FragColor = rgb;
}
