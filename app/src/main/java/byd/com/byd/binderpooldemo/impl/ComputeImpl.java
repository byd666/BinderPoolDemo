package byd.com.byd.binderpooldemo.impl;

import android.os.RemoteException;

import byd.com.byd.binderpooldemo.ICompute;

/**
 * 作者：byd666 on 2017/10/20 14:41
 * 邮箱：sdkjdxbyd@163.com
 */

public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a+b;
    }
}
