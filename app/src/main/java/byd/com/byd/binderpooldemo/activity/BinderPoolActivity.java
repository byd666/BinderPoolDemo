package byd.com.byd.binderpooldemo.activity;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import byd.com.byd.binderpooldemo.ICompute;
import byd.com.byd.binderpooldemo.ISecurityCenter;
import byd.com.byd.binderpooldemo.R;
import byd.com.byd.binderpooldemo.impl.BinderPool;
import byd.com.byd.binderpooldemo.impl.ComputeImpl;
import byd.com.byd.binderpooldemo.impl.SecurityCenterImpl;

public class BinderPoolActivity extends AppCompatActivity {
    ISecurityCenter mSecurityCenter;
    ICompute mCompute;
    private static final String TAG="BinderPoolActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //开启一个子线程，去验证binderPool的连接
        new Thread(new Runnable() {
            @Override
            public void run() {
                BinderPool binderPool=BinderPool.getInstance(BinderPoolActivity.this);
                IBinder securityBinder=binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
                mSecurityCenter= SecurityCenterImpl.asInterface(securityBinder);
                Log.d(TAG,"visit ISecurityCenter");
                String msg="hello word，this is android";
                System.out.println("content:"+msg);
                try {
                    String password=mSecurityCenter.encrypt(msg);
                    System.out.println("encrypt:"+password);
                    System.out.println("decrypt:"+mSecurityCenter.decrypt(password));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
             /** ___________________________________无敌分割线___________________________________________*/
                Log.d(TAG,"visit ICompute");
                IBinder computeBinder=binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
                mCompute= ComputeImpl.asInterface(computeBinder);
                try {
                    System.out.println("8+9="+mCompute.add(8,9));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
