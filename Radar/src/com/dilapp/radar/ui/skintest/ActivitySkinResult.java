package com.dilapp.radar.ui.skintest;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.DailyTestSkin.TestSkinReq;
import com.dilapp.radar.domain.AnalyzeType;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.ProductsTestSkin;
import com.dilapp.radar.domain.ProductsTestSkin.ProductsTestSkinReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.found.ActivityTopicDetail;
import com.dilapp.radar.util.ReleaseUtils;
import com.dilapp.radar.util.SerializableUtil;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.util.UmengUtils;
import com.dilapp.radar.view.SpiderWebViewSimple;
import com.dilapp.radar.wifi.AllKfirManager;
import com.dilapp.radar.wifi.Content;

/**
 * Created by Administrator on 2015/4/26.
 */
public class ActivitySkinResult extends BaseActivity implements
		View.OnClickListener {

	// 肤质测试的部位
	public final static int[] SKIN_TEST_PARTS = Constants.SKIN_TEST_PARTS;
	private TitleView mTitle;
	private TextView tv_skin;
	private TextView tv_skin_tips;
	private TextView tv_goto_bbs;
	private TestSkinReq resultF;
	private TestSkinReq resultC;
	private int mSkinType = 1;//1 干性 2 油性 3 混合

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_skin_result);
		setResult(RESULT_OK);

		ZBackgroundHelper.setBackgroundForActivity(this, ZBackgroundHelper.TYPE_BLACK_BLUR);
		Intent data = getIntent();
		resultF = (TestSkinReq) data.getSerializableExtra(Constants.EXTRA_TAKING_RESULT(Constants.PART_FOREHEAD));
		resultC = (TestSkinReq) data.getSerializableExtra(Constants.EXTRA_TAKING_RESULT(Constants.PART_CHEEK));
		
		if(resultF != null && resultC != null){
			int oilF = resultF.getOil();
			int oilC = resultC.getOil();
			Slog.d("OILF : "+oilF+"  OILC : "+oilC);
			if(oilF >= 70 && oilC >= 70){
				mSkinType = 2;
			}else if(oilF >= 70 || oilC >= 70){
				mSkinType = 3;
			}else{
				mSkinType = 1;
			}
		}else{
			Slog.e("Error SKIN TYPE RESULT IS NULL!!!!");
		}

		View title = findViewById(TitleView.ID_TITLE);
		mTitle = new TitleView(this, title);
		mTitle.setCenterText(R.string.skin_result_title, null);
		mTitle.setLeftIcon(R.drawable.btn_back_white, this);
		// mTitle.setRightIcon(R.drawable.btn_share, null);
		mTitle.setBackgroundColor(getResources().getColor(R.color.test_title_color));

		tv_skin = findViewById_(R.id.tv_skin);
		tv_skin_tips = findViewById_(R.id.tv_skin_tips);
		tv_goto_bbs = findViewById_(R.id.tv_goto_bbs);

		changeResultByType();
		saveSkinTestResult();
		
		//add by kfir
		resultF.setType(AnalyzeType.SKIN);
		resultC.setType(AnalyzeType.SKIN);
		String rId = ""+System.currentTimeMillis();
		resultF.setRid(rId);
		resultF.setSubtype(AnalyzeType.BEFORE);
		resultC.setRid(rId);
		resultC.setSubtype(AnalyzeType.AFTER);
		handleUpdateData(resultF, resultC);
		if(ReleaseUtils.CAUSE_END_AFTER_SKINTEST){
			AllKfirManager.getInstance(this).endSkinTest();
		}
		if(getIntent() != null && getIntent().getBooleanExtra("umeng", false)){
			UmengUtils.onEventSkinTest(this, UmengUtils.TYPE_TEST_SKIN);
		}
	}
	
	private void handleUpdateData(TestSkinReq resultF, TestSkinReq resultC){
		if(resultF == null || resultC == null) return;
		ProductsTestSkin mProductsBeen = ReqFactory.buildInterface(this, ProductsTestSkin.class);
		ProductsTestSkinReq mReq = new ProductsTestSkinReq();
		mReq.setBefore(resultF);
		mReq.setAfter(resultC);
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
			finish();
			break;
		case TitleView.ID_RIGHT:

			break;
		case R.id.btn_goto_bbs: {
			long topic = mSkinType == 2 ? SharePreCacheHelper.getTopicIdOil(this) :
					(mSkinType == 3 ? SharePreCacheHelper.getTopicIdMix(this) :
							SharePreCacheHelper.getTopicIdDry(this));
			Intent intent = new Intent(this, ActivityTopicDetail.class);
			intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, topic);
			startActivity(intent);
			break;
		}
		case R.id.btn_face_test: {
			finish();
			Intent intent = new Intent(this, ActivitySkinChoosePart.class);
			intent.putExtra(Constants.EXTRA_CHOOSE_PART_IS_TEST, Constants.TEST_PREVIEW);
			startActivity(intent);
			AllKfirManager.getInstance(this).startSkinTest();
			break;
		}
		default:
			break;
		}

	}
	private void changeResultByType(){
		tv_skin.setText(Constants.getSkinTypeString(this, mSkinType));
		switch(mSkinType){
			case 1:
				tv_goto_bbs.setText(R.string.skin_goto_bbs_gan);
				tv_skin_tips.setText(R.string.test_dry_skin_detail);
				break;
			case 2:
				tv_goto_bbs.setText(R.string.skin_goto_bbs_you);
				tv_skin_tips.setText(R.string.test_oil_skin_detail);
				break;
			case 3:
				tv_goto_bbs.setText(R.string.skin_goto_bbs_zhong);
				tv_skin_tips.setText(R.string.test_middle_skin_detail);
				break;
		}
	}

	// 将数据保存到本地
	private void saveSkinTestResult() {
		Intent data = getIntent();
		for (int i = 0; i < SKIN_TEST_PARTS.length; i++) {
			int part = SKIN_TEST_PARTS[i];
			TestSkinReq req = (TestSkinReq) data.getSerializableExtra(Constants
					.EXTRA_TAKING_RESULT(part));
			String path = Constants.SKIN_TEST_RESULT_PATH(this, part);
			SerializableUtil.writeSerializableObject(path, req);
		}
	}
}
