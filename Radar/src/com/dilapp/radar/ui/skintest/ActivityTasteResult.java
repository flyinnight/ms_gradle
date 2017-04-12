package com.dilapp.radar.ui.skintest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.AnalyzeType;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.ProductsTestSkin;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.DailyTestSkin.TestSkinReq;
import com.dilapp.radar.domain.ProductsTestSkin.ProductsTestSkinReq;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.comm.ActivityInputHistory;
import com.dilapp.radar.util.ReleaseUtils;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.util.UmengUtils;
import com.dilapp.radar.view.LinearLayoutForListView;
import com.dilapp.radar.view.OverScrollView;
import com.dilapp.radar.wifi.AllKfirManager;
import com.dilapp.radar.ui.skintest.SkinLevelRulesHelper.*;

import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 * Created by Administrator on 2015/4/26.
 */
public class ActivityTasteResult extends BaseActivity implements View.OnClickListener {


    public final static int REQ_INPUT_TASTE_NAME = 10;
    private TitleView mTitle;
    private EditText et_taste_name;
    private TextView tv_part_date;
    private ViewGroup vg_taste_name;
    private TextView tv_taste_name;
    private LinearLayoutForListView vg_values;
    // private TextView btn_more_args;
    private OverScrollView osv_scroll;

    private SkinLevelRulesHelper rulesHelper;

    private TestSkinReq dataBefore;
    private TestSkinReq dataAfter;
    private boolean isLook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taste_result);
        setResult(RESULT_OK);
        ZBackgroundHelper.setBackgroundForActivity(this, ZBackgroundHelper.TYPE_BLACK_BLUR);

        rulesHelper = new SkinLevelRulesHelper(getApplicationContext());
        Intent data = getIntent();

        isLook = data.getBooleanExtra(Constants.EXTRA_PRODUCT_RESULT_LOOK, false);
        int choosedPart = data.getIntExtra(Constants.EXTRA_SKIN_TAKING_CHOOSED_PART, 0);
        List<TestSkinReq> list = ((ArrayList<TestSkinReq>) data.getSerializableExtra(Constants.EXTRA_TAKING_RESULT(choosedPart)));
        dataBefore = list.get(0);
        dataAfter = list.get(1);
        dataBefore.setType(AnalyzeType.SKIN_PRODUCTS);
        dataAfter.setType(AnalyzeType.SKIN_PRODUCTS);
        dataBefore.setSubtype(0);
        dataAfter.setSubtype(1);

        View title = findViewById(TitleView.ID_TITLE);
        mTitle = new TitleView(this, title);
        mTitle.setLeftIcon(R.drawable.btn_back_white, this);
        // mTitle.setRightIcon(R.drawable.btn_share, this);
        mTitle.setCenterText(R.string.taste_result_title, null);
        mTitle.setBackgroundColor(getResources().getColor(R.color.test_title_color));

        et_taste_name = findViewById_(R.id.et_taste_name);
        vg_taste_name = findViewById_(R.id.vg_taste_name);
        tv_taste_name = findViewById_(R.id.tv_taste_name);
        tv_part_date = findViewById_(R.id.tv_part_date);

        tv_part_date.setText(
                getString(R.string.taste_result_tips,
                        data.getStringExtra(Constants.EXTRA_SKIN_TAKING_TEXT_INFO),
                        new SimpleDateFormat(getString(R.string.taste_result_format)).format(new Date(System.currentTimeMillis())))
        );

        vg_values = findViewById_(R.id.vg_values);
        // add(3);
        // btn_more_args  = findViewById_(R.id.btn_more_args);

        osv_scroll = findViewById_(R.id.osv_scroll);

        deleteTempResult();
        fillUiData(dataBefore, dataAfter);

        if (isLook) {
            String str = dataBefore.getCosmeticID() == null || "".equals(dataBefore.getCosmeticID().trim()) ?
                    getString(R.string.test_unknown_product) : dataBefore.getCosmeticID();
            et_taste_name.setVisibility(View.GONE);
            vg_taste_name.setVisibility(View.VISIBLE);

            et_taste_name.setText(str);
            tv_taste_name.setText(str);
            mTitle.setRightIcon(R.drawable.btn_share, this);
        }

		//add by kfir
        String rId = ""+System.currentTimeMillis();
        dataBefore.setRid(rId);
        dataBefore.setSubtype(AnalyzeType.BEFORE);
        dataAfter.setRid(rId);
        dataAfter.setSubtype(AnalyzeType.AFTER);
        handleUpdateData(dataBefore, dataAfter);
        if(ReleaseUtils.CAUSE_END_AFTER_SKINTEST){
    			AllKfirManager.getInstance(this).endSkinTest();
        }
		UmengUtils.onEventSkinTest(this, UmengUtils.TYPE_TEST_PRODUCT);
    }

    @Override
    public void onBackPressed() {
            requestUploadData();
        super.onBackPressed();
    }


    private void handleUpdateData(TestSkinReq resultF, TestSkinReq resultS){
		if(resultF == null || resultS == null) return;
		ProductsTestSkin mProductsBeen = ReqFactory.buildInterface(this, ProductsTestSkin.class);
		ProductsTestSkinReq mReq = new ProductsTestSkinReq();
		mReq.setBefore(resultF);
		mReq.setAfter(resultS);
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			
			@Override
			public void call(BaseResp resp) {
				// TODO Auto-generated method stub
				if(resp.isRequestSuccess()){
					Slog.i("upload test data SUCCESS!!!!!");
				}else{
					Slog.i("upload test data FAILED!!!!!");
				}
			}
		};
		addCallback(node);
		mProductsBeen.productsTestSkinAsync(mReq, node);
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleView.ID_LEFT:
                    requestUploadData();

                finish();
                break;
            case TitleView.ID_RIGHT:
                break;
            case R.id.tv_taste_name:
            case R.id.et_taste_name:
                if (isLook) break;
                Intent intent =new Intent(this, ActivityInputHistory.class);
                intent.putExtra(Constants.EXTRA_INPUT_HISTORY_NAME, "SkinProduct");
                intent.putExtra(Constants.EXTRA_INPUT_HISTORY_TEXT, et_taste_name.getText().toString());
                intent.putExtra(Constants.EXTRA_INPUT_HISTORY_HINT, getString(R.string.taste_input_taste));
                intent.putExtra(Constants.EXTRA_INPUT_HISTORY_SIZE, 5);
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this,

                        // Now we provide a list of Pair items which contain the view we can transitioning
                        // from, and the name of the view it is transitioning to, in the launched activity
                        new Pair<View, String>(et_taste_name,
                                "share:et_taste_name"));
                // Now we can start the Activity, providing the activity options as a bundle
                // ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
                if(et_taste_name.getVisibility() == View.VISIBLE) {
                    ActivityCompat.startActivityForResult(this, intent, REQ_INPUT_TASTE_NAME, activityOptions.toBundle());
                } else {
                    ActivityCompat.startActivityForResult(this, intent, REQ_INPUT_TASTE_NAME, null/*activityOptions.toBundle()*/);
                }
                // startActivityForResult(intent, REQ_INPUT_TASTE_NAME);
                break;
            /*case R.id.btn_more_args:
                UIItem[] sv = (UIItem[]) vg_values.getTag();
                if(vg_values.getChildCount() < sv.length) {
                    add(sv, 3, sv.length);
                    btn_more_args.setVisibility(View.GONE);
                }
                break;*/
            case R.id.btn_history:
                if (isLook) {
                    finish();
                } else {
                    startActivity(new Intent(this, ActivityHistory.class));
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_INPUT_TASTE_NAME && resultCode == RESULT_OK) {
            String str = data.getStringExtra(Constants.RESULT_INPUT_HISTORY_TEXT);
            if(str != null && !"".equals(str.trim())) {
                et_taste_name.setVisibility(View.GONE);
                vg_taste_name.setVisibility(View.VISIBLE);

                et_taste_name.setText(str);
                tv_taste_name.setText(str);
                mTitle.setRightIcon(R.drawable.btn_share, this);
                dataBefore.setCosmeticID(str);
                dataAfter.setCosmeticID(str);
            } else {
                dataBefore.setCosmeticID(null);
                dataAfter.setCosmeticID(null);
                et_taste_name.setVisibility(View.VISIBLE);
                vg_taste_name.setVisibility(View.GONE);

                et_taste_name.setText("");
                tv_taste_name.setText("");
                mTitle.setRightIcon(null, this);
            }
        }
    }

    private void fillUiData(TestSkinReq before, TestSkinReq after) {
        if(before == null) {
            before = new TestSkinReq();
        }
        if(after == null ) {
            after = new TestSkinReq();
        }
        UIItem[] sv = sortData(before, after);
        //add(sv, 0, 3);
        vg_values.setAdapter(new TasteResultAdapter(Arrays.asList(sv)));
    }

    private UIItem[] sortData(TestSkinReq be, TestSkinReq af) {
        if(be == null) {
            be = new TestSkinReq();
        }
        if(af == null ) {
            af = new TestSkinReq();
        }
        UIItem[] vs = new UIItem[6];
        vs[0] = new UIItem("Water", be.getWater(), af.getWater(), getString(R.string.test_water));
        vs[1] = new UIItem("Oil", be.getOil(), af.getOil(), getString(R.string.test_oil));
        vs[2] = new UIItem("Elastic", be.getEastic(), af.getOil(), getString(R.string.test_eastic));
        vs[3] = new UIItem("Sensitive", be.getSensitive(), af.getSensitive(), getString(R.string.test_sensitive));
        vs[4] = new UIItem("Whitening", be.getWhitening(), af.getWhitening(), getString(R.string.test_whitening));
        vs[5] = new UIItem("Pore", be.getPore(), af.getPore(), getString(R.string.test_pore));
        Arrays.sort(vs, new Comparator<UIItem>() {
            @Override
            public int compare(UIItem lhs, UIItem rhs) {
                return lhs.compareTo(rhs);
            }
        });
        for (UIItem v : vs) {
            android.util.Log.i("III", " " + v.key + " " + v.value);
        }
        return vs;
    }
    
    private void deleteTempResult() {
    	String path = Constants.PRODUCT_TEST_TEMP_RESULT_PATH(this);
    	new java.io.File(path).delete();
    }

    private void requestUploadData() {
        if (!isLook) {
            return;
        }
        d("III_request", "update test datas.");
        BaseCall<BaseResp> call = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    d("III_request", "update test datas success.");
                } else {
                    d("III_request", "update fail msg->" + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        ProductsTestSkin pts = ReqFactory.buildInterface(this, ProductsTestSkin.class);
        ProductsTestSkinReq req = new ProductsTestSkinReq();
        req.setBefore(dataBefore);
        req.setAfter(dataAfter);
        pts.productsTestSkinAsync(req, call);
    }

    class UIItem implements Comparable<UIItem> {
        String key;
        int value;
        int before;
        int after;
        String name;

        public UIItem(String key, int before, int after, String name) {
            this.key = key;
            this.before = before;
            this.after = after;
            this.value = after - before;
            this.name = name;
        }

        @Override
        public int compareTo(UIItem another) {
            if(another == null) {
                return -1;
            }
            return this.value > another.value ? -1 : 1;
        }
    }

    class TasteResultAdapter extends LinearLayoutForListView.LinearLayoutForListViewAdapter {

        private List<UIItem> list;

        public TasteResultAdapter(List<UIItem> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public UIItem getItem(int position) {
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
                convertView = getLayoutInflater().inflate(R.layout.item_taste_result, null);
                convertView.setTag(vh = new ViewHolder(convertView));
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            UIItem item = getItem(position);
            if (item == null) return convertView;

            vh.tv_name.setText(item.name);
            vh.pb_before.setProgress(item.before);
            vh.pb_after.setProgress(item.after);
            vh.tv_score_before.setText(item.before + "");
            vh.tv_score_after.setText(item.after + "");
            vh.tv_use_diff.setText(Math.abs(item.value) + "");
            vh.tv_effetc_eval.setText(rulesHelper.getDiffEvaluation(item.key, item.value/* < 0 ? 0 : item.value*/));
            vh.iv_ris_fall.setLevel(item.value < 0 ? 2 : 1);
            SkinRuleDiff[] diffs = rulesHelper.getSkinDiffs(item.key);
            for (int i = 0; i < diffs.length; i++) {
                if (item.value >= diffs[i].getMin() && item.value <= diffs[i].getMax()) {
                    vh.tv_effetc_eval.getBackground().setLevel(i);
                }
            }
            return convertView;
        }
    }

    class ViewHolder {
        TextView tv_name;
        ProgressBar pb_before;
        ProgressBar pb_after;
        TextView tv_score_before;
        TextView tv_score_after;
        TextView tv_use_diff;
        TextView tv_effetc_eval;
        Drawable iv_ris_fall;

        ViewHolder(View itemView) {
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            pb_before = (ProgressBar) itemView.findViewById(R.id.pb_before);
            pb_after = (ProgressBar) itemView.findViewById(R.id.pb_after);
            tv_score_before = (TextView) itemView.findViewById(R.id.tv_score_before);
            tv_score_after = (TextView) itemView.findViewById(R.id.tv_score_after);
            tv_use_diff = (TextView) itemView.findViewById(R.id.tv_use_diff);
            tv_effetc_eval = (TextView) itemView.findViewById(R.id.tv_effetc_eval);
            iv_ris_fall = tv_use_diff.getCompoundDrawables()[0];
        }
    }
}
