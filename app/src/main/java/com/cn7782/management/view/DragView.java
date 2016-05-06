package com.cn7782.management.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by tangweny on 2015/12/30.
 */
public class DragView extends LinearLayout {

    private ViewDragHelper viewDrag;
    private View sosView;
    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        viewDrag = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        sosView = getChildAt(0);
//        sosView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent sosIntent = new Intent(getContext(),FallDownWarnActivity.class);
//                getContext().startActivity(sosIntent);
//            }
//        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    //将事件传递给ViewDragHelper处理
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDrag.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDrag.processTouchEvent(event);
        return true;
    }

    //滑动实现还是依靠Scroller
    @Override
    public void computeScroll() {
        if(viewDrag.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }


    //回调函数
    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {

            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int leftBound = getPaddingLeft();
            int rightBound = getWidth() - child.getWidth() - leftBound;
            int newLeft = Math.min(Math.max(left, leftBound), rightBound);
            return newLeft;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int topBound = getPaddingTop();
            int bottomBound = getHeight() - child.getHeight() - topBound;
            int newLeft = Math.min(Math.max(top, topBound), bottomBound);
            return newLeft;
        }
        @Override
        public int getViewHorizontalDragRange(View child) {
//            return sosView == child ? child.getWidth() : 0;

            return getMeasuredWidth()-child.getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
//            return sosView == child ? child.getHeight() : 0;

            return getMeasuredHeight()-child.getMeasuredHeight();
        }
    };
}
