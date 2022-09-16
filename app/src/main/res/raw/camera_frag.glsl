#extension GL_OES_EGL_image_external : require
//设置精度
precision lowp float;
//纹理坐标
varying vec2 aCoord;
//纹理对象
uniform samplerExternalOES vTexture;

//片源着色器   - 三分屏
void main() {

    float x =  aCoord.x;
    float y =  aCoord.y;
    float a = 1.0/3.0;

//  - 二分屏
//    if (x < 0.5){
//        x+= 0.25;
//    } else if (x> 0.5){
//        x-= 0.25;
//    }

//  - 九分屏
//    if (x < 0.3){
//        x+= a;
//    } else if (x> 2.0 *a){
//        x-= a;
//    }
//
//
//    if (y < 0.3){
//        y+= a;
//    } else if (y> 2.0*a){
//        y-= a;
//    }


//  - 四分屏
    if (x < 0.5){
        x+= 0.25;
    } else if (x> 0.5){
        x-= 0.25;
    }

    if (y < 0.5){
        y+= 0.25;
    } else if (y> 0.5){
        y-= 0.25;
    }



    gl_FragColor = texture2D(vTexture, vec2(x, y));
}
