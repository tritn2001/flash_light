package com.lutech.flashlight.util;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.lutech.flashlight.callback.HandleEventCheckPermissionListener;

import java.util.List;

public class PermissionManager {
    private final Context context;

    private LocationManager locationManager;


    public PermissionManager(Context context) {
        this.context = context;
    }

    public boolean isReadPhoneGranted() {
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_DENIED;
    }


    public boolean isCameraGranted() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED;
    }

    public void requestPermissionNotification(HandleEventCheckPermissionListener handleEventCheckPermissionListener) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                handleEventCheckPermissionListener.onAcceptPermissions();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                handleEventCheckPermissionListener.onDeniedPermissions();

            }
        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.POST_NOTIFICATIONS)
                .check();
    }

    public boolean isNotificationServiceRunning() {
        ContentResolver contentResolver = context.getContentResolver();
        String enabledNotificationListeners =
                Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = context.getPackageName();
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }

    public void gotoAllowNotificationAccess() {
        context.startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }

    public void requestPermissionReadPhoneAndCamera(HandleEventCheckPermissionListener handleEventCheckPermissionListener) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                handleEventCheckPermissionListener.onAcceptPermissions();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                handleEventCheckPermissionListener.onDeniedPermissions();

            }
        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)
                .check();
    }


}
