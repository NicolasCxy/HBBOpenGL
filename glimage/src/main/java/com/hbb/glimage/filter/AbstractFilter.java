package com.hbb.glimage.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.hbb.glimage.utils.OpenGLUtils;

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

    private  FloatBuffer vertexBuffer,textureBuffer;
    private int program;
    private int vPosition;
    private int vCoord;
    private int vTexture;

//    public AbstractFilter(Context context){
//        AbstractFilte
//    }

    public AbstractFilter(Context context, int vertex, int fragment) {
        String vertexShader = OpenGLUtils.readRawTextFile(context, vertex);
        String fragSharder = OpenGLUtils.readRawTextFile(context, fragment);
        //创建总程序
        program = OpenGLUtils.makeProgram(vertexShader, fragSharder);

        //创建数据源
        vertexBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.clear();
        vertexBuffer.put(VERTEX);


        textureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.clear();
        textureBuffer.put(TEXTURE);
    }

    public void onDraw(int textureId){
        GLES20.glUseProgram(program);
        vertexBuffer.position(0);
        textureBuffer.position(0);

        vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        vCoord = GLES20.glGetAttribLocation(program, "vCoord");
//        int vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        //获取片源参数，纹理对象，用来接图层
        vTexture = GLES20.glGetUniformLocation(program, "vTexture");

        //顶点赋值 - 世界坐标
        GLES20.glVertexAttribPointer(vPosition,2,GLES20.GL_FLOAT,false,0,vertexBuffer);
        //启用
        GLES20.glEnableVertexAttribArray(vPosition);
        //顶点赋值 - 片源着色器
        GLES20.glVertexAttribPointer(vCoord,2,GLES20.GL_FLOAT,false,0,textureBuffer);
        //启用
        GLES20.glEnableVertexAttribArray(vCoord);

        //启用图层
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        //关联
        GLES20.glUniform1i(vTexture,0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

    }
}
