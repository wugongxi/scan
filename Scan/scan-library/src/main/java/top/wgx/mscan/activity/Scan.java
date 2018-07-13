package top.wgx.mscan.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by BM-WGX on 2016/12/30.
 */

public interface Scan  {
    int c =  0x00001100;
    void Scan(Fragment f);
    void Scan(android.app.Fragment f);
    void Scan();
    void ScanHelp(int requestCode, int resultCode, Intent data, ScanforResult s);
    void ScanPermissionsHelp(int requestCode, String[] permissions, int[] grantResults, ScanPermission sp);
}
