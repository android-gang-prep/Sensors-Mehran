package com.example.mehranm1;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ChartRecord extends View {

    List<RecordItemModel> items;
    Paint line;
    List<RecordItemModel> sortItem;
    int paddingHor = 30;
    int paddingVertical = 30;
    private Paint paint;
    private Paint circlePaint;
    private Path path = new Path();
    List<ImageModel> image;

    public ChartRecord(Context context) {
        super(context);
        init(context);

    }

    public ChartRecord(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public ChartRecord(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public ChartRecord(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        items = new ArrayList<>();
        line = new Paint(Paint.ANTI_ALIAS_FLAG);
        line.setDither(true);
        line.setStyle(Paint.Style.STROKE);
        line.setColor(Color.BLACK);
        line.setStrokeWidth(3f * context.getResources().getDisplayMetrics().density);
        paint = new Paint();
        circlePaint = new Paint();
        sort();


    }

    private void sort() {
        sortItem = new ArrayList<>(items);
        Collections.sort(sortItem, (Comparator) (o1, o2) -> {
            Double p1 = ((RecordItemModel) o1).getLoc();
            Double p2 = ((RecordItemModel) o2).getLoc();
            return p2.compareTo(p1);
        });
    }

    private int getPos(double d) {
        for (int i = 0; i < sortItem.size(); i++) {
            if (sortItem.get(i).getLoc() == d)
                return i;
        }
        return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (image != null) {
                    for (int i = 0; i < image.size(); i++) {
                        if (x > image.get(i).getLeft() && x < image.get(i).getLeft() + (int) (40 * getContext().getResources().getDisplayMetrics().density) && y > image.get(i).getTop() && y < image.get(i).getTop() + (int) (40 * getContext().getResources().getDisplayMetrics().density)) {
                            if (onClick != null) {
                                onClick.onClick(image.get(i).getRecordItemModel());
                            }
                            break;
                        }
                    }
                }
                return true;
        }
        return false;
    }

    float visible = 0;
    PathMeasure pathMeasure;
    final Path visiblePath = new Path();
    final float[] pos = new float[2];

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (animator == null) {
            data();
            startAnimate();
            return;
        }

        if (visible == 0 || items.size() < 1)
            return;


        if (visible < 1f && pathMeasure != null) {
            visiblePath.reset();
            pathMeasure.getSegment(0f, pathMeasure.getLength() * visible, visiblePath, true);
            pathMeasure.getPosTan(pathMeasure.getLength() * visible, pos, null);
            canvas.drawPath(visiblePath, line);

            for (int i = 0; i < image.size(); i++) {
                if (image.get(i).getLeft() > pos[0])
                    break;
                if (i == 0) {
                    setCircleText("START", canvas, image.get(i).getLeft(), image.get(i).getTop());
                } else if (i == image.size() - 1) {
                    setCircleText("END", canvas, image.get(i).getLeft(), image.get(i).getTop());
                } else {
                    canvas.drawBitmap(image.get(i).getBitmap(), image.get(i).getLeft(), image.get(i).getTop(), paint);

                }
            }

        } else {
            canvas.drawPath(path, line);
            for (int i = 0; i < image.size(); i++) {

                if (i == 0) {
                    setCircleText("START", canvas, image.get(i).getLeft(), image.get(i).getTop());
                } else if (i == image.size() - 1) {
                    setCircleText("END", canvas, image.get(i).getLeft(), image.get(i).getTop());
                } else {
                    canvas.drawBitmap(image.get(i).getBitmap(), image.get(i).getLeft(), image.get(i).getTop(), paint);
                }
            }
        }

    }


    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        int width = (int) (30 * getContext().getResources().getDisplayMetrics().density);
        int height = (int) (30 * getContext().getResources().getDisplayMetrics().density);
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(width / 2, height / 2,
                width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public void setCircleText(String str, Canvas canvas, float left, float top) {
        int width = (int) (30 * getContext().getResources().getDisplayMetrics().density);

        paint.setColor(Color.WHITE);
        paint.setTextSize(25f);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        Rect bounds = new Rect();

        paint.getTextBounds(str, 0, str.length(), bounds);

        circlePaint.setColor(Color.GRAY);
        circlePaint.setAntiAlias(true);

        canvas.drawCircle(left, top, width / 2, circlePaint);

        canvas.drawText(str, left, top, paint);
    }

    public void addData(RecordItemModel recordItemModel) {
        items.add(recordItemModel);
        sort();
        invalidate();
    }

    private ValueAnimator animator = null;

    public void addAllData(List<RecordItemModel> recordItemModel) {
        if (items.size() > 0)
            return;
        items.addAll(recordItemModel);
        sort();
        invalidate();
    }

    AdapterRec.OnClick onClick;

    public void setListener(AdapterRec.OnClick onClick) {
        this.onClick = onClick;
    }

    ValueAnimator colorA;

    public void startAnimate() {
        if (animator != null)
            animator.cancel();

        if (colorA != null)
            colorA.cancel();
        visible = 0f;
        pathMeasure = new PathMeasure(path, false);
        invalidate();
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000);
        animator.setStartDelay(300);
        animator.addUpdateListener(a -> {
            visible = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.start();

        colorA = ValueAnimator.ofArgb(Color.parseColor("#d45e80"), Color.parseColor("#d7e97b"), Color.parseColor("#a899ea"), Color.parseColor("#9bcdf0"));
        colorA.setRepeatCount(ValueAnimator.INFINITE);
        colorA.setDuration(2000);
        colorA.addUpdateListener(a -> {
            line.setColor((int) a.getAnimatedValue());
            invalidate();
        });
        colorA.start();
    }

    private void data() {
        image = new ArrayList<>();

        int space = (int) ((getWidth() - (paddingHor * 2 * getContext().getResources().getDisplayMetrics().density)) / (items.size() - 1));
        int spaceH = (int) (getHeight() - (paddingVertical * 2 * getContext().getResources().getDisplayMetrics().density)) / (items.size() - 1);


        float startY = spaceH * getPos(items.get(0).getLoc());

        if (startY <= (paddingVertical * getContext().getResources().getDisplayMetrics().density)) {
            startY += (paddingVertical * getContext().getResources().getDisplayMetrics().density);
        }

        path.moveTo((paddingVertical * getContext().getResources().getDisplayMetrics().density), startY);

        for (int i = 1; i < items.size(); i++) {
            double lat = items.get(i).getLat();
            double lng = items.get(i).getLng();

            double n = (lat * lng);

            float stopX = (space * (i - 1)) + space;
            float stopY = spaceH * getPos(n);


            if (stopY >= getHeight()) {
                stopY = getHeight() - (paddingVertical * getContext().getResources().getDisplayMetrics().density);
            }
            if (stopY < (paddingVertical * getContext().getResources().getDisplayMetrics().density)) {
                stopY += (paddingVertical * getContext().getResources().getDisplayMetrics().density);
            }

            if (i == items.size() - 1) {
                stopX = getWidth() - (paddingHor * getContext().getResources().getDisplayMetrics().density);
            }

            if (i == 1) {
                image.add(new ImageModel(null, (float) (paddingVertical * getContext().getResources().getDisplayMetrics().density), (float) startY + (30 * getContext().getResources().getDisplayMetrics().density), items.get(i)));
            }
            if (!items.get(i).getImg().isEmpty()) {
                image.add(new ImageModel(getCroppedBitmap(BitmapFactory.decodeFile(items.get(i).getImg())), (float) stopX, (float) stopY + (10 * getContext().getResources().getDisplayMetrics().density), items.get(i)));
            } else {
                image.add(new ImageModel(null, (float) stopX, (float) stopY + (30 * getContext().getResources().getDisplayMetrics().density), items.get(i)));
            }

            path.lineTo(stopX, stopY);


        }


    }

    private RectF getRectF(int i) {
        RecordItemModel recordItemModel;
        if (i>items.size()-1)
            return new RectF(0, 0, 0, 0);
        int space = (int) ((getWidth() - (paddingHor * 2 * getContext().getResources().getDisplayMetrics().density)) / (items.size() - 1));
        int spaceH = (int) (getHeight() - (paddingVertical * 2 * getContext().getResources().getDisplayMetrics().density)) / (items.size() - 1);

        double lat = items.get(i).getLat();
        double lng = items.get(i).getLng();
        double n = (lat * lng);

        float stopX = (space * (i - 1)) + space;
        float stopY = spaceH * getPos(n);


        if (stopY >= getHeight()) {
            stopY = getHeight() - (paddingVertical * getContext().getResources().getDisplayMetrics().density);
        }
        if (stopY < (paddingVertical * getContext().getResources().getDisplayMetrics().density)) {
            stopY += (paddingVertical * getContext().getResources().getDisplayMetrics().density);
        }

        if (i == items.size() - 1) {
        if (i == items.size() - 1) {
            stopX = getWidth() - (paddingHor * getContext().getResources().getDisplayMetrics().density);
        }

        return new RectF(stopX, stopY, stopX, stopY);
    }
}
