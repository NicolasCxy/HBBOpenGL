package com.hbb.gl.filter;

import android.content.Context;
import android.opengl.GLES11;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;


import com.hbb.gl.utils.OpenGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * GL程序加载类
 */
public class AbstractFilter {
    //顶点坐标
    float[] VERTEX = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    //纹理坐标
    float[] TEXTURE = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    private final FloatBuffer vertexBuffer, textureBuffer;
    protected final int program;
    private int vPosition;
    private int vCoord;
    private int vTexture;
    protected float[] mtx;

    public int mWidth;
    public int mHeight;

    public AbstractFilter(Context context, int vertex, int fragment) {
        String vertexShader = OpenGLUtils.readRawTextFile(context, vertex);
        String fragSharder = OpenGLUtils.readRawTextFile(context, fragment);
        //创建总程序
        program = OpenGLUtils.makeProgram(vertexShader, fragSharder);

        //获取GL程序属性 （就是属性的地址值，对Java层来说没意义）
        vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        vCoord = GLES20.glGetAttribLocation(program, "vCoord");
//        int vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        //获取片源参数，纹理对象，用来接图层
        vTexture = GLES20.glGetUniformLocation(program, "vTexture");

        //创建数据源
        vertexBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.clear();
        vertexBuffer.put(VERTEX);


        textureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.clear();
        textureBuffer.put(TEXTURE);
    }

    public int onDraw(int textureId) {
     return onDraw(textureId, null);
    }

    public int onDraw(int textureId, float[] mtx) {
        this.mtx = mtx;
        GLES20.glUseProgram(program);
        vertexBuffer.position(0);
        textureBuffer.position(0);

        //顶点赋值 - 世界坐标
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        //启用
        GLES20.glEnableVertexAttribArray(vPosition);
        //顶点赋值 - 片源着色器
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        //启用
        GLES20.glEnableVertexAttribArray(vCoord);

        //启用图层
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        //关联
        GLES20.glUniform1i(vTexture, 0);

        beforeDraw();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        //将处理后的纹理返回
        return textureId;
    }

    protected void beforeDraw() {

    }

    public void setSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }
}
