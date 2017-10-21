package byd.com.byd.binderpooldemo.impl;

import android.os.RemoteException;

import byd.com.byd.binderpooldemo.ISecurityCenter;

/**
 * 作者：byd666 on 2017/10/20 14:39
 * 邮箱：sdkjdxbyd@163.com
 */

public class SecurityCenterImpl extends ISecurityCenter.Stub {

    private static final char SECRET_CODE='^';
    @Override
    public String encrypt(String content) throws RemoteException {
        char [] chars=content.toCharArray();
        for (int i=0;i<chars.length;i++)
        {
            chars[i] ^=SECRET_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
