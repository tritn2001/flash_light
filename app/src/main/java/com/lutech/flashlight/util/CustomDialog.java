package com.lutech.flashlight.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;

import com.lutech.flashlight.R;
import com.lutech.flashlight.callback.HandleEventCheckPermissionListener;

public class CustomDialog {
    private final Context context;

    public static final int TYPE_PERMISSION_LOCATION = 0;
    public static final int TYPE_PERMISSION_CAMERA = 1;
    public static final int TYPE_PERMISSION_EXTERNAL = 2;


    public CustomDialog(Context context) {
        this.context = context;
    }

    public Dialog setDialog(int layout) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        return dialog;
    }

    public Dialog dialogCheckPermissionCallPhoneState(HandleEventCheckPermissionListener handleEventCheckPermissionListener) {
        Dialog dialog = setDialog(R.layout.layout_check_permission);
        dialog.findViewById(R.id.btnOkPermission).setOnClickListener(view -> {
            new PermissionManager(context).requestPermissionReadPhoneAndCamera(handleEventCheckPermissionListener);
            dialog.dismiss();
        });


        return dialog;
    }

    public Dialog dialogInstallSuccess() {
        Dialog dialog = setDialog(R.layout.layout_dialog_install_success);
        dialog.findViewById(R.id.btnOk).setOnClickListener(view -> {
            dialog.dismiss();
        });


        return dialog;
    }

    public Dialog dialogInstalling() {
        Dialog dialog = setDialog(R.layout.layout_dialog_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


}
