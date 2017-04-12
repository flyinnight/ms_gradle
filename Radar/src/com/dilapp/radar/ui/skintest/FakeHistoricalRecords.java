package com.dilapp.radar.ui.skintest;

import android.widget.AdapterView;

import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.HistoricalRecords;
import com.dilapp.radar.util.CalendarUtils;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by husj1 on 2015/11/11.
 */
public class FakeHistoricalRecords extends HistoricalRecords {
    @Override
    public void historicalRecordsAsync(HistoricalReq bean, BaseCall<MHistoricalResp> call) {

    }

    @Override
    public void queryAverageTestDataAsync(AverageDataReq req, BaseCall<MAverageResp> call) {
        if (req == null) return;

        Random r = new Random();
        MAverageResp resp = new MAverageResp();
        resp.setStatus("SUCCESS");
        resp.setForeheadValue(generateAverageData(r, req.getEndTime(), req.getStartTime(), req.getQueryType() - 1));
        resp.setCheekValue(generateAverageData(r, req.getEndTime(), req.getStartTime(), req.getQueryType() - 1));
        resp.setEyeValue(generateAverageData(r, req.getEndTime(), req.getStartTime(), req.getQueryType() - 1));
        resp.setNoseValue(generateAverageData(r, req.getEndTime(), req.getStartTime(), req.getQueryType() - 1));
        resp.setHandValue(generateAverageData(r, req.getEndTime(), req.getStartTime(), req.getQueryType() - 1));
        if (!call.cancel) {
            call.call(resp);
        }
    }

    private AverageData generateAverageData(Random r, long start, long end, int dateType) {
        AverageData ad = new AverageData();
        LinkedList<AverageResult> list = new LinkedList<AverageResult>();
        while (start > end) {
            list.add(generateAverageResult(r, start));
            start = subTimeByDateType(start, dateType);
        }
        /*int empty = r.nextInt(10);
        for (int i = 0; i < empty; i++) {
            list.set(r.nextInt(list.size()), null);
        }*/
        int empty = r.nextInt(list.size());
        for (int i = 0; i < empty; i++) {
            list.remove(r.nextInt(list.size()));
        }
        ad.setValue(list);
        return ad;
    }

    private AverageResult generateAverageResult(Random r, long time) {
        AverageResult ar = new AverageResult();
        ar.setAnalyzeTime(time);
        ar.setWaterResult(r.nextInt(101));
        ar.setOilResult(r.nextInt(101));
        ar.setElasticResult(r.nextInt(101));
        ar.setWhiteningResult(r.nextInt(101));
        ar.setSensitiveResult(r.nextInt(101));
        ar.setPoreResult(r.nextInt(101));
        return ar;
    }

    @Override
    public void queryLocalRecordsAsync(HistoricalReq bean, BaseCall<MHistoricalResp> call) {

    }

    private final int DATE_INTERVAL = 1;

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
}
