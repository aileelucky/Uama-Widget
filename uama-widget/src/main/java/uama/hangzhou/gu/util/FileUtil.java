package uama.hangzhou.gu.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件外部存储工具类
 * 1：文本存储
 * 2：assests inputstream文件流存储
 * 3：图片存储
 */
public class FileUtil {
    private static int bufferd = 1024;
    private static final int MIN_SIZE = 10 * 1024;
    public static final String IMAGE_FILE_PATH = "image";
    public static final String IMG_TYPE_PNG = "png";
    public static final String IMG_TYPE_JPEG = "jpeg";

    public static final String FILE_DIR = "uama";//项目存储目录
    /**
     * 手机的缓存根目录
     */
    private static String mDataRootPath = null;
    private static FileUtil fileUtil = null;

    private Context context;

    /**
     * 創建文件，各類狀態值
     */
    private static final int CREATE_SUC_NEW = 0;//創建成功，初創
    private static final int CREATE_SUC_EXITE = 1;//創建成功，已存在
    private static final int CREATE_SUC_NEEDMORE = 1;//創建成功,内存即将不足
    private static final int CREATE_FAIL_DIR = 2;//文件夹创建失败
    private static final int CREATE_FAIL_STORE_LACK = 3;//創建失敗，存儲空間不足
    private static final int CREATE_FAIL_OTHER = 4;//其他未知错误

    /**
     * 错误提示
     */
    private static final String CREATE_SUC_NEW_MSG = "創建成功，初創";
    private static final String CREATE_SUC_EXITE_MSG = "創建成功，已存在";
    private static final String CREATE_SUC_NEEDMORE_MSG = "創建成功,内存即将不足";
    private static final String CREATE_FAIL_DIR_MSG = "文件夹创建失败";
    private static final String CREATE_FAIL_STORE_LACK_MSG = "創建失敗，存儲空間不足";
    private static final String CREATE_FAIL_OTHER_MSG = "其他未知错误";

    /*
     * <!-- 在SDCard中创建与删除文件权限 --> <uses-permission
     * android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/> <!--
     * 往SDCard写入数据权限 --> <uses-permission
     * android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     */
    private FileUtil() {
    }

    private FileUtil(Context context) {
        this.context = context;
    }

    public static FileUtil getInstance(Context context) {
        if (fileUtil == null) {
            fileUtil = new FileUtil(context);
        }
        return fileUtil;
    }

    /**
     * 判断当前sd卡是否可用
     *
     * @return true 可用 false 不可用
     */
    public boolean isSdcardAvailable() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }


    /**
     * 获取sd卡目录,主要考虑sd卡不可用处理(未)
     *
     * @return
     */
    private String getSDPath() {
        if (isSdcardAvailable())
            return Environment.getExternalStorageDirectory()
                    + File.separator;
        else
            return (mDataRootPath = context.getCacheDir().getPath()) + File.separator;
    }

    /**
     * 获取缓存路径
     *
     * @return
     */
    public String getDiskCacheDir() {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获取子目录
     *
     * @param director
     * @return
     */
    private String getDirectorPath(String director) {
        return getDirPath() + director + File.separator;//先判断文件夹是否创建成功
    }

    /**
     * 获取项目使用根目录
     */
    private String getDirPath() {
        return getSDPath() + File.separator + FILE_DIR + File.separator;//先判断文件夹是否创建成功
    }


    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    public long getSDCardExtraSize() {
        if (isSdcardAvailable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getBlockSize();
            return freeBlocks * availableBlocks / 1024;
        }
        return 0;
    }

    /**
     * 获取sd卡size大小
     *
     * @return
     */
    public long getSDCardAllSizeKB() {
        // get path of sdcard
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // get single block size(Byte)
        long blockSize = sf.getBlockSize();
        // 获取所有数据块数
        long allBlocks = sf.getBlockCount();
        // 返回SD卡大小
        return (allBlocks * blockSize) / 1024; // KB

    }


    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }


    /**
     * 获取系统存储路径
     *
     * @return
     */
    public String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }


    // 判断文件是否存在:部分路径
    public boolean isFileExist(String director) {
        File file = new File(getDirPath() + director);
        return file.exists();
    }

    //全路径
    public static boolean isFileExistByAbsolute(String absolutePath) {
        File file = new File(absolutePath);
        return file.exists();
    }

    /**
     * create multiple director
     * 创建文件夹
     *
     * @param director
     * @return
     */
    public boolean createFile(String director) {
        //已经存在该文件
        if (isFileExist(director))
            return true;
        File file = new File(getSDPath() + FILE_DIR + File.separator);
        //判断是否创建失败
        if (!file.mkdirs() && !(isFileExistByAbsolute(getSDPath() + FILE_DIR))) {
            return false;
        }
        File fileDir = new File(file, director);
        if (!fileDir.mkdirs()) {
            return false;
        }
        return true;
    }

    /**
     * 不建子文件夹检测根目录是否创建成功
     *
     * @return
     */
    public boolean createFile() {
        File file = new File(getSDPath() + FILE_DIR + File.separator);
        //判断是否创建失败
        if (!file.mkdirs() && !(isFileExistByAbsolute(getSDPath() + FILE_DIR))) {
            return false;
        }
        return true;
    }

    /**
     * create multiple director
     * 获取文件夹/文件
     *
     * @param director 文件夹目录 or 文件目录
     * @return
     */
    public File getFile(String director) {
        if (isFileExist(director))
            return new File(getDirPath() + director);
        else
            return null;
    }

    /**
     * 获取图片
     *
     * @param director 需要后缀名
     * @return
     */
    public File getPicFile(String director) {
        String path = IMAGE_FILE_PATH + File.separator + director;
        if (isFileExist(path))
            return new File(getDirPath() + path);
        else
            return null;
    }

    /**
     * @param fileName 文件名 需要添加后缀名：无后缀名无法识别
     * @param content  传入字符串
     * @param isAppend true表示该文件存在，操作该文件添加content false表示直接使用content覆盖该文件
     * @return
     */
    public int writeToSDCardFile(String fileName, String content, boolean isAppend) {
        return writeToSDCardFile(null, fileName, content, "", isAppend);
    }

    /**
     * 创建文件
     *
     * @param directory 文件夹目录
     * @param fileName  文件名 需要添加后缀名：无后缀名无法识别
     * @param content   传入字符串
     * @param isAppend  true表示该文件存在，操作该文件添加content false表示直接使用content覆盖该文件
     * @return
     */
    public int writeToSDCardFile(String directory, String fileName, String content, boolean isAppend) {
        return writeToSDCardFile(directory, fileName, content, "", isAppend);
    }

    /**
     * @param directory (you don't need to begin with
     *                  Environment.getExternalStorageDirectory()+File.separator)
     *                  目前考虑，当sd卡无法使用时，使用内存空间存储；使用外部存储时，判断外部存储空间是否足够
     * @param fileName
     * @param content
     * @param encoding  (UTF-8...)
     * @param isAppend  : Context.MODE_APPEND
     * @return 成功or失败各类状态值
     */
    public int writeToSDCardFile(String directory, String fileName,
                                 String content, String encoding, boolean isAppend) {
        // mobile SD card path +path
        File file = null;
        OutputStream os = null;
        int returnType = CREATE_SUC_NEW;
        byte[] bytes = null;
        String filePath;
        try {
            bytes = content.getBytes();
            if (TextUtils.isEmpty(directory)) {
                filePath = fileName;
                if (!createFile()) {
                    ToastUtil.show(context, CREATE_FAIL_DIR_MSG);
                    return CREATE_FAIL_DIR;
                }
            } else {
                filePath = directory + File.separator + fileName;
                if (!createFile(directory)) {
                    ToastUtil.show(context, CREATE_FAIL_DIR_MSG);
                    return CREATE_FAIL_DIR;
                }
            }
            //内存卡空间不足，失败
            if (getSDCardExtraSize() <= bytes.length && getSDPath().contains("storage")) {
                ToastUtil.show(context, CREATE_FAIL_STORE_LACK_MSG);
                return CREATE_FAIL_STORE_LACK;
            }


            if (isFileExist(filePath)) {
                returnType = CREATE_SUC_EXITE;
                file = getFile(filePath);
            } else {
                file = new File(getDirPath() + filePath);
                returnType = CREATE_SUC_NEW;
            }

            os = new FileOutputStream(file, isAppend);
            if (encoding.equals("")) {
                os.write(content.getBytes());
            } else {
                os.write(content.getBytes(encoding));
            }
            os.flush();
            //内存即将不足

            if (getSDCardExtraSize() < MIN_SIZE) {
                ToastUtil.show(context, CREATE_SUC_NEEDMORE_MSG);
                return CREATE_SUC_NEEDMORE;
            } else {
                return returnType;
            }
        } catch (IOException e) {
            Log.e("FileUtil", "writeToSDCardFile:" + e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return CREATE_FAIL_OTHER;
    }

    /**
     * 目前考虑，当sd卡无法使用时，使用内存空间存储；使用外部存储时，判断外部存储空间是否足够
     * write data from inputstream to SDCard
     */
    public File writeToSDCardFromInput(String directory, String fileName,
                                       InputStream input) {
        File file = null;
        OutputStream os = null;
        String filePath;
        try {
            if (TextUtils.isEmpty(directory)) {
                filePath = fileName;
                if (!createFile()) {
                    ToastUtil.show(context, CREATE_FAIL_DIR_MSG);
                    return file;
                }
            } else {
                filePath = directory + File.separator + fileName;
                if (!createFile(directory)) {
                    ToastUtil.show(context, CREATE_FAIL_DIR_MSG);
                    return file;
                }
            }
            if (input.available() > getSDCardExtraSize() && getSDPath().contains("sdcard")) {
                ToastUtil.show(context, CREATE_FAIL_STORE_LACK_MSG);
                return file;
            }
            file = new File(getDirPath() + filePath);
            os = new FileOutputStream(file);
            byte[] data = new byte[bufferd];
            int length = -1;
            while ((length = input.read(data)) != -1) {
                os.write(data, 0, length);
            }
            // clear cache
            os.flush();
        } catch (Exception e) {
            Log.e("FileUtil", "" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * this url point to image(jpg)
     *
     * @param url
     * @return image name
     */
    public String getUrlLastString(String url) {
        String[] str = url.split("/");
        int size = str.length;
        return str[size - 1];
    }

    /**
     * url md5处理唯一话
     *
     * @param key
     * @return
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 保存图片为PNG or JPEG
     * 目前考虑，当sd卡无法使用时，使用内存空间存储；使用外部存储时，判断外部存储空间是否足够
     *
     * @param bitmap   带存储图片
     * @param fileName 文件名，目录固定为sd目录+image+filename
     * @param imgType  图片存储格式，目前只支持 png，jpeg，默认jpeg
     * @return null，或者file
     */
    public File saveImageByType(Bitmap bitmap, String fileName, String imgType) {
        File file = null;
        FileOutputStream out = null;
        try {
            if (!createFile(IMAGE_FILE_PATH)) {
                ToastUtil.show(context, CREATE_FAIL_DIR_MSG);
                return file;
            }
            if (bitmap.getByteCount() > getSDCardExtraSize() && getSDPath().contains("sdcard")) {
                ToastUtil.show(context, CREATE_FAIL_STORE_LACK_MSG);
                return file;
            }
            //在此处理图片存储的格式
            Bitmap.CompressFormat type = Bitmap.CompressFormat.JPEG;
            if (IMG_TYPE_PNG.equals(imgType)) {
                type = Bitmap.CompressFormat.PNG;
                if (!fileName.contains(".png"))
                    fileName += ".png";
            } else {
                if (!fileName.contains(".jpeg"))
                    fileName += ".jpeg";
            }
            file = new File(getDirPath() + IMAGE_FILE_PATH + File.separator + fileName);
            out = new FileOutputStream(file);

            if (bitmap.compress(type, 100, out)) {
                out.flush();
                out.close();
            }
            return file;
        } catch (FileNotFoundException e) {
            Log.e("FileUtil", "" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 使用其，存入图片资源
     *
     * @param resourceId
     * @param fileName
     * @param imgType
     * @return
     */
    public File saveImageById(int resourceId, String fileName, String imgType) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        File file = saveImageByType(bitmap, fileName, imgType);
        bitmap.recycle();
        return file;
    }

    /**
     * 删除文件，相对路径，不需要输入sd根路径
     *
     * @param fileName
     * @return
     */
    public boolean deleteFile(String fileName) {
        if (isFileExist(getDirPath() + fileName)) {
            File file = getFile(fileName);
            return file.delete();
        }
        return false;
    }

    /**
     * 传入为绝对路径
     *
     * @param fileName
     * @return
     */
    public static boolean deleteFileByAB(String fileName) {
        if (isFileExistByAbsolute(fileName)) {
            File file = new File(fileName);
            return file.delete();
        }
        return false;
    }

    /**
     * 获取图片目录
     *
     * @param fileName 使用该工具类创建的图片，可以通过图片名补全绝对路径
     * @return
     */
    public String getAbsolutePath(String fileName) {
        return getDirPath() + IMAGE_FILE_PATH + File.separator + fileName;
    }

    /**
     * 删除图片
     *
     * @param fileName 使用该工具类创建的图片，可以直接传入图片名进行删除
     * @return
     */
    public boolean deleteImage(String fileName) {
        return deleteFileByAB(getAbsolutePath(fileName));
    }


    /**
     * 删除SD卡或者手机的缓存图片和目录
     */
    public void deleteAllImage() {
        deleteAllDirectory(IMAGE_FILE_PATH);
    }

    /**
     * 删除SD卡或者手机的缓存图片和目录
     */
    public void deleteAllDirectory(String directory) {
        File dirFile = new File(getDirPath() + directory);
        if (!dirFile.exists()) {
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(dirFile, children[i]).delete();
            }
        }
        dirFile.delete();
    }

    /**
     * 删除app所有存入内容
     */
    public void deleteAllDirectory() {
        File dirFile = new File(getSDPath() + File.separator + FILE_DIR);
//        if (!dirFile.exists()) {
//            return;
//        }
//        if (dirFile.isDirectory()) {
//            String[] children = dirFile.list();
//            for (int i = 0; i < children.length; i++) {
//                new File(dirFile, children[i]).delete();
//            }
//        }
//        dirFile.delete();
        delete(dirFile);
    }

    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    public static String getAssets(Context con, String file) {
        // Convert the buffer into a string.
        String text = null;
        try {
            InputStream is = con.getAssets().open(file);
            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            text = new String(buffer, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}