package com.spc.myapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.spc.myapplication.utils.NetUtils;

public class NetWorkBroadcastReceiver extends BroadcastReceiver {

    /**
     * 返回网络标签
     * @param type
     * @return
     */
    private String getConnType(int type) {
        String connType = "";
        if(type == NetUtils.NETWORK_MOBILE) {
            connType = "移动数据流量";
        } else if(type == NetUtils.NETWORK_WIFI) {
            connType = "WIFI";
        } else if(type == NetUtils.NETWORK_NONE) {
            connType = "当前网络未连接";
        }
        return connType;
    }

    /**
     * 注册广播
     * @param context
     * @param netWork
     */
    public static void registerReceiver(Context context, NetWorkBroadcastReceiver netWork) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(netWork, filter);
    }

    /**
     * 取消注册广播
     * @param context
     * @param netWork
     */
    public static void unRegisterReceiver(Context context, NetWorkBroadcastReceiver netWork) {
        context.unregisterReceiver(netWork);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 监听wifi的打开与关闭，与wifi的连接无关
//            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
//            Log.e("TAG", "wifiState:" + wifiState);
//            switch (wifiState) {
//                case WifiManager.WIFI_STATE_DISABLED:
//                    //WIFI关闭后
//
//                    break;
//                case WifiManager.WIFI_STATE_DISABLING:
//                    //WIFI关闭时
//
//                    break;
//                default:
//            }
//        }
        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取联网状态的NetworkInfo对象
            NetworkInfo info = connectivity.getActiveNetworkInfo();

            switch (NetUtils.getNetType(info)) {
                case NetUtils.NETWORK_WIFI:
                case NetUtils.NETWORK_MOBILE:

                    Toast.makeText(context, "您当前正在使用" + getConnType(info.getType()),
                            Toast.LENGTH_LONG).show();
                    Log.i("TAG", getConnType(info.getType()) + "已连接");
                    break;
                case NetUtils.NETWORK_NONE:
                    Log.i("TAG", getConnType(NetUtils.NETWORK_NONE));
                    Toast.makeText(context, getConnType(NetUtils.NETWORK_NONE),
                            Toast.LENGTH_LONG).show();
                    break;
            }

        }
    }

}
