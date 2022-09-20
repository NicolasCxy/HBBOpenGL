package com.hbb.gl.egBase;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.view.Surface;

import com.hbb.gl.filter.VideoFilter;

public class EGLBase {

    private EGLDisplay mEglDisplay;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;
    private final VideoFilter mScreenFilter;

    public EGLBase(Context context, int width, int height, Surface surface, EGLContext eglContext) {
        //创建GLThread,主要目的是为了不影响渲染

        /**
         * 1、创建Display 虚拟的
         * 2、创建上下文
         * 3、进行Display 和 surface进行绑定，后续直接渲染到Surface上面
         */

        createEGL(eglContext);
        //把Surface贴到  mEglDisplay ，发生关系
        int[] attrib_list = {
                EGL14.EGL_NONE
        };

        //最后数据会绘制到mEglSurface，mEglDisplay画框，surface画布，
        mEglSurface = EGL14.eglCreateWindowSurface(mEglDisplay, mEglConfig, surface, attrib_list, 0);
        // 绑定当前线程的显示设备及上下文， 之后操作opengl，就是在这个虚拟显示上操作
        if (!EGL14.eglMakeCurrent(mEglDisplay,mEglSurface,mEglSurface,mEglContext)) {
            throw  new RuntimeException("eglMakeCurrent 失败！");
        }

        //初始化绘制程序
        mScreenFilter = new VideoFilter(context);
        mScreenFilter.setSize(width,height);
    }

    private void createEGL(EGLContext eglContext) {
        mEglDisplay =  EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);

        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }
        //初始化显示器
        int[] version = new int[2];
        // 12.1020203
        //major：主版本 记录在 version[0]
        //minor : 子版本 记录在 version[1]
        if (!EGL14.eglInitialize(mEglDisplay, version, 0, version, 1)) {
            throw new RuntimeException("eglInitialize failed");
        }

        // egl 根据我们配置的属性 选择一个配置
        int[] attrib_list = {
                EGL14.EGL_RED_SIZE, 8, // 缓冲区中 红分量 位数
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, //egl版本 2
                EGL14.EGL_NONE
        };


        EGLConfig[] configs = new EGLConfig[1];
        int[] num_config = new int[1];
        // attrib_list：属性列表+属性列表的第几个开始
        // configs：获取的配置 (输出参数)
        //num_config: 长度和 configs 一样就行了
        if (!EGL14.eglChooseConfig(mEglDisplay, attrib_list, 0,
                configs, 0, configs.length, num_config, 0)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }

        mEglConfig = configs[0];
        int[] ctx_attrib_list = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, //egl版本 2
                EGL14.EGL_NONE
        };

        mEglContext = EGL14.eglCreateContext(mEglDisplay, mEglConfig, eglContext, ctx_attrib_list, 0);

        // 创建失败
        if (mEglContext == EGL14.EGL_NO_CONTEXT) {
            throw new RuntimeException("EGL Context Error.");
        }
        //此刻EGL线程就创建好了

    }

    public void draw(int textureId,long timestamp){

        //绑定当前线程
        if(!EGL14.eglMakeCurrent(mEglDisplay,mEglSurface,mEglSurface,mEglContext)){
            throw  new RuntimeException("eglMakeCurrent 失败！");
        }

        //渲染到 mEglSurface
        mScreenFilter.onDraw(textureId);
        //设置时间戳
        EGLExt.eglPresentationTimeANDROID(mEglDisplay,mEglSurface,timestamp);
        //刷新到surface，此刻数据以及到了dsp芯片了，mediaCoder 进行编码
        EGL14.eglSwapBuffers(mEglDisplay,mEglSurface);
    }

    /**
     * 销毁自定义GL线程
     */
    public void release() {
        //        销毁  mEglSurface  和  mEglDisplay绑定关系
        EGL14.eglDestroySurface(mEglDisplay, mEglSurface);
        EGL14.eglMakeCurrent(mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT);
//          mEglDisplay    设值成没有上下文
        EGL14.eglDestroyContext(mEglDisplay, mEglContext);
//          mEglDisplay   mEglContext 进行销毁
        EGL14.eglReleaseThread();
//         关机  mEglDisplay
        EGL14.eglTerminate(mEglDisplay);
    }
}
