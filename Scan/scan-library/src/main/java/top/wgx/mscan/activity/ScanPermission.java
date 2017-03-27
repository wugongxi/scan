package top.wgx.mscan.activity;

/**
 * Created by BM-WGX on 2017/1/9.
 */

public interface ScanPermission {
    void ScaforfaildResult(String[] permissions, int[] grantResults);

    void ScaforSucceedResult(String[] permissions, int[] grantResults);
}
