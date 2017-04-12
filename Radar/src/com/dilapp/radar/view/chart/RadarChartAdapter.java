package com.dilapp.radar.view.chart;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * Created by husj1 on 2015/10/8.
 */
public abstract class RadarChartAdapter {

    /**
     * 大于
     */
    public final static int COMPARE_GT = 1;
    /**
     * 小于
     */
    public final static int COMPARE_LT = -1;
    /**
     * 等于
     */
    public final static int COMPARE_EQ = 0;

    final static int WHAT_DATASETCHANGED = 10;
    final static int WHAT_CHILDREN_REDRAW = 20;

    RadarChartView mChart;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_DATASETCHANGED:
                    setChildrenDatas();
                    mHandler.sendEmptyMessage(WHAT_CHILDREN_REDRAW);
                    break;
                case WHAT_CHILDREN_REDRAW:
                    mChart.notifyChildrenRedraw();
                    break;
            }
        }
    };
    private AsyncTask mTask;

    private Runnable mRunning = new Runnable() {
        @Override
        public void run() {
            setChildrenDatas();
            mHandler.sendEmptyMessage(WHAT_CHILDREN_REDRAW);
        }
    };
    private Thread mThread;


    public abstract String getTitle(int location);

    public String getSecondTitle(int location) {
        return "";
    }

    ;

    public abstract boolean isPointX(Object o, int location);

    /**
     * 返回一个0-100的整数
     *
     * @param o
     * @return
     */
    public abstract int getPointY(Object o);//

    /**
     * @param o
     * @return
     */
    public abstract String getPointText(Object o);

    public abstract RadarChartRuler[] getRulers();// 获得刻度

    private void setChildrenDatas() {

        if (mChart != null) {
            mChart.setChildrenDatas();
        }
    }

    public void notifyDataSetChanged() {
        // 第1次：不用Handler的话，用户作死的滑那个滚轮，很容易崩溃。
        // 作死的滑：3个一起滑，一只手滑一个轮子，而且还是手指快速(Zi Xin)一扫的那种。
        // 不要问我第3只手的问题！
        // 就和某些人敲回车键一样的感觉

        // 第2次：不得不说，这样使用又解决了另外一个导致崩溃的问题。
        // 图表，和轮子一起滚，瞬间崩溃，现在好了，so easy
        // 担心还会出现其他问题，暂时没有发现，就这样了 2015 11.10 18:55 上面是同一天的
        mHandler.removeMessages(WHAT_DATASETCHANGED);
        mHandler.removeMessages(WHAT_CHILDREN_REDRAW);
        mHandler.sendEmptyMessage(WHAT_DATASETCHANGED);

        /*if (mTask != null) {
            if (!mTask.isCancelled() || mTask.getStatus() != AsyncTask.Status.FINISHED)
                mTask.cancel(true);
        }
        mTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                setChildrenDatas();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                mChart.notifyChildrenRedraw();
            }
        };
        mTask.execute();*/

        /*
       if(mThread != null && mThread.isAlive()) {
            mThread.interrupt();
        }
        if (mThread == null || mThread.isInterrupted()) {
            mThread = new Thread(mRunning);
            mThread.start();
        }*/
    }

    /**
     * 需要排好序的数据，否则影响效率
     * @param index
     * @return
     */
    public abstract Object get(int index);

    public abstract int size();

    /**
     * 比较值与位置之间的距离
     * @param o 值
     * @param location 位置
     * @return
     */
    public abstract int compare(Object o, int location);

    /**
     * 测量2个数据的间隔
     * @param o1
     * @param o2
     * @return
     */
    public abstract int measure(Object o1, Object o2);

    public static class RadarChartRuler {

        /**
         * 显示的文本
         */
        private String text;
        /**
         * 尺度
         */
        private int value;
        /**
         * 是否画线
         */
        private boolean drawLine;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public boolean isDrawLine() {
            return drawLine;
        }

        public void setDrawLine(boolean drawLine) {
            this.drawLine = drawLine;
        }
    }
}
