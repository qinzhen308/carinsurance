package com.paulz.carinsurance.view;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import com.paulz.carinsurance.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by adelbert on 14-2-20.
 */
public class PageSlidingIndicator extends HorizontalScrollView {

    public TabItemOnClick mOnTabItemClick;

    public interface IconTabProvider {
        public int getPageIconResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[]{
            android.R.attr.textSize,
            android.R.attr.textColor
    };
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public ViewPager.OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int tabCount;

    private int lastPosition = -1;
    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    private boolean checkedTabWidths = false;

    private int indicatorColor = 0xFF666666;
    private int underlineColor = 0x1A000000;
    private int dividerColor = 0x1A000000;

    private boolean shouldExpand = false;
    private boolean textAllCaps = true;

    private int scrollOffset = 52;
    private int indicatorHeight = 8;
    private int underlineHeight = 2;
    private int dividerPadding = 12;
    private int tabPadding = 24;
    private int dividerWidth = 1;

    private int tabTextSize = 14;
    private int tabTextUnSelectedColor = 0xFF27272f;
    private int tabTextSelectedColor = 0xFFe30c26;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;

    private int lastScrollX = 0;

    private int tabBackgroundResId = R.drawable.background_tab;

    private Locale locale;
    private boolean textChangeSizeOnScroll = false;
    private int tabSelectedTextSize; // 选中时，textViewSize大小
    private int bottomLinePadding; // 选中时，横线的左右padding距离
    private OnScrollChangedListener onScrollChangedListener;

    public PageSlidingIndicator(Context context) {
        this(context, null);
    }

    public PageSlidingIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageSlidingIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);//定义滚动视图是否可以伸缩其内容以填充视口.
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
        tabTextUnSelectedColor = a.getColor(1, tabTextUnSelectedColor);
        tabSelectedTextSize = tabTextSize + 1; // 设置选中后的字体大小 + 4px

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.PageSlidingIndicator);

        tabTextSelectedColor = a.getColor(R.styleable.PageSlidingIndicator_tabTextSelectedColor, tabTextSelectedColor);
        indicatorColor = a.getColor(R.styleable.PageSlidingIndicator_indicatorColor, indicatorColor);
        underlineColor = a.getColor(R.styleable.PageSlidingIndicator_underlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.PageSlidingIndicator_dividerColor, dividerColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.PageSlidingIndicator_indicatorHeight, indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(R.styleable.PageSlidingIndicator_underlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.PageSlidingIndicator_dividerPadding, dividerPadding);
        tabPadding = a.getDimensionPixelSize(R.styleable.PageSlidingIndicator_tabPaddingLeftRight, tabPadding);
        tabBackgroundResId = a.getResourceId(R.styleable.PageSlidingIndicator_tabBackground, tabBackgroundResId);
        shouldExpand = a.getBoolean(R.styleable.PageSlidingIndicator_shouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.PageSlidingIndicator_scrollOffset, scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.PageSlidingIndicator_textAllCaps, textAllCaps);
        textChangeSizeOnScroll = a.getBoolean(R.styleable.PageSlidingIndicator_isChangeTextSizeBig, textChangeSizeOnScroll);
        bottomLinePadding = a.getDimensionPixelSize(R.styleable.PageSlidingIndicator_bottomLinePadding, 0);

        //v3.6.7手机周边需要选中tab字体变大，默认的2不够大，所以添加一个属性可以设置大的具体数值，单位是px
        tabSelectedTextSize = tabTextSize + a.getDimensionPixelSize(R.styleable.PageSlidingIndicator_biggerTextSize, 2); // 设置选中后的字体大小 + 4px

        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        //去掉滑动到终点时的效果
        try {
            Method method = getClass().getMethod("setOverScrollMode", int.class);
            Field field = getClass().getField("OVER_SCROLL_NEVER");
            if (method != null && field != null) {
                method.invoke(this, field.getInt(View.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(pageListener);
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {

            if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
            } else {
                addTextTab(i,
                        pager.getAdapter().
                                getPageTitle(i).
                                toString());
            }

        }

        updateTabStyles();
        setStatus();

        checkedTabWidths = false;

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });
    }

    private void setStatus() {
        setTextStatus(pager.getCurrentItem());
    }

    private void addTextTab(final int position, String title) {
        TextView tab;
        if (getContext()!=null){
            tab = new TextView(getContext());
            tab.setText(title);
            tab.setFocusable(true);
            tab.setGravity(Gravity.CENTER);
            tab.setSingleLine();

            tab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null == pager) return;
                    pager.setCurrentItem(position);
                    if (null != mOnTabItemClick) {
                        mOnTabItemClick.onTabClick(position);
                    }
                }
            });

            //just for 积分商城
            if (0 != indicatorWidth) {
                tab.setWidth(indicatorWidth / tabCount);
            }
            tabsContainer.addView(tab);
        }
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setFocusable(true);
        tab.setImageResource(resId);

        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnTabItemClick) {
                    mOnTabItemClick.onTabClick(position);
                }
                pager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pager.setCurrentItem(position);
                    }
                }, 100);


            }
        });
        tabsContainer.addView(tab);
    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            v.setLayoutParams(defaultTabLayoutParams);
            //v.setBackgroundResource(tabBackgroundResId);
            if (shouldExpand) {
                v.setPadding(0, 0, 0, 0);
            } else {
                v.setPadding(tabPadding, 0, tabPadding, 0);
            }

            if (v instanceof TextView) {

                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);
                tab.setTextColor(tabTextUnSelectedColor);

                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                /*if (textAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(locale));
                    }
                }*/
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!shouldExpand || MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            return;
        }

        int myWidth = getMeasuredWidth();
        int childWidth = 0;
        for (int i = 0; i < tabCount; i++) {
            childWidth += tabsContainer.getChildAt(i).getMeasuredWidth();
        }
        if (!checkedTabWidths && childWidth > 0 && myWidth > 0) {
            if (childWidth <= myWidth) {
                for (int i = 0; i < tabCount; i++) {
                    tabsContainer.getChildAt(i).setLayoutParams(expandedTabLayoutParams);
                }
            }
            checkedTabWidths = true;
        }
    }

    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw indicator line

        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        //积分商城
        if (indicatorWidth != 0) {
            int tvLength = getTextViewLength(((TextView) currentTab));
            bottomLinePadding = (indicatorWidth / tabCount - tvLength) / 2;
        }

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);

        }

        canvas.drawRect(lineLeft + bottomLinePadding, height - indicatorHeight, lineRight - bottomLinePadding, height, rectPaint);

        // draw underline

        /*rectPaint.setColor(underlineColor);
        canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);*/

        // draw divider

        /*dividerPaint.setColor(dividerColor);
        for (int i = 0; i < tabCount - 1; i++) {
            View tab = tabsContainer.getChildAt(i);
            canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
        }*/
    }

    public int getTextViewLength(TextView tv) {
        TextPaint paint = tv.getPaint();
        // 得到使用该paint写上text的时候,像素为多少
        int textLength = (int) paint.measureText(tv.getText().toString());
        return textLength;
    }

    public interface OnScrollChangedListener {
        public void OnScrollChangedListener(int l, int t, int oldl, int oldt);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        onScrollChangedListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangedListener != null) {
            onScrollChangedListener.OnScrollChangedListener(l, t, oldl, oldt);
        }
    }

    private class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            currentPosition = position;
            currentPositionOffset = positionOffset;
            scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));
            invalidate();
            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            setTextStatus(position);
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }
    }

    public void setTextStatus(int position) {
        if (position != lastPosition) {
            if (lastPosition != -1) {
                setTabTextColor(lastPosition, false);
            }

            setTabTextColor(position, true);
            lastPosition = position;
        }
    }

    public void setTabTextColor(int position, boolean isSelect) {
        TextView selectView = ((TextView) tabsContainer.getChildAt(position));
        if (selectView != null) {
            if (isSelect) {
                selectView.setTextColor(tabTextSelectedColor);
                if (textChangeSizeOnScroll) {
                    selectView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabSelectedTextSize);
                }
            } else {
                selectView.setTextColor(tabTextUnSelectedColor);
                if (textChangeSizeOnScroll) {
                    selectView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                }
            }
        }
    }

    public void setTextChangeSizeOnScroll(boolean choose){
        textChangeSizeOnScroll = choose;
    }

    public void setTabUnSelectedColor(String color) {
        tabTextUnSelectedColor = Color.parseColor("#" + color);
    }

    public void setTabSelectedColor(String color) {
        tabTextSelectedColor = Color.parseColor("#" + color);
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextUnSelectedColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextUnSelectedColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextUnSelectedColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    //just for 积分商城，很不爽，但是没找到 fragment 上面 onMeasure 中 setLayoutParams 不起作用的原因
    private int indicatorWidth = 0;

    public void setIndicatorWidth(int width) {
        indicatorWidth = width;
    }

    public interface TabItemOnClick {
        void onTabClick(int position);
    }

    public void setOnTabItemClick(TabItemOnClick mOnTabItemClick) {
        this.mOnTabItemClick = mOnTabItemClick;
    }
//    //品牌团
//    FaceCommCallBack callBack;
//    public void setCallBack(FaceCommCallBack callBack) {
//        this.callBack = callBack;
//    }
}
