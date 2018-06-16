package com.joy.dl.daiandroidtools;

import android.app.Application;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Created by dl on 2018/06/16 0016.
 */

public class ZoomItemHorizontalScrollView extends HorizontalScrollView {

    private PageEvent pageEvent;

    public ZoomItemHorizontalScrollView(Context context) {
        this(context, null);
    }

    public ZoomItemHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomItemHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float  scale         = 0.9f;                                        //没有被滑倒的item的缩小比例
    private float  marginSpace   = UITools.dip2px(10);                          //每一项的间隔，实际呈现效果还大些，因为包含了缩小item所空出来的空间
    private float  showPartWidth = UITools.dip2px(20);                          //下一个item提前露出的宽度
    private double itemWidth     = (UITools.getScreenWidth() - 3 * marginSpace
            - 2 * showPartWidth)
            / (2 * (1 - scale) / 2 + 1);                 //item的计算在草稿纸上，这是解二元一次方程解出来的算式。两个方程分别是：2倍的留白+item宽度+2*showPartWidth=screemWidth；留白=marginSpace+（1-scale）/2*itemWidth
    private float  pageWidth     = (float) (itemWidth + marginSpace);           //中心到中心的距离再加上间隔,Ps:移动时，两个item，一个放大，一个缩小，实际间距是保持不变的

    /**
     * @param scale 缩小比例
     * @param marginSpace 空白间隔
     * @param showPartWidth
     */
    public void init(float scale, float marginSpace, float showPartWidth) {
        this.scale = scale;
        this.marginSpace = marginSpace;
        this.showPartWidth = showPartWidth;
        this.itemWidth = (UITools.getScreenWidth() - 3 * marginSpace - 2 * showPartWidth)
                / (2 * (1 - scale) / 2 + 1);
        this.pageWidth = (float) (itemWidth + marginSpace);

        LinearLayout container = (LinearLayout) ZoomItemHorizontalScrollView.this.getChildAt(0);
        for (int i = 0; i < container.getChildCount(); i++) {

            container.getChildAt(i).getLayoutParams().width = (int) itemWidth;
            if (i == 0) {
                ((LinearLayout.LayoutParams) container.getChildAt(i)
                        .getLayoutParams()).leftMargin = (int) (UITools.getScreenWidth() - itemWidth)
                        / 2;
            } else {
                if (i == container.getChildCount() - 1) {
                    ((LinearLayout.LayoutParams) container.getChildAt(i)
                            .getLayoutParams()).rightMargin = (int) (UITools.getScreenWidth()
                            - itemWidth)
                            / 2;
                }
                ((LinearLayout.LayoutParams) container.getChildAt(i)
                        .getLayoutParams()).leftMargin = (int) marginSpace;
                container.getChildAt(i).setScaleX(scale);
                container.getChildAt(i).setScaleY(scale);
            }
        }

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        final int toPageIndex = (int) Math.round(
                                ZoomItemHorizontalScrollView.this.getScrollX() * 1.0 / pageWidth);
                        ZoomItemHorizontalScrollView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                ZoomItemHorizontalScrollView.this
                                        .smoothScrollTo((int) pageWidth * toPageIndex, 0);
                                if (pageEvent != null) {
                                    pageEvent.onPageSelected(toPageIndex);
                                }
                            }
                        });
                        break;
                }

                return false;
            }
        });
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);

        final int toPageIndex = (int) Math
                .round(ZoomItemHorizontalScrollView.this.getScrollX() * 1.0 / pageWidth);
        if (toPageIndex > 0) {
            refreshScale(toPageIndex - 1, x);
        }
        refreshScale(toPageIndex, x);
        if (toPageIndex < ((LinearLayout) this.getChildAt(0)).getChildCount() - 1) {
            refreshScale(toPageIndex + 1, x);
        }
    }

    private void refreshScale(int index, int x) {
        int screenMiddleX = x + UITools.getScreenWidth() / 2;
        LinearLayout container = (LinearLayout) this.getChildAt(0);
        float itemMiddleX = (float) (showPartWidth + marginSpace + itemWidth / 2
                + index * pageWidth); //index * pageWidth + container.getChildAt(index).getMeasuredWidth() / 2;
        float distance = Math.abs(itemMiddleX - screenMiddleX);
        if (distance < pageWidth) {
            double scale1 = 1 - (distance) / pageWidth * Float.valueOf(1 - scale);
            container.getChildAt(index).setScaleX((float) scale1);
            container.getChildAt(index).setScaleY((float) scale1);
        } else {
            container.getChildAt(index).setScaleX(scale);
            container.getChildAt(index).setScaleY(scale);
        }
    }

    public void setCurrentPageIndex(final int index) {
        if (index >= ((LinearLayout) this.getChildAt(0)).getChildCount()) {
            return;
        }
        this.post(new Runnable() {
            @Override
            public void run() {
                ZoomItemHorizontalScrollView.this.smoothScrollTo((int) pageWidth * index, 0);
            }
        });
    }

    public void setPageEvent(PageEvent event) {
        this.pageEvent = event;
    }

    public static interface PageEvent {
        public void onPageSelected(int index);
    }
}
