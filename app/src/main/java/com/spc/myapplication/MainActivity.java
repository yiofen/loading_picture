package com.spc.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.spc.myapplication.broadcast.NetWorkBroadcastReceiver;
import com.spc.myapplication.utils.NetUtils;
import com.spc.myapplication.utils.PermManager;
import com.spc.myapplication.utils.RequestCode;
import com.spc.myapplication.utils.TPhoto;
import com.spc.myapplication.utils.Upload;

public class MainActivity extends AppCompatActivity {

    private Boolean isRegistered = false;
    private NetWorkBroadcastReceiver netWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //设置全屏显示
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        netWork = new NetWorkBroadcastReceiver();
        NetWorkBroadcastReceiver.registerReceiver(MainActivity.this, netWork);
        isRegistered = true;

        findViewById(R.id.localUpload).setOnClickListener(onClickListener);
        findViewById(R.id.shootUpload).setOnClickListener(onClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isRegistered) {
            NetWorkBroadcastReceiver.unRegisterReceiver(MainActivity.this, netWork);
            isRegistered = false;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!NetUtils.checkNetConnected()) {//检查网络是否连接
                Toast.makeText(MainActivity.this,
                                "当前网络未连接",
                                Toast.LENGTH_LONG).show();
            } else {//已开启网络
                switch (v.getId()) {
                    case R.id.localUpload:
                        //本地上传图片
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//API>23
                            //获取动态权限
                            if (ContextCompat.checkSelfPermission
                                    (MainActivity.this, PermManager.PERMISSIONS[0]) !=
                                    PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(
                                        MainActivity.this,
                                        new String[]{PermManager.PERMISSIONS[0]},
                                        RequestCode.REQUEST_PERM);
                            } else {//已获取权限
                                Upload.upload(MainActivity.this);
                            }
                        } else {
                            Upload.upload(MainActivity.this);
                        }
                        break;
                    case R.id.shootUpload:
                        //拍摄上传图片
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(MainActivity.this,
                                PermManager.PERMISSIONS[2]) != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(
                                        MainActivity.this,
                                        new String[]{PermManager.PERMISSIONS[0],
                                        PermManager.PERMISSIONS[2]}, RequestCode.TOOK_PHOTO);
                            } else {
                                TPhoto.photo(MainActivity.this);
                            }
                        } else {
                            TPhoto.photo(MainActivity.this);
                        }
                        break;
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case RequestCode.REQUEST_PERM:
                //选择相册上传
                if(grantResults.length > 0 && grantResults[0] == PackageManager.
                        PERMISSION_GRANTED) {
                    Upload.upload(MainActivity.this);
                } else {
                    Toast.makeText(this, "若要使用该功能，需要开启文件读写权限",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case RequestCode.TOOK_PHOTO:
                //拍摄上传图片并保存
                if(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    TPhoto.photo(MainActivity.this);
                } else {
                    Toast.makeText(this, "若要使用该功能，需要开启文件读写和相机权限",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.CHOOSE_PHOTO:
                if(resultCode == RESULT_OK) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Upload.handleImageOnKitKat(data, MainActivity.this);
                    } else {
                        Upload.handleImageBeforeKitKat(data, MainActivity.this);
                    }
                }
                break;
            case RequestCode.TOOK_PHOTO:
                if(resultCode == RESULT_OK) {
                    TPhoto.upload();
                }
                break;
            default:
        }
    }

}


