package com.dilapp.radar.ui.skintest;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.AnalyzeType;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.HistoricalRecords;
import com.dilapp.radar.domain.HistoricalRecords.*;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.util.CalendarUtils;
import com.dilapp.radar.view.chart.RadarChartView;
import com.dilapp.radar.view.chart.RadarChartView.OnItemScrollListener;
import com.dilapp.radar.view.chart.TestRecordAdapter;
import com.dilapp.radar.widget.NumberPicker;
import com.dilapp.radar.widget.NumberPicker.OnValueChangeListener;

import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 * Created by husj1 on 2015/4/28.
 */
public class FragmentHistoryDaily extends BaseFragment implements View.OnClickListener,
        OnValueChangeListener, OnItemScrollListener {

    // 赋值时，一定要搞清楚这3个值的关系。
    public static final int DATE_INTERVAL = 21;// 21小时，21天，21月
    // RadarChartStyle.graphicalSplitCount = 7
    public static final int CHART_LOAD_PAGE = 3;// 21小时，21天，21月

    private DateFormat df;

    private RadarChartView rcv_hour;
    private RadarChartView rcv_day;
    private RadarChartView rcv_month;
    private RadarChartView[] rcv_charts;
    private NumberPicker np_part;
    private NumberPicker np_category;
    private NumberPicker np_date;
    private AverageRecordAdapter adapter;
    private boolean[] loading;// 真正加载中
    private long[] starts, ends;

    private HistoricalRecords recorder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_history_daily);
        setCacheView(true);
        Resources res = mContext.getResources();

        TestRecordAdapter.test = false;// Constants.TEST_PREVIEW;
        adapter = new AverageRecordAdapter(mContext);

        String[] parts = new String[]{ res.getString(R.string.normal_forehead), res.getString(R.string.normal_cheek), res.getString(R.string.normal_eye), res.getString(R.string.normal_nose), res.getString(R.string.normal_hand) };
        String[] categorys = new String[]{ res.getString(R.string.test_total), res.getString(R.string.test_water), res.getString(R.string.test_oil), res.getString(R.string.test_eastic), res.getString(R.string.test_whitening), res.getString(R.string.test_sensitive), res.getString(R.string.test_pore) };
        String[] dates = new String[]{ res.getString(R.string.test_in_hour), res.getString(R.string.test_in_day), res.getString(R.string.test_in_month) };

        long[] hours = CalendarUtils.calcFirstAndLastMilltsForHour(adapter.getStartTime());
        long[] hours2 = CalendarUtils.calcFirstAndLastMilltsForHour(adapter.getStartTime() - CalendarUtils.NH * (DATE_INTERVAL + 7 - 1));
        long[] days = CalendarUtils.calcFirstAndLastMilltsForDay(adapter.getStartTime());
        long[] days2 = CalendarUtils.calcFirstAndLastMilltsForDay(adapter.getStartTime() - CalendarUtils.ND * (DATE_INTERVAL + 7 - 1));
        long[] months = CalendarUtils.calcFirstAndLastMilltsForMonth(adapter.getStartTime());
        long[] months2 = CalendarUtils.calcFirstAndLastMilltsForMonth(subMonthTime(adapter.getStartTime(), DATE_INTERVAL + 7 - 1));
        starts = new long[] { hours[1], days[1], months[1] };
        ends = new long[] { hours2[0], days2[0], months2[0]};
        loading = new boolean[dates.length];

        if (Constants.TEST_PREVIEW) {
            recorder = new FakeHistoricalRecords();
        } else {
            recorder = ReqFactory.buildInterface(mContext, HistoricalRecords.class);
        }
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//        findViewById(R.id.btn_test1).setOnClickListener(this);
        rcv_hour = findViewById(R.id.rcv_hour);
        rcv_day = findViewById(R.id.rcv_day);
        rcv_month = findViewById(R.id.rcv_month);
        rcv_charts = new RadarChartView[] { rcv_hour, rcv_day, rcv_month };
        np_part = findViewById(R.id.np_part);
        np_category = findViewById(R.id.np_category);
        np_date = findViewById(R.id.np_date);
        np_part.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_category.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_date.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        np_part.setDisplayedValues(parts);
        np_part.setMinValue(1);
        np_part.setMaxValue(parts.length);
        // setValue 和 setOnValueChangedListener的执行顺序代表了是否要触发valueChanged事件
        np_part.setOnValueChangedListener(this);
        np_part.setValue(1);
        np_category.setDisplayedValues(categorys);
        np_category.setMinValue(0);
        np_category.setMaxValue(categorys.length - 1);
        np_category.setValue(0);
        np_category.setOnValueChangedListener(this);
        np_date.setDisplayedValues(dates);
        np_date.setMinValue(0);
        np_date.setMaxValue(dates.length - 1);
        np_date.setValue(0);
        np_date.setOnValueChangedListener(this);

        rcv_hour.setAdapter(adapter);
        setChartsVisibility(0);
        for (int i = 0; i < rcv_charts.length; i++)
            rcv_charts[i].setScrollListener(this);

        /* TODO
        int newVal = np_part.getValue();
        int index = newVal - 1;
        if (ends[index] == 0) ends[index] = System.currentTimeMillis();
        arequestDatas(newVal, ends[index] - 1, ends[index] - atime, requestCall);*/
        requestDatas(starts[0], ends[0], 0 + 1, 0);
        requestDatas(starts[1], ends[1], 1 + 1, 0);
        requestDatas(starts[2], ends[2], 2 + 1, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        /*case R.id.btn_test1:{
            rcv_charts[adapter.getDateType()].addPanels(1);
            break;
        }*/
        }
    }

    private long subTimeByDateType(long time, int dateType) {
        switch (dateType) {
            default:
            case 0:
                return time - CalendarUtils.NH * DATE_INTERVAL;
            case 1:
                return time - CalendarUtils.ND * DATE_INTERVAL;
            case 2:
                return subMonthTime(time, DATE_INTERVAL);
        }
    }

    private long subMonthTime(long startTime, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        calendar.add(Calendar.MONTH, -months);
        return calendar.getTimeInMillis();
    }

    private String[] convertStrings(Map<Integer, Integer> map) {
        String[] strs = new String[map.size()];
        int i = 0;
        for (Integer key : map.keySet()) {
            strs[i++] = getString(map.get(key));
        }
        return strs;
    }

    private void setNumberPickerStyle(NumberPicker np) {
        if (np == null) {
            return;
        }
        try {
            Field inField = np.getClass().getDeclaredField("mIncrementButton");
            Field deField = np.getClass().getDeclaredField("mDecrementButton");
            Field textField = np.getClass().getDeclaredField("mInputText");

            inField.setAccessible(true);
            deField.setAccessible(true);
            textField.setAccessible(true);

            EditText inputText = (EditText) textField.get(np);
            inputText.setBackgroundResource(R.drawable.btn_test_global);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        d("III", "old " + oldVal + ", new " + newVal);
        if (picker == np_part) {
            /*if (!selects[newVal - 1]) {
                // 找个一年的数据玩玩
                int index = newVal - 1;
                showWaitingDialog((AsyncTask) null);
                if (ends[index] == 0) ends[index] = System.currentTimeMillis();
                arequestDatas(newVal, ends[index] - 1, ends[index] - atime, requestCall);
            } else */{
                adapter.setPart(newVal);
                adapter.notifyDataSetChanged();
            }
        } else if (picker == np_category) {
            adapter.setCategory(newVal);
            adapter.notifyDataSetChanged();
        } else if (picker == np_date) {
            setChartsVisibility(newVal);
            adapter.setDateType(newVal);
            rcv_charts[newVal].setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private void setChartsVisibility(int dateType) {
        int h = View.GONE, d = View.GONE, m = View.GONE;
        switch (dateType) {
            default:
            case 0:
                h = View.VISIBLE;
                break;
            case 1:
                d = View.VISIBLE;
                break;
            case 2:
                m = View.VISIBLE;
                break;
        }
        if (rcv_hour.getVisibility() != h) {
            rcv_hour.setVisibility(h);
        }
        if (rcv_day.getVisibility() != d) {
            rcv_day.setVisibility(d);
        }
        if (rcv_month.getVisibility() != m) {
            rcv_month.setVisibility(m);
        }
    }

    /**
     *
     * @param start
     * @param end
     * @param queryType 要把 dateType + 1
     */
    private void requestDatas(final long start, final long end, final int queryType, final int add) {

        final int dateType = queryType - 1;
        loading[dateType] = true;
        AverageDataReq req = new AverageDataReq();
        req.setType(AnalyzeType.DAILY);
        req.setStartTime(end);
        req.setEndTime(start);
        req.setQueryType(queryType);
        d("III_request", "s " + df.format(new Date(start)) +
                ", e " + df.format(new Date(end)) + ", query " + queryType);
        BaseCall<MAverageResp> call = new BaseCall<MAverageResp>() {
            @Override
            public void call(MAverageResp resp) {
                loading[dateType] = false;
                if (resp != null && resp.isRequestSuccess()) {
                    starts[dateType] = ends[dateType] - 1;
                    ends[dateType] = subTimeByDateType(ends[dateType], dateType);
                    adapter.addAnslyzeDatas(resp, dateType);
                    d("III_request", queryType + " query success");
                    RadarChartView rcv_chart = rcv_charts[dateType];
                    rcv_chart.addPanels(add);
                    if (dateType == adapter.getDateType()) {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    d("III_request", queryType + " query fail msg->" + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        addCallback(call);
        recorder.queryAverageTestDataAsync(req, call);
    }

    @Override
    public void onItemScrolling(RadarChartView view, int first, int last, int oldFirst, int oldLast, int count) {
        d("III", "  f " + first + ", last " + last + ", of " + oldFirst + ", ol " + oldLast + ", c " + count);
        if (first == 0 && last == 0 && count < 30) {
            AverageRecordAdapter adapter = ((AverageRecordAdapter) view.getAdapter());
            int dateType = adapter.getDateType();
            if (loading[dateType]) return;// 正在拉数据
            requestDatas(starts[dateType], ends[dateType], dateType + 1, CHART_LOAD_PAGE);
        }
    }
    /*
    private void requestDatas(final int part, final long start, final long end, final RequestCall handler) {

        final DateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        d("III_request", "part " + part +
                ", start " + df.format(new Date(start)) +
                ", end " + df.format(new Date(end)));
        BaseCall<MHistoricalResp> call = new BaseCall<MHistoricalResp>() {
            @Override
            public void call(MHistoricalResp resp) {
                dimessWaitingDialog();
                if (resp != null && resp.isRequestSuccess()) {
                    starts[part - 1] = start;
                    ends[part - 1] = end;
                    List<FacialAnalyzeResp> values = resp.getValue();
                    if (values != null && values.size() != 0) {
                        StringBuilder log = new StringBuilder("[ ");
                        for (int i = 0; i < values.size(); i++) {
                            FacialAnalyzeResp data = values.get(i);
                            try {
                                JSONObject jo = new JSONObject(JsonUtils.toJson(data));
                                jo.put("analyzeTime", df.format(new Date(jo.getLong("analyzeTime"))));
                                log.append(jo.toString());
                            } catch (JSONException e) {
                            }
                            if (i != values.size() - 1) {
                                log.append(", ");
                            }
                        }
                        log.append(" ]");
                        d("III_request", part + " dates success size is " + resp.getValue().size() + "->" + log);

                    } else {
                        d("III_request", part + " dates success, but dates is empty.");
                    }
                    adapter.addAnslyzeDatas(resp.getValue());
                    if (handler != null) {
                        handler.onRequested(part);
                    }
                } else {
                    d("III_request", "dates fail.");
                }
            }
        };
        addCallback(call);
        HistoricalReq req = new HistoricalReq();
        req.setType(AnalyzeType.DAILY);
        req.setAnalyzePart(part);
        req.setStartTime(end);// 把我的理解转换一下
        req.setEndTime(start);
        req.setPageTime(start);
        recorder.historicalRecordsAsync(req, call);
    }*/
}
