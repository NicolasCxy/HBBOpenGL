package com.hbb.gl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLView extends GLSurfaceView implements GLListener {

    private CameraRender cameraRender;

    public MyGLView(Context context) {
        super(context);
    }

    public MyGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置版本号
        setEGLContextClientVersion(2);
        //创建Render
        cameraRender = new CameraRender(getContext(),this);
        cameraRender.setGLListener(this);
        setRenderer(cameraRender);
        //手动模式
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);


    }

    @Override
    public void updateGL() {
        requestRender();
    }

    public void stopRecord() {
        cameraRender.stopRecord();
    }

    public void startRecord() {
        cameraRender.startRecord();
    }
}
