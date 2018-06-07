package com.example.administrator.myapplication.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.administrator.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * barChart
 *
 * @author wys
 */
public class BarChart extends LBaseView {
    private GestureDetector detector;
    protected List<Double> mDatas;
//    protected List<String> mDescription;
    protected Paint mDataLinePaint;
    protected int defaultLineColor = Color.argb(255, 74, 134, 232);
    protected int descriptionColor;
    protected int dataColor;
    private int mWidth;
    private int mHeight;
    private int mShowNumber;

    private float perBarW;
    private Double maxData;

    private int mMaxScrollx;
    protected int defaultBorderColor = Color.argb(255, 217, 217, 217);
    protected Paint mBorderLinePaint;
    protected Paint mTextPaint;
    protected Paint mPointPaint;
    protected int descriptionTextSize;
    protected int dataTextSize;

    private int mBottomPadding;
    private int mLeftPadding;
    private int mTopPadding;


    public BarChart(Context context) {
        super(context);
        init(context, null);
    }


    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.barCharts);
        defaultBorderColor = t.getColor(R.styleable.barCharts_borderColor, defaultBorderColor);
        descriptionTextSize = (int) t.getDimension(R.styleable.barCharts_labelTextSize, 20);
        dataTextSize = (int) t.getDimension(R.styleable.barCharts_dataTextSize, 20);
        descriptionColor = t.getColor(R.styleable.barCharts_descriptionTextColor, Color.GRAY);
        dataColor = t.getColor(R.styleable.barCharts_dataTextColor, Color.GRAY);
        mShowNumber = t.getInteger(R.styleable.barCharts_barShowNumber, 6);
        canClickAnimation = t.getBoolean(R.styleable.barCharts_isClickAnimation, false);
        t.recycle();

        detector = new GestureDetector(context, new BarGesture());
        mDatas = new ArrayList<>();
//        mDescription = new ArrayList<>();

        mDataLinePaint = new Paint();
        mDataLinePaint.setAntiAlias(true);
        mDataLinePaint.setColor(defaultLineColor);
        mDataLinePaint.setStyle(Paint.Style.STROKE);

        mBorderLinePaint = new Paint();
        mBorderLinePaint.setColor(defaultBorderColor);
        mBorderLinePaint.setStyle(Paint.Style.STROKE);
        mBorderLinePaint.setStrokeWidth(dp2px(1));
        mBorderLinePaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(descriptionTextSize);

        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStrokeWidth(dp2px(2));
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(Color.RED);
    }

    public BarChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        setDataLineWidth();
    }

    private void setDataLineWidth() {
        if (mDatas.size() > 0) {
            mDataLinePaint.setStrokeWidth(mWidth / (mShowNumber * 2));
            mMaxScrollx = (mWidth / mShowNumber) * mDatas.size() - mWidth;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        perBarW = mWidth / mShowNumber;
        canvas.translate(0, mHeight - mBottomPadding);
        setMaxData();
        canvas.drawLine(0, 0, mMaxScrollx + mWidth, 0, mBorderLinePaint);
        drawCharts(canvas);
    }

    //绘制连接线
    private void drawCharts(Canvas canvas) {
        float lastX = 0f, lastY = 0f;
        for (int i = 0; i < mDatas.size(); i++) {
            String perData = String.valueOf(scale < 1 ? Math.round(mDatas.get(i) * scale) : mDatas.get(i));
            float x = (i + 0.5f) * perBarW;
            float y = (float) ((mHeight - mTopPadding - mBottomPadding) / maxData * mDatas.get(i));
            //绘制数据点
            canvas.drawCircle(
                    x ,
                    -y * scale,
                    dp2px(4),
                    mPointPaint);
            //绘制连接线
            if (i > 0) {
                canvas.drawLine(lastX, lastY, x ,
                        -y * scale, mPointPaint);
            }
            //判断文字绘制在上方还是下方（根据趋势）绘制数据文本
            mTextPaint.setTextSize(dataTextSize);
            mTextPaint.setColor(dataColor);
            if (lastY >= -y * scale){
                canvas.drawText(perData,
                        x - mTextPaint.measureText(perData) / 2,
                        -y * scale - dataTextSize/2,
                        mTextPaint);
            } else {
                canvas.drawText(perData,
                        x - mTextPaint.measureText(perData) / 2,
                        -y * scale + dataTextSize*3/2,
                        mTextPaint);
            }
            //绘制X轴描述文本
            mTextPaint.setTextSize(descriptionTextSize);
            mTextPaint.setColor(descriptionColor);
            canvas.drawText(""+i,
                    x - mTextPaint.measureText(""+i) / 2,
                    descriptionTextSize,
                    mTextPaint);
            lastX = x ;
            lastY = -y * scale;

        }

    }


    public void startCliclkAnimation() {
        if (canClickAnimation) {
            animator.start();
        }
    }


    public void setBootomDrawPadding(int bottomy) {
        mBottomPadding = bottomy;
    }

    public void setLeftDrawPadding(int left) {
        mLeftPadding = left;
    }

    public void setTopDrawPadding(int left) {
        mTopPadding = left;
    }


    private void setMaxData() {
        this.maxData = 12d;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (detector.onTouchEvent(motionEvent)) {
            return detector.onTouchEvent(motionEvent);
        }
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture(motionEvent);
                break;
            default:
                break;
        }

        return false;
    }

    private void endGesture(MotionEvent motionEvent) {

    }

    private class BarGesture extends GestureDetector.SimpleOnGestureListener {
        private int preScrollX = 0;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int scrollX = getScrollX();
            int minScrollX = -scrollX;
            if (scrollX > mMaxScrollx && distanceX > 0) {
                distanceX = 0;
                scrollBy((int) distanceX, 0);
            } else {
                if (distanceX < minScrollX) {
                    distanceX = minScrollX;
                }
                scrollBy((int) distanceX, 0);
            }
            preScrollX = scrollX;
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            startCliclkAnimation();
            return super.onSingleTapUp(e);
        }
    }

    public void setDatas(List<Double> mDatas, boolean isAnimation) {
        this.mDatas.clear();
        this.mDatas.addAll(mDatas);
//        this.mDescription = mDesciption;
        setDataLineWidth();
        if (isAnimation) {
            animator.start();
        } else {
            scale = 1;
            postInvalidate();
        }
    }

    public void addEndMoreData(List<Double> mDatas) {
        this.mDatas.addAll(mDatas);
        setDataLineWidth();

        scale = 1;
        postInvalidate();
    }
    public void addAData(Double aData) {
        this.mDatas.add(aData);
        setDataLineWidth();
        scale = 1;
        postInvalidate();
    }

    private int startX = 0;

    public void addStartMoreData(List<Double> mDatas) {
        mDatas.addAll(this.mDatas);
        this.mDatas.clear();
        this.mDatas.addAll(mDatas);
//        (mWidth / mShowNumber) *
        startX = (mWidth / mShowNumber) * mDatas.size();
        setDataLineWidth();
        postInvalidate();
    }
}