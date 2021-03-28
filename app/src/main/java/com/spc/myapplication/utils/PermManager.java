package com.spc.myapplication.utils;

import android.Manifest;

/**
 * 动态权限管理
 */
public class PermManager {
    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

}
