package com.hbb.gl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRender implements GLSurfaceView.Renderer, Preview.OnPreviewOutputUpdateListener, SurfaceTexture.OnFrameAvailableListener {

    private Context mContext;
    private SurfaceTexture mSurfaceTexture;
    //camera 在 GPU 数据缓冲区的位置
    private  int textures = 0;

    private GLListener mGLListener;
    private ScreenFilter mScreenFilter;
    private  MyGLView myGLView;

    public CameraRender(Context context, MyGLView myGLView) {
        this.mContext = context;
        this.myGLView = myGLView;
        initCamera();
    }

    private void initCamera() {
        CameraHelper cameraHelper = new CameraHelper
                ((LifecycleOwner)  mContext,
                        this);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mSurfaceTexture.attachToGLContext(textures);

        mSurfaceTexture.setOnFrameAvailableListener(this);
        //初始化OPENGL程序
        mScreenFilter = new ScreenFilter(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mSurfaceTexture.updateTexImage();

        float[] mtx = new float[16];
        mSurfaceTexture.getTransformMatrix(mtx);

        mScreenFilter.onDraw(myGLView.getWidth(),myGLView.getHeight(),mtx,textures);
    }

    @Override
    public void onUpdated(Preview.PreviewOutput output) {
        //存放了Camera的原始数据
        mSurfaceTexture = output.getSurfaceTexture();
    }

    /**
     * Camera有数据通知OpenGL更新
     * @param surfaceTexture
     */
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if(null != mGLListener){
            mGLListener.updateGL();
        }
    }

    public void setGLListener(GLListener mGLListener) {
        this.mGLListener = mGLListener;
    }



}
