package com.zspirytus.enjoymusic.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zspirytus.enjoymusic.interfaces.ViewInject;
import com.zspirytus.enjoymusic.utils.LogUtil;
import com.zspirytus.enjoymusic.utils.ToastUtil;
import com.zspirytus.enjoymusic.view.activity.BaseActivity;

import java.lang.reflect.Field;

/**
 * Created by ZSpirytus on 2018/8/2.
 */

public abstract class BaseFragment extends Fragment {

    private BaseActivity parentActivity;

    @Override
    public void onAttach(Context context) {
        parentActivity = (BaseActivity) context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        autoInjectAllField(view);
        return view;
    }

    public void autoInjectAllField(View view) {
        try {
            Class<?> clazz = this.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ViewInject.class)) {
                    ViewInject inject = field.getAnnotation(ViewInject.class);
                    int id = inject.value();
                    if (id > 0) {
                        field.setAccessible(true);
                        field.set(this, view.findViewById(id));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public final void e(String message) {
        String TAG = this.getClass().getSimpleName();
        LogUtil.e(TAG, message);
    }

    public final void toast(String message) {
        ToastUtil.showToast(parentActivity, message);
    }

    public BaseActivity getParentActivity() {
        return parentActivity;
    }

    public abstract Integer getLayoutId();

}
