/*
 * 杭州绿漫科技有限公司
 * Copyright (c) 16-6-25 下午3:22.
 */

package uama.hangzhou.gu.widget.photo;

/**
 * Created by gujiajia on 2016/6/25.
 * E-mail 965939858@qq.com
 * Tel: 15050261230
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.List;

import gu.hangzhou.uama.widget.R;
import uama.hangzhou.gu.util.DeviceUtils;
import uama.hangzhou.gu.util.SDCardImageLoader;
import uama.hangzhou.gu.util.ViewUtils;
import uama.hangzhou.gu.widget.MessageDialog;

public class PublishImageGridVIewAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mImageList;
    private int maxCounts;
    private int height;
    private ShowChooseMenu showChooseMenu;

    public PublishImageGridVIewAdapter(Context context, List<String> mImageList, int maxCounts, ShowChooseMenu showChooseMenu) {
        mContext = context;
        this.mImageList = mImageList;
        this.maxCounts = maxCounts;
        this.showChooseMenu = showChooseMenu;
        this.height = (int) (DeviceUtils.getDisplayWidth(context) - DeviceUtils.convertDpToPixel(context, 75)) / 4;
        if(this.mImageList == null){
            this.mImageList = new ArrayList<>();
        }
    }

    public void deleteItem(int position) {
        mImageList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mImageList.size() < maxCounts-1) {
            return mImageList.size() + 1;
        } else {
            return maxCounts;
        }
    }

    @Override
    public Object getItem(int position) {
        return mImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.publish_gridview_item, parent, false);
            viewHolder.ivPic = (ImageView) convertView.findViewById(R.id.iv_grid_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ivPic.setTag(new Integer(position));

        if (position < mImageList.size()) {
            String path = mImageList.get(position);
            viewHolder.ivPic.setTag(path);
            viewHolder.ivPic.setVisibility(View.VISIBLE);
                SDCardImageLoader.getInstance(mContext).loadImage(4, path, viewHolder.ivPic);
        } else {
            if (position == maxCounts && !mImageList.isEmpty()) {
                viewHolder.ivPic.setVisibility(View.GONE);
            } else {
                viewHolder.ivPic.setVisibility(View.VISIBLE);
                viewHolder.ivPic.setImageResource(R.mipmap.publish_add_photo);
            }
        }
        ViewUtils.setRelativeLayoutWH(viewHolder.ivPic, height, height);
        viewHolder.ivPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageList.size() != maxCounts && getCount() - 1 == position) {
                    DeviceUtils.closeKeyBoard((Activity)mContext);
//                    InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputMethodManager.hideSoftInputFromWindow(((Activity)mContext).getCurrentFocus().getWindowToken(), 0);
                    showChooseMenu.show();
                } else {
                    String strArray[] = {mContext.getString(R.string.delete)};
                    MessageDialog.showBottomMenu(mContext, strArray, new MessageDialog.MenuDialogOnItemClickListener() {
                        @Override
                        public void onItemClick(int index) {
                            switch (index) {
                                case 1:
                                    deleteItem(position);
                                    break;
                            }
                        }
                    });
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView ivPic;
    }
    public interface ShowChooseMenu{
        void show();
    };
}
