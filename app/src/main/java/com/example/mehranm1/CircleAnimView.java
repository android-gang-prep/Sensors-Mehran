package com.example.mehranm1;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CircleAnimView extends View {
    public CircleAnimView(Context context) {
        super(context);
        init();
    }

    public CircleAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CircleAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

   /* Paint cir1;
    Paint cir2;
    Paint cir3;*/

    Paint cir;
    Paint cirL;
    Paint cirL2;
    Paint cirL3;

    private void init() {
        list = new ArrayList<>();
        cir = getPaint("#FF7043");
        cirL = getPaint("#FF7043");
        cirL.setStyle(Paint.Style.STROKE);
        cirL.setStrokeWidth(5);

        cirL2 = getPaint("#FF7043");
        cirL2.setStyle(Paint.Style.STROKE);
        cirL2.setStrokeWidth(5);

        cirL3 = getPaint("#FF7043");
        cirL3.setStyle(Paint.Style.STROKE);
        cirL3.setStrokeWidth(5);

        Paint cirL4 = getPaint("#FF7043");
        cirL4.setStyle(Paint.Style.STROKE);
        cirL4.setStrokeWidth(5);

        list.add(cirL);
        list.add(cirL2);
        list.add(cirL3);
        list.add(cirL4);


/*        cir1 = new Paint();
        cir1.setStyle(Paint.Style.FILL);
        cir1.setColor(Color.parseColor("#29B6F6"));

        cir2 = new Paint();
        cir2.setStyle(Paint.Style.FILL);
        cir2.setColor(Color.parseColor("#4FC3F7"));

        cir3 = new Paint();
        cir3.setStyle(Paint.Style.FILL);
        cir3.setColor(Color.parseColor("#81D4FA"));*/

/*        list = new ArrayList<>();

        list.add(getPaint("#29B6F6"));
        list.add(getPaint("#005073"));
        list.add(getPaint("#4FC3F7"));
        list.add(getPaint("#81D4FA"));
        list.add(getPaint("#107dac"));
        list.add(getPaint("#1ebbd7"));
        list.add(getPaint("#71c7ec"));*/

    }

    private Paint getPaint(String color) {
        Paint cir3 = new Paint();
        cir3.setStyle(Paint.Style.FILL);
        cir3.setColor(Color.parseColor(color));
        return cir3;
    }

    List<Paint> list;

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        cx = getWidth() / 2;
        cy = getHeight() / 2;
        radius = (float) (Math.min(getWidth(), getHeight()) / 2 * 0.95);


 /*       if (animation >= 1) {
            Paint itemToMove = list.get(0);
            int index = list.indexOf(itemToMove);
            list.remove(index);
            list.add(itemToMove);
        }*/

        float p = 0.70f;

        canvas.drawCircle(cx, cy, radius * p, cir);
        p += (animation * 0.05);
        p -= 0.01f;

        float aM = Math.abs(animation-1f);
        for (int i = 0; i < list.size(); i++) {
            canvas.drawCircle(cx, cy, radius * p, list.get(i));
            if (i > 0)
                list.get(i).setAlpha((int) ((int) (255 / i)*aM));
            p += 0.05;
        }


    /*    float p = 1f / list.size();
        Log.i("TAG", "onDraw: " + getHeight());
        for (int i = 1; i < list.size(); i++) {
            float pp = p * i;
            canvas.drawCircle(cx, cy, radius * pp, list.get(i));
            Log.i("TAG", "onDraw: " + p + " * " + i + " = " + pp + " " + radius * pp);
        }*/

    }

    float cx;
    float cy;
    float radius;

    ValueAnimator valueAnimator = null;
    float animation = 0f;

    public void startAnimation() {

        animation = 0f;
        valueAnimator = ObjectAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(a -> {
            animation = (float) a.getAnimatedValue();
            invalidate();
        });
        valueAnimator.start();
    }
}
