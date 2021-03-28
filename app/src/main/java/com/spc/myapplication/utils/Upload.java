package com.spc.myapplication.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 上传图片给服务器
 */
public class Upload {
    private static Context mContext;
    private static AppCompatActivity mActivity;

    //根目录
    private static final String BASE_DIR = Environment.getExternalStorageDirectory().getPath();

    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/*");
    private static final OkHttpClient client = new OkHttpClient();

    private Upload() {
    }

    public static void upload(Context context) {
        //打开相册,处理选中照片
        getImg(context);
    }

    private static void getImg(Context context) {
        mContext = context;
        mActivity = (AppCompatActivity) mContext;
        openAlbum();
    }

    public static void runUpload(String imagePath) {
        if(imagePath != null) {
            //将选中照片封装为File对象
            File file = new File(imagePath);
            run(file);
        }
    }

    private static void run(File f) {
        final File img = f;
        new Thread() {
            @Override
            public void run() {
                //子线程需要做的工作
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file",img.getName(),
                                RequestBody.create(img, MEDIA_TYPE_JPG))
                        .build();

                //设置为自己的ip地址
                Request request = new Request.Builder()
                        .url("http://"+"www.spcnu.cn"+"/savefile/")
                        .post(requestBody)
                        .build();

                try(Response response = client.newCall(request).execute()){
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 打开相册
     */
    private static void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        mActivity.startActivityForResult(intent, RequestCode.CHOOSE_PHOTO);
    }

    /**
     * 处理API>=19的图片选择操作
     * @param data
     */
    @TargetApi(19)
    public static void handleImageOnKitKat(Intent data, Context context) {
        String imagePath = null;
        mContext = context;
        Uri uri = data.getData();//返回Uri对象
        if(DocumentsContract.isDocumentUri(mContext, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.
                        EXTERNAL_CONTENT_URI, selection);
            } else if("com.android.providers.downloads.documents".
                    equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        ImgUtils.displayImage(imagePath);
        runUpload(imagePath);
    }

    /**
     * 处理API<19的图片选择操作
     * @param data
     */
    public static void handleImageBeforeKitKat(Intent data, Context context) {
        mContext = context;
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        ImgUtils.displayImage(imagePath);
        runUpload(imagePath);
    }

    /**
     * 获取指定文件类型图片的路径
     * @param uri
     * @param selection
     * @return
     */
    private static String getImagePath(Uri uri, String selection) {
        String path = null;
        if(mContext != null) {
            Cursor cursor = mContext.getContentResolver().query(
                    uri,
                    null,
                    selection,
                    null,
                    null);
            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndex(
                            MediaStore.Images.Media.DATA));
                }
                cursor.close();
            }
        }
        return path;
    }
}
