#extension GL_OES_EGL_image_external : require
//设置精度
precision lowp float;
//纹理坐标
varying vec2 aCoord;
//纹理对象
uniform samplerExternalOES vTexture;


//六边形的边长
const float mosaicSize = 0.03;
//马赛克
void main() {

    float length = mosaicSize;
    //矩形的高的比例为√3，取值 √3/2 ，也可以直接取√3
    float TR = 0.866025;
    //矩形的长的比例为3，取值 3/2 = 1.5，也可以直接取3
    float TB = 1.5;

    //取出纹理坐标
    float x = aCoord.x;
    float y = aCoord.y;

    //根据纹理坐标计算出对应的矩阵坐标
    //即 矩阵坐标wx = int（纹理坐标x/ 矩阵长），矩阵长 = TB*len
    //即 矩阵坐标wy = int（纹理坐标y/ 矩阵宽），矩阵宽 = TR*len
    int wx = int(x / TB / length);
    int wy = int(y / TR / length);
    vec2 v1, v2, vn;

    //判断左上角顶点所在行数的奇偶性来判断中心点
    //判断wx是否为偶数，等价于 wx % 2 == 0
    if (wx/2 * 2 == wx) {
        if (wy/2 * 2 == wy) { //偶行偶列
            //(0,0),(1,1)
            //在内部再乘以(length * TB)就得到了顶点的纹理坐标
            v1 = vec2(length * TB * float(wx), length * TR * float(wy));
            v2 = vec2(length * TB * float(wx+1), length * TR * float(wy+1));
        } else { //偶行奇列
            //(0,1),(1,0)
            v1 = vec2(length * TB * float(wx), length * TR * float(wy+1));
            v2 = vec2(length * TB * float(wx+1), length * TR * float(wy));
        }
    } else {
        if (wy/2 * 2 == wy) { //奇行偶列
            //(0,1),(1,0)
            v1 = vec2(length * TB * float(wx), length * TR * float(wy+1));
            v2 = vec2(length * TB * float(wx+1), length * TR * float(wy));
        } else { //奇行奇列
            //(0,0),(1,1)
            v1 = vec2(length * TB * float(wx), length * TR * float(wy));
            v2 = vec2(length * TB * float(wx+1), length * TR * float(wy+1));
        }
    }
    //利用距离公式，计算中心点与当前像素点的距离
    float s1 = sqrt(pow(v1.x-x, 2.0) + pow(v1.y-y, 2.0));
    float s2 = sqrt(pow(v2.x-x, 2.0) + pow(v2.y-y, 2.0));

    //选择距离小的则为六边形的中心点，且获取它的颜色
    vn = (s1 < s2) ? v1 : v2;
    //获取六边形中心点的颜色值

    vec4 color = texture2D(vTexture, vn);
    gl_FragColor = color;
}
