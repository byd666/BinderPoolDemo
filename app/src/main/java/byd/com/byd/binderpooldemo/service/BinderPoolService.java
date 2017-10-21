package byd.com.byd.binderpooldemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import byd.com.byd.binderpooldemo.impl.BinderPool;

/**
 * 作者：byd666 on 2017/10/21 10:47
 * 邮箱：sdkjdxbyd@163.com
 */

public class BinderPoolService extends Service {
    private static final String TAG="BinderPoolService";
    private Binder mBinderPool=new BinderPool.BinderPoolImpl();
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBinder()");
        return mBinderPool;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
