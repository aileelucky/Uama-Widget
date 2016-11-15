/*
 * 杭州绿漫科技有限公司
 * Copyright (c) 16-6-27 下午3:59.
 */

package uama.hangzhou.gu.util;

import android.content.Context;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by gujiajia on 2016/6/27.
 * E-mail 965939858@qq.com
 * Tel: 15050261230
 */

public class UniversalDeviceUtils {
    public static int getDisplayHeight(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int getDisplayWidth(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }
    //获取保存图片的时间命名
    public static String getLocalTime(){
        String dataStr = "";
        String str = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        str = formatter.format(curDate);
        dataStr = "Uama_"+str.substring(0,4)+"_"+str.substring(4,6)+"_"+str.substring(6,8)+"_"+str.substring(8,14)+"_"+ UUID.randomUUID()+".jpg";
        return dataStr;
    }
}
