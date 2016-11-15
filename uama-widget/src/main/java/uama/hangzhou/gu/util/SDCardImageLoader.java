/*
 * 杭州绿漫科技有限公司
 * Copyright (c) 16-6-25 下午3:34.
 */

package uama.hangzhou.gu.util;

/**
 * Created by gujiajia on 2016/6/25.
 * E-mail 965939858@qq.com
 * Tel: 15050261230
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gu.hangzhou.uama.widget.R;

public class SDCardImageLoader {
    private static SDCardImageLoader mInstance;
    //缓存
    private LruCache<String, Bitmap> imageCache;
    // 固定2个线程来执行任务
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private Handler handler = new Handler();

    private int screenW, screenH;
    private int maxWidth;

    public static SDCardImageLoader getInstance(Context context){
        synchronized (SDCardImageLoader.class){
            if (mInstance == null){
                mInstance = new SDCardImageLoader(DeviceUtils.getDisplayWidth(context), DeviceUtils.getDisplayHeight(context));
            }
        }
        return mInstance;
    }

    private SDCardImageLoader(int screenW, int screenH) {
        this.screenW = screenW;
        this.screenH = screenH;

        maxWidth = 1000;

        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 临时改成4M
        cacheSize = 4 * 1024 * 1024;

        // 设置图片缓存大小为程序最大可用内存的1/8
        imageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    public Bitmap loadDrawableForPublish(final int smallRate, final String filePath,
                                         final ImageCallback callback) {
        // 直接从本地读取
        executorService.submit(new Runnable() {
            public void run() {
                try {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filePath, opt);

                    // 获取到这个图片的原始宽度和高度
                    int picWidth = opt.outWidth;
                    int picHeight = opt.outHeight;

                    //读取图片失败时直接返回
                    if (picWidth == 0 || picHeight == 0) {
                        return;
                    }

                    //初始压缩比例
                    opt.inSampleSize = smallRate;
                    // 根据屏的大小和图片大小计算出缩放比例
                    if (picWidth > picHeight) {
                        if (picWidth > maxWidth)
                            opt.inSampleSize *= picWidth / maxWidth;
                    } else {
                        if (picHeight > screenH)
                            opt.inSampleSize *= picHeight / screenH;
                    }

                    //这次再真正地生成一个有像素的，经过缩放了的bitmap
                    opt.inJustDecodeBounds = false;
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath, opt);

                    int bitmapWidth = bitmap.getWidth();

                    if (bitmapWidth > maxWidth){
                        float scale = (float)maxWidth/(float)bitmap.getWidth();
                        Matrix matrix = new Matrix();
                        matrix.postScale(scale, scale); //长和宽放大缩小的比例

                        Bitmap tempBmp = bitmap;
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                        tempBmp.recycle();
                    }

                    int angle = Utility.readPictureDegree(filePath);

                    if (angle != 0) {
                        Matrix m = new Matrix();
                        int width1 = bitmap.getWidth();
                        int height1 = bitmap.getHeight();
                        m.setRotate(angle); // 旋转angle度

                        Bitmap tempBmp = bitmap;
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width1, height1, m, true);
                        tempBmp.recycle();
                    }

                    final Bitmap finalBitmap = bitmap;
                    handler.post(new Runnable() {
                        public void run() {
                            callback.imageLoaded(finalBitmap);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return null;
    }

    public Bitmap loadDrawable(final int smallRate, final String filePath,
                               final ImageCallback callback) {
        // 如果缓存过就从缓存中取出数据
        if (imageCache.get(filePath) != null) {
            return imageCache.get(filePath);
        }

        // 如果缓存没有则读取SD卡
        executorService.submit(new Runnable() {
            public void run() {
                try {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filePath, opt);

                    // 获取到这个图片的原始宽度和高度
                    int picWidth = opt.outWidth;
                    int picHeight = opt.outHeight;

                    //读取图片失败时直接返回
                    if (picWidth == 0 || picHeight == 0) {
                        return;
                    }

                    //初始压缩比例
                    opt.inSampleSize = smallRate;
                    // 根据屏的大小和图片大小计算出缩放比例
                    if (picWidth > picHeight) {
                        if (picWidth > screenW)
                            opt.inSampleSize *= picWidth / screenW;
                    } else {
                        if (picHeight > screenH)
                            opt.inSampleSize *= picHeight / screenH;
                    }

                    //这次再真正地生成一个有像素的，经过缩放了的bitmap
                    opt.inJustDecodeBounds = false;
                    Bitmap bmp = BitmapFactory.decodeFile(filePath, opt);

                    int angle = Utility.readPictureDegree(filePath);

                    if (angle != 0) {
                        Matrix m = new Matrix();
                        int width1 = bmp.getWidth();
                        int height1 = bmp.getHeight();
                        m.setRotate(angle); // 旋转angle度

                        Bitmap tempBmp = bmp;
                        bmp = Bitmap.createBitmap(bmp, 0, 0, width1, height1, m, true);
                        tempBmp.recycle();
                    }

                    //存入map
                    imageCache.put(filePath, bmp);
                    final Bitmap finalBitmap = bmp;
                    handler.post(new Runnable() {
                        public void run() {
                            callback.imageLoaded(finalBitmap);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return null;
    }

    /**
     * 异步读取SD卡图片，并按指定的比例进行压缩（最大不超过屏幕像素数）
     *
     * @param smallRate 压缩比例，不压缩时输入1，此时将按屏幕像素数进行输出
     * @param filePath  图片在SD卡的全路径
     * @param imageView 组件
     */
    public void loadImage(int smallRate, final String filePath, final ImageView imageView) {
        Bitmap bmp = loadDrawable(smallRate, filePath, new ImageCallback() {
            @Override
            public void imageLoaded(Bitmap bmp) {
                if (imageView.getTag().equals(filePath)) {
                    if (bmp != null) {
                        imageView.setImageBitmap(bmp);
                    } else {
                        imageView.setImageResource(R.mipmap.empty_photo);
                    }
                }
            }
        });

        if (bmp != null) {
            if (imageView.getTag().equals(filePath)) {
                imageView.setImageBitmap(bmp);
            }
        } else {
            imageView.setImageResource(R.mipmap.empty_photo);
        }

    }


    // 对外界开放的回调接口
    public interface ImageCallback {
        // 注意 此方法是用来设置目标对象的图像资源
        public void imageLoaded(Bitmap imageDrawable);
    }
}
