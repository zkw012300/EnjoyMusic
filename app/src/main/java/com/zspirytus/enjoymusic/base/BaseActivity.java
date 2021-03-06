package com.zspirytus.enjoymusic.base;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.zspirytus.basesdk.annotations.LayoutIdInject;
import com.zspirytus.basesdk.annotations.ViewInject;
import com.zspirytus.basesdk.utils.LogUtil;
import com.zspirytus.basesdk.utils.PixelsUtil;
import com.zspirytus.basesdk.utils.ToastUtil;
import com.zspirytus.zspermission.ZSPermission;

import java.lang.reflect.Field;

/**
 * 所有Activity的基类，负责权限申请回调、view注入以及全局Tool的定义
 * Created by ZSpirytus on 2018/8/2.
 */

public abstract class BaseActivity extends AppCompatActivity
        implements ZSPermission.OnPermissionListener {

    protected static final int REQUEST_APP_SETTINGS = 233;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        autoInjectLayoutId();
        autoInjectAllField();
        setTransparentStatusBar();

        if (savedInstanceState != null) {
            onMRestoreInstanceState(savedInstanceState);
            initView();
            registerEvent();
        } else {
            initView();
            initData();
            registerEvent();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterEvent();
    }

    protected void onMRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ZSPermission.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_APP_SETTINGS) {
            onGoToSettingsCallback();
        }
    }

    @Override
    public void onGranted() {
        // do nothing, processed by child activity if necessary
    }

    @Override
    public void onDenied() {
        // do nothing, processed by child activity if necessary
    }

    @Override
    public void onNeverAsk() {
        // do nothing, processed by child activity if necessary
    }

    protected void onGoToSettingsCallback() {
        // do nothing, processed by child activity if necessary
    }

    public void setTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void setLightStatusIconColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int option = decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(option);
        }
    }

    public void setDefaultStatusIconColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int option = decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(option);
        }
    }

    public void setTransparentNavBar() {
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public void setDefaultNavBar() {
        Window w = getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public void setFullScreenOrNot(boolean isFullScreen) {
        if (isFullScreen) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
        }
    }

    public void autoInjectAllField() {
        try {
            Class<?> clazz = this.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ViewInject.class)) {
                    ViewInject inject = field.getAnnotation(ViewInject.class);
                    int id = inject.value();
                    if (id > 0) {
                        field.setAccessible(true);
                        field.set(this, this.findViewById(id));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void autoInjectLayoutId() {
        Class<?> clazz = this.getClass();
        if (clazz.isAnnotationPresent(LayoutIdInject.class)) {
            LayoutIdInject layoutIdInject = clazz.getAnnotation(LayoutIdInject.class);
            int layoutId = layoutIdInject.value();
            if (layoutId > 0) {
                setContentView(layoutId);
            }
        }
    }

    protected void goToSettings() {
        /*Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, REQUEST_APP_SETTINGS);*/
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_APP_SETTINGS);
    }

    protected void initView() {
    }

    protected void initData() {
    }

    protected void registerEvent() {
    }

    protected void unregisterEvent() {
    }

    protected void fixNavBarHeight(View... views) {
        int navBarHeight = PixelsUtil.getNavigationBarHeight(this);
        for (View view : views) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            lp.bottomMargin = navBarHeight;
        }
    }

    /**
     * Log or Global Tools
     */

    public final void e(String message) {
        String TAG = this.getClass().getSimpleName();
        LogUtil.e(TAG, message);
    }

    public final void out(Object message) {
        LogUtil.out(message);
    }

    public final void toast(String message) {
        ToastUtil.showToast(this, message);
    }

}
