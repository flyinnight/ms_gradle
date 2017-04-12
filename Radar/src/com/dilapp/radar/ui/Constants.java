package com.dilapp.radar.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.AnalyzeType;
import com.dilapp.radar.util.MineInfoUtils;

public class Constants {

    // 测试模块不需要设备，走通流程
    public final static boolean TEST_PREVIEW = false;
    // 社区超级管理员false
    public final static boolean TOPIC_SUPER_ADNIMISTRATOR = false;
    // 压缩帖子图片
    public final static boolean COMPRESS_POST_IMAGE = true;

    public final static int SKIN_TEST_MIN_SDK = 18;
    // 请从小到大按顺序写
    public final static long[] TOPIC_AD_IDS = new long[]{1};

    public final static int PLAN_STEPS_MAX = 50;

    // 填写从短信SDK应用后台注册得到的APPKEY
    public static final String APPKEY = "9d79d291f123";
    // 填写从短信SDK应用后台注册得到的APPSECRET
    public static final String APPSECRET = "e828b78f05e6f4ad124db63c6aaa812a";

    public static final SparseArray<String> PLAN_EFFECTS;
    public static final SparseArray<String> PLAN_PARTS;

    static {
        // 这个是按照Key的Has值来排序的，拿出来是不一定是输入时的顺序
        PLAN_EFFECTS = new SparseArray<String>(12);
        PLAN_EFFECTS.put(R.string.plan_effect_oil, "oil");
        PLAN_EFFECTS.put(R.string.plan_effect_acne, "acne");
        PLAN_EFFECTS.put(R.string.plan_effect_spot, "spot");
        PLAN_EFFECTS.put(R.string.plan_effect_pore, "pore");
        PLAN_EFFECTS.put(R.string.plan_effect_bask, "bask");
        PLAN_EFFECTS.put(R.string.plan_effect_tight, "tight");
        PLAN_EFFECTS.put(R.string.plan_effect_water, "water");
        PLAN_EFFECTS.put(R.string.plan_effect_other, "other");
        PLAN_EFFECTS.put(R.string.plan_effect_black, "black");
        PLAN_EFFECTS.put(R.string.plan_effect_repair, "repair");
        PLAN_EFFECTS.put(R.string.plan_effect_whitening, "whitening");
        PLAN_EFFECTS.put(R.string.plan_effect_maintenance, "maintenance");

        PLAN_PARTS = new SparseArray<String>(4);
        PLAN_PARTS.put(R.string.plan_part_eye, "eye");
        PLAN_PARTS.put(R.string.plan_part_nose, "nose");
        PLAN_PARTS.put(R.string.plan_part_hand, "hand");
        PLAN_PARTS.put(R.string.plan_part_cheek, "cheek");
    }

    /**
     * {@link com.dilapp.radar.ui.comm.ActivityInputHistory} String
     */
    public final static String EXTRA_INPUT_HISTORY_NAME = "input_history_name::-";

    /**
     * {@link com.dilapp.radar.ui.comm.ActivityInputHistory} String
     */
    public final static String EXTRA_INPUT_HISTORY_TEXT = "input_history_TEXT::-";

    /**
     * {@link com.dilapp.radar.ui.comm.ActivityInputHistory} String
     */
    public final static String EXTRA_INPUT_HISTORY_HINT = "input_history_hint::-";

    /**
     * {@link com.dilapp.radar.ui.comm.ActivityInputHistory} int
     */
    public final static String EXTRA_INPUT_HISTORY_SIZE = "input_history_size::-";

    /**
     * {@link com.dilapp.radar.ui.comm.ActivityInputHistory} String
     */
    public final static String RESULT_INPUT_HISTORY_TEXT = "input_history_text::result";


    /**
     * 话题ID
     */
    public final static String EXTRA_TOPIC_DETAIL_ID = "topic_detail_ID::";// long

    public final static String EXTRA_POST_DETAIL_CONTENT = "post_content::";// MPostListResp
    /**
     * 是否Banner贴，optional
     */
    public final static String EXTRA_POST_DETAIL_BANNER = "post_banner_priority::i";// int
    /**
     * 是否精选贴，optional
     */
    public final static String EXTRA_POST_DETAIL_HIGHLIGHTS = "post_highlights::b";// boolean

    /**
     * 详情贴删除时返回的ID
     */
    public final static String RESULT_POST_DETAIL_DELETE_ID = "post_delete::b";// long

    /**
     * {@link #EXTRA_SEND_COMMENT_FLAG_POST}
     * {@link #EXTRA_SEND_COMMENT_FLAG_PLAN}
     *
     */
    public final static String EXTRA_SEND_COMMENT_FLAG = "post_comment_flag::";
    public final static int EXTRA_SEND_COMMENT_FLAG_POST = 0;
    public final static int EXTRA_SEND_COMMENT_FLAG_PLAN = 1;

    public final static String EXTRA_SEND_COMMENT_TOPIC_ID = "topic_detail_ID::";// long
    public final static String EXTRA_SEND_COMMENT_PARENT_POST_ID = "topic_parent_id:";
    public final static String EXTRA_SEND_COMMENT_RESULT = "comment_result::";

    //add by kfir
    public final static String EXTRA_EDIT_TOP_CONTENT = "edit_top_content";

    /**
     * {@link #EXTRA_EDIT_POST_IS_MODIFY} 为true, 此参数不需要填，为false，次参数必填
     * 帖子所属的话题的ID MTopicResp
     */
    public final static String EXTRA_EDIT_POST_TOPIC = "topic_content::";

    /**
     * 发送广播更新数据
     */
    public final static String FOUND_TOPIC_COLLECT = "found_topic_collect";

    /**
     * 修改帖子 boolean
     */
    public final static String EXTRA_EDIT_POST_IS_MODIFY = "topic_is_modify::";
    /**
     * {@link #EXTRA_EDIT_POST_IS_MODIFY } 为true ,此参数必选, 为false, 此参数不需要填
     * 需要修改的帖子 MPostResp
     */
    public final static String EXTRA_EDIT_POST_MODIFY_POST = "topic_modify_post::";
    /**
     * 是否修改了护肤方案 boolean
     */
    public final static String EXTRA_EDIT_POST_IS_PLAN = "topic_modify_plan::";
    /**
     * 护肤方案内容 PostReleaseReq
     */
    public final static String EXTRA_EDIT_POST_PLAN_CONTENT = "post_plan_content::";

    /**
     * 预设值的帖子内容 {@link com.dilapp.radar.ui.topic.PresetPostModel} 选填
     */
    public final static String EXTRA_EDIT_POST_PRESET_CONTENT = "post_preset_content:-";

    /**
     * 告诉ReorderPostPre界面，你是否是调到前台的 boolean
     */
    public final static String EXTRA_EDIT_POST_PRE_REORDER_TO_FRONT = "reorder_to_front::";

    // * 代表可选
    /**
     * ActivityConfirmTaking 需要的参数
     */
    public final static String EXTRA_CONFIRM_TAKING_EPIDERMIS_PATH = "epidermis_path";// String
    public final static String EXTRA_CONFIRM_TAKING_GENUINE_PATH = "genuine_path";// String
    /**
     * 已选择的部位
     *
     * @{link SKIN_NO }
     * @{link SKIN_ERROR }
     * @{link SKIN_INPUT_NOT }
     * @{link SKIN_OUT_FOCUS }
     * @{link SKIN_OK }
     */
    public final static String EXTRA_CONFIRM_TAKING_IS_SKIN_IMG = "is_skin_image";// int
    /**
     * 不是皮肤
     */
    // SKIN_OK SKIN_IMAGE_ERROR SKIN_INPUT_NOT_SKIN SKIN_OUT_FOCUS
    public final static int SKIN_ERROR = -1;
    public final static int SKIN_INPUT_NOT = -2;
    public final static int SKIN_OUT_FOCUS = -3;
    public final static int SKIN_OK = 1;

    /**
     * String to Integer
     *
     * @param msg
     * @return
     */
    public static int SKIN_STRING2INTEGER(String msg) {
        if (msg == null) {
            return SKIN_ERROR;
        }
        if (msg.contains("SKIN_OK")) {
            return SKIN_OK;
        } else if (msg.contains("SKIN_INPUT_NOT_SKIN")) {
            return SKIN_INPUT_NOT;
        } else if (msg.contains("SKIN_OUT_FOCUS")) {
            return SKIN_OUT_FOCUS;
        } else {
            return SKIN_ERROR;
        }
    }

    /**
     * 测试用参数 Boolean
     */
    public final static String EXTRA_CHOOSE_PART_IS_TEST = "choose_part_is_test";

    /**
     * 比如脸部皮肤图像
     */
    public final static String EXTRA_SKIN_TAKING_TEXT_INFO = "text_info--";
    /**
     * 已选择的部位
     * <p/>
     * {@link #PART_FOREHEAD }
     * {@link #PART_EYE }
     * {@link #PART_NOSE }
     * {@link #PART_CHEEK }
     * {@link #PART_HAND }
     */
    public final static String EXTRA_SKIN_TAKING_CHOOSED_PART = "choosed_part";// *
    public final static int PART_FOREHEAD = R.string.normal_forehead;
    public final static int PART_EYE = R.string.normal_eye;
    public final static int PART_NOSE = R.string.normal_nose;
    public final static int PART_CHEEK = R.string.normal_cheek;
    public final static int PART_HAND = R.string.normal_hand;
    public final static String EXTRA_PRODUCT_RESULT_LOOK = "result_look,look";// boolean

    public static int getPartByStringID(int id) {
        int result = 0;
        switch (id) {
            case Constants.PART_FOREHEAD:
                result = AnalyzeType.FOREHEAD;
                break;
            case Constants.PART_CHEEK:
                result = AnalyzeType.CHEEK;
                break;
            case Constants.PART_EYE:
                result = AnalyzeType.EYE;
                break;
            case Constants.PART_HAND:
                result = AnalyzeType.HAND;
                break;
            case Constants.PART_NOSE:
                result = AnalyzeType.NOSE;
                break;
        }
        return result;
    }

    public static int getStringIDByPart(int part) {
        int result = 0;
        switch (part) {
            case AnalyzeType.FOREHEAD:
                result = Constants.PART_FOREHEAD;
                break;
            case AnalyzeType.CHEEK:
                result = Constants.PART_CHEEK;
                break;
            case AnalyzeType.EYE:
                result = Constants.PART_EYE;
                break;
            case AnalyzeType.HAND:
                result = Constants.PART_HAND;
                break;
            case AnalyzeType.NOSE:
                result = Constants.PART_NOSE;
                break;
        }
        return result;
    }

    /**
     * 肤质测试默认的部位
     */
    public final static int[] SKIN_TEST_PARTS = new int[]{Constants.PART_FOREHEAD, Constants.PART_CHEEK};
    /**
     * String Array
     */
    public final static String EXTRA_SKIN_TAKING_RESULT_BUTTON_TEXTS = "result_button_texts";// *
    /**
     * Class Name (String) Array
     */
    public final static String EXTRA_SKIN_TAKING_RESULT_ACTIVITIES = "result_activities";//

    public final static String EXTRA_SKIN_TAKING_CURRENT_INDEX = "Current_Activities_Index";
    public final static String EXTRA_SKIN_TAKING_LOGOS = "LOGO_PPPPP.";// drawableRes Array

    private final static String EXTRA_TAKING_RESULT = "taking_result";

    public final static String EXTRA_TAKING_RESULT(int partId) {
        return EXTRA_TAKING_RESULT + ":" + partId;
    }

    /**
     * 护肤品测试前的文件保存路径
     *
     * @param context
     * @return
     */
    public final static String PRODUCT_TEST_TEMP_RESULT_PATH(Context context) {
        return context.getCacheDir().getAbsoluteFile()
                + "/tmp_product_result.ser";
    }

    /**
     * 肤质测试结果
     *
     * @param context
     * @return
     */
    public final static String SKIN_TEST_RESULT_PATH(Context context, int part) {
        return context.getFilesDir().getAbsolutePath() + "/skin_result_" + part + ".ser";
    }

    /**
     * boolean 是否修改话题
     */
    public final static String EXTRA_TOPIC_EDIT_IS_MODIFY = "topic_edit_is_modify--?";

    /**
     * MTopicResp 要修改的话题的内容
     */
    public final static String EXTRA_TOPIC_EDIT_CONTENT = "topic_edit_content--!";

    /**
     * String
     */
    public final static String EXTRA_USER_OTHERS_USER_ID = "userId";
    public final static String EXTRA_USER_OTHERS_USER_CONTENT = "user_content";

    /**
     * int
     */
    public final static String EXTRA_FOCUS_FANS_PAGE = "focus_fans--";

    /**
     * 获取性别的文字
     * @param context
     * @param gender
     * @return
     */
    public final static String getGenderString(Context context, int gender) {
        return context.getString(
                gender == 2 ? R.string.woman :
                gender == 1 ? R.string.man : R.string.secret);
    }

    /**
     * 获取皮肤类型的文字描叙
     * @param context
     * @param type
     * @return
     */
    public final static String getSkinTypeString(Context context, int type) {
        return context.getString(
                type == 2 ? R.string.test_oil_skin :
                        type == 1 ? R.string.test_dry_skin : R.string.test_middle_skin);
    }

    /**
     * 获取地址的String
     * @param context
     * @param addressID
     * @return
     */
    public final static String getAddressString(Context context, String addressID) {

        if (!TextUtils.isEmpty(addressID)) {
            return MineInfoUtils.getStringByLocationID(context, addressID);
        } else {
            return context.getString(R.string.unknown);
        }
    }
}
