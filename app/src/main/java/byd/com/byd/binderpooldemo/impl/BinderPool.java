package byd.com.byd.binderpooldemo.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

import byd.com.byd.binderpooldemo.IBinderPool;
import byd.com.byd.binderpooldemo.service.BinderPoolService;

/**
 * 作者：byd666 on 2017/10/21 10:01
 * 邮箱：sdkjdxbyd@163.com
 * Binder的连接池
 */

public class BinderPool {
    private static String TAG = "BinderPool";
    public static final int BINDER_SECURITY_CENTER = 1;
    public static final int BINDER_NONE = -1;
    public static final int BINDER_COMPUTE = 0;
    private IBinderPool mBinderPool;
    private CountDownLatch mConnectBinderPoolCountDownLatch;
    private Context mContext;
    private static volatile BinderPool sInstance;

    public BinderPool(Context context) {
        this.mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    //设计一个懒汉式单例模式，用来返回BinderPool的对象
    public static BinderPool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BinderPool.class) {
                if (sInstance == null) {
                    sInstance = new BinderPool(context);
                }
            }
        }
        return sInstance;
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //连接远程服务端
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mConnectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /**
     * 通过binderCode从线程池中查询相应的binder对象
     * @param binderCode binder对象唯一的标识
     * @return binder 根据binderCode找到相应的Binder对象，没有找到返回null
     */
    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        try{
            if (mBinderPool != null) {
                binder = mBinderPool.queryBinder(binderCode);
            }
        }catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        //销毁Binder对象
        @Override
        public void binderDied() {
            Log.w(TAG, "binder died");
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
            mBinderPool = null;
            connectBinderPoolService();
        }
    };

    //连接远程服务端Service
    private synchronized void connectBinderPoolService() {
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent intent = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(intent, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static class BinderPoolImpl extends IBinderPool.Stub {
        //请求转发的实现方法，当Binder连接池连接上远程服务时，
        //会根据不同的模块的标识即binderCode返回不同的Binder对象
        //通过这个Binder对象所执行的操作全部发生在远程服务端
        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_SECURITY_CENTER:
                    binder = new SecurityCenterImpl();
                    break;
                case BINDER_COMPUTE:
                    binder = new ComputeImpl();
                    break;
                default:
                    break;
            }
            return binder;
        }
    }

}
