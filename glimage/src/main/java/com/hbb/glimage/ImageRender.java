package com.hbb.glimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.hbb.glimage.filter.ImageFilter;
import com.hbb.glimage.utils.OpenGLUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ImageRender implements GLSurfaceView.Renderer {


    Context mContext;
    private ImageFilter imageFilter;
    private int texture;

    public ImageRender(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //初始化数据源
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test);
        //初始化openGl程序
        imageFilter = new ImageFilter(mContext);
        texture = OpenGLUtils.makeTextureByBitmap(bitmap);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //启动程序开始绘制
        imageFilter.onDraw(texture);
    }
}
