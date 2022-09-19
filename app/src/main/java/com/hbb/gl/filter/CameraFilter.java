package com.hbb.gl.filter;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.hbb.gl.R;
import com.hbb.gl.utils.OpenGLUtils;

/**
 * 离屏渲染 - 不显示，只处理数据
 */
public class CameraFilter extends AbstractFilter {
    private static final String TAG = "CameraFilter";

    private final int vMatrix;

    public CameraFilter(Context context) {
        super(context, R.raw.camera_vert, R.raw.camera_frag_6);
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
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
    }

    @Override
    public int onDraw(int textureId) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        super.onDraw(textureId);
        //返回FBO数据
        return mFrameBufferTextures[0];
    }

    @Override
    protected void beforeDraw() {
        Log.i(TAG, "beforeDraw:@ " + mtx);
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mtx, 0);
    }
}
