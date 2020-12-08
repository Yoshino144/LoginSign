package com.cat.login.View;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * DrawHook
 * Created by Zane on 2015/3/4.
 */
public class TickView extends View {
    private int width;
    private int height;

    float factor;//进度因子:0-1
    float scaleAX = 0.3659f;
    float scaleAY = 0.4588f;
    float scaleBX = 0.5041f;
    float scaleBY = 0.6006f;
    float scaleCX = 0.7553f;
    float scaleCY = 0.3388f;
    private PathMeasure tickPathMeasure;
    private Path path;

    //绘制圆弧的进度值
    private int progress = 0;
    private AnimatorSet set;
    private ValueAnimator animation;
    private ValueAnimator animation1;
    private Paint paint;
    private Path pathTick;
    private Animator.AnimatorListener listener;

    public TickView(Context context) {
        this(context, null);
    }

    public TickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        paint = new Paint();
        //设置画笔颜色
        paint.setColor(Color.parseColor("#ffffff"));
        //设置圆弧的宽度
        paint.setStrokeWidth(5);
        //设置圆弧为空心
        paint.setStyle(Paint.Style.STROKE);
        //消除锯齿
        paint.setAntiAlias(true);

        path = new Path();
        pathTick = new Path();
        tickPathMeasure = new PathMeasure();

        animation = ValueAnimator.ofInt(0, 100);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (int) animation.getAnimatedValue();
                postInvalidate();//刷新
            }
        });
        animation1 = ValueAnimator.ofFloat(0f, 1f);
        animation1.setDuration(500);
        animation1.setInterpolator(new LinearInterpolator());
        animation1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                factor = (float) animation.getAnimatedValue();//更新进度因子
                postInvalidate();//刷新
            }
        });
        set = new AnimatorSet();
        set.play(animation)
                .after(animation1);
        set.setDuration(1000);
    }
    //绘制

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        pathTick.moveTo((width-50) * scaleAX, (height) * scaleAY);
        pathTick.lineTo((width-20) * scaleBX, (height+15) * scaleBY);
        pathTick.lineTo(width * scaleCX, height * scaleCY);
        tickPathMeasure.setPath(pathTick, false);
        /*
         * On KITKAT and earlier releases, the resulting path may not display on a hardware-accelerated Canvas.
         * A simple workaround is to add a single operation to this path, such as dst.rLineTo(0, 0).
         */
        tickPathMeasure.getSegment(0, factor * tickPathMeasure.getLength(), path, true);
        path.rLineTo(0, 0);
        canvas.drawPath(path, paint);
//        if(factor>=1){
//            /**
//             * 绘制圆弧
//             */
//            //获取圆心的x坐标
//            int center = getWidth() / 2;
//            //圆弧半径
//            //知道圆心坐标，圆上点坐标，求半径公式(x-a)^2+(y-b)^2=r^2,(a,b)圆心坐标
//            int radius = (int) Math.sqrt(Math.pow((width * scaleCX-getWidth() / 2),2)+Math.pow((height * scaleCY-getWidth() / 2),2));
//            //定义的圆弧的形状和大小的界限
//            RectF rectF = new RectF(center - radius - 1, center - radius - 1, center + radius + 1, center + radius + 1);
//
//            //两点坐标，求正弦，然后求角度
//            float degress = (float)Math.toDegrees(Math.asin((center-height * scaleCY)/radius))-1;
//            //根据进度画圆弧
//            canvas.drawArc(rectF, -degress, -340 * progress / 100, false, paint);
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public void setListener(Animator.AnimatorListener listener) {
        this.listener = listener;
    }

    /**
     * 开始打勾动画
     */
    public void start() {
        stop();
        path = new Path();
        //属性动画-插值器刷新
        if (set != null) {
            if (listener != null)
                set.addListener(listener);
            set.start();
        }
    }

    public void stop() {
        if (set != null) {
            set.end();
        }
    }
}