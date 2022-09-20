package com.hbb.gl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;

import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;

import com.hbb.gl.filter.CameraFilter;
import com.hbb.gl.filter.VideoFilter;
import com.hbb.gl.record.MediaRecord;
import com.hbb.gl.utils.CameraHelper;

import java.io.File;

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
    private MediaRecord mMediaRecord;

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
        //camera相关
        mSurfaceTexture.attachToGLContext(textures);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        //初始化OPENGL程序
        mScreenFilter = new VideoFilter(mContext);
        cameraFilter = new CameraFilter(mContext);
        String path
                = new File(Environment.getExternalStorageDirectory(), "0920Input.mp4").getAbsolutePath();
        EGLContext eglContext = EGL14.eglGetCurrentContext();
        //初始化录屏程序
        mMediaRecord = new MediaRecord(mContext,path,480,640,eglContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
//        mScreenFilter.setSize(width,height);
        cameraFilter.setSize(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //请求更新数据
        mSurfaceTexture.updateTexImage();
        //获取变化矩阵，做画面矫正
        mSurfaceTexture.getTransformMatrix(mtx);
        //离屏渲染
        int textureId = cameraFilter.onDraw(textures, mtx);
        //拿到FBO的数据进行绘制
        mScreenFilter.onDraw(textureId);
        //通过FBO纹理ID拿到数据进行编码绘制
        mMediaRecord.enCodeFrame(textureId,mSurfaceTexture.getTimestamp());
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

    public void stopRecord() {
        mMediaRecord.stop();
    }

    public void startRecord() {
        mMediaRecord.start();
    }
}
