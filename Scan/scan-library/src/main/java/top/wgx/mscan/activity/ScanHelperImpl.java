package top.wgx.mscan.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by BM-WGX on 2016/12/30.
 */

public class ScanHelperImpl implements Scan {
    private Activity a;
    @Override
    public void Scan(Fragment f) {
        f.startActivityForResult(getI(), c);
}

    public Activity getA() {
        return this.a;
    }

    public ScanHelperImpl setA(Activity a) {
        this.a = a;
        return this;
    }

    @Override
    public void Scan( android.app.Fragment f) {
        f.startActivityForResult(getI(), c);
    }

    @NonNull
    private Intent getI() {
        if (this.a==null){
            Log.e("----log--err","a=null");
        }
        return new Intent(this.a, CaptureActivity.class);
    }

    @Override
    public void Scan() {
        if (this.a==null){
            Log.e("----log--err","a==null");
        }
        this.a.startActivityForResult(getI(), c);
    }

    @Override
    public void ScanHelp(int r, int re, Intent d, ScanforResult s) {
        if (r == c) {
            if (d != null) {
                s.ScaforSucceedResult(d.getExtras().getString(CaptureActivity.result));
            } else {
                s.ScaforfaildResult("");
            }
        }
    }
    @Override
    public void ScanPermissionsHelp(int requestCode, String[] permissions, int[] grantResults, ScanPermission sp) {
        if (requestCode == Scan.c) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (sp!=null){
                    sp.ScaforSucceedResult(permissions,grantResults);
                }
                if (getA()!=null){
                this.Scan();
                }
            } else {
                if (sp!=null) {
                    sp.ScaforfaildResult(permissions, grantResults);
                }else {
                    if (a!=null){
                    Toast.makeText(a,"请在权限管理中设置“允许”使用相机",Toast.LENGTH_LONG).show();
                    }
                }

            }
            return;
        }
    }
}
