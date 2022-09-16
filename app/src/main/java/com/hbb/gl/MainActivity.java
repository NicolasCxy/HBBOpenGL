package com.hbb.gl;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    /**
     *  GL渲染流程
     *  1、绑定Render，初始化Camera，获取surfaceTexture，进行更新监听
     *  2、有数据更新之后刷新GLSurfaceView
     *  3、启动OPEN GL 模块
     *     - 加载片源代码，加载顶点代码
     *     - 创建程序记载片源、着色进行编译。将数据放到ByteBuffer中进行传递
     *     - onSurfaceChanged触发的时候进行绘制
     *     - 创建图层，片源渲染时需要把缓冲区和图层进行绑定
     *     - 获取顶点参数、获取片源参数，进行赋值
     *     - 通过GL进行绘制
     *     - （记得传矩阵进去，进行矫正）
     *  4、数据走向：
     *     - Camera 数据直接输出到GPU缓冲区
     *     - 创建图层进和缓存区进行绑定
     *     - 将图层传到片源程序
     *     - 通过采样器 根据坐标和图层采数据，最终进行绘制渲染
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            }, 1);

        }
        return false;
    }
}