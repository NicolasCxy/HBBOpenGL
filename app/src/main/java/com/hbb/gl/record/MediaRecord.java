package com.hbb.gl.record;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;

import com.hbb.gl.egBase.EGLBase;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 录制 - 流程
 * - 1、初始化编码器，获取surface
 * - 2、创建新的GL线程，要不然会影响渲染：1、创建display配置属性 2、创建上下文 3、display和编码器surface进行绑定，生成GLSurface
 * - 3、GLSurface 和 surface 关联，片源渲染数据 -> GLSurface -> surface ,surface是编码器提供的，直接到编码器。
 * - 4、拿到FBO的纹理ID，不能直接获取数据，需要通过GL程序将数据渲染到GLSurface中
 * - 5、编码器内置编码之后，取出来进行保存。
 * <p>
 * 疑问 ：
 * - 如何把数据拿出来？ （文章说通过glMapBuffer 把显存和内存进行映射就可以，后续跟进一下）
 */
public class MediaRecord {

    private Context mContext;
    private String mPath;
    private int mWidth;
    private int mHeight;
    private EGLContext mEglContext;
    private Surface mInputSurface;
    private MediaCodec mMediaCodec;
    private Handler mGLHandler;
    private MediaMuxer mediaMuxer;
    private boolean isStart;
    private EGLBase mEgLBase;
    private float mSpeed = 1;//5
    private int dataIndex = -1;
    private MediaFormat mediaFormat;

    public MediaRecord(Context mContext, String mPath, int mWidth, int mHeight, EGLContext mEglContext) {
        this.mContext = mContext;
        this.mPath = mPath;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        this.mEglContext = mEglContext;

//        initMediaCode();
//        initGLThread();
    }


    private void initMediaCode() {
        try {
            Log.i("cxyTest", "initMediaCode - > mWidth: " + mWidth + ",height: " + mHeight);
            mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight);

            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1500_000);
            //帧率
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20);
            //关键帧间隔
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);
            //设置颜色
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);


            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);

            mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            mInputSurface = mMediaCodec.createInputSurface();

            //初始化合成器
            mediaMuxer = new MediaMuxer(mPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            Log.i("cxyTest", "initMediaCode - > mediaMuxer: " + mediaMuxer);

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("cxyTest", "initMediaCode -> error :" + e.getMessage());

        }

    }

    private void initGLThread() {
        HandlerThread handlerThread = new HandlerThread("VideoCodec");
        handlerThread.start();
        mGLHandler = new Handler(handlerThread.getLooper());

        mGLHandler.post(new Runnable() {
            @Override
            public void run() {
                mEgLBase = new EGLBase(mContext, mWidth, mHeight, mInputSurface, mEglContext);
                mMediaCodec.start();
                isStart = true;
            }
        });
    }


    public void start() {
//        isStart = true;
        initMediaCode();
        initGLThread();
//        mGLHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                mMediaCodec.start();
//            }
//        });
    }


    /**
     * 开始编码
     *
     * @param textureId fbo纹理ID，存放了处理后的数据
     * @param timestamp 时间戳
     */
    public void enCodeFrame(int textureId, long timestamp) {
        if (!isStart) {
            return;
        }

        mGLHandler.post(new Runnable() {
            @Override
            public void run() {
                mEgLBase.draw(textureId, timestamp);
                //读取编码数据
                getEncodeData(false);
            }
        });
    }


    /**
     * 读取编码数据
     *
     * @param endOfStream
     */
    private void getEncodeData(boolean endOfStream) {
        if (endOfStream) {
            //输出结束符
            mMediaCodec.signalEndOfInputStream();
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//        while (true) {
        int index = mMediaCodec.dequeueOutputBuffer(bufferInfo, 10_000);
        Log.i("cxyTest", "getEncodeData -> index@!: " + index);
        if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            MediaFormat outputFormat = mMediaCodec.getOutputFormat();
            dataIndex = mediaMuxer.addTrack(outputFormat);
            mediaMuxer.start();
        }

        while (index > 0) {
            ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(index);
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                bufferInfo.size = 0;
            }

            if (bufferInfo.size > 0) {
                bufferInfo.presentationTimeUs = (long) (bufferInfo.presentationTimeUs / mSpeed);
            }
            outputBuffer.position(bufferInfo.offset);
            outputBuffer.limit(bufferInfo.size);

            mediaMuxer.writeSampleData(dataIndex, outputBuffer, bufferInfo);

            mMediaCodec.releaseOutputBuffer(index, false);
            index = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
//            Log.i("cxyTest", "getEncodeData -> index: " + index);
//            switch (index) {
//                case MediaCodec.INFO_TRY_AGAIN_LATER:       //编码错误可以重试一把
//                    if (!endOfStream) {
//                        break;
//                    }
//                    break;
//                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
//                    MediaFormat outputFormat = mMediaCodec.getOutputFormat();
//                    dataIndex = mediaMuxer.addTrack(outputFormat);
//                    mediaMuxer.start();
//                    break;
//                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:    //输出队列发生改变
//                    break;
//                default:
//
//                    break;
//            }
//
//        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//            break;
//        }

//        }

    }


    private void getCodec(boolean endOfStream) {
//            从surface 取到数据  并且输出到视频文件中
        if (endOfStream) {
//            编码出一个流结束符
            mMediaCodec.signalEndOfInputStream();
        }
        //输出缓冲区
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//if()     编码
        while (true) {
//<0 >=0  索引   1    -1   2
            int index = mMediaCodec.dequeueOutputBuffer(bufferInfo, 10_000);
            if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // 如果是停止 我继续循环
                // 继续循环 就表示不会接收到新的等待编码的图像
                // 相当于保证mediacodec中所有的待编码的数据都编码完成了，不断地重试 取出编码器中的编码好的数据
                // 标记不是停止 ，我们退出 ，下一轮接收到更多数据再来取输出编码后的数据

//                咱们的数据没有编码好
                if (!endOfStream) {
                    break;
                }


            } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//直播    一路    直播了     添加了 直播  而

                //开始编码 就会调用一次
                MediaFormat outputFormat = mMediaCodec.getOutputFormat();
                //配置封装器
                // 增加一路指定格式的媒体流 视频
                dataIndex = mediaMuxer.addTrack(outputFormat);
                mediaMuxer.start();
//编码  和 分装   H264  封装

            } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//             少编码   一帧   大1  不大2


            } else {
//                index》=0
//成功 取出一个有效的输出
                ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(index);
                //如果获取的ByteBuffer 是配置信息 ,不需要写出到mp4
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    bufferInfo.size = 0;
                }
                if (bufferInfo.size != 0) {
                    //写出 outputBuffer数据   ----》  封装格式中 mp4  极快
                    bufferInfo.presentationTimeUs = (long) (bufferInfo.presentationTimeUs / mSpeed);

                    //写到mp4
                    //根据偏移定位  bufferInfo.offset  ==0
                    outputBuffer.position(bufferInfo.offset);
                    //ByteBuffer 可读写总长度
                    outputBuffer.limit(bufferInfo.offset + bufferInfo.size);

                    mediaMuxer.writeSampleData(dataIndex, outputBuffer, bufferInfo);

                }

                //输出缓冲区 我们就使用完了，可以回收了，让mediacodec继续使用
                mMediaCodec.releaseOutputBuffer(index, false);


                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break;
                }

            }
        }

    }


    public void stop() {
        isStart = false;
        mGLHandler.post(new Runnable() {
            @Override
            public void run() {
                getEncodeData(true);
//                mediaMuxer.stop();
                mEgLBase.release();
                mMediaCodec.release();
                mMediaCodec = null;
                mediaMuxer.stop();
                mediaMuxer.release();
                mediaMuxer = null;
                mEgLBase = null;
                mInputSurface = null;
            }
        });
    }
}
