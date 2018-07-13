package top.wgx.mscan.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Vector;

import top.wgx.mscan.R;
import top.wgx.mscan.camera.CameraManager;
import top.wgx.mscan.decoding.CaptureActivityHandler;
import top.wgx.mscan.decoding.InactivityTimer;
import top.wgx.mscan.view.ViewfinderView;


/**
 * Initial the camera
 *
 * @author wu
 */
public class CaptureActivity extends Activity implements Callback {
    public static final String result = "result";
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private View status_Bar, titleBar;
    private ImageView rlBack;
    Handler mHandler = new Handler();
    /**
     * Called when the activity is first created.
     */
    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        status_Bar = findViewById(R.id.status_Bar);
        //ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
        CameraManager.init(getApplication());
        viewfinderView = findViewById(R.id.viewfinder_view);
        titleBar = findViewById(R.id.titleBar);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        setLeftBackButton();
        setStatusBar(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        //quit the scan view

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    public void setLeftBackButton() {
        View rl_back = findViewById(R.id.rl_back);
        rl_back.setVisibility(View.VISIBLE);
        ImageView mIv_back = (ImageView) findViewById(R.id.iv_back);
        if (mIv_back != null) {
            mIv_back.setImageResource(R.mipmap.button_back_y);
            mIv_back.setVisibility(View.VISIBLE);
            rl_back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CaptureActivity.this.finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();

        //FIXME
        if (resultString.equals("")) {//Scan failed!
//            Toast.makeText(CaptureActivity.this, "扫描失败", Toast.LENGTH_SHORT).show();
        } else {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(CaptureActivity.result, resultString);
            resultIntent.putExtras(bundle);
            this.setResult(RESULT_OK, resultIntent);
        }
        CaptureActivity.this.finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /**
     * 设置沉浸式状态栏 不调用，则没有沉浸式，主题默认状态栏状态
     *
     * @param isFULLSCREEN 是否全屏，//有状态栏，有背景
     * @Method setStatusBarFullandNagivation 是否有底部导航 // 应对华为等手机
     */
    protected void setStatusBar(boolean isFULLSCREEN) {
            Window win = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            win.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.setStatusBarColor(Color.parseColor("#161918"));//可以自定义状态栏颜色
            return;
        }else {
            return;
        }
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (!isFULLSCREEN) {

                mHandler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                int statusHeight = getStatusBarHeight();
//                                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) status_Bar.getLayoutParams();
//                                params.height = statusHeight;
//                                topBar.setMargins(0, statusHeight, 0, 0);
//                                status_Bar.setLayoutParams(params);
//                                status_Bar.setBackgroundColor(Color.parseColor("#161918"));
//                                status_Bar.setVisibility(View.VISIBLE);
                                Log.e("  -height", "--" + statusHeight);
//                                titleBar.getHeight()
//                                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) viewfinderView.getLayoutParams();
//                                layoutParams.setMargins(0, status_Bar.getLayoutParams().height + statusHeight, 0, 0);
//                                viewfinderView.setLayoutParams(layoutParams);
                            }
                        }, 10);

            } else {
                mHandler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                status_Bar.setVisibility(View.GONE);
                                onHideTopbar();
                            }
                        }, 10);
            }
        }
        */
    }

    public void onHideTopbar() {
        if (status_Bar != null) {
            status_Bar.setVisibility(View.GONE);
        }
    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    protected int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}