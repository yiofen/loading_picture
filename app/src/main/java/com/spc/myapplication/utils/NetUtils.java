package com.spc.myapplication.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 网络处理工具
 */
public class NetUtils {
    public static final int NETWORK_NONE = -1;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_MOBILE = 0;

    private static Boolean isConn = false;

    /**
     * 获取网络类型
     * @param info
     * @return
     */
    public static int getNetType(NetworkInfo info) {
        isConn = false;
        if(info != null) {
            if(NetworkInfo.State.CONNECTED == info.getState() && info.isConnected()) {
                switch (info.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        //WIFI
                        isConn = true;
                        return NETWORK_WIFI;
                    case ConnectivityManager.TYPE_MOBILE:
                        //移动数据
                        isConn = true;
                        return NETWORK_MOBILE;
                }
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

    /**
     * 判断网络是否连接
     * @return
     */
    public static Boolean checkNetConnected() {
        return isConn;
    }

    /**
     * 获取IP
     * @param host
     * @return
     */
    public static String GetInetAddress(String host) {
        String IPAddress = "";
        InetAddress ReturnStr1 = null;
        try {
            ReturnStr1 = java.net.InetAddress.getByName(host);
            IPAddress = ReturnStr1.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return IPAddress;
        }
        return IPAddress;
    }

}
