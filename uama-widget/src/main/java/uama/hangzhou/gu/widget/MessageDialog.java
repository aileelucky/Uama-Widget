package uama.hangzhou.gu.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import gu.hangzhou.uama.widget.R;
import uama.hangzhou.gu.util.DeviceUtils;

/**
 * Created by gujiajia on 2016/5/26.
 * E-mail 965939858@qq.com
 * Tel: 15050261230
 */
public class MessageDialog {

    /*
     *底部的dialog
     */
    public static android.app.Dialog showBottomMenu(Context context, String[] str, final MenuDialogOnItemClickListener menuDialogClickListener) {
        return ShowBottomMenuDialog(context, str, menuDialogClickListener);
    }

    private static android.app.Dialog ShowBottomMenuDialog(Context context, String[] str, final MenuDialogOnItemClickListener menuDialogClickListener) {
        final android.app.Dialog dialog = new android.app.Dialog(context, R.style.DialogStyle);
        dialog.setCancelable(true);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_popupwindow_bottommenu, null);
        dialog.setContentView(view);
        TextView menu4 = (TextView) view.findViewById(R.id.pop_5);
        TextView menu3 = (TextView) view.findViewById(R.id.pop_4);
        TextView menu2 = (TextView) view.findViewById(R.id.pop_2);
        TextView menu1 = (TextView) view.findViewById(R.id.pop_1);
        TextView missBtn = (TextView) view.findViewById(R.id.pop_3);
        View divider1 = view.findViewById(R.id.popup_window_bottom_divider1);
        View divider2 = view.findViewById(R.id.popup_window_bottom_divider2);
        View divider3 = view.findViewById(R.id.popup_window_bottom_divider3);
        switch (str.length) {
            case 1:
                menu1.setVisibility(View.VISIBLE);
                menu1.setText(str[0]);
                break;
            case 2:
                divider1.setVisibility(View.VISIBLE);
                menu1.setVisibility(View.VISIBLE);
                menu2.setVisibility(View.VISIBLE);
                menu1.setText(str[0]);
                menu2.setText(str[1]);
                break;
            case 3:
                divider1.setVisibility(View.VISIBLE);
                divider2.setVisibility(View.VISIBLE);
                menu1.setVisibility(View.VISIBLE);
                menu2.setVisibility(View.VISIBLE);
                menu3.setVisibility(View.VISIBLE);
                menu1.setText(str[0]);
                menu2.setText(str[1]);
                menu3.setText(str[2]);
                break;
            case 4:
                divider1.setVisibility(View.VISIBLE);
                divider2.setVisibility(View.VISIBLE);
                divider3.setVisibility(View.VISIBLE);
                menu1.setVisibility(View.VISIBLE);
                menu2.setVisibility(View.VISIBLE);
                menu3.setVisibility(View.VISIBLE);
                menu4.setVisibility(View.VISIBLE);
                menu1.setText(str[0]);
                menu2.setText(str[1]);
                menu3.setText(str[2]);
                menu4.setText(str[3]);
        }
        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialogClickListener.onItemClick(1);
                dialog.dismiss();
            }
        });
        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialogClickListener.onItemClick(2);
                dialog.dismiss();
            }
        });
        menu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialogClickListener.onItemClick(3);
                dialog.dismiss();
            }
        });
        menu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialogClickListener.onItemClick(4);
                dialog.dismiss();
            }
        });
        missBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            lp.width = DeviceUtils.getDisplayHeight(context) / 2;
        } else {
            lp.width = DeviceUtils.getDisplayWidth(context);
        }
        mWindow.setAttributes(lp);
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setWindowAnimations(R.style.dialog_bottom_in);
        dialog.show();
        return dialog;
    }

    public interface MenuDialogOnItemClickListener {
        public abstract void onItemClick(int index);
    }

}
