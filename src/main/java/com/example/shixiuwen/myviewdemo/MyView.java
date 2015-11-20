package com.example.shixiuwen.myviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by Administrator on 2015/11/19.
 */
public class MyView extends View {

    Paint paint;
    Bitmap speBitmap;
    Bitmap backBitmap;

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        //得到背景转盘图
        backBitmap = getRecBitmap(getContext(), R.drawable.back02);

        //将背景转盘图片设置为自己想要的大小（已实现，能够正常缩放和设置想要指定的值）
        backBitmap = getBitmapByPix(backBitmap, getContext(), 150);

        //得到正方形专辑图片
        speBitmap = getRecBitmap(getContext(), R.drawable.music);
        //切割得到圆形专辑图片
        speBitmap = getRoundedCornerBitmap(speBitmap, speBitmap.getHeight() / 2);
        //将专辑信息图片设置指定宽度，参考比例(内圈占据外圈半径的几分之几)
        speBitmap = getBitmapByPix(speBitmap, getContext(), 100);
        //专辑图片叠加到转盘上
        backBitmap = toConformBitmap(backBitmap, speBitmap);

        paint = new Paint();
        //添加光盘反光渲染效果
        paint.setShader(new BitmapShader(backBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        paint.setShader(new SweepGradient(backBitmap.getWidth() / 2,
                backBitmap.getWidth() / 2, new int[]{0x46FFFFFF,
                0x00000000, 0x46FFFFFF, 0x00000000, 0x46FFFFFF}, new float[]{0.1F, 0.2F, 0.6F, 0.7F, 1.0F}));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawBitmap(backBitmap, 0, 0, null);
        canvas.drawCircle(backBitmap.getHeight() / 2, backBitmap.getHeight() / 2, backBitmap.getHeight() / 2, paint);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    /**
     * 一个得到正方形图片的方法
     *
     * @param context
     * @param id
     * @return
     */
    public Bitmap getRecBitmap(Context context, int id) {
        Bitmap newBitmap = BitmapFactory.decodeResource(getResources(), id);
        //获得原图的宽高
        int mWidth = newBitmap.getWidth();
        int mHeight = newBitmap.getHeight();
        //设置挖取得区域
        Bitmap outputBitmap = null;
        if (mWidth >= mHeight) {
            outputBitmap = Bitmap.createBitmap(newBitmap, (mWidth - mHeight) / 2, 0, mHeight, mHeight);
        } else {
            outputBitmap = Bitmap.createBitmap(newBitmap, 0, (mHeight - mWidth) / 2, mWidth, mWidth);
        }

        return outputBitmap;
    }

    /**
     * 由正方形图片切得圆形图片
     *
     * @param bitmap
     * @param pixels
     * @return
     */

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 绘制组合的Bitmap
     *
     * @param background 背景图
     * @param foreground 前置歌曲图片光盘
     * @return
     */
    private Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }

        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        //create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        cv.drawBitmap(background, 0, 0, null);//在 0，0坐标开始画入bg
        cv.drawBitmap(foreground, (bgWidth - fgWidth) / 2, (bgHeight - fgHeight) / 2, null);//在 0，0坐标开始画入fg ，可以从任意位置画入

        cv.save(Canvas.ALL_SAVE_FLAG);//保存
        cv.restore();//存储
        return newbmp;
    }

    /**
     * 图片大小缩放
     *
     * @param bitmap
     * @param context
     * @return(int)(width * scaleWidth)
     */
    public Bitmap getBitmapByPix(Bitmap bitmap, Context context, float scale) {
        // 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 设置想要的大小
        int newWidth = dip2px(context, scale);
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        // 得到新的图片
        return Bitmap.createScaledBitmap(bitmap, (int) (width * scaleWidth), (int) (height * scaleWidth), true);
    }

    /**
     * 转动动画
     *
     * @param isPlay
     * @param view
     * @param during
     */
    public void rotate(boolean isPlay, View view, int during) {
        if (isPlay) {
            RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(during);
            ra.setRepeatCount(-1);
            //设置不停旋转的样式（匀速），默认为开始和结束的时候减速
            ra.setInterpolator(new LinearInterpolator());
            view.startAnimation(ra);
        }

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public Drawable bitToDraw(Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
        return bd;
    }

}
