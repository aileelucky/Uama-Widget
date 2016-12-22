package uama.hangzhou.gu.widget.photo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import gu.hangzhou.uama.widget.R;
import uama.hangzhou.gu.constant.Constants;
import uama.hangzhou.gu.photochoose.PhotoWallActivity;
import uama.hangzhou.gu.util.CacheFileUtils;
import uama.hangzhou.gu.util.FileUtil;
import uama.hangzhou.gu.util.ImageFactory;
import uama.hangzhou.gu.widget.MessageDialog;

/**
 * Created by gujiajia on 2016/9/29.
 * E-mail 965939858@qq.com
 * Tel: 15050261230
 * 4 张占位图的选择器
 */

public class FourPicturesChoose {
    private ImageView imageView1, imageView2, imageView3, imageView4;
    private ImageView imgViewList[] = new ImageView[4];
    private Activity activity;
    public ArrayList<String> imageList;
    public String mNewImageFilePath;

    public FourPicturesChoose(Activity activity, ImageView imageView1, ImageView imageView2, ImageView imageView3, ImageView imageView4) {
        this.activity = activity;
        this.imageView1 = imageView1;
        this.imageView2 = imageView2;
        this.imageView3 = imageView3;
        this.imageView4 = imageView4;
        init();
    }

    private void init() {
        imageList = new ArrayList<>();
        imgViewList[0] = imageView1;
        imgViewList[1] = imageView2;
        imgViewList[2] = imageView3;
        imgViewList[3] = imageView4;
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(0);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(1);
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(2);
            }
        });
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(3);
            }
        });
    }

    //弹出拍照、相册选择
    public void showPopupWindow(final int position) {
        final String strArray[];
        if (position < imageList.size()) {
            strArray = new String[]{activity.getString(R.string.delete)};
        } else {
            strArray = new String[]{activity.getString(R.string.choose_photo), activity.getString(R.string.take_camera)};
        }
        MessageDialog.showBottomMenu(activity, strArray, new MessageDialog.MenuDialogOnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                if (position < imageList.size()) {
                    if (index == 1) {
                        imageList.remove(position);
                        upDateImageGroup();
                    }
                    return;
                }
                switch (index) {
                    case 1:
                        goToChooseImage();
                        break;
                    case 2:
                        goToTakePhoto();
                        break;
                }
            }
        });
    }


    //刷新选中的图片
    private void upDateImageGroup() {
        imgViewList[0].setImageResource(R.mipmap.camera_default);
        imgViewList[1].setImageResource(R.mipmap.camera_default_ext);
        imgViewList[2].setImageResource(R.mipmap.camera_default_ext);
        imgViewList[3].setImageResource(R.mipmap.camera_default_ext);
        for (int i = 0; i < imageList.size(); i++) {
            imgViewList[i].setImageBitmap(ImageFactory.getImage(imageList.get(i)));
        }
    }

    //拍照
    public void goToTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mNewImageFilePath = CacheFileUtils.getUpLoadPhotosPath();
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA, new File(mNewImageFilePath).getAbsolutePath());
        Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.Images.ImageColumns.ORIENTATION, 0);
        activity.startActivityForResult(intent, Constants.TAKE_PHOTO);
    }

    //选择照片
    private void goToChooseImage() {
        Intent intent = new Intent(activity, PhotoWallActivity.class);
        intent.putExtra(PhotoWallActivity.SelectedCounts, imageList);
        intent.putExtra(PhotoWallActivity.MaxCounts, 4);
        activity.startActivityForResult(intent, Constants.SELECT_IMAGE);
    }

    public void setImageList(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                File imageFile = new File(mNewImageFilePath);
                Uri uri = Uri.fromFile(imageFile);
                activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                imageList.add(FileUtil.getRealFilePath(activity, uri));
                upDateImageGroup();
            }
        } else if (requestCode == Constants.SELECT_IMAGE) {
            if (resultCode == 1991) {
                if (data == null) {
                    return;
                }
                imageList.clear();
                imageList.addAll(data.getStringArrayListExtra("paths"));
                upDateImageGroup();
            }
        }
    }

    //获取选中的图片list
    public ArrayList<String> getChosenImageList() {
        return imageList;
    }
}
