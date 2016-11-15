package uama.hangzhou.gu.widget.photo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

import gu.hangzhou.uama.widget.R;
import uama.hangzhou.gu.constant.Constants;
import uama.hangzhou.gu.photochoose.PhotoWallActivity;
import uama.hangzhou.gu.util.CacheFileUtils;
import uama.hangzhou.gu.util.FileUtil;
import uama.hangzhou.gu.widget.MessageDialog;
import uama.hangzhou.gu.widget.MyGridView;

/**
 * Created by gujiajia on 2016/9/29.
 * E-mail 965939858@qq.com
 * Tel: 15050261230
 * 图片添加器，正常情况下
 */

public class PhotoChoose {
    private MyGridView myGridView;
    private PublishImageGridVIewAdapter imageGridVIewAdapter;
    private ArrayList<String> mImageList;//保存选择的图片非常重要
    private int maxCounts;//最大选择图片数量
    private Activity activity;
    public String mNewImageFilePath;

    public PhotoChoose(Activity activity, MyGridView myGridView, int maxCounts) {
        this.myGridView = myGridView;
        this.activity = activity;
        this.maxCounts = maxCounts;
        init();
    }

    private void init() {
        mImageList = new ArrayList<>();
        imageGridVIewAdapter = new PublishImageGridVIewAdapter(activity, mImageList, maxCounts, new PublishImageGridVIewAdapter.ShowChooseMenu() {
            @Override
            public void show() {
                showPopupWindow();
            }
        });
        myGridView.setAdapter(imageGridVIewAdapter);
    }

    //弹出拍照、相册选择
    public void showPopupWindow() {
        if (activity == null) {
            return;
        }
        String strArray[] = {activity.getString(R.string.choose_photo), activity.getString(R.string.take_camera)};
        MessageDialog.showBottomMenu(activity, strArray, new MessageDialog.MenuDialogOnItemClickListener() {
            @Override
            public void onItemClick(int index) {
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

    //拍照
    public void goToTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mNewImageFilePath = CacheFileUtils.getUpLoadPhotosPath();
        Uri uri = Uri.fromFile(new File(mNewImageFilePath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.Images.ImageColumns.ORIENTATION, 0);
        activity.startActivityForResult(intent, Constants.TAKE_PHOTO);
    }

    //选择照片
    private void goToChooseImage() {
        Intent intent = new Intent(activity, PhotoWallActivity.class);
        intent.putExtra(PhotoWallActivity.SelectedCounts, mImageList);
        intent.putExtra(PhotoWallActivity.MaxCounts, maxCounts);
        activity.startActivityForResult(intent, Constants.SELECT_IMAGE);
    }

    public void setImageList(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.SELECT_IMAGE) {
            if (resultCode == 1991) {
                if (data == null) {
                    return;
                }
                PublishImageGridVIewAdapter adapter = (PublishImageGridVIewAdapter) myGridView.getAdapter();
                mImageList.clear();
                mImageList.addAll(data.getStringArrayListExtra("paths"));
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == Constants.TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                File imageFile = new File(mNewImageFilePath);
                Uri uri = Uri.fromFile(imageFile);
                activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                PublishImageGridVIewAdapter adapter = (PublishImageGridVIewAdapter) myGridView.getAdapter();
                mImageList.add(FileUtil.getRealFilePath(activity, uri));
                adapter.notifyDataSetChanged();
            }
        }
    }

    //获取选中的图片列表
    public ArrayList<String> getChosenImageList() {
        return mImageList;
    }
}
