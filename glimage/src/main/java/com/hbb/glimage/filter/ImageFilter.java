package com.hbb.glimage.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.hbb.glimage.R;

import static android.opengl.GLES20.GL_TEXTURE_2D;

public class ImageFilter extends AbstractFilter {

//    private final int texture;

    public ImageFilter(Context context) {
        super(context, R.raw.base_vert, R.raw.base_frag);
//        texture = initTexture(bitmap);
    }

//
//    public int getTexture() {
//        return texture;
//    }

    public int initTexture(Bitmap bitmap){
        /**
         * 1.创建纹理
         * 2.设置水平、垂直拉伸显示模式
         * 3.设置放大缩小效果，马赛克还是锐化
         * 4.bitmap和纹理进行绑定
         */

        int[] textures = new int[1];
//        创建纹理
        GLES20.glGenTextures(1, textures, 0);
        //绑定纹理
        GLES20.glBindTexture(GL_TEXTURE_2D, textures[0]);

        //
        GLES20.glTexParameterf(GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);

        GLES20.glTexParameterf(GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);

//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
//                GLES20.GL_CLAMP_TO_EDGE);

//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
//                GLES20.GL_NEAREST);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
//                GLES20.GL_LINEAR);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);



        //绑定
        GLUtils.texImage2D(GL_TEXTURE_2D,0,bitmap,0);

        return textures[0];
    }
}
