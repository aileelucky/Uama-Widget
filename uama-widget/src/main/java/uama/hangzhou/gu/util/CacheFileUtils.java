/*
 * 杭州绿漫科技有限公司
 * Copyright (c) 16-6-27 上午10:26.
 */

package uama.hangzhou.gu.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by gujiajia on 2016/6/27.
 * E-mail 965939858@qq.com
 * Tel: 15050261230
 */

public class CacheFileUtils {
    /**
     * 获取照片文件路径
     * @return
     */
    public static String getUpLoadPhotosPath() {
        StringBuffer fileSB = new StringBuffer();
        String key = String.format("%s.jpg", CreatKeyUtil.generateSequenceNo());
        fileSB.append(getImagePath()).append(File.separator).append(key);
        return fileSB.toString();
    }

    public static String getImagePath() {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), FileUtil.IMAGE_FILE_PATH);
        if (!file.mkdirs()) {
            Log.e("App", "Directory not created");
        }
        return file.getPath();
    }
}
