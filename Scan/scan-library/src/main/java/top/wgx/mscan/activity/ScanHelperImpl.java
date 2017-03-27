package top.wgx.mscan.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Toast;


/**
 * Created by BM-WGX on 2016/12/30.
 */

public class ScanHelperImpl implements Scan {
    @Override
    public void Scan(Activity a, Fragment f) {
        f.startActivityForResult(getI(a), c);
}

    @Override
    public void Scan(Activity a, android.app.Fragment f) {
        f.startActivityForResult(getI(a), c);
    }

    @NonNull
    private Intent getI(Activity a) {
        return new Intent(a, CaptureActivity.class);
    }

    @Override
    public void Scan(Activity a) {
        a.startActivityForResult(getI(a), c);
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
    public void ScanPermissionsHelp(Activity a, int requestCode, String[] permissions, int[] grantResults, ScanPermission sp) {
        if (requestCode == Scan.c) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (sp!=null){
                    sp.ScaforSucceedResult(permissions,grantResults);
                }
                Scan(a);
            } else {
                if (sp!=null) {
                    sp.ScaforfaildResult(permissions, grantResults);
                }else {
                    Toast.makeText(a,"请在权限管理中设置“允许”使用相机",Toast.LENGTH_LONG).show();
                }

            }
            return;
        }
    }
}
