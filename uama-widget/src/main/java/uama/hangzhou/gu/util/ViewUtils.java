package uama.hangzhou.gu.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.File;
import java.util.List;

/**
 * Created by DWCloud on 2016/4/19.
 */
public class ViewUtils {
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ((0 < timeD && timeD < 500)||timeD<0) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static void setRelativeLayoutWH(View view, int width, int height) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    public static void setLinearLayoutWH(View view, int width, int height) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    public static void setRecyclerViewWH(View view, int width, int height) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }
    public static void setAbsListViewLayoutWH(View view, int width, int height) {
        AbsListView.LayoutParams params = (AbsListView.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }
    public static void setViewGroupWH(View view, int width, int height) {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams(); //取控件当前的布局参数
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }
    //获取小图链接
    public static String getSmallUrl(String url) {
        if(url == null || url.equals("")){
            return "";
        }
        int Suffix = url.length() - url.lastIndexOf(".");
        String newUrl = url.substring(0, url.length() - Suffix) + "_small" + url.substring(url.length() - Suffix, url.length());
        return newUrl;
    }

    public static void setText(TextView t, String text) {
        if (TextUtils.isEmpty(text)) {
            t.setText("");
        } else {
            text = text.trim();
            t.setText(text);
        }
    }

    public static void setText(TextView t, String insert, String text) {
        if (TextUtils.isEmpty(text)) {
            t.setText("");
        } else {
            text = text.trim();
            t.setText(insert + text);
        }
    }
}
