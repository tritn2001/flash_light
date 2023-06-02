package com.lutech.flashlight.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;

public class CustomDialog {
    private Context context;

    public static final int TYPE_PERMISSION_LOCATION = 0;
    public static final int TYPE_PERMISSION_CAMERA = 1;
    public static final int TYPE_PERMISSION_EXTERNAL = 2;


    public CustomDialog(Context context) {
        this.context = context;
    }

    public Dialog setDialog(int layout) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        return dialog;
    }




}
