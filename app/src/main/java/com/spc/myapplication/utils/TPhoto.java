package com.spc.myapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

/**
 * 调用相机拍摄
 */
public class TPhoto {
    private static Context mContext;
    private static AppCompatActivity mActivity;
    private static Uri imageUri;
    private static File outImgPath;

    //根目录
    private static final String BASE_DIR = Environment.getExternalStorageDirectory().getPath();
    private static final String ROOT_DIR = "testUpload/srcPath/";

    public static void photo(Context context) {
        photo1(context);
    }

    private static void Init() {
        mActivity = null;
        mContext = null;
        imageUri = null;
        outImgPath = null;
    }

    public static void upload() {
        if(imageUri != null) {
            //重构图片文件名称
            File f = new File(BASE_DIR, ROOT_DIR + ImgUtils.createPicName());
            outImgPath.renameTo(f);

            System.out.println(outImgPath.getAbsolutePath());

            Log.i("IMAGE_PATH", f.getPath());
            Upload.runUpload(f.getPath());
        }
    }

    private static void photo1(Context context) {
        Init();
        mContext = context;
        mActivity = (AppCompatActivity) mContext;
        savePicture();
    }

    private static void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        mActivity.startActivityForResult(intent, RequestCode.TOOK_PHOTO);
    }

    /**
     * 保存拍摄的图片
     */
    private static void savePicture() {
        outImgPath = new File(BASE_DIR, ROOT_DIR + "temp.jpg");
        try {
            if(outImgPath.exists()) {
                outImgPath.delete();
            }
            outImgPath.createNewFile();
            System.out.println(outImgPath.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(
                    mContext,
                    "com.spc.myapplication.fileprovider",
                    outImgPath);
        } else {
            imageUri = Uri.fromFile(outImgPath);
        }
//        if(imageUri != null) {
//            Toast.makeText(mContext, imageUri.getPath(), Toast.LENGTH_LONG).show();
//            System.out.println(imageUri.getPath());
//        }
        openCamera();
    }
}
