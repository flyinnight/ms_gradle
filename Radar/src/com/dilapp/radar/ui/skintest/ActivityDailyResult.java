package com.dilapp.radar.ui.skintest;

import static com.dilapp.radar.textbuilder.utils.L.array2String;
import static com.dilapp.radar.textbuilder.utils.L.d;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.AnalyzeType;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.DailyTestSkin;
import com.dilapp.radar.domain.DailyTestSkin.TestSkinReq;
import com.dilapp.radar.domain.DailyTestSkin.TestSkinResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.GetPostList.PostsTestReq;
import com.dilapp.radar.domain.GetPostList.TopicPostListResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.textbuilder.utils.JsonUtils;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.found.SkinSchemeActivity;
import com.dilapp.radar.ui.mine.ActivityMyCarePlan;
import com.dilapp.radar.ui.share.ShareBoard;
import com.dilapp.radar.ui.skintest.SkinLevelRulesHelper.SkinRuleDiff;
import com.dilapp.radar.ui.topic.ActivityPostDetail;
import com.dilapp.radar.ui.topic.TopicHelper;
import com.dilapp.radar.util.ABFileUtil;
import com.dilapp.radar.util.MD5;
import com.dilapp.radar.util.PathUtils;
import com.dilapp.radar.util.ReleaseUtils;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.util.UmengUtils;
import com.dilapp.radar.util.ViewUtils;
import com.dilapp.radar.view.LinearLayoutForListView;
import com.dilapp.radar.view.LinearLayoutForListView.LinearLayoutForListViewAdapter;
import com.dilapp.radar.view.OverScrollView;
import com.dilapp.radar.view.OverScrollView.OnScrollChangedListener;
import com.dilapp.radar.view.SpiderWebView;
import com.dilapp.radar.view.SpiderWebView.SpiderWebType;
import com.dilapp.radar.widget.BaseDialog;
import com.dilapp.radar.wifi.AllKfirManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * Created by husj1 on 2015/6/10.
 */
public class ActivityDailyResult extends BaseActivity implements View.OnClickListener, Comparator<MPostResp>, OnItemClickListener, OnScrollChangedListener {

    /*
    水分：补水、缺水、锁水，保湿
    油分：控油、去油、油性，油脂
    敏感：敏感、过敏、血丝、红肿
    美白：美白、斑、肤色、面膜
    毛孔：毛孔、粉刺、黑头
    弹性：弹性、紧致、皱纹*/
    private final static Map<String, String[]> KEYWORDS;

    static {
        /*vg_water.setValue(testValue[0]);
        vg_elastic.setValue(testValue[1]);
        vg_whitening.setValue(testValue[2]);
        vg_pore.setValue(testValue[3]);
        vg_sensitive.setValue(testValue[4]);
        vg_oil.setValue(testValue[5]);*/
        KEYWORDS = new HashMap<String, String[]>();
        KEYWORDS.put("Water", new String[]{"补水", "缺水", "锁水", "保湿"});
        KEYWORDS.put("Elastic", new String[]{"弹性", "紧致", "皱纹"});
        KEYWORDS.put("Whitening", new String[]{"美白", "斑", "肤色", "面膜"});
        KEYWORDS.put("Pore", new String[]{"毛孔", "粉刺", "黑头"});
        KEYWORDS.put("Sensitive", new String[]{"敏感", "过敏", "血丝", "红肿"});
        KEYWORDS.put("Oil", new String[]{"控油", "去油", "油性", "油脂"});
    }

    private TitleView mTitle;
    private SpiderWebView sv_draw;

/*    private SeekBarDoubleView sbd_water;
    private SeekBarDoubleView sbd_oil;
    private SeekBarDoubleView sbd_whitening;
    private SeekBarDoubleView sbd_eastic;
    private SeekBarDoubleView sbd_sensitive;
    private SeekBarDoubleView sbd_pore;*/

    private DailyPointer vg_water;
    private DailyPointer vg_oil;
    private DailyPointer vg_elastic;
    private DailyPointer vg_sensitive;
    private DailyPointer vg_pore;
    private DailyPointer vg_whitening;

    private ViewGroup vg_option;
    private TextView tv_skin;
    private TextView tv_detail;
    private ImageView iv_one;
    private ImageView iv_two;
    private View v_temp;
    private LinearLayoutForListView lv_recommended;
    private RecommedAdapter lv_adapter;
    private OverScrollView osv_scroll;

    private SkinLevelRulesHelper rulesHelper;
    private TestSkinReq testResult;

    private LinearLayout mShareCover;

    private DailyTestSkin mDailyBean;
    private BaseDialog mContrast;

    private final UMSocialService mController = UMServiceFactory
            .getUMSocialService(UmengUtils.DESCRIPTOR);
    private SHARE_MEDIA mPlatform = SHARE_MEDIA.WEIXIN;

    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_result);
        setResult(RESULT_OK);
        ZBackgroundHelper.setBackgroundForActivity(this, ZBackgroundHelper.TYPE_BLACK_BLUR);
        String text = getIntent().getStringExtra(Constants.EXTRA_SKIN_TAKING_TEXT_INFO);

        rulesHelper = new SkinLevelRulesHelper(getApplicationContext());

        Intent data = getIntent();
        int partId = data.getIntExtra(Constants.EXTRA_SKIN_TAKING_CHOOSED_PART, 0);
        testResult = (TestSkinReq) data.getSerializableExtra(Constants.EXTRA_TAKING_RESULT(partId));

        if (partId == Constants.PART_CHEEK) {
            testResult.setSkinAge(testResult.getSkinAge() + 3);
        } else if (partId == Constants.PART_HAND) {
            testResult.setSkinAge(testResult.getSkinAge() + 6);
        }

        mTitle = new TitleView(this, findViewById(TitleView.ID_TITLE));
        mTitle.setCenterText(R.string.normal_result_title, null);
        mTitle.setLeftIcon(R.drawable.btn_back_white, this);
        mTitle.setRightIcon(R.drawable.btn_share, this);
        mTitle.setBackgroundColor(getResources().getColor(R.color.test_title_color));

        vg_option = findViewById_(R.id.vg_option);
        tv_skin = findViewById_(R.id.tv_skin);
        tv_skin.setText(text);
        /*sbd_water	  = findViewById_(R.id.sbd_water);
        sbd_oil		  = findViewById_(R.id.sbd_oil);
        sbd_whitening = findViewById_(R.id.sbd_whitening);
        sbd_eastic	  = findViewById_(R.id.sbd_eastic);
        sbd_sensitive = findViewById_(R.id.sbd_sensitive);
        sbd_pore	  = findViewById_(R.id.sbd_pore);*/
        vg_water = new DailyPointer(findViewById(R.id.vg_water), rulesHelper.getSkinRules("Water"));
        vg_oil = new DailyPointer(findViewById(R.id.vg_oil), rulesHelper.getSkinRules("Oil"));
        vg_elastic = new DailyPointer(findViewById(R.id.vg_eastic), rulesHelper.getSkinRules("Elastic"));
        vg_sensitive = new DailyPointer(findViewById(R.id.vg_sensitive), rulesHelper.getSkinRules("Sensitive"));
        vg_pore = new DailyPointer(findViewById(R.id.vg_pore), rulesHelper.getSkinRules("Pore"));
        vg_whitening = new DailyPointer(findViewById(R.id.vg_whitening), rulesHelper.getSkinRules("Whitening"));
        osv_scroll = findViewById_(R.id.osv_scroll);
        lv_recommended = findViewById_(R.id.lv_recommended);
        lv_adapter = new RecommedAdapter();
        lv_recommended.setAdapter(lv_adapter);
        lv_recommended.setOnItemClickListener(this);
        iv_one = findViewById_(R.id.iv_one);
        iv_two = findViewById_(R.id.iv_two);

        tv_detail = findViewById_(R.id.tv_detail);
        sv_draw = findViewById_(R.id.sv_draw);
        sv_draw.setMinAndMax(0, 100);
        sv_draw.setTypes(getSpiderWebTypes(testResult));
        sv_draw.setInterpolator(new BounceInterpolator());
        v_temp = findViewById_(R.id.v_temp);

        mShareCover = findViewById_(R.id.share_cover);
        ViewUtils.measureView(mShareCover);
        v_temp.getLayoutParams().height = mShareCover.getMeasuredHeight();
        v_temp.getParent().requestLayout();
        height = mShareCover.getMeasuredHeight();
        osv_scroll.setOnScrollChangedListener(this);

        View contrast = findViewById(R.id.vg_contrast);
        ((ViewGroup) contrast.getParent()).removeView(contrast);
        String rgbPath = data.getStringExtra(Constants.EXTRA_CONFIRM_TAKING_EPIDERMIS_PATH);
        ImageLoader.getInstance().displayImage("file://" + rgbPath, iv_one);
        ImageLoader.getInstance().displayImage("file://" + getHealthSkinImagePath(), iv_two);

        mContrast = new BaseDialog(this, R.style.ShadowDialog);
        mContrast.setContentView(contrast);
        mContrast.setCanceledOnTouchOutside(false);
        //d("III", "height " + height);

//        sbd_water.setValues(0, 0);
//        sbd_oil.setValues(100, 100);
//        sbd_whitening.setValues(7, 0);
//        sbd_eastic.setValues(95, 100);
//        sbd_sensitive.setValues(1, 0);
//        sbd_pore.setValues(25, 75);
        setUiDataFromBean(testResult, null);
        requestRecommendPost(getLastThree(testResult), 0);

        testResult.setType(AnalyzeType.DAILY);
        String rId = "" + System.currentTimeMillis();
        testResult.setRid(rId);
        // handleUpdateData(testResult);
        configPlatforms();

        //add by kfir
        if (ReleaseUtils.CAUSE_END_AFTER_SKINTEST) {
            AllKfirManager.getInstance(this).endSkinTest();
        }
        UmengUtils.onEventSkinTest(this, UmengUtils.TYPE_TEST_DAILY);
        requestUploadData();
    }

    private void handleUpdateData(TestSkinReq result) {
        if (result == null) return;
        if (mDailyBean == null) {
            mDailyBean = ReqFactory.buildInterface(this, DailyTestSkin.class);
        }
        BaseCall<TestSkinResp> node = new BaseCall<TestSkinResp>() {

            @Override
            public void call(TestSkinResp resp) {
                // TODO Auto-generated method stub
                if (resp.isRequestSuccess()) {
                    Slog.i("upload test data SUCCESS!!!!!");
                } else {
                    Slog.i("upload test data FAILED!!!!!");
                }
            }
        };
        addCallback(node);
        mDailyBean.dailyTestSkinAsync(result, node);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case TitleView.ID_LEFT:
                finish();
                break;
            case TitleView.ID_RIGHT:
                openShare();
                break;
            case R.id.btn_curr_plan:
                Intent jump_plan = new Intent(this, ActivityMyCarePlan.class);
                startActivity(jump_plan);
                break;
            case R.id.btn_cont_health:
                mContrast.show();
                break;
            case R.id.btn_history:
                startActivity(new Intent(this, ActivityHistory.class));
                break;
            case R.id.btn_close:
                mContrast.dismiss();
                break;
        }
    }

/*    @Override
    public void onBackPressed() {
        if (uploaded) {
            super.onBackPressed();
        } else {

        }
    }*/

    public String getHealthSkinImagePath() {
        String name = MD5.getMD5("rgb_standard");
        File skin = new File(getFilesDir(), name);
        if (!skin.isFile()) {
            InputStream is = null;
            OutputStream os = null;
            try {
                is = getAssets().open("img_rgb_standard.jpg");
                os = new FileOutputStream(skin);

                int len;
                byte[] buff = new byte[1024];
                while ((len = is.read(buff)) != -1) {
                    os.write(buff, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return skin.getAbsolutePath();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private void openShare() {
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
                SHARE_MEDIA.SMS);
//    	WeiXinShareContent mWContent = new WeiXinShareContent();
        mShareCover.setBackgroundResource(R.drawable.bg_testskin);
        mShareCover.setDrawingCacheEnabled(true);
        mShareCover.buildDrawingCache();
        Bitmap map = mShareCover.getDrawingCache();
        Bitmap b = Bitmap.createBitmap(map, 0, 0, map.getWidth(), map.getHeight());
        mShareCover.destroyDrawingCache();
        mShareCover.setDrawingCacheEnabled(false);
        mShareCover.setBackground(null);
        UMImage mimage = new UMImage(this, b);
        mController.setShareImage(mimage);
//    	mController.setAppWebSite("http://121.41.79.23/radar");
        mController.getConfig().setSinaCallbackUrl("http://sns.whalecloud.com/sina2/callback");
        ABFileUtil.saveBitmap2SDAbsolute(PathUtils.SHARE_IMAGE_PATH, b, 100);
//    	mWContent.setShareImage(mimage);
//    	mWContent.setTitle("友盟分享测试");
//    	mWContent.setTargetUrl("http://www.baidu.com");
//    	mWContent.setAppWebSite(null);
//    	mController.setShareMedia(mWContent);
//    	mController.openShare(this, false);
        ShareBoard mBoard = new ShareBoard(this);
        mBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.TOP, 0, 0);
//    	mController.postShare(this, SHARE_MEDIA.WEIXIN, null);
    }

    private void configPlatforms() {
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        addWXPlatform();
    }

    /**
     * @return
     * @功能描述 : 添加微信平台分享
     */
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appId = UmengUtils.WEIXIN_APPID;
        String appSecret = UmengUtils.WEIXIN_SECRET;
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(this, appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }


    private void setUiDataFromBean(TestSkinReq req, TestSkinResp resp) {
        if (req == null) {
            req = new TestSkinReq();
        }
        if (resp == null) {
            resp = new TestSkinResp();
        }

        Resources res = getResources();
        List<int[]> values = new ArrayList<int[]>(2);
        int[] bgValue = new int[sv_draw.getTypes().length];
        for (int i = 0; i < bgValue.length; i++) bgValue[i] = sv_draw.getMax();
        int[] testValue = getValuesFromTestSkinReq(req);// testValue(sv_draw.getTypes().length,
        // sv_draw.getMax());
        values.add(bgValue);
        values.add(testValue);
        // values.add(new int[]{ 60, 90, 50, 50, 50, 45});
//        for (int i = 0; i < sv_draw.getTypes().length; i++)
//            sv_draw.getTypes()[i].setValue(getTextFromValue(testValue[i]));
        List<Paint> paints = new ArrayList<Paint>(2);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(res.getColor(R.color.test_normal_result_value));
        paint.setStyle(Paint.Style.FILL);

        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(0xccffffff);
        paint2.setStyle(Paint.Style.FILL);

        paints.add(paint2);// 这个是背景
        paints.add(paint);

        Paint small = new Paint(Paint.ANTI_ALIAS_FLAG);
        small.setTextAlign(Paint.Align.CENTER);
        small.setTextSize(res.getDimensionPixelSize(R.dimen.test_normal_result_spider_small_text_size));
        small.setColor(res.getColor(R.color.test_normal_result_spider_result_text_color));
        Paint big = new Paint(small);
        big.setFakeBoldText(true);
        big.setTextSize(res.getDimensionPixelSize(R.dimen.test_normal_result_spider_big_text_size));
//        values[0] = result.getWater();
//        values[1] = result.getEastic();
//        values[2] = result.getWhitening();
//        values[3] = result.getPore();
//        values[4] = result.getSensitive();
//        values[5] = result.getOil();
        vg_water.setValue(testValue[0]);
        vg_elastic.setValue(testValue[1]);
        vg_whitening.setValue(testValue[2]);
        vg_pore.setValue(testValue[3]);
        vg_sensitive.setValue(testValue[4]);
        vg_oil.setValue(testValue[5]);

        sv_draw.setResultText(getString(R.string.normal_result_skin_age), small, "" + req.getSkinAge(), big, null, small);
        sv_draw.setValues(values, paints);
        sv_draw.show(/*1500*/);// 1秒内显示完动画

        // 有左边2个圆设置值
        /*sbd_water	 .setValues(resp.getWaterAvg(),		req.getWater());
        sbd_oil		 .setValues(resp.getOilAvg(),		req.getOil());
        sbd_eastic	 .setValues(resp.getElasticAvg(),	req.getEastic());
        sbd_sensitive.setValues(resp.getSensitiveAvg(), req.getSensitive());
        sbd_whitening.setValues(resp.getWhiteningAvg(), req.getWhitening());
        sbd_pore	 .setValues(resp.getPoreAvg(),		req.getPore());*/

        // 获取数值数值最好的2个，显示相应的评语
        SortValue[] vs = getFirstTwo(req);
        String dest1 = rulesHelper.getDescription(vs[0].name, vs[0].value);
        String dest2 = rulesHelper.getDescription(vs[1].name, vs[1].value);
        d("III_view", "0 " + vs[0].toString() + ", " + dest1 + ", 1 " + vs[1].toString() + ", " + dest2);
        if (dest1 != null && dest2 != null) {// 有2项好的
            tv_detail.setText(getString(R.string.test_eval_skin, dest1, dest2));
        } else if (dest1 != null || dest2 != null) {// 有1项好的
            dest1 = dest1 != null ? dest1 : dest2;
            dest2 = getString(R.string.test_eval_supp);
            tv_detail.setText(getString(R.string.test_eval_skin, dest1, dest2));
        } else {// 没有好的
            tv_detail.setText(getString(R.string.test_eval_low));
        }
    }

    private SortValue[] getFirstTwo(TestSkinReq data) {
        if (data == null) {
            data = new TestSkinReq();
        }
        SortValue[] vs = new SortValue[6];
        vs[0] = new SortValue("Water", data.getWater());
        vs[1] = new SortValue("Oil", data.getOil());
        vs[2] = new SortValue("Elastic", data.getEastic());
        vs[3] = new SortValue("Sensitive", data.getSensitive());
        vs[4] = new SortValue("Whitening", data.getWhitening());
        vs[5] = new SortValue("Pore", data.getPore());
        Arrays.sort(vs, new Comparator<SortValue>() {
            @Override
            public int compare(SortValue lhs, SortValue rhs) {
                return lhs.compareTo(rhs);
            }
        });
        for (SortValue v : vs) {
            //  android.util.Log.i("III", " " + v.name + " " + v.value);
        }
        return new SortValue[]{vs[vs.length - 1], vs[vs.length - 2]};
    }

    private SortValue[] getLastThree(TestSkinReq data) {
        if (data == null) {
            data = new TestSkinReq();
        }
        SortValue[] vs = new SortValue[6];
        vs[0] = new SortValue("Water", data.getWater());
        vs[1] = new SortValue("Oil", data.getOil());
        vs[2] = new SortValue("Elastic", data.getEastic());
        vs[3] = new SortValue("Sensitive", data.getSensitive());
        vs[4] = new SortValue("Whitening", data.getWhitening());
        vs[5] = new SortValue("Pore", data.getPore());
        Arrays.sort(vs, new Comparator<SortValue>() {
            @Override
            public int compare(SortValue lhs, SortValue rhs) {
                return lhs.compareTo(rhs);
            }
        });
        for (SortValue v : vs) {
            android.util.Log.i("III", " " + v.name + " " + v.value);
        }
        return new SortValue[]{vs[0], vs[1], vs[2]};
    }

    private SpiderWebType[] getSpiderWebTypes(TestSkinReq req) {
        if (req == null) {
            req = new DailyTestSkin.TestSkinReq();
        }
        Resources res = getResources();
        int nameColor = res.getColor(R.color.spiderWeb_type_text_color);
        SpiderWebType[] types = new SpiderWebType[6];
        types[0] = new SpiderWebView.SpiderWebType(getString(R.string.test_water), nameColor,
                res.getColor(R.color.test_normal_result_water_text_color));
        types[0].setValue(rulesHelper.getEvaluation("Water", req.getWater()));
        types[1] = new SpiderWebView.SpiderWebType(getString(R.string.test_eastic),
                nameColor,
                res.getColor(R.color.test_normal_result_eastic_text_color));
        types[1].setValue(rulesHelper.getEvaluation("Elastic", req.getEastic()));
        types[2] = new SpiderWebView.SpiderWebType(getString(R.string.test_whitening),
                nameColor,
                res.getColor(R.color.test_normal_result_whitening_text_color));
        types[2].setValue(rulesHelper.getEvaluation("Whitening", req.getWhitening()));
        types[3] = new SpiderWebView.SpiderWebType(getString(R.string.test_pore), nameColor,
                res.getColor(R.color.test_normal_result_pore_text_color));
        types[3].setValue(rulesHelper.getEvaluation("Pore", req.getPore()));
        types[4] = new SpiderWebView.SpiderWebType(getString(R.string.test_sensitive),
                nameColor,
                res.getColor(R.color.test_normal_result_sensitive_text_color));
        types[4].setValue(rulesHelper.getEvaluation("Sensitive", req.getSensitive()));
        types[5] = new SpiderWebView.SpiderWebType(getString(R.string.test_oil), nameColor,
                res.getColor(R.color.test_normal_result_oil_text_color));
        types[5].setValue(rulesHelper.getEvaluation("Oil", req.getOil()));
        return types;
    }

    private int[] getValuesFromTestSkinReq(DailyTestSkin.TestSkinReq result) {
        int[] values = new int[6];
        values[0] = result.getWater();
        values[1] = result.getEastic();
        values[2] = result.getWhitening();
        values[3] = result.getPore();
        values[4] = result.getSensitive();
        values[5] = result.getOil();
        return values;
    }

    private void requestRecommendPost(final SortValue[] sv, final int index) {

        if (sv == null || index >= sv.length) {
            d("III_request", "data error index " + index);
            return;
        }
        GetPostList gpl = ReqFactory.buildInterface(this, GetPostList.class);
        PostsTestReq req = new PostsTestReq();
        req.setPostParam(KEYWORDS.get(sv[index].name));
        d("III_request", "请求推荐贴 " + index + " " + sv[index].name + ", params " + array2String((Object[]) KEYWORDS.get(sv[index].name)));
        req.setStartNo(1);
        gpl.recommendPostsByTestAsync(req, new BaseCall<TopicPostListResp>() {
            @Override
            public void call(TopicPostListResp resp) {

                if (resp != null && resp.isRequestSuccess()) {
                    List<MPostResp> posts = resp.getPostLists();
                    if (posts != null && posts.size() > 0) {
                        Collections.sort(posts, ActivityDailyResult.this);
                        for (int i = 0; i < posts.size(); i++) {
                            MPostResp p = posts.get(i);
                            d("III", "i " + i + ", " + p.getLocalPostId() + " PostViewCount " + p.getPostViewCount());
                        }
                        MPostResp post = posts.get(posts.size() - 1);
                        if (!lv_adapter.getList().contains(post)) {
                            lv_adapter.addItem(post);
                            Slog.f("Filelog: requestRecommendPost postId: " + post.getId());
                        } else {
                            d("III_request", "重复的不加");
                        }
                    }
                    requestRecommendPost(sv, index + 1);
                } else {
                    d("III_request", "请求推荐失败 " + (resp != null ? resp.getMessage() : null));
                }
            }
        });
    }

    private void requestUploadData() {

        DailyTestSkin dts = ReqFactory.buildInterface(this, DailyTestSkin.class);
        BaseCall<TestSkinResp> call = new BaseCall<TestSkinResp>() {
            @Override
            public void call(TestSkinResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    d("III_request", "upload success.");
                } else {
                    d("III_request", "upload fail msg->" + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        //addCallback(call); 这里就不add到Callback中管理了，因为无论如何都要上传的。
        TestSkinReq req = testResult;
        d("III_request", "update test datas." + JsonUtils.toJson(req));
        req.setType(AnalyzeType.DAILY);
        dts.dailyTestSkinAsync(req, call);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ActivityPostDetail.class);
        // 貌似ChildFragment主动回调不到onActivityResult
        // id传到详情界面
        // intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, item.getId());
        intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, (Serializable) lv_adapter.getItem(position));
        startActivity(intent);
    }

    private int firstY;

    @Override
    public void onScrollChanged(int x, int y, int oldx, int oldy) {

        if (oldy == 0 && firstY == 0) {
            firstY = y;
        }
        y -= firstY;
        // d("III_scroll", "y " + y + ", oldy " + oldy + ", height " + vg_option.getMeasuredHeight());
        if (y <= height && y >= 0) {
            float offset = height - y;
            // d("III_scroll", "y " + y + ", alpha " + (offset / height));
            ViewCompat.setAlpha(mShareCover, offset / height);
        }

        if (y >= vg_option.getMeasuredHeight() && vg_option.getVisibility() != View.GONE) {
            vg_option.setVisibility(View.GONE);
            // d("III_scroll", "setGone");
        } else if (y < vg_option.getMeasuredHeight() && vg_option.getVisibility() != View.VISIBLE) {
            vg_option.setVisibility(View.VISIBLE);
            // d("III_scroll", "setVisible");
        }
    }

    @Override
    public void scrollBottom() {

    }

    class SortValue implements Comparable<SortValue> {
        String name;
        int value;

        public SortValue(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public int compareTo(SortValue another) {
            if (another == null) {
                return -1;
            }
            return this.value < another.value ? -1 : 1;
        }

        @Override
        public String toString() {
            return "{ '" + name + "' : " + value + "}";
        }
    }

    @Override
    public int compare(MPostResp lhs, MPostResp rhs) {
        if (lhs.getPostViewCount() > rhs.getPostViewCount()) {
            return 1;
        }
        if (lhs.getPostViewCount() < rhs.getPostViewCount()) {
            return -1;
        }
        return 0;
    }

    class DailyPointer {

        public static final int MIN = 0;
        public static final int MAX = 100;
        private static final float INTERVAL = 17 * 2;

        private SkinRuleDiff[] rules;
        private TextView tv_value;
        private ImageView iv_pointer;

        private int value;

        public DailyPointer(View v, SkinRuleDiff[] rules) {
            tv_value = (TextView) v.findViewById(R.id.tv_value);
            iv_pointer = (ImageView) v.findViewById(R.id.iv_pointer);
            this.rules = rules;
        }

        public void setValue(int val) {
            tv_value.setText(val + "");
            ViewCompat.setRotation(iv_pointer, (360f - INTERVAL) / (MAX - MIN) * val);
            for (int i = 0; i < rules.length; i++) {
                if (val >= rules[i].getMin() && val <= rules[i].getMax()) {
                    iv_pointer.getDrawable().setLevel(i);
                    break;
                }
            }
            this.value = val;
        }

        public int getValue() {
            return value;
        }
    }

    class RecommedAdapter extends LinearLayoutForListView.LinearLayoutForListViewAdapter {

        private List<MPostResp> list;

        public RecommedAdapter() {
            list = new ArrayList<MPostResp>(3);
        }

        public List<MPostResp> getList() {
            return list;
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public MPostResp getItem(int position) {
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
                convertView = getLayoutInflater().inflate(R.layout.item_daily_post_recommend, null);
                convertView.setTag(vh = new ViewHolder(convertView));
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            MPostResp item = getItem(position);
            if (item == null) return convertView;

            String url = TopicHelper.wrappeImagePath(
                    item.getThumbURL() != null &&
                            item.getThumbURL().size() > 0 &&
                            item.getThumbURL().get(0) != null &&
                            !"".equals(item.getThumbURL().get(0).trim()) ?
                            item.getThumbURL().get(0) :
                            item.getUserHeadIcon());
            String topic = (item.getTopicTitle() != null && !"".equals(item.getTopicTitle().trim()) ?
                    getResources().getString(R.string.topic_prefix, item.getTopicTitle()) : "");
            String title = topic + (item.getPostTitle() != null ? item.getPostTitle().trim() : "unknown title");

            ImageLoader.getInstance().displayImage(url, vh.iv_image);
            Spannable s = new SpannableString(title);
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.test_primary)), 0, topic.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            vh.tv_title.setText(s);
            return convertView;
        }

        @Override
        public void addItem(Object item) {
            list.add((MPostResp) item);
            super.addItem(item);
        }
    }

    class ViewHolder {

        ImageView iv_image;
        TextView tv_title;

        public ViewHolder(View itemView) {
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
