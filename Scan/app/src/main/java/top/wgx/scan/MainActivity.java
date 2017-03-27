package top.wgx.scan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import top.wgx.mscan.activity.ScanHelper;
import top.wgx.mscan.activity.ScanforResult;

/**
 *
 * 简单实现二维码/条形码扫描
 * 开始扫描：ScanHelper.getScanHelper().Scan(this);
 * 接收结果：interface ScanforResult
 * @author WU gongxi
 *
 */

public class MainActivity extends AppCompatActivity {

    private ScanHelper mScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Onclick(View view) {
        mScan = ScanHelper.getScanHelper().Scan(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mScan.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mScan.onActivityResult(requestCode, resultCode, data, new ScanforResult() {
            @Override
            public void ScaforSucceedResult(String r) {
                Toast.makeText(MainActivity.this,"succeed--"+r,Toast.LENGTH_LONG).show();
            }

            @Override
            public void ScaforfaildResult(String r) {
                Toast.makeText(MainActivity.this,"faild--"+r,Toast.LENGTH_LONG).show();
            }
        });
    }
}
