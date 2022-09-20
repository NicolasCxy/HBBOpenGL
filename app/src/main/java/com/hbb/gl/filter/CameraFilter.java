package com.hbb.gl.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import com.hbb.gl.R;
import com.hbb.gl.utils.OpenGLUtils;

/**
 * 离屏渲染 - 不显示，只处理数据
 */
public class CameraFilter extends AbstractFilter {
    private static final String TAG = "CameraFilter";

//    private final int vMatrix;

    public CameraFilter(Context context) {
        super(context, R.raw.camera_vert, R.raw.camera_frag_6);
//        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
    }

    public void destroyFrameBuffers() {
        //删除fbo的纹理
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(1, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }
        //删除fbo
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
    }

    private int[] mFrameBuffers;
    private int[] mFrameBufferTextures;

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
                mFrameBuffers = new int[1];
                //1、创建FBO
                GLES20.glGenFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
                //2、创建FBO对应的纹理
                mFrameBufferTextures = new int[1]; //用来记录纹理id
                //创建纹理
                OpenGLUtils.glGenTextures(mFrameBufferTextures);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
                //进行绑定
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                        0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                //3、FBO - 纹理进行绑定
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                        GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);
        //        4、解绑
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);

        Log.d(TAG, "onCxySize！@！  -width: " + width + ",height: " + height);

    }

    @Override
    public int onDraw(int textureId, float[] matrix) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        super.onDraw(textureId,matrix);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        //返回FBO数据
        return mFrameBufferTextures[0];

//        Log.i(TAG, "onCxyDraw!!");
        //设置显示窗口
//        GLES20.glViewport(0, 0, mWidth, mHeight);
//        //不调用的话就是默认的操作glsurfaceview中的纹理了。显示到屏幕上了
//        //这里我们还只是把它画到fbo中(缓存)
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
//        //使用着色器
//        GLES20.glUseProgram(program);
//        //传递坐标
//        vertexBuffer.position(0);
//        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
//        GLES20.glEnableVertexAttribArray(vPosition);
//
//        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);
//
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        //因为这一层是摄像头后的第一层，所以需要使用扩展的  GL_TEXTURE_EXTERNAL_OES
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
//        GLES20.glUniform1i(vTexture, 0);
//        //绘制
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
//        //绘制完毕后进行解绑
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
////        返回fbo的纹理id
//        return mFrameBufferTextures[0];
    }


    @Override
    protected void beforeDraw() {
        Log.i(TAG, "beforeDraw: ");
        GLES20.glUniformMatrix4fv(vMatrix, 1, false,mtx , 0);
    }
}
