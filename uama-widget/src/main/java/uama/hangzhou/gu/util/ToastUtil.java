/*
 * 杭州绿漫科技有限公司
 * Copyright (c) 2016.
 */

package uama.hangzhou.gu.util;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import gu.hangzhou.uama.widget.R;


public class ToastUtil {
    private static Toast toast;

    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            toast.cancel();
            toast = null;//toast隐藏后，将其置为null
        }
    };

    public static void show(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast, null);//自定义布局
        TextView text = (TextView) view.findViewById(R.id.toast_message);//显示的提示文字
        text.setText(message);
        mHandler.removeCallbacks(r);
        if (toast == null) {//只有mToast==null时才重新创建，否则只需更改提示文字
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 150);
        }
        toast.setView(view);
        mHandler.postDelayed(r, 2000);//延迟1秒隐藏toast
        toast.show();
    }

    public static void showShort(Context context, String message) {
        show(context, message);
    }

    public static void show(Context context, @StringRes int info) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast, null);//自定义布局
        TextView text = (TextView) view.findViewById(R.id.toast_message);//显示的提示文字
        text.setText(context.getString(info));
        mHandler.removeCallbacks(r);
        if (toast == null) {//只有mToast==null时才重新创建，否则只需更改提示文字
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 150);
        }
        toast.setView(view);
        mHandler.postDelayed(r, 2000);//延迟1秒隐藏toast
        toast.show();
    }


    public static void showLong(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast, null);//自定义布局
        TextView text = (TextView) view.findViewById(R.id.toast_message);//显示的提示文字
        text.setText(message);
        mHandler.removeCallbacks(r);
        if (toast == null) {//只有mToast==null时才重新创建，否则只需更改提示文字
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 150);
        }
        toast.setView(view);
        mHandler.postDelayed(r, 3000);//延迟1秒隐藏toast
        toast.show();
    }


    public static void showLong(Context context, @StringRes int info) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast, null);//自定义布局
        TextView text = (TextView) view.findViewById(R.id.toast_message);//显示的提示文字
        text.setText(context.getString(info));
        mHandler.removeCallbacks(r);
        if (toast == null) {//只有mToast==null时才重新创建，否则只需更改提示文字
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 150);
        }
        toast.setView(view);
        mHandler.postDelayed(r, 2000);//延迟1秒隐藏toast
        toast.show();
    }

    public static void showDelay(Context context, String message, int delay) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast, null);//自定义布局
        TextView text = (TextView) view.findViewById(R.id.toast_message);//显示的提示文字
        text.setText(message);
        mHandler.removeCallbacks(r);
        if (toast == null) {//只有mToast==null时才重新创建，否则只需更改提示文字
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 150);
        }
        toast.setView(view);
        mHandler.postDelayed(r, delay);//延迟1秒隐藏toast
        toast.show();
    }

    public static void showDelay(Context context, @StringRes int message, int delay) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast, null);//自定义布局
        TextView text = (TextView) view.findViewById(R.id.toast_message);//显示的提示文字
        text.setText(context.getString(message));
        mHandler.removeCallbacks(r);
        if (toast == null) {//只有mToast==null时才重新创建，否则只需更改提示文字
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 150);
        }
        toast.setView(view);
        mHandler.postDelayed(r, delay);//延迟1秒隐藏toast
        toast.show();
    }


    public static void showMaxLengthTip(Context context, int size){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast, null);//自定义布局
        TextView text = (TextView) view.findViewById(R.id.toast_message);//显示的提示文字
        text.setText("不能超过"+size+"字");
        mHandler.removeCallbacks(r);
        if (toast == null) {//只有mToast==null时才重新创建，否则只需更改提示文字
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 150);
        }
        toast.setView(view);
        mHandler.postDelayed(r, 3000);//延迟1秒隐藏toast
        toast.show();
    }

    //	public static void show(Context context, String info) {
//		if (null == toast) {
//			toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
//			// toast.setGravity(Gravity.CENTER, 0, 0);
//		} else {
//			toast.setText(info);
//		}
//		toast.show();
////		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
//	}

//	public static void show(Context context, @StringRes int info) {
//		if (null == toast) {
//			toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
//			// toast.setGravity(Gravity.CENTER, 0, 0);
//		} else {
//			toast.setText(info);
//		}
//		toast.show();
//	}

    //	public static void showLong(Context context, String info) {
//		if (null == toast) {
//			toast = Toast.makeText(context, info, Toast.LENGTH_LONG);
//			// toast.setGravity(Gravity.CENTER, 0, 0);
//		} else {
//			toast.setText(info);
//		}
//		toast.show();
//	}

//	public static void showLong(Context context,  @StringRes int info) {
//		if (null == toast) {
//			toast = Toast.makeText(context, info, Toast.LENGTH_LONG);
//			// toast.setGravity(Gravity.CENTER, 0, 0);
//		} else {
//			toast.setText(info);
//		}
//		toast.show();
////		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
//	}
}
