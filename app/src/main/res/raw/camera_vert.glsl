//世界坐标
attribute vec4 vPosition;
//纹理坐标
attribute vec4 vCoord;
//顶点坐标，传给着色器去对应位置采样
varying vec2 aCoord;
//变化矩阵，用来矫正视图
uniform mat4 vMatrix;

void main() {
    gl_Position = vPosition;
    aCoord = (vMatrix * vCoord).xy;
}
