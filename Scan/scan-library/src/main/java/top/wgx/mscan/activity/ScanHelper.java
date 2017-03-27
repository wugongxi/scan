package top.wgx.mscan.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 *
 * 简单实现二维码/条形码扫描
 * 开始扫描：ScanHelper.getScanHelper().Scan(this);
 * 接收结果：interface ScanforResult
 *
 * Created by BM-WGX on 2016/12/30.
 */

public class ScanHelper {
    private Scan mSH;
    private static ScanHelper sH = null;
    private Activity a;

    private ScanHelper() {
        mSH = new ScanHelperImpl();
    }

    public static ScanHelper getScanHelper() {

        return (sH != null) ? sH : new ScanHelper();
    }

    public ScanHelper Scan(Activity a, Fragment f) {
        this.a = a;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(a,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mSH.Scan(a, f);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                a.requestPermissions(new String[]{Manifest.permission.CAMERA}, Scan.c);
            }
        }
        return this;
    }

    public ScanHelper Scan(Activity a, android.app.Fragment f) {
        this.a = a;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(a,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mSH.Scan(a, f);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                a.requestPermissions(new String[]{Manifest.permission.CAMERA}, Scan.c);
            }
        }
        return this;
    }


    public ScanHelper Scan(Activity a) {
        this.a = a;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(a,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mSH.Scan(a);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                a.requestPermissions(new String[]{Manifest.permission.CAMERA}, Scan.c);
            }
        }

        return this;
    }

    public void onActivityResult(int r, int re, Intent d, @NonNull ScanforResult sr) {
        if (d != null) {
            mSH.ScanHelp(r, re,d, sr);
        }else {
            throw new IllegalStateException("错误的调用");
        }
        sH=null;
        mSH=null;
    }

    public ScanHelper onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults,@NonNull ScanPermission sp) {
        mSH.ScanPermissionsHelp(a, requestCode, permissions, grantResults, sp);
        a=null;
        return this;
    }
    public ScanHelper onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        onRequestPermissionsResult(requestCode, permissions, grantResults, null);
        return this;
    }
}
