package com.dilapp.radar.view.chart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.dilapp.radar.textbuilder.utils.JsonUtils;
import com.dilapp.radar.view.CustomHorizontalScrollView;
import com.dilapp.radar.view.CustomHorizontalScrollView.OnScrollChangedListener;
import com.dilapp.radar.view.chart.RadarChartAdapter.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by husj1 on 2015/10/8.
 * 图表运行可能会有一些卡顿，没办法
 */
public class RadarChartView extends LinearLayout implements OnScrollChangedListener {

    private static final int INIT_VIEW_COUNT = 4;
    private static final int INVALID_POSITION = -1;

    private static void d(String msg) {
        if (true) {
            com.dilapp.radar.textbuilder.utils.L.d("III_RadarChartView", msg);
        }
    }

    private RadarChartAdapter mAdapter;// 适配器
    RadarChartStyle mStyle;// 样式
    private RuleView mRuleView;// 尺子
    private CustomHorizontalScrollView mScrollView;// 水平滚动控件
    private LinearLayout mContainer;
    // <<<<<
    private int mFirstPosition = INVALID_POSITION, mOldFirstPosition = INVALID_POSITION;
    private int mLastPosition = INVALID_POSITION, mOldLastPosition = INVALID_POSITION;
    private Points mEmpty = new Points();
    private LinkedList<Points> mPointses = new LinkedList<Points>();
    private boolean mArrangementing;// 担心数据整理和滚动一起执行
    private OnItemScrollListener mScrollListener;
    private LinkedList<Object> mList = new LinkedList<Object>();

    public RadarChartView(Context context) {
        this(context, null);
    }

    public RadarChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScrollView = new CustomHorizontalScrollView(context);
        mScrollView.setOnScrollChangedListener(this);
        /*mPrevDraw = new GraphicalView(context, attrs, defStyleAttr);
        mCurrDraw = new GraphicalView(context, attrs, defStyleAttr);
        mNextDraw = new GraphicalView(context, attrs, defStyleAttr);
        mDraws = new GraphicalView[] { mNextDraw, mCurrDraw, mPrevDraw };*/
        mRuleView = new RuleView(context);
        mContainer = new LinearLayout(context);

        RadarChartStyle style = new RadarChartStyle(context).build();
        final int len = INIT_VIEW_COUNT;
        for (int i = 0; i < len; i++) {
            GraphicalView gv = new GraphicalView(context/*, attrs, defStyleAttr*/);
            gv.mStart = style.graphicalSplitCount * (len - 1 - i);
            mContainer.addView(gv);
        }
        setStyle(style);

        mScrollView.addView(mContainer);

        addView(mScrollView);
        addView(mRuleView);

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mScrollView.scrollTo(mContainer.getMeasuredWidth(), 0);
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public RadarChartAdapter getAdapter() {
        return mAdapter;
    }

    public void addPanels(int count) {
        if (count <= 0) return;
        int childCount = mContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            GraphicalView gv = new GraphicalView(getContext());
            gv.mAdapter = mAdapter;
            gv.mStyle = mStyle;
            gv.mStart = childCount * mStyle.graphicalSplitCount;
            mContainer.addView(gv, 0);
            childCount++;
        }
        mScrollView.smoothScrollBy(mStyle.graphicalWidth * count, 0);
    }

    public void setAdapter(RadarChartAdapter adapter) {
        if (adapter != null) {
            adapter.mChart = this;
        }
        RadarChartAdapter old = this.mAdapter;
        if (this.mAdapter != adapter) {
            if (old != null) {
                old.mChart = null;
            }
            this.mAdapter = adapter;

            notifyAdapterChanged();
        }
    }

    public void setScrollListener(OnItemScrollListener scrollListener) {
        this.mScrollListener = scrollListener;
    }

    private void notifyAdapterChanged() {
        setChildrenAdapter(mAdapter);

        if (mAdapter != null) {
            mRuleView.rulers = mAdapter.getRulers();
            mAdapter.notifyDataSetChanged();// 由Adapter来调用吧
//            setChildrenDatas();
//            notifyChildrenRedraw();
        }
    }

    private void setChildrenAdapter(RadarChartAdapter adapter) {
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            ((GraphicalView) mContainer.getChildAt(i)).mAdapter = adapter;
        }
    }

    void setChildrenDatas() {
        mArrangementing = true;
        List<Object> datas = mList;
        datas.clear();
        for (int i = 0; i < mAdapter.size(); i++) {
            Object o = mAdapter.get(i);
            if (o != null) {
                datas.add(o);
            }
        }

        int lIndex = 0;
        int lCount = 1;

        int frontSpace = 0;
        Object front = null;// 为了后面的找到前面的，把对象记录下来

        int behindSapce = -1;

        int startIndex = -1;// 这回事记住需要找到后面的数组
        StringBuilder sb = new StringBuilder();
        int count = mStyle.graphicalSplitCount;
        int pageCount = mContainer.getChildCount();

        List<Points> pointses = mPointses;
        for (int i = 0; i < pointses.size(); i++) {
            Points p = pointses.get(i);
            if (p != null) {
                p.clear();
            }
        }
        pointses.clear();
        if (mFirstPosition != INVALID_POSITION && mLastPosition != INVALID_POSITION) {
            for (int i = mFirstPosition; i <= mLastPosition; i++) {
                GraphicalView gv = (GraphicalView) mContainer.getChildAt(i);
                gv.recycle();
            }
        }

        final int len = count * pageCount;

        // 这种无脑循环找数据的效率很低很低
        for (int i = 0; i < len; i++) {
            if (datas.size() == 0) {
                d("find done!");
                break;
            }
            sb.append("curr location " + i);
            boolean find = false;// 只是为了输出日志
            int childIndex = i % count;
            int pageIndex = i / count;
            Points p;
            if (pageIndex < pointses.size()) {
                // 已经赋过值了。。
                if ((p = pointses.get(pageIndex)) != null) {

                } else {
                    pointses.set(pageIndex, p = new Points());
                }
            } else {
                pointses.add(p = new Points());
            }
            // p.clear();
            //GraphicalView gv = mDraws[drawIndex];
            //Points p = new Points();
            //gv.mPoints = p;
            if (p.mDatas == null) p.mDatas = new Object[lCount][];

            if (childIndex == 0 && pageIndex != 0) {
                // 需要把数据链起来，链接前面的数据
                if (p.mDataFronts == null) {
                    p.mDataFronts = new Object[lCount];
                    p.mFrontSub = new int[lCount];
                }
                p.mDataFronts[lIndex] = front;
                p.mFrontSub[lIndex] = frontSpace;
                d(" front drawIndex " + pageIndex + ", space " + p.mFrontSub[lIndex]);
            }
            if (p.mDatas[lIndex] == null) p.mDatas[lIndex] = new Object[count];
            for (int j = 0; j < datas.size(); j++) {
                behindSapce++;
                if (datas.get(j) == null) {
                    datas.remove(j);
                    j--;
                    continue;
                }
                if (mAdapter.isPointX(datas.get(j), i)) {
                    find = true;
                    Object o = datas.remove(j);
                    p.mDatas[lIndex][childIndex] = o;

                    front = o;
                    frontSpace = 0;
                    if (startIndex != -1) {
                        for (int k = startIndex; k < pageIndex; k++) {
                            // d("behind k " + k + ", pi " + pageIndex + ", ci " + childIndex);
                            behindSapce = (pageIndex - 1 - k) * count + childIndex + 1;
                            Points ps = pointses.get(k);
                            if (ps.mDataBehinds == null) {
                                ps.mDataBehinds = new Object[lCount];
                                ps.mBehindSub = new int[lCount];
                            }
                            ps.mBehindSub[lIndex] = behindSapce;
                            ps.mDataBehinds[lIndex] = o;
                            d(" behind k " + k + ", space " + ps.mBehindSub[lIndex]);
                        }
                        startIndex = -1;
                    }
                    sb.append(" finded " + (j));
                    j--;
                    break;
                }
            }
            if (childIndex == count - 1 && pageIndex != pageCount - 1) {
                // 需要把数据链起来，链接后面的数据
                // lastIndexs.add(drawIndex);
                if (startIndex == -1) {
                    startIndex = pageIndex;
                }
            }
            frontSpace++;
            if (!find) {
                // gv.mDatas[lIndex][childIndex] = null;
                sb.append(" not find.");
            }
            d(sb.toString());
            sb.delete(0, sb.length());
        }
        d("Finded Size-->" + pointses.size() + ", unfind " + JsonUtils.toJson(datas));
        mArrangementing = false;
        /*int l = 0;
        for (int i = drawCount - 1; i >= 0; i--) {
            GraphicalView gv = (GraphicalView) mContainer.getChildAt(i);
            // mDraws[i].mStart = count * i;
            if (l < pointses.size()) {
                Points p = pointses.get(l);
                p.mStart = count * l;
                gv.setPoints(p);
            }
            l++;
        }*/
    }

    private Object findDataByPositon(int position, List<Object> list) {

        return null;
    }

    private int binary(List<Object> list, int index) {
        if (list == null) return -1;
        int low = 0;
        int high = list.size() - 1;
        while (low <= high) {
            int middle = (low + high) / 2;
            Object o = list.get(middle);

            int compare = mAdapter.compare(o, index);
            if (compare == RadarChartAdapter.COMPARE_EQ) {
                return middle;
            }
            if (compare > RadarChartAdapter.COMPARE_EQ) {
                low = middle + 1;
            }
            if (compare < RadarChartAdapter.COMPARE_EQ) {
                high = middle - 1;
            }
        }
        return -1;
    }

    public RadarChartStyle getStyle() {
        return mStyle;
    }

    public void setStyle(RadarChartStyle style) {
        if (style == null) {
            style = new RadarChartStyle(getContext()).build();
        }
        this.mStyle = style;
        notifyStyleChanged();
    }

    private void clearGraphical(GraphicalView gv) {
        if (gv != null && gv.getPoints() != null) {
            gv.getPoints().clear();
        }
    }

    private void notifyStyleChanged() {
        d("notifyStyleChanged");
        setChildrenStyle(mStyle);
        notifyChildrenRedraw();
    }

    private void setChildrenStyle(RadarChartStyle style) {
        d("setChildrenStyle");
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            ((GraphicalView) mContainer.getChildAt(i)).mStyle = style;
        }
    }

    void notifyChildrenRedraw() {
        if (mAdapter == null) return;
        if (mStyle == null) return;

        List<Points> pointses = mPointses;
        final int count = mContainer.getChildCount();
        if (mFirstPosition != INVALID_POSITION && mLastPosition != INVALID_POSITION) {
            StringBuilder logs = new StringBuilder("notifyChildrenRedraw ")
                    .append(mFirstPosition).append("-").append(mLastPosition)
                    .append("->");
            for (int i = mFirstPosition; i <= mLastPosition; i++) {
                GraphicalView gv = (GraphicalView) mContainer.getChildAt(i);
                int pIndex = count - 1 - i;
                // 感觉到处矫正这个Start总是没有错的
                logs.append("{v").append(i).append("|p").append(pIndex).append(":");
                gv.mStart = mStyle.graphicalSplitCount * pIndex;
                Points points;
                if (gv.getPoints() == null && pIndex < pointses.size()) {
                    points = (pointses.get(pIndex));
                    logs.append("seted");
                } else {
                    points = mEmpty;
                    logs.append("empty");
                }
                gv.setPoints(points);
                logs.append("}, ");
            }
            d(logs.toString());
        }
        mRuleView.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int defWidth = mStyle.mRound.width() + getPaddingLeft() + getPaddingRight();
        int defHeight = mStyle.mRound.height() + getPaddingTop() + getPaddingBottom();
        mStyle.mRound.set(0, 0, mStyle.measure(widthMeasureSpec, defWidth), mStyle.measure(heightMeasureSpec, defHeight));
        setMeasuredDimension(mStyle.mRound.width(), mStyle.mRound.height());
        mStyle.mRound.left += getPaddingLeft();
        mStyle.mRound.top += getPaddingTop();
        mStyle.mRound.right -= getPaddingRight();
        mStyle.mRound.bottom -= getPaddingBottom();
    }

    public void onScrollChanged(int x, int y, int oldx, int oldy) {
        // 这个是拿View，所以是按照View的顺序算出index的。
        // 就是>>>>
        final int count = mContainer.getChildCount();
        int edge = (mStyle.graphicalMaxOnScreen - 1) * mStyle.graphicalWidth - mStyle.screenWidth;
        mFirstPosition = x / mStyle.graphicalWidth;
        mLastPosition = mFirstPosition + mStyle.screenWidth / mStyle.graphicalWidth + (x % mStyle.graphicalWidth > edge ? 1 : 0);
        mLastPosition = mLastPosition >= count ? count - 1 : mLastPosition;

        /*com.dilapp.radar.textbuilder.utils.L.d("III_scroll",
                "x " + x + ", y " + y + ", ox " + oldx + ", oy " + oldy +
                        ", t " + (x % mStyle.graphicalWidth) +
                        ", f " + mFirstPosition + ", l " + mLastPosition);*/
        boolean isChange = false;
        if (!mArrangementing) {
            boolean printLog = false;
            // 所以，这里就要对Pointses的下标进行转换
            List<Points> pointses = mPointses;
            List<Object> list = mList;
            // 判断位置，前方的位置。
            StringBuilder sb = new StringBuilder("First newIndex " + mFirstPosition + ", oldIndex " + mOldFirstPosition);
            if (mOldFirstPosition > mFirstPosition && mOldFirstPosition != INVALID_POSITION) {
                printLog = true;
                isChange = true;
                // 绘图，画新的
                for (int i = mOldFirstPosition - 1; i >= mFirstPosition; i--) {
                    GraphicalView gv = (GraphicalView) mContainer.getChildAt(i);
                    // 转换下标完成
                    int pIndex = count - 1 - i;
                    gv.mStart = mStyle.graphicalSplitCount * pIndex;
                    sb.append(", i " + i + ": count " + count + ", pIndex " + pIndex);
                    Points p;
                    if (pIndex < pointses.size()) {
                        p = pointses.get(pIndex);
                    } else {
                        p = mEmpty;
                    }
                    // <---------------------------------------------> TODO
                    // wrappePoints(p, list, gv);
                    // >---------------------------------------------<
                    gv.setPoints(p);
                    // 但是这里需要转换一下下标...
                }
            } else if (mOldFirstPosition < mFirstPosition && mOldFirstPosition != INVALID_POSITION) {
                printLog = true;
                isChange = true;
                // 这里是回收
                for (int i = mOldFirstPosition; i < mFirstPosition; i++) {
                    GraphicalView gv = (GraphicalView) mContainer.getChildAt(i);
                    sb.append(", i " + i + ": recycle pIndex " + (mContainer.getChildCount() - 1 - i));
                    gv.recycle();
                }
            }
            if (printLog) d(sb.toString());

            printLog = false;
            // 判断位置，后方的位置。
            sb.setLength(0);
            sb = new StringBuilder("Last newIndex " + mLastPosition + ", oldIndex " + mOldLastPosition);
            if (mOldLastPosition > mLastPosition && mOldLastPosition != INVALID_POSITION) {
                printLog = true;
                isChange = true;
                // 这里是回收
                for (int i = mLastPosition + 1; i <= mOldLastPosition; i++) {
                    GraphicalView gv = (GraphicalView) mContainer.getChildAt(i);
                    sb.append(", i " + i + ": recycle pIndex " + (mContainer.getChildCount() - 1 - i));
                    gv.recycle();
                }
            } else if (mOldLastPosition < mLastPosition && mOldLastPosition != INVALID_POSITION) {
                printLog = true;
                isChange = true;
                // 绘图，画新的
                for (int i = mOldLastPosition; i <= mLastPosition; i++) {
                    GraphicalView gv = (GraphicalView) mContainer.getChildAt(i);
                    // 转换下标完成
                    int pIndex = count - 1 - i;
                    gv.mStart = mStyle.graphicalSplitCount * pIndex;
                    sb.append(", i " + i + ": count " + count + ", pIndex " + pIndex);
//                sb.append(", count " + count + ", pIndex " + pIndex);
                    Points p;
                    if (pIndex < pointses.size()) {
                        p = pointses.get(pIndex);
                    } else {
                        p = mEmpty;
                    }
                    // <---------------------------------------------> TODO
                    // wrappePoints(p, list, gv);
                    // >---------------------------------------------<
                    gv.setPoints(p);
                    // 但是这里需要转换一下下标...
                }
            }
            if (printLog) d(sb.toString());
        } else {
            d("First " + mFirstPosition + ", OldFirst " + mOldFirstPosition +
                    ", Last " + mLastPosition + ", OldLast " + mOldLastPosition);
        }

        if (mScrollListener != null && isChange) {
            mScrollListener.onItemScrolling(this, mFirstPosition, mLastPosition,
                    mOldFirstPosition, mOldLastPosition, count);
        }
        mOldFirstPosition = mFirstPosition;
        mOldLastPosition = mLastPosition;
    }

    @Override
    public void scrollBottom() {

    }

    private void wrappePoints(Points p, List<Object> list, GraphicalView gv) {
        int lIndex = 0;
        p.ensure(1, mStyle.graphicalSplitCount);
        p.clear();
        int[] temp = new int[mStyle.graphicalSplitCount];
        for (int j = gv.mStart, b = 0; j < gv.mStart + mStyle.graphicalSplitCount; j++, b++) {
            int index = binary(list, j);
            temp[b] = index;
            if (index != -1) {
                p.mDatas[lIndex][b] = list.get(index);
            }

            if (p.mDataFronts[lIndex] == null && index != -1 && index != 0) {
                p.mDataFronts[lIndex] = list.get(index - 1);
                p.mFrontSub[lIndex] = mAdapter.measure(list.get(index - 1), p.mDatas[lIndex][b]);
            }
            if (p.mDataBehinds[lIndex] == null && index != -1 && index != list.size() - 1) {
                p.mDataBehinds[lIndex] = list.get(index + 1);
                p.mBehindSub[lIndex] = mAdapter.measure(list.get(index + 1), p.mDatas[lIndex][b]);
            }
        }
    }

    class RuleView extends View {

        private RadarChartRuler[] rulers;

        public RuleView(Context context) {
            super(context);
        }

        public RuleView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public RuleView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int defWidth = mStyle.mRuleRound.width() + getPaddingLeft() + getPaddingRight();
            int defHeight = mStyle.mRuleRound.height() + getPaddingTop() + getPaddingBottom();
            mStyle.mRuleRound.set(0, 0,
                    RadarChartStyle.measure(widthMeasureSpec, defWidth),
                    RadarChartStyle.measure(heightMeasureSpec, defHeight));
            setMeasuredDimension(mStyle.mRuleRound.width(), mStyle.mRuleRound.height());
            mStyle.mRuleRound.left += getPaddingLeft();
            mStyle.mRuleRound.top += getPaddingTop();
            mStyle.mRuleRound.right -= getPaddingRight();
            mStyle.mRuleRound.bottom -= getPaddingBottom();
        }
    }

    public class Points {
        int[] mFrontSub;
        Object[] mDataFronts;
        Object[][] mDatas;
        Object[] mDataBehinds;
        int[] mBehindSub;

        public void ensure(int count, int len) {
            if (mFrontSub == null) mFrontSub = new int[len];
            if (mDataFronts == null) mDataFronts = new Object[len];

            if (mDatas == null) mDatas = new Object[count][];
            for (int i = 0; i < count; i++) {
                if (mDatas[i] == null) mDatas[i] = new Object[len];
            }

            if (mDataBehinds == null) mDataBehinds = new Object[len];
            if (mBehindSub == null) mBehindSub = new int[len];
        }

        public void clear() {
            if (mDatas != null)
                for (int i = 0; i < mDatas.length; i++) {
                    if (mDataFronts != null) mDataFronts[i] = null;
                    if (mFrontSub != null) mFrontSub[i] = 0;
                    if (mDataBehinds != null) mDataBehinds[i] = null;
                    if (mBehindSub != null) mBehindSub[i] = 0;
                    if (mDatas[i] != null)
                        for (int j = 0; j < mDatas[i].length; j++) {
                            mDatas[i][j] = null;
                        }
                }
        }
    }

    public interface OnItemScrollListener {
        void onItemScrolling(RadarChartView view, int first, int last, int oldFirst, int oldLast, int count);
    }
}
