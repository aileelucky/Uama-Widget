/*
 * 杭州绿漫科技有限公司
 * Copyright (c) 16-6-27 下午12:44.
 */

package uama.hangzhou.gu.photochoose;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import gu.hangzhou.uama.widget.R;
import uama.hangzhou.gu.util.SDCardImageLoader;


/**
 * PhotoWall中GridView的适配器
 *
 * @author gujiajia
 */

public class PhotoWallAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> imagePathList = null;
    private int maxNum;
    private ArrayList<String> selectedImageList;

    public PhotoWallAdapter(Context context, ArrayList<String> imagePathList, ArrayList<String> selectedImageList, int maxNum) {
        this.context = context;
        this.imagePathList = imagePathList;
        if (selectedImageList == null) {
            this.selectedImageList = new ArrayList<>();
        } else {
            this.selectedImageList = selectedImageList;
        }
        this.maxNum = maxNum;
    }

    @Override
    public int getCount() {
        return imagePathList == null ? 0 : imagePathList.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String filePath = (String) getItem(position);

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.photo_wall_item, parent,false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.photo_wall_item_photo);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.photo_wall_item_cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        boolean bSelected = isSelected(filePath);

        if (bSelected) {
            holder.imageView.setColorFilter(context.getResources().getColor(R.color.image_checked_bg));
        } else {
            holder.imageView.setColorFilter(null);
        }

        holder.checkBox.setChecked(bSelected);
        if (!TextUtils.isEmpty(filePath)) {
            holder.imageView.setTag(filePath);
            SDCardImageLoader.getInstance(context).loadImage(4, filePath, holder.imageView);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelected(filePath)) {
                    if (selectedImageList.size() > maxNum - 1) {
                        ((PhotoWallActivity) context).showErrorDialog();
                        holder.checkBox.setChecked(false);
                        return;
                    } else {
                        selectedImageList.add(filePath);
                        holder.checkBox.setChecked(true);
                        holder.imageView.setColorFilter(context.getResources().getColor(R.color.image_checked_bg));
                    }
                } else {
                    selectedImageList.remove(filePath);
                    holder.imageView.setColorFilter(null);
                    holder.checkBox.setChecked(false);
                }
                ((PhotoWallActivity) context).setChooseCounts(selectedImageList.size());
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> imageList = new ArrayList<String>();
                imageList.add("file://" + filePath);
                Intent intent = new Intent(context, ImagePagerActivity.class);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList<String>) imageList);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        CheckBox checkBox;
    }


    public ArrayList<String> getSelectList() {
        return selectedImageList;
    }

    private boolean isSelected(String path) {
        int selectCounts = selectedImageList.size();
        for (int i = 0; i < selectCounts; i++) {
            if (path.equals(selectedImageList.get(i))) {
                return true;
            }
        }
        return false;
    }
}
