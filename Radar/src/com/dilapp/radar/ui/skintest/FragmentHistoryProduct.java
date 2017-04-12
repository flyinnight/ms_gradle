package com.dilapp.radar.ui.skintest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.AnalyzeType;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.DailyTestSkin.TestSkinReq;
import com.dilapp.radar.domain.HistoricalRecords;
import com.dilapp.radar.domain.HistoricalRecords.*;
import com.dilapp.radar.domain.ProductsTestSkin.ProductsTestSkinReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.textbuilder.utils.JsonUtils;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.util.CalendarUtils;
import com.dilapp.radar.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 * Created by husj1 on 2015/4/28.
 */
public class FragmentHistoryProduct extends BaseFragment implements View.OnClickListener {

    private DateFormat weekDateFormat;
    private EmptyView ev_empty;
    private PullToRefreshListView ptr_list;
    private ProductAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_history_product);
        setCacheView(true);
        weekDateFormat = new SimpleDateFormat(getString(R.string.history_week_date_format));
        ptr_list = findViewById(R.id.ptr_list);
        ev_empty = findViewById(R.id.ev_empty);
        adapter = new ProductAdapter();
        ptr_list.setAdapter(adapter);
        ptr_list.setOnItemClickListener(adapter);
        requestDatas(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private TestSkinReq convert(FacialAnalyzeResp resp) {
        if (resp == null) return null;
        TestSkinReq tsr = new TestSkinReq();
        tsr.setType(resp.getType());
        tsr.setSubtype(resp.getSubtype());
        tsr.setAnalyzeTime(resp.getAnalyzeTime());
        tsr.setUid(resp.getUid());
        tsr.setRid(resp.getRid());
        tsr.setAnalyzePlace(resp.getAnalyzePlace());
        tsr.setCosmeticID(resp.getCosmeticID());
        tsr.setSchemaID(resp.getSchemaID());
        tsr.setLabelID(resp.getLabelID());
        tsr.setPart(resp.getAnalyzePart());
        tsr.setWater(resp.getWaterResult());
        tsr.setOil(resp.getOilResult());
        tsr.setElastic(resp.getElasticResult());
        tsr.setWhitening(resp.getWhiteningResult());
        tsr.setSensitive(resp.getSensitiveResult());
        tsr.setPore(resp.getPoreResult());
        tsr.setSkinAge(resp.getSkinAgeResult());
        return tsr;
    }

    private void requestDatas(int page) {
        d("III_request", "request products.");
        BaseCall<MHistoricalResp> call = new BaseCall<MHistoricalResp>() {
            @Override
            public void call(MHistoricalResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    List<FacialAnalyzeResp> values = resp.getValue();
                    if (values != null && !values.isEmpty()) {
                        ev_empty.setVisibility(View.GONE);
                        d("III_request", "request products success. " + values.size() + ", " + JsonUtils.toJson(values));
                        List<ProductsTestSkinReq> reqs = new ArrayList<ProductsTestSkinReq>(values.size() / 2);
                        for (int i = 0; i < values.size() - 1; i += 2) {
                            FacialAnalyzeResp first = values.get(i);
                            FacialAnalyzeResp second = values.get(i + 1);
                            ProductsTestSkinReq ptkr = new ProductsTestSkinReq();
                            if (first.getSubtype() == 0) {
                                ptkr.setBefore(convert(first));
                                ptkr.setAfter(convert(second));
                            } else {
                                ptkr.setBefore(convert(second));
                                ptkr.setAfter(convert(first));
                            }
                            reqs.add(ptkr);
                        }
                        adapter.setList(reqs);
                        adapter.notifyDataSetChanged();
                    } else {
                        ev_empty.setVisibility(View.VISIBLE);
                        d("III_request", "request products success, but datas is empty.");
                    }
                } else {
                    ev_empty.setVisibility(View.VISIBLE);
                    d("III_request", "request fail. msg->" + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        long start = System.currentTimeMillis();
        long end = start - CalendarUtils.ND * 365 * 2;
        HistoricalRecords hr = ReqFactory.buildInterface(mContext, HistoricalRecords.class);
        HistoricalReq req = new HistoricalReq();
        req.setType(AnalyzeType.SKIN_PRODUCTS);
        req.setStartTime(end);
        req.setEndTime(start);
        req.setPageTime(start);
        addCallback(call);
        hr.historicalRecordsAsync(req, call);
    }

    class ProductAdapter extends BaseAdapter implements OnItemClickListener {

        private List<ProductsTestSkinReq> list;

        public List<ProductsTestSkinReq> getList() {
            return list;
        }

        public void setList(List<ProductsTestSkinReq> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public ProductsTestSkinReq getItem(int position) {
            return list != null && position < list.size() ? list.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_history_product, null);
                convertView.setTag(vh = new ViewHolder(convertView));
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            TestSkinReq item = getItem(position).getBefore();
            if (item == null) {
                return convertView;
            }
            String product = item.getCosmeticID() == null || "".equals(item.getCosmeticID().trim()) ?
                    getString(R.string.test_unknown_product) : item.getCosmeticID();
            vh.tv_product.setText(product);
            vh.tv_datetime.setText(weekDateFormat.format(new Date(item.getAnalyzeTime())));
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            position--;
            d("III_adapter", "position " + position);
            ProductsTestSkinReq item = getItem(position);
            Intent intent = new Intent(mContext, ActivityTasteResult.class);
            ArrayList<TestSkinReq> params = new ArrayList<TestSkinReq>(2);
            params.add(item.getBefore());
            params.add(item.getAfter());
            int part = item.getBefore().getPart();
            intent.putExtra(Constants.EXTRA_PRODUCT_RESULT_LOOK, true);
            intent.putExtra(Constants.EXTRA_SKIN_TAKING_TEXT_INFO, getPartString(part));
            intent.putExtra(Constants.EXTRA_SKIN_TAKING_CHOOSED_PART, part);
            intent.putExtra(Constants.EXTRA_TAKING_RESULT(part), params);
            startActivity(intent);
        }

        private String getPartString(int part) {
            switch (part) {
                case AnalyzeType.FOREHEAD:
                    return getString(R.string.normal_forehead);
                case AnalyzeType.CHEEK:
                    return getString(R.string.normal_cheek);
                case AnalyzeType.EYE:
                    return getString(R.string.normal_eye);
                case AnalyzeType.NOSE:
                    return getString(R.string.normal_nose);
                case AnalyzeType.HAND:
                    return getString(R.string.normal_hand);
                default:
                    return "unknown";
            }
        }
    }

    class ViewHolder {
        TextView tv_product;
        TextView tv_datetime;

        public ViewHolder(View itemView) {
            tv_product = (TextView) itemView.findViewById(R.id.tv_product);
            tv_datetime = (TextView) itemView.findViewById(R.id.tv_datetime);
        }
    }
}
