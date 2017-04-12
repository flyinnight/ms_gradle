package com.dilapp.radar.ui.skintest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import android.content.Context;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.AnalyzeType;
import com.dilapp.radar.domain.HistoricalRecords.AverageData;
import com.dilapp.radar.domain.HistoricalRecords.AverageResult;
import com.dilapp.radar.domain.HistoricalRecords.MAverageResp;
import com.dilapp.radar.util.CalendarUtils;
import com.dilapp.radar.view.chart.RadarChartAdapter;

/**
 * Created by Dylan on 2015/11/10.
 */
public class AverageRecordAdapter extends RadarChartAdapter {


    private long startTime = System.currentTimeMillis();
    private Context context;
    private String[] num;
    private DateFormat dfDay;
    private DateFormat dfHour;
    private List<AverageResult> list;
    private DataSet set = new DataSet(startTime);

    private int part = AnalyzeType.FOREHEAD;
    private int dateType;
    private int category;

    public AverageRecordAdapter(Context context) {
        this.context = context;
        // ProductsTestSkin
        num = context.getResources().getStringArray(R.array.chinese_number);
        dfDay = new SimpleDateFormat(context.getResources().getString(R.string.datefmt_MM_dd));
        dfHour = new SimpleDateFormat(context.getResources().getString(R.string.datefmt_whats_hour));
        list = getCurrentAverageData().getValue();
    }

    public void addAnslyzeDatas(MAverageResp resp, int dateType) {
        set.addMAverageResp(resp, dateType);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
        list = getCurrentAverageData().getValue();
    }

    public int getDateType() {
        return dateType;
    }

    public void setDateType(int dateType) {
        this.dateType = dateType;
        list = getCurrentAverageData().getValue();
    }

    private MAverageResp getCurrentMAverageResp() {
        return set.getCurrentMAverageResp(dateType);
    }

    private AverageData getCurrentAverageData() {
        return set.getCurrentAverageData(part, dateType);
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
        AverageResult far = (AverageResult) o;
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
        AverageResult far1 = (AverageResult) o1;
        AverageResult far2 = (AverageResult) o2;
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
        AverageResult far = (AverageResult) o;
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
        AverageResult far = (AverageResult) o;
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
        AverageResult far = (AverageResult) o;
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
        private long startTime;
        private MAverageResp hour;
        private MAverageResp day;
        private MAverageResp month;

        public DataSet(long startTime) {
            this.startTime = startTime;

            hour = new MAverageResp();
            ensureMAverageResp(hour);

            day = new MAverageResp();
            ensureMAverageResp(day);

            month = new MAverageResp();
            ensureMAverageResp(month);
        }

        private void ensureMAverageResp(MAverageResp resp) {
            if (resp == null) return;
            if (resp.getForeheadValue() == null) {
                resp.setForeheadValue(new AverageData());
            }
            if (resp.getCheekValue() == null) {
                resp.setCheekValue(new AverageData());
            }
            if (resp.getEyeValue() == null) {
                resp.setEyeValue(new AverageData());
            }
            if (resp.getNoseValue() == null) {
                resp.setNoseValue(new AverageData());
            }
            if (resp.getHandValue() == null) {
                resp.setHandValue(new AverageData());
            }
            ensureAverageData(resp.getForeheadValue());
            ensureAverageData(resp.getCheekValue());
            ensureAverageData(resp.getEyeValue());
            ensureAverageData(resp.getNoseValue());
            ensureAverageData(resp.getHandValue());
        }

        private void ensureAverageData(AverageData data) {
            if (data == null) return;
            if (data.getValue() == null) {
                data.setValue(new ArrayList<AverageResult>());
            }
        }

        private MAverageResp getCurrentMAverageResp(int dateType) {
            switch (dateType) {
                default:
                case 0:
                    return hour;
                case 1:
                    return day;
                case 2:
                    return month;
            }
        }

        private AverageData getCurrentAverageData(int part, int dateType) {
            MAverageResp resp = getCurrentMAverageResp(dateType);
            switch (part) {
                default:
                case AnalyzeType.FOREHEAD:
                    return resp.getForeheadValue();
                case AnalyzeType.CHEEK:
                    return resp.getCheekValue();
                case AnalyzeType.EYE:
                    return resp.getEyeValue();
                case AnalyzeType.NOSE:
                    return resp.getNoseValue();
                case AnalyzeType.HAND:
                    return resp.getHandValue();
            }
        }

        public void addMAverageResp(MAverageResp data, int dateType) {
            if (data == null) return;
            MAverageResp main;
            switch (dateType) {
                case 0: {
                    main = hour;
                    break;
                }
                case 1: {
                    main = day;
                    break;
                }
                case 2: {
                    main = month;
                    break;
                }
                default:
                    throw new NoSuchElementException("Not this type! " + dateType);
            }

            addAverageData(main.getForeheadValue(), data.getForeheadValue(), AnalyzeType.FOREHEAD);
            addAverageData(main.getCheekValue(), data.getCheekValue(), AnalyzeType.CHEEK);
            addAverageData(main.getEyeValue(), data.getEyeValue(), AnalyzeType.EYE);
            addAverageData(main.getNoseValue(), data.getNoseValue(), AnalyzeType.NOSE);
            addAverageData(main.getHandValue(), data.getHandValue(), AnalyzeType.HAND);
        }

        public void addAverageData(AverageData main, AverageData data, int part) {
            if (data == null || data.getValue() == null) return;
            main.getValue().addAll(data.getValue());
            switch (part) {
                default:
                case AnalyzeType.FOREHEAD: {
                    break;
                }
                case AnalyzeType.CHEEK: {
                    break;
                }
                case AnalyzeType.EYE: {
                    break;
                }
                case AnalyzeType.NOSE: {
                    break;
                }
                case AnalyzeType.HAND: {
                    break;
                }
            }
        }
    }
}
