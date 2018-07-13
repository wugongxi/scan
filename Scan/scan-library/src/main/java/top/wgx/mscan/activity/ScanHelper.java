package top.wgx.mscan.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * 简单实现二维码/条形码扫描
 * 开始扫描：ScanHelper.getScanHelper().Scan(this);
 * 接收结果：interface ScanforResult
 * <p>
 * Created by BM-WGX on 2016/12/30.
 */

public class ScanHelper {
    private ScanHelperImpl mSH;
    private static ScanHelper sH = null;
    private Bitmap mBitmap = null;
    private String mResult = null;

    private ScanHelper() {
        mSH = new ScanHelperImpl();
    }

    public static ScanHelper getScanHelper() {
        return (sH != null) ? sH : new ScanHelper();
    }

    public ScanHelper Scan(Activity a, Fragment f) {
        mSH.setA(a);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(a,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mSH.Scan(f);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                a.requestPermissions(new String[]{Manifest.permission.CAMERA}, Scan.c);
            }
        }
        return this;
    }

    public ScanHelper Scan(Activity a, android.app.Fragment f) {
        mSH.setA(a);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(a,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mSH.Scan(f);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                a.requestPermissions(new String[]{Manifest.permission.CAMERA}, Scan.c);
            }
        }
        return this;
    }


    public ScanHelper Scan(Activity a) {
        mSH.setA(a);
        Log.e("---log-err"," a==null "+(mSH.getA()==null));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(a,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mSH.Scan();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                a.requestPermissions(new String[]{Manifest.permission.CAMERA}, Scan.c);
            }
        }

        return this;
    }

    public void onActivityResult(int r, int re, Intent d, @NonNull ScanforResult sr) {
        mSH.ScanHelp(r, re, d, sr);
        this.onDesroy();
    }

    public ScanHelper onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, @NonNull ScanPermission sp) {
        mSH.ScanPermissionsHelp(requestCode, permissions, grantResults, sp);
        return this;
    }

    public ScanHelper onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        onRequestPermissionsResult(requestCode, permissions, grantResults, null);
        return this;
    }

    public void onDesroy() {
        mSH.setA(null);
        mSH = null;
        this.mResult = null;
        this.mBitmap = null;
        sH = null;
    }
}
