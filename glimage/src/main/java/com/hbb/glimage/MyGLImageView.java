package com.hbb.glimage;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLImageView extends GLSurfaceView {
    public MyGLImageView(Context context) {
        this(context,null);
    }

    public MyGLImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setRenderer(new ImageRender(getContext()));
    }
}
