package com.hbb.gl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;

import com.hbb.gl.filter.CameraFilter;
import com.hbb.gl.filter.VideoFilter;
import com.hbb.gl.utils.CameraHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRender implements GLSurfaceView.Renderer, Preview.OnPreviewOutputUpdateListener, SurfaceTexture.OnFrameAvailableListener {

    private Context mContext;
    private SurfaceTexture mSurfaceTexture;
    //camera 在 GPU 数据缓冲区的位置
    private  int textures = 0;

    private GLListener mGLListener;
    private VideoFilter mScreenFilter;
    private  MyGLView myGLView;
    private CameraFilter cameraFilter;
    float[] mtx = new float[16];

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
        mScreenFilter = new VideoFilter(mContext);
        cameraFilter = new CameraFilter(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        cameraFilter.setSize(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //请求更新数据
        mSurfaceTexture.updateTexImage();
        //获取变化矩阵，做画面矫正
        mSurfaceTexture.getTransformMatrix(mtx);
        //进行绘制
        int textureId = cameraFilter.onDraw(textures, mtx);
        //处理上次搞完的数据
        mScreenFilter.onDraw(textureId);
//        mScreenFilter.onDraw(myGLView.getWidth(),myGLView.getHeight(),mtx,textures);
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
