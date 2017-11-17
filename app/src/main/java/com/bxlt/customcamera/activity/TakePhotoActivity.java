package com.bxlt.customcamera.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.bxlt.customcamera.R;
import com.bxlt.customcamera.camera.CameraCall;
import com.bxlt.customcamera.camera.CameraPreviewView;
import com.bxlt.customcamera.utils.FileUtils;
import com.bxlt.customcamera.utils.ScaleGestureListener;

import java.io.File;

/**
 * 拍照界面
 * Created by Lrxc on 2017/11/13.
 */

public class TakePhotoActivity extends AppCompatActivity implements View.OnClickListener, CameraCall {
    private String TAG = "lrxc";
    private CameraPreviewView camePreview;
    private int mSgType = 2;//1 不开启闪光灯 2自动 3长亮
    private ImageView mIvFlash;// 闪光灯按钮
    private ScaleGestureDetector gestureDetector;//缩放手势

    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerapreview);

        initView();
    }

    private void initView() {
        camePreview = (CameraPreviewView) findViewById(R.id.camePreview);
        mIvFlash = (ImageView) findViewById(R.id.img_left_flash);
        camePreview.setOnCameraListener(this);
        //点击对焦
//        camePreview.setOnClickListener(this);
        mIvFlash.setOnClickListener(this);
        findViewById(R.id.camera_take).setOnClickListener(this);
        findViewById(R.id.camera_front).setOnClickListener(this);
        findViewById(R.id.imageView1).setOnClickListener(this);

        //缩放手势
        gestureDetector = new ScaleGestureDetector(this, new ScaleGestureListener(camePreview));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_take:
                dialog = ProgressDialog.show(this, "", "拍照中", true, true);
                camePreview.takePicture(); //拍照
                break;
            case R.id.camera_front:
                //切换摄像头
                camePreview.switchFrontCamera();
                break;
            case R.id.img_left_flash:
                //闪光灯模式
                switchFlashMode();
                break;
            case R.id.camePreview:
                //手动对焦
                camePreview.autoFocus();
                break;
            case R.id.imageView1:
                finish();
                break;
        }
    }

    //切换闪关灯模式
    private void switchFlashMode() {
        switch (mSgType) {
            case 1:
                mIvFlash.setImageResource(R.drawable.ic_camera_top_bar_flash_auto_normal);
                camePreview.setIsOpenFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                mSgType = 2;
                break;
            case 2:
                mIvFlash.setImageResource(R.drawable.ic_camera_top_bar_flash_torch_normal);
                camePreview.setIsOpenFlashMode(Camera.Parameters.FLASH_MODE_ON);
                mSgType = 3;
                break;
            case 3:
                mIvFlash.setImageResource(R.drawable.ic_camera_top_bar_flash_off_normal);
                camePreview.setIsOpenFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mSgType = 1;
                break;
        }
    }


    @Override
    public void onCameraData(byte[] data) {
        //保存到本地
        File file = new FileUtils().saveToSDCard(data);
        dialog.dismiss();

        //跳转到预览界面
        Intent intent = new Intent(this, SigninShowActivity.class);
        intent.putExtra("jpgFile", file.getAbsolutePath());//图片保存路径
        intent.putExtra("cameraPosition", camePreview.cameraPosition);//当前前后摄像头
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK)
            finish();//签到成功后关闭拍照页面
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //手势缩放识别手势
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}