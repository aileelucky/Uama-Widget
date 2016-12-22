package uama.hangzhou.gu.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gujiajia on 2016/11/21.
 * E-mail 965939858@qq.com
 * Tel: 15050261230
 * desc:图片处理工具
 */

public class ImageFactory {

    public static final String Path = Environment.getExternalStorageDirectory() + File.separator + "uama" + File.separator + "cache" + File.separator + "cimage";//缓存图片文件夹路径

    public static void copyFileUsingFileChannels(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outPutChannel = null;
        try {
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outPutChannel = new FileInputStream(dest).getChannel();
                outPutChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
            }
        } finally {
            try {
                inputChannel.close();
                outPutChannel.close();
            } catch (IOException e) {
            }
        }
    }

    public static Uri createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "UAMA_cache" + timeStamp + "_";
        File storageDir = new File(Path);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,/* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
        }

        return Uri.fromFile(image);
    }

    //压缩图片，传入路径，返回file
    public static File sword(String path) {
        File outputFile = new File(path);
        long fileSize = outputFile.length();
        final long fileMaxSize = 100 * 1024;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            int angle = readPictureDegree(path);
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int height = options.outHeight;
            int width = options.outWidth;
            double scale;
            if (fileSize >= fileMaxSize) {
                scale = Math.sqrt((float) fileSize / fileMaxSize);
            } else {
                scale = 1.0;
            }
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            if (angle != 0) {
                Matrix m = new Matrix();
                int bWidth = bitmap.getWidth();
                int bHeight = bitmap.getHeight();
                m.setRotate(angle); // 旋转angle度
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bWidth, bHeight, m, true);
            }
            outputFile = new File(createImageFile().getPath());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
                if (fileSize >= fileMaxSize) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                }
                fos.close();
            } catch (IOException e) {
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            } else {
                File tempFile = outputFile;
                outputFile = new File(createImageFile().getPath());
                copyFileUsingFileChannels(tempFile, outputFile);
            }
        } catch (Exception e) {
        }
        return outputFile;
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
        }
        return degree;
    }

    //删除生成的缓存图片文件夹
    public static void deleteAllFiles(File file) {
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                deleteAllFiles(f);
            }
            file.delete();
        }
    }
    public static Bitmap getImage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        int angle = Utility.readPictureDegree(srcPath);

        if (angle != 0) {
            Matrix m = new Matrix();
            int width1 = bitmap.getWidth();
            int height1 = bitmap.getHeight();
            m.setRotate(angle); // 旋转angle度

            Bitmap tempBmp = bitmap;
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width1, height1, m, true);
            tempBmp.recycle();
        }
        return bitmap;//压缩好比例大小后再进行质量压缩
    }
}
