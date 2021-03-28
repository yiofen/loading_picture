package com.spc.myapplication.utils;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 图片工具类
 */
public class ImgUtils {

    /**
     * 图像压缩并保存
     * @param image
     * @param filePath
     */
    public static void compImageToFile(Bitmap image, String filePath) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;//压缩率
        int length = 1024;//指定压缩长度限制
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while(baos.toByteArray().length/length > 1000 ) {
            baos.reset();//清空baos
            //这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 5;//每次都减少10
        }

        OutputStream os = new BufferedOutputStream(new FileOutputStream(filePath));
        os.write(baos.toByteArray());
        os.flush();
        if(os != null) {
            os.close();
        }

//        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//        //把ByteArrayInputStream数据生成图片
//        Bitmap bitmap = BitmapFactory.decodeStream(bais, null, null);
    }

    /**
     * 图片显示
     * @param imagePath
     */
    public static void displayImage(String imagePath) {
        if(imagePath != null) {
//            System.out.println(imagePath);
        }
    }

    public static String createPicName() {
        String imageName = "";
        String head = "SPC_";
        String suffix = ".jpg";

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date0 = new Date();
        String date = sDateFormat.format(date0);

        imageName = head + date + suffix;
        return imageName;
    }

    /**
     * 根据当前时间生成图片名称
     * @return
     */
    @Deprecated
    public static String createImageName() {
        String imagePath = "";
        String head = "SPC_";
        String suffix = ".jpg";

        Calendar calendar = Calendar.getInstance();
        //获取系统时间
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date1 = addZero(year, month, day);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        String date2 = addZero(hour, min, sec);

        String date = date1 + "_" + date2;
        imagePath = head + date + suffix;
        return imagePath;
    }

    /**
     * 补零操作
     * @param objs
     * @return
     */
    private static String addZero(Object... objs) {
        StringBuilder temp = new StringBuilder();
        for(Object obj:objs) {
            if((int) obj < 10) {
                obj = "0" + obj;
            }
            temp.append(obj);
        }

        return temp.toString();
    }
}
