package com.dilapp.radar.view.chart;

import static com.dilapp.radar.textbuilder.utils.L.d;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.util.SparseArray;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.AnalyzeType;
import com.dilapp.radar.domain.HistoricalRecords.FacialAnalyzeResp;
import com.dilapp.radar.textbuilder.utils.JsonUtils;
import com.dilapp.radar.util.CalendarUtils;

/**
 * Created by husj1 on 2015/10/28.
 */
public class TestRecordAdapter extends RadarChartAdapter {


    public static boolean test = false;
    private long startTime = System.currentTimeMillis();
    private Context context;
    private String[] num;
    private DateFormat dfDay;
    private DateFormat dfHour;
    private List<FacialAnalyzeResp> list;
    private DataSet[] sets = new DataSet[] {
            new DataSet(test), new DataSet(test), new DataSet(test),
            new DataSet(test), new DataSet(test)
    };

    private int part = AnalyzeType.FOREHEAD;
    private int dateType;
    private int category;

    public TestRecordAdapter(Context context) {
        this.context = context;
//        HistoricalRecords hr = ReqFactory.buildInterface(context, HistoricalRecords.class);
//        hr.historicalRecordsAsync()
        // ProductsTestSkin
        num = context.getResources().getStringArray(R.array.chinese_number);
        dfDay = new SimpleDateFormat(context.getResources().getString(R.string.datefmt_MM_dd));
        dfHour = new SimpleDateFormat(context.getResources().getString(R.string.datefmt_whats_hour));
        list = sets[part - 1].hour;
    }

    public void addAnslyzeDatas(List<FacialAnalyzeResp> datas) {
        if (datas == null) return;
        for (int i = 0; i < datas.size(); i++) {
            FacialAnalyzeResp data = datas.get(i);
            if (data == null || data.getAnalyzePart() == 0) {
                d("III_adapter", "error data " + i + ", " + JsonUtils.toJson(data));
                continue;
            }
            sets[data.getAnalyzePart() - 1].addAnalyzeData(data);
        }
    }


    private void mergeData(int count, FacialAnalyzeResp main, FacialAnalyzeResp data) {
        main.setWaterResult((main.getWaterResult() * count + data.getWaterResult()) / (1 + count));
        main.setOilResult((main.getOilResult() * count + data.getOilResult()) / (++count));
        main.setElasticResult((main.getElasticResult() * count + data.getElasticResult()) / (1 + count));
        main.setWhiteningResult((main.getWhiteningResult() * count + data.getWhiteningResult()) / (1 + count));
        main.setSensitiveResult((main.getSensitiveResult() * count + data.getSensitiveResult()) / (1 + count));
        main.setPoreResult((main.getPoreResult() * count + data.getPoreResult()) / (1 + count));
        main.setSkinAgeResult((main.getSkinAgeResult() * count + data.getSkinAgeResult()) / (1 + count));
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
        setDateType(dateType);
    }

    public int getDateType() {
        return dateType;
    }

    public void setDateType(int dateType) {
        //if (this.dateType != dateType) {
            this.dateType = dateType;
            switch (dateType) {
                case 1: {
                    list = sets[part - 1].day;
                    break;
                }
                case 2: {
                    list = sets[part - 1].month;
                    break;
                }
                default: {
                    list = sets[part - 1].hour;
                    break;
                }
            }
            // notifyDataSetChanged();
        //}
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    private String getChineseNum(int week) {
        switch (week) {
            case Calendar.MONDAY:
                return num[1];
            case Calendar.TUESDAY:
                return num[2];
            case Calendar.WEDNESDAY:
                return num[3];
            case Calendar.THURSDAY:
                return num[4];
            case Calendar.FRIDAY:
                return num[5];
            case Calendar.SATURDAY:
                return num[6];
            case Calendar.SUNDAY:
            default:
                return num[7];
        }
    }

    @Override
    public Object get(int index) {
        return list != null && index < list.size() ? list.get(index) : null;
    }

    @Override
    public int size() {
        return list != null ? list.size() : 0;
    }

    @Override
    public int compare(Object o, int location) {
        FacialAnalyzeResp far = (FacialAnalyzeResp) o;
        long otime = far.getAnalyzeTime();
        Calendar ocale = Calendar.getInstance();
        ocale.setTimeInMillis(otime);
        int result = COMPARE_EQ;
        switch (dateType) {
            default:
            case 0: {
                long time = startTime - location * CalendarUtils.NH;
                if (!CalendarUtils.sameHour(otime, time)) {
                    result = otime > time ? COMPARE_GT : COMPARE_LT;
                }
                break;
            }
            case 1: {
                long time = startTime - location * CalendarUtils.ND;
                if (!CalendarUtils.sameDay(otime, time)) {
                    result = otime > time ? COMPARE_GT : COMPARE_LT;
                }
                break;
            }
            case 2: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);
                calendar.add(Calendar.MONTH, -location);
                long time = calendar.getTimeInMillis();
                if (!CalendarUtils.sameMonth(otime, time)) {
                    result = otime > time ? COMPARE_GT : COMPARE_LT;
                }
                break;
            }
        }
        return result;
    }

    @Override
    public int measure(Object o1, Object o2) {
        FacialAnalyzeResp far1 = (FacialAnalyzeResp) o1;
        FacialAnalyzeResp far2 = (FacialAnalyzeResp) o2;
        long time1 = far1.getAnalyzeTime(), time2 = far2.getAnalyzeTime();
        int result;
        switch (dateType) {
            default:
            case 0: {
                result = CalendarUtils.diffHour(time1, time2);
                break;
            }
            case 1: {
                result = CalendarUtils.diffDay(time1, time2);
                break;
            }
            case 2: {
                result = CalendarUtils.diffMonth(time1, time2);
                break;
            }
        }
        return result;
    }

    @Override
    public String getTitle(int location) {
        String date;
        switch (dateType) {
            case 1: {
                long time = startTime - location * CalendarUtils.ND;
                if (CalendarUtils.sameDay(startTime, time)) {
                    date = context.getResources().getString(R.string.date_today);
                } else if (CalendarUtils.sameDay(startTime - CalendarUtils.ND, time)) {
                    date = context.getResources().getString(R.string.date_yesterday);
                } else {
                    date = dfDay.format(new Date(time));
                }
                break;
            }
            case 2: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);
                calendar.add(Calendar.MONTH, -location);
                date = context.getResources().getString(
                        R.string.datefmt_whats_month,
                        "" + (calendar.get(Calendar.MONTH) + 1));
                break;
            }
            default:
                long time = startTime - location * CalendarUtils.NH;
                date = dfHour.format(new Date(time));
                break;
        }
        return date;
    }

    @Override
    public String getSecondTitle(int location) {
        String date;

        switch (dateType) {
            case 1: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime - location * CalendarUtils.ND);
                int week = calendar.get(Calendar.DAY_OF_WEEK);
                date = context.getString(R.string.datefmt_whats_week, getChineseNum(week));
                break;
            }
            case 2: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);
                calendar.add(Calendar.MONTH, -location);
                int m = calendar.get(Calendar.MONTH) + 1;
                if (m == 1) {
                    date = context.getString(R.string.datefmt_whats_year, "" + calendar.get(Calendar.YEAR));
                } else {
                    date = "";
                }
                break;
            }
            default: {
                long time = startTime - location * CalendarUtils.NH;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                int hour = calendar.get(Calendar.HOUR);
                if (hour == 0) {
                    date = dfDay.format(new Date(time));
                } else {
                    date = "";
                }
                break;
            }
        }
        return date;
    }

    @Override
    public boolean isPointX(Object o, int location) {
        if (o == null) return false;
        FacialAnalyzeResp far = (FacialAnalyzeResp) o;
        switch (dateType) {
            case 1: {
                long time = startTime - location * CalendarUtils.ND;
                return CalendarUtils.sameDay(far.getAnalyzeTime(), time);
            }
            case 2: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);
                calendar.add(Calendar.MONTH, -location);
                return CalendarUtils.sameMonth(far.getAnalyzeTime(), calendar.getTimeInMillis());
            }
            default: {
                long time = startTime - location * CalendarUtils.NH;
                return CalendarUtils.sameHour(far.getAnalyzeTime(), time);
            }
        }
    }

    @Override
    public int getPointY(Object o) {
        FacialAnalyzeResp far = (FacialAnalyzeResp) o;
        switch (category) {
            case AnalyzeType.WATER:
                return far.getWaterResult();
            case AnalyzeType.OIL:
                return far.getOilResult();
            case AnalyzeType.ELASTIC:
                return far.getElasticResult();
            case AnalyzeType.WHITENING:
                return far.getWhiteningResult();
            case AnalyzeType.SENSITIVE:
                return far.getSensitiveResult();
            case AnalyzeType.PORE:
                return far.getPoreResult();
            default:
                return (far.getWaterResult() + far.getOilResult() + far.getElasticResult() +
                        far.getWhiteningResult() + far.getSensitiveResult() + far.getPoreResult()) / 6;
        }
    }

    @Override
    public String getPointText(Object o) {
        if (o == null) return null;
        FacialAnalyzeResp far = (FacialAnalyzeResp) o;
        switch (category) {
            case AnalyzeType.WATER:
                return far.getWaterResult() + "";
            case AnalyzeType.OIL:
                return far.getOilResult() + "";
            case AnalyzeType.ELASTIC:
                return far.getElasticResult() + "";
            case AnalyzeType.WHITENING:
                return far.getWhiteningResult() + "";
            case AnalyzeType.SENSITIVE:
                return far.getSensitiveResult() + "";
            case AnalyzeType.PORE:
                return far.getPoreResult() + "";
            default:
                return (far.getWaterResult() + far.getOilResult() + far.getElasticResult() +
                        far.getWhiteningResult() + far.getSensitiveResult() + far.getPoreResult()) / 6 + "";
        }
    }

    @Override
    public RadarChartRuler[] getRulers() {
        return new RadarChartRuler[0];
    }

    private long getCurrentTime(int location) {

        long time;
        switch (dateType) {
            case 1: {
                time = startTime - location * CalendarUtils.ND;
                break;
            }
            case 2: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);
                calendar.add(Calendar.MONTH, -location);

                time = calendar.getTimeInMillis();
                break;
            }
            default: {
                time = startTime - location * CalendarUtils.NH;
                break;
            }
        }
        return time;
    }

    class DataSet {
        private List<FacialAnalyzeResp> hour = new LinkedList<FacialAnalyzeResp>();
        private SparseArray<Integer> hourCount = new SparseArray<Integer>();
        private List<FacialAnalyzeResp> day = new LinkedList<FacialAnalyzeResp>();
        private SparseArray<Integer> dayCount = new SparseArray<Integer>();
        private List<FacialAnalyzeResp> month = new LinkedList<FacialAnalyzeResp>();
        private SparseArray<Integer> monthCount = new SparseArray<Integer>();

        public DataSet(boolean test) {
            if (!test) return;
            Random random = new Random();
            for (int i = 0; i < 27; i++) {
                FacialAnalyzeResp a = new FacialAnalyzeResp();
                a.setAnalyzeTime(startTime - CalendarUtils.NH * i);
                a.setAnalyzePart(AnalyzeType.EYE);
                a.setWaterStandard(60);
                a.setOilStandard(60);
                a.setSensitiveStandard(60);
                a.setPoreStandard(60);
                a.setElasticStandard(60);
                a.setWhiteningStandard(60);
                a.setSkinAgeStandard(25);

                a.setWaterResult(random.nextInt(101));
                a.setOilResult(random.nextInt(101));
                a.setElasticResult(random.nextInt(101));
                a.setSensitiveResult(random.nextInt(101));
                a.setWhiteningResult(random.nextInt(101));
                a.setPoreResult(random.nextInt(101));
                hour.add(a);
                hourCount.put(i, 1);
            }
            int temp = random.nextInt(10);
            for (int i = 0; i < temp; i++) {
                int index = random.nextInt(hour.size());
                hour.set(index, null);
                hourCount.put(index, 1);
            }
            for (int i = 0; i < 27; i++) {
                FacialAnalyzeResp a = new FacialAnalyzeResp();
                a.setAnalyzeTime(startTime - CalendarUtils.ND * i);
                a.setAnalyzePart(AnalyzeType.EYE);
                a.setWaterStandard(60);
                a.setOilStandard(60);
                a.setSensitiveStandard(60);
                a.setPoreStandard(60);
                a.setElasticStandard(60);
                a.setWhiteningStandard(60);
                a.setSkinAgeStandard(25);

                a.setWaterResult(random.nextInt(101));
                a.setOilResult(random.nextInt(101));
                a.setElasticResult(random.nextInt(101));
                a.setSensitiveResult(random.nextInt(101));
                a.setWhiteningResult(random.nextInt(101));
                a.setPoreResult(random.nextInt(101));
                day.add(a);
                dayCount.put(i, 1);
            }
            temp = random.nextInt(10);
            for (int i = 0; i < temp; i++) {
                int index = random.nextInt(day.size());
                day.set(random.nextInt(day.size()), null);
                dayCount.put(index, 0);
            }


            for (int i = 0; i < 27; i++) {
                FacialAnalyzeResp a = new FacialAnalyzeResp();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);
                calendar.add(Calendar.MONTH, -i);
                // int m = calendar.get(Calendar.MONTH) + 1;
                a.setAnalyzeTime(calendar.getTimeInMillis());
                a.setAnalyzePart(AnalyzeType.EYE);
                a.setWaterStandard(60);
                a.setOilStandard(60);
                a.setSensitiveStandard(60);
                a.setPoreStandard(60);
                a.setElasticStandard(60);
                a.setWhiteningStandard(60);
                a.setSkinAgeStandard(25);

                a.setWaterResult(random.nextInt(101));
                a.setOilResult(random.nextInt(101));
                a.setElasticResult(random.nextInt(101));
                a.setSensitiveResult(random.nextInt(101));
                a.setWhiteningResult(random.nextInt(101));
                a.setPoreResult(random.nextInt(101));
                month.add(a);
            }
            temp = random.nextInt(10);
            for (int i = 8; i < 20; i++) {
                int index = i;
                month.set(index, null);
                monthCount.put(index, 0);
            }
            d("III_adapter", "GenHourCount " + hour.size() + ", GenDayCount " + day.size() + ", GenMonthCount " + month.size());
        }

        public void addAnalyzeData(FacialAnalyzeResp data) {
            if (data == null) return;
            boolean find = false;
            for (int i = 0; i < hour.size(); i++) {
                FacialAnalyzeResp resp = hour.get(i);
                if (resp == null) continue;
                if (CalendarUtils.sameHour(data.getAnalyzeTime(), resp.getAnalyzeTime())) {
                    find = true;
                    Integer temp = hourCount.get(i);
                    int count = temp == null || temp < 1 ? 1 : temp;
                    mergeData(count, resp, data);
                    hourCount.put(i, count + 1);
                    break;
                }
            }
            if (!find) {
                hour.add(data);
                hourCount.put(hour.size() - 1, 1);
            }

            find = false;
            for (int i = 0; i < day.size(); i++) {
                FacialAnalyzeResp resp = day.get(i);
                if (resp == null) continue;
                if (CalendarUtils.sameDay(data.getAnalyzeTime(), resp.getAnalyzeTime())) {
                    Integer temp = dayCount.get(i);
                    int count = temp == null || temp < 1 ? 1 : temp;
                    mergeData(count, resp, data);
                    dayCount.put(i, count + 1);
                    break;
                }
            }
            if (!find) {
                day.add(data);
                dayCount.put(day.size() - 1, 1);
            }

            for (int i = 0; i < month.size(); i++) {
                FacialAnalyzeResp resp = month.get(i);
                if (resp == null) continue;
                if (CalendarUtils.sameDay(data.getAnalyzeTime(), resp.getAnalyzeTime())) {
                    Integer temp = monthCount.get(i);
                    int count = temp == null || temp < 1 ? 1 : temp;
                    mergeData(count, resp, data);
                    monthCount.put(i, count + 1);
                    break;
                }
            }
            if (!find) {
                month.add(data);
                monthCount.put(month.size() - 1, 1);
            }
        }
    }
}
