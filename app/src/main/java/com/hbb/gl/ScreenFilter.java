package com.hbb.gl;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ScreenFilter {
    private Context mContext;

    float[] VERTEX = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    float[] TEXTURE = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
    };


    private final int program;
    private final FloatBuffer vertexBuffer, textureBuffer;
    private int vPosition;
    private int vCoord;
    private int vTexture;
    private int vMatrix;

    public ScreenFilter(Context mContext) {
        //加载顶点程序
        String vertexShader = readRawTextFile(mContext, R.raw.camera_vert);
        //创建片源程序，加载代码
        int vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vShader, vertexShader);
        //编译，配置
        GLES20.glCompileShader(vShader);

        int[] state = new int[1];
        GLES20.glGetShaderiv(vShader, GLES20.GL_COMPILE_STATUS, state, 0);

        if (state[0] != GLES20.GL_TRUE) {
            //失败
            throw new IllegalStateException("load vertex shader:" + GLES20.glGetShaderInfoLog
                    (vShader));
        }

        //====================================加载片源===================================

        String fragSharder = readRawTextFile(mContext, R.raw.camera_frag_6);
        int fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fShader, fragSharder);
//java  javac  class
        //编译（配置）
        GLES20.glCompileShader(fShader);
//编译成功
        //查看配置 是否成功
        state = new int[1];
        GLES20.glGetShaderiv(fShader, GLES20.GL_COMPILE_STATUS, state, 0);
        if (state[0] != GLES20.GL_TRUE) {
            //失败
            throw new IllegalStateException("load fragment shader:" + GLES20.glGetShaderInfoLog
                    (fShader));
        }

        //====================================创建总程序，进行关联===================================
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vShader);
        GLES20.glAttachShader(program, fShader);

        //进行连接，关联程序
        GLES20.glLinkProgram(program);

        //建立byteBuffer,进行赋值
        vertexBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.clear();
        vertexBuffer.put(VERTEX);

        textureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.clear();
        textureBuffer.put(TEXTURE);


    }


    public void onDraw(int width, int height, float[] mtx, int textures) {
        GLES20.glViewport(0, 0, width, height);

        //使用程序
        GLES20.glUseProgram(program);

        vertexBuffer.position(0);
        textureBuffer.position(0);

        //获取顶点参数
        vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        vCoord = GLES20.glGetAttribLocation(program, "vCoord");
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");

        //获取片源参数，纹理对象，用来接图层
        vTexture = GLES20.glGetUniformLocation(program, "vTexture");

        //顶点赋值 - 世界坐标
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        //启用
        GLES20.glEnableVertexAttribArray(vPosition);
        //顶点赋值 - 纹理坐标
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        //启用
        GLES20.glEnableVertexAttribArray(vCoord);

        //创建图层
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE0, textures);

        //图层交给OpenGl处理
        GLES20.glUniform1i(vTexture,0);
        GLES20.glUniformMatrix4fv(vMatrix,1,false,mtx,0);


        //进行绘制  三角形、开始0 、4个点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);


    }


    public String readRawTextFile(Context context, int rawId) {
        InputStream is = context.getResources().openRawResource(rawId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


}
