package top.wgx.mscan.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

import top.wgx.mscan.R;
import top.wgx.mscan.camera.CameraManager;


/**
 * Created by BM-WGX on 2017/1/4.
 */

/**
 *
 * 这种观点是覆盖在上面的相机预览。它增加了取景器矩形和部分
 *透明外,以及激光扫描仪点动画和结果。
 *
 * @author BM-WGX
 */
public final class ViewfinderView extends View {
    //下面三个变量不用管，一般都是这个标准大小
    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 10L;
    private static final int OPAQUE = 0xFF;

    GradientDrawable mDrawable;
    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private int laserFrameBoundColor ;
    private int laserFrameCornerWidth;
    private int laserFrameCornerLength;
    private int drawTextColor = Color.WHITE;
    private boolean drawTextGravityBottom = false;
    private String drawText = "将取景框对准二维码即可自动扫描";
    private int drawTextSize;
    private int drawTextMargin;
    private final int resultPointColor;


    private int slideTop;

    private int slideBottom;

    private static final int SPEEN_DISTANCE = 5;
    boolean isFirst;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        laserFrameBoundColor=resources.getColor(R.color.delete_color);//color
        laserFrameCornerLength=40;
        laserFrameCornerWidth=12;
        drawTextSize=50;
        drawTextMargin=100;
        possibleResultPoints = new HashSet<ResultPoint>(2);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }

        if(!isFirst){
            isFirst = true;
            slideTop = frame.top;
            slideBottom = frame.bottom;
        }

        actionView(canvas,frame);


        postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
    }
    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

    public void drawFrameCorner(Canvas canvas, Rect frame) {
        paint.setColor(laserFrameBoundColor);
        paint.setStyle(Paint.Style.FILL);
// 左上角
        canvas.drawRect(frame.left - laserFrameCornerWidth, frame.top, frame.left, frame.top
                + laserFrameCornerLength, paint);
        canvas.drawRect(frame.left - laserFrameCornerWidth, frame.top - laserFrameCornerWidth, frame.left
                + laserFrameCornerLength, frame.top, paint);
// 右上角
        canvas.drawRect(frame.right, frame.top, frame.right + laserFrameCornerWidth,
                frame.top + laserFrameCornerLength, paint);
        canvas.drawRect(frame.right - laserFrameCornerLength, frame.top - laserFrameCornerWidth,
                frame.right + laserFrameCornerWidth, frame.top, paint);
// 左下角
        canvas.drawRect(frame.left - laserFrameCornerWidth, frame.bottom - laserFrameCornerLength,
                frame.left, frame.bottom, paint);
        canvas.drawRect(frame.left - laserFrameCornerWidth, frame.bottom, frame.left
                + laserFrameCornerLength, frame.bottom + laserFrameCornerWidth, paint);
// 右下角
        canvas.drawRect(frame.right, frame.bottom - laserFrameCornerLength, frame.right
                + laserFrameCornerWidth, frame.bottom, paint);
        canvas.drawRect(frame.right - laserFrameCornerLength, frame.bottom, frame.right
                + laserFrameCornerWidth, frame.bottom + laserFrameCornerWidth, paint);
    }

    public void drawText(Canvas canvas, Rect frame) {
        int width = canvas.getWidth();
        final float textWidth = paint.measureText(drawText);
        paint.setColor(drawTextColor);
        paint.setTextSize(new BigDecimal(width*0.66/14).floatValue());

        float x = (width - textWidth) / 2;
        float y = drawTextGravityBottom ? frame.bottom + drawTextMargin : frame.top - drawTextMargin*2;
        canvas.drawText(drawText, x, y, paint);
    }

    public void drawRect2(Canvas canvas, Rect frame){
        canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
        canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
        canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
        canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);
    }

    public void drawRect1(Canvas canvas, Rect frame){
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);
    }

    public void lineMove(Canvas canvas, Rect frame){
        slideTop += SPEEN_DISTANCE;
        if(slideTop >= frame.bottom){
            slideTop = frame.top;
        }
        Rect lineRect = new Rect();
        lineRect.left = frame.left;
        lineRect.right = frame.right;
        lineRect.top = slideTop;
        lineRect.bottom = slideTop + 18;
        canvas.drawBitmap(((BitmapDrawable)(getResources().getDrawable(R.drawable.qrcode_scan_line))).getBitmap(), null, lineRect, paint);//======

    }


    public void actionView(Canvas canvas,Rect frame){

        if (resultBitmap != null) {
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
            drawRect1(canvas, frame);
            drawRect2(canvas, frame);
            drawFrameCorner(canvas, frame);
            drawText(canvas,frame);
            lineMove(canvas, frame);

            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
                }
            }
        }
    }}