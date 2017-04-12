package com.dilapp.radar.ui.topic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.BuildConfig;
import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.FollowUser;
import com.dilapp.radar.domain.FollowUser.FollowUserReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.UpdateGetUser.GetUserResp;
import com.dilapp.radar.textbuilder.BBSDescribeItem;
import com.dilapp.radar.textbuilder.BBSLinkManager;
import com.dilapp.radar.textbuilder.BBSTextBuilder;
import com.dilapp.radar.textbuilder.impl.BBSTextBuilderImpl;
import com.dilapp.radar.textbuilder.utils.JsonUtils;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.Permissions;
import com.dilapp.radar.ui.mine.ActivityOthers;
import com.dilapp.radar.util.AndroidBugsSolution;
import com.dilapp.radar.view.AnimationListenerAdapter;
import com.dilapp.radar.view.CustomScrollView;
import com.dilapp.radar.view.PullToRefreshCustomScrollView;
import com.dilapp.radar.viewbuilder.BBSViewBuilder;
import com.dilapp.radar.viewbuilder.BBSViewGetter;
import com.dilapp.radar.viewbuilder.impl.BBSViewBuilderImpl;
import com.dilapp.radar.widget.ButtonsDialog;
import com.dilapp.radar.widget.ButtonsDialog.ButtonGroup;
import com.dilapp.radar.widget.ButtonsDialog.ButtonItem;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.lenovo.text.bbsbuild.BBSViewGetterImpl;
import com.lenovo.text.span.ActionSpan;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.dilapp.radar.textbuilder.utils.L.d;
import static com.dilapp.radar.textbuilder.utils.L.w;

/**
 * Created by husj1 on 2015/7/6.
 */
public abstract class ActivityPostBase extends BaseFragmentActivity implements
        OnClickListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        EmojiconGridFragment.OnEmojiconClickedListener,
        AndroidBugsSolution.OnKeyboardListener,
        CustomScrollView.OnScrollChangedListener,
        PullToRefreshBase.OnRefreshListener2<CustomScrollView>,
        View.OnLongClickListener {
    protected Context context;
    protected final static int REQ_SEND_COMMENT = 10;
    protected final static int REQ_EDIT_POST = 20;
    protected static int SOFT_KEYBOARD_DELAYED_TIME = 80;

    protected final static int REPLY_TO_COMMENT = R.id.vg_comment;// 回复评论
    protected final static int REPLY_TO_REPLY = R.id.vg_reply;// 回复回复 xxx@xxx


    /**
     * 用于获取 xxx@xxx 的这段文字。
     * @param userSpan
     * @param toUserSpan
     * @param data
     * @return
     */
    public static CharSequence getReplyChars(ClickableSpan userSpan, ClickableSpan toUserSpan, PostBaseEntity data) {
        // Sp

        if (data == null) {
            return null;
        }
        String username = data.getNickname() == null ? "unknown" : data.getNickname();
        String toUsername = data.getToNickname() == null ? "unknown" : data.getToNickname();
        String content = data.getContent() == null ? "unknown" : data.getContent();
        StringBuilder sb = new StringBuilder();
        sb.append(username);
        if (data.getToNickname() != null && !"".equals(data.getToNickname().trim())) {
            sb.append("@").append(toUsername);
        }
        sb.append("：").append(content);
        Spannable sp = new SpannableString(sb.toString());
        // sp.subSequence(0, username.length());
        sp.setSpan(userSpan, 0, username.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (data.getToNickname() != null && !"".equals(data.getToNickname().trim())) {
            int start = username.length() + 1;
            sp.setSpan(toUserSpan, start, start + data.getToNickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sp;
    }

    protected PostBaseAdapter mAdapter;

    protected Animation inToTop, outToTop, inOption, outOption, inEditMsg,
            outEditMsg, fadeIn, fadeOut, fromBottonIn, fromBottomOut;

    /* @@@@@@@@@@@@@@@@@@@@@@@@
    protected TitleView mTitle;*/
    protected BBSViewBuilder mBBSViewBuilder;
    protected BBSTextBuilder mBBSTextBuilder;
    protected BBSViewGetter mBBSViewGetter;
    protected ButtonsDialog mOptionDialog;
    protected ButtonsDialog mDeleteDialog;

    protected View vg_loading;
    protected View btn_to_top;
    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    protected TextView tv_topic;*/
    protected ViewGroup vg_content;
    protected PullToRefreshCustomScrollView osv_scroll;
    protected TextView tv_title, tv_right;
    protected ImageView iv_header;
    protected TextView tv_nickname;
    protected TextView tv_gender;
    protected TextView tv_level;
    protected TextView tv_datetime;
    protected TextView tv_like;
    protected TextView tv_reply;
    protected Button btn_focus;
    protected ViewGroup post_container;
    protected TextView tv_total_comment;
    protected View v_option_dis;
//    protected View v_sofa_dis;

    protected ViewGroup vg_option;
    protected TextView tv_is_collection;
    protected TextView tv_is_like;
    protected ViewGroup vg_like;
    protected ViewGroup vg_collection;

    protected ViewGroup vg_edit_msg;
    protected EditText et_message;
    protected ImageButton ibtn_switch;
    protected View btn_send;
    protected EmojiconsFragment fragmentEmoji;

    protected TextView tv_sofa;
    protected View vg_comment_end;

    protected Button btn_edit;
    protected Button btn_delete;
    // add by kfir
    protected Button btn_top;
    protected Button btn_banner;
    protected RelativeLayout mHeadLayout;

    protected ViewGroup vg_comments;

    protected InputMethodManager imm;

    protected int flag;
    protected int scrollY;
    protected long topicID;
    protected long postID;
    protected PostBaseEntity postMain;// 主贴
    protected int currPage = 1;
    protected int totalPage = -1;
    protected boolean addBrowseCount;
    // protected LinkedList<MPostResp> datas = new LinkedList<MPostResp>();

    private boolean isScrollReply = true;// 界面一滚动，评论就会消失，这个值是去除误操作 TODO 暂时没卵用

    protected DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
            .displayer(new FadeInBitmapDisplayer(200))
            .showImageForEmptyUri(R.drawable.img_default_head)
            .showImageOnLoading(R.drawable.img_default_head)
            .showImageOnFail(R.drawable.img_default_head).build();


    private BroadcastReceiver postReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!getReleasedFilter().equals(intent.getAction())) return;

            PostBaseEntity resp = mAdapter.from(intent.getSerializableExtra("RespData"));

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_post_detail);
    }

    protected void initView() {
        ensureConverter();
        AndroidBugsSolution.assistActivity(this, this);
        context = getApplicationContext();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Animation inTran = AnimationUtils.loadAnimation(context,
                R.anim.in_from_bottom);
        // new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
        // Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f,
        // Animation.RELATIVE_TO_SELF, 0f);
        // inTran.setDuration(300);
        Animation outTran = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                0f, Animation.RELATIVE_TO_PARENT, 1f);// AnimationUtils.loadAnimation(context,
        // R.anim.out_from_left);
        outTran.setDuration(300);
        Animation inFade = AnimationUtils.loadAnimation(context,
                android.R.anim.fade_in);
        Animation outFade = AnimationUtils.loadAnimation(context,
                android.R.anim.fade_out);
        // AnimationSet inset = new AnimationSet(false);
        // inset.addAnimation(inTran);
        // inset.addAnimation(inFade);
        // inset.setDuration(300);
        AnimationSet outset = new AnimationSet(false);
        outset.addAnimation(outTran);
        outset.addAnimation(outFade);
        outset.setDuration(300);

        inToTop = inFade;
        outToTop = outFade;
        inOption = inFade;
        outOption = outFade;
        inEditMsg = inFade;
        outEditMsg = outset;
        inEditMsg.setDuration(0);
        fadeIn = inFade;
        fadeOut = outFade;
        fromBottonIn = inTran;
        fromBottomOut = AnimationUtils.loadAnimation(context, R.anim.out_from_bottom);
        ;

        Intent data = getIntent();

        BBSLinkManager.registerLink("action", ActionSpan.class);
        /* @@@@@@@@@@@@@@@@@@@@@@@@
        mTitle = new TitleView(context, findViewById(TitleView.ID_TITLE));
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		tv_topic = findViewById_(R.id.tv_topic);*/
        vg_content = findViewById_(R.id.vg_content);
        vg_loading = findViewById_(R.id.vg_loading);
        btn_to_top = findViewById_(R.id.btn_to_top);
        osv_scroll = findViewById_(R.id.osv_scroll);
        tv_title = findViewById_(R.id.tv_title);
        iv_header = findViewById_(R.id.iv_header);
        tv_nickname = findViewById_(R.id.tv_nickname);
        tv_gender = findViewById_(R.id.tv_gender);
        tv_level = findViewById_(R.id.tv_level);
        tv_datetime = findViewById_(R.id.tv_datetime);
        tv_like = findViewById_(R.id.tv_like);
        tv_reply = findViewById_(R.id.tv_reply);
        btn_focus = findViewById_(R.id.btn_focus);
        vg_option = findViewById_(R.id.vg_option);
        tv_is_collection = findViewById_(R.id.tv_is_collection);
        tv_is_like = findViewById_(R.id.tv_is_like);
        vg_like = findViewById_(R.id.vg_like);
        vg_collection = findViewById_(R.id.vg_collection);
        post_container = findViewById_(R.id.post_container);
        tv_total_comment = findViewById_(R.id.tv_total_comment);
        v_option_dis = findViewById_(R.id.v_option_dis);
//        v_sofa_dis = findViewById_(R.id.v_sofa_dis);
        // ((AnimationDrawable)((ImageView)vg_loading).getDrawable()).start();

        vg_edit_msg = findViewById_(R.id.vg_edit_msg);
        et_message = findViewById_(R.id.et_message);
        ibtn_switch = findViewById_(R.id.ibtn_switch);
        btn_send = findViewById_(R.id.btn_send);
        fragmentEmoji = (EmojiconsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.emojicons);
        tv_sofa = findViewById_(R.id.tv_sofa);
        vg_comment_end = findViewById_(R.id.vg_comment_end);

        btn_edit = findViewById_(R.id.btn_edit);
        btn_delete = findViewById_(R.id.btn_delete);

        // add by kfir
        btn_top = findViewById_(R.id.btn_top);
        btn_banner = findViewById_(R.id.btn_banner);
        mHeadLayout = findViewById_(R.id.user_head);

        vg_comments = findViewById_(R.id.vg_comments);
        getSupportFragmentManager().beginTransaction().hide(fragmentEmoji).commit();
        osv_scroll.getRefreshableView().setOverScrollMode(
                CustomScrollView.OVER_SCROLL_NEVER);
        osv_scroll.getRefreshableView().setOnScrollChangedListener(this);
        osv_scroll.getRefreshableView().setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (vg_edit_msg.getVisibility() == View.VISIBLE
                                && isScrollReply) {
                            isScrollReply = true;
                            hideReply();
                        } else if (imm.isActive()) {
                            setSoftKeyboardVisiable(false);
                        }
                        return false;
                    }
                });
        btn_to_top.bringToFront();
        osv_scroll.setOnRefreshListener(this);

        // com.dilapp.radar.util.ViewUtils.measureView(post_container);
        // d("III", "container width " + post_container.getMeasuredWidth());
        mBBSViewGetter = new BBSViewGetterImpl(this, getLayoutInflater());
        mBBSTextBuilder = new BBSTextBuilderImpl("");
        mBBSViewBuilder = new BBSViewBuilderImpl(this,
                BBSViewBuilder.MODE_NORMAL, mBBSTextBuilder, mBBSViewGetter);
        mBBSViewBuilder.setDividerDrawableRes(R.drawable.divider_transparent);
        post_container.addView(mBBSViewBuilder.getContainer(),
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        // test();
        postMain = mAdapter.from(data.getSerializableExtra(Constants.EXTRA_POST_DETAIL_CONTENT));
        postID = postMain.getId();
        setPreview(postMain.getLocalId() != 0 && postMain.getId() == 0);

        vg_option.setVisibility(View.GONE);
        vg_loading.setVisibility(View.VISIBLE);
        osv_scroll.setVisibility(View.GONE);

        View dialog = findViewById_(R.id.vg_delete);
        ((ViewGroup) dialog.getParent()).removeView(dialog);
        mOptionDialog = new ButtonsDialog(this);
        // mOptionDialog.setGroups(getOtherButtonGroups());
        mOptionDialog.setContentView(dialog);
        mOptionDialog.setWidthFullScreen();
        dialog.setVisibility(View.VISIBLE);

        View enterDialog = findViewById_(R.id.vg_delete_enter);
        ((ViewGroup) enterDialog.getParent()).removeView(enterDialog);
        mDeleteDialog = new ButtonsDialog(this);
        // mDeleteDialog.setGroups(getOtherButtonGroups());
        mDeleteDialog.setContentView(enterDialog);
        mDeleteDialog.setWidthFullScreen();
        enterDialog.setVisibility(View.VISIBLE);

        boolean highlights = data.getBooleanExtra(
                Constants.EXTRA_POST_DETAIL_HIGHLIGHTS, false);
        btn_top.setTag(highlights);
        if (highlights) {
            btn_top.setText(R.string.edit_post_untop);
        }
        int banner = data.getIntExtra(Constants.EXTRA_POST_DETAIL_BANNER, 0);
        btn_banner.setTag(banner);
        if (banner > 0) {
            btn_banner.setText(R.string.edit_post_unbanner);
        }
        /* @@@@@@@@@@@@@@@@@@
        requestData(postID, currPage, true, GetPostList.GET_DATA_LOCAL);*/
    }

    private boolean first = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && first) {
            d("III", "container width " + post_container.getMeasuredWidth());
            int width = post_container.getMeasuredWidth()
                    - (post_container.getPaddingLeft() + post_container.getPaddingRight());
            if (width <= 0) {
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                width = wm.getDefaultDisplay().getWidth()
                        - getResources().getDimensionPixelSize(
                        R.dimen.topic_detail_padding_l_r) * 2;
            }
            ((BBSViewGetterImpl) mBBSViewGetter).setParentWidth(width);

            if (postMain.getId() == 0 && postMain.getLocalId() != 0) {
                setUIFromData(postMain, 0);
            }
            // MPostListResp resp = (MPostListResp)
            // getIntent().getSerializableExtra(Constants.EXTRA_POST_DETAIL_CONTENT);
            // setUIFromData(resp, 0);
            first = false;
        }

    }

    private void test() {
        if (!BuildConfig.DEBUG)
            return;
        mBBSTextBuilder = new BBSTextBuilderImpl(TopicHelper.getDemo(this,
                "test/demo2.json"));
        mBBSViewBuilder.buildTextBuilder(mBBSTextBuilder);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
		/* @@@@@@@@@@@@@@@@@@@@@@@@
		case TitleView.ID_LEFT:
			finish();
			break;
		case TitleView.ID_RIGHT:
			hideReply();
            if (Permissions.canPostModify(getApplicationContext(), postMain)) {
				btn_edit.setVisibility(View.VISIBLE);
			} else {
				btn_edit.setVisibility(View.GONE);
			}
			btn_delete.setText(R.string.detail_delete_post);
			btn_delete.setTag(postMain);
			// add by kfir
			if (Permissions.canBannerOperate(getApplicationContext(), postMain)) {
				btn_banner.setVisibility(View.VISIBLE);
			} else {
				btn_banner.setVisibility(View.GONE);
			}
			if (Permissions.canPostCollectionOperate(getApplicationContext(), postMain)) {
				btn_top.setVisibility(View.VISIBLE);
			} else {
				btn_top.setVisibility(View.GONE);
			}
			mOptionDialog.show();
			break;
		case R.id.tv_topic: {
			Intent intent = new Intent(this, ActivityTopicDetail.class);
			intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, topicID);
			i("topicID:", "------" + topicID);
			startActivity(intent);
			break;
		}*/
            case R.id.iv_header: {
                // #TODO Start
                PostBaseEntity entity = (PostBaseEntity) v.getTag();
                String userid = entity != null ? entity.getUserId() : null;
                if (entity == null || userid == null || "".equals(userid.trim())) {
                    w("III", "UserID 为空，你要我怎么看");
                    break;
                }
                d("III", "UserID " + userid);
                if (userid.equals(SharePreCacheHelper.getUserID(this))) {
                    break;
                }
                Intent intent = new Intent(this, ActivityOthers.class);
                intent.putExtra(Constants.EXTRA_USER_OTHERS_USER_ID, userid);
                intent.putExtra(Constants.EXTRA_USER_OTHERS_USER_CONTENT, convertPostToUser(entity));
                startActivity(intent);
                break;
            }
            case R.id.btn_focus: {
                PostBaseEntity data = (PostBaseEntity) v.getTag();
                if (data == null) {
                    d("III", "没有User，不能关注");
                    break;
                }
                requestFocus(data, !data.isFocusUser(), v, (TextView) v);
                break;
            }
            case R.id.vg_share: {
                // osv_scroll.smoothScrollToBottom();
                break;
            }
            /* @@@@@@@@@@@@@@@@@@@@@@@@@@@
            case R.id.vg_collection: {
                if (!(v.getTag() instanceof MPostResp)) {
                    d("III_logic", "没有Post, 不能收藏操作");
                    break;
                }
                MPostResp data = (MPostResp) v.getTag();
                requestCollection(data, !data.isStoreUp(),
                        (Drawable) v.getTag(R.id.vg_collection), v);
                break;
            }
            case R.id.btn_agree:
            case R.id.vg_like: {
                if (!(v.getTag() instanceof MPostResp)) {
                    d("III_logic", "没有Post, 不能点赞");
                    break;
                }
                MPostResp data = (MPostResp) v.getTag();
//			if (data.isLike()) { Toast.makeText(this, R.string.detail_liked,
//			Toast.LENGTH_SHORT).show(); break; }
                requestLike(data, !data.isLike(),
                        (Drawable) v.getTag(R.id.vg_like), v,
                        (TextView) v.getTag(R.id.btn_agree));
                break;
            }*/
            case R.id.tv_sofa:
            case R.id.vg_comment: {
                Intent intent = new Intent(this, ActivityPostComment.class);
                intent.putExtra(Constants.EXTRA_SEND_COMMENT_PARENT_POST_ID, postID);
                intent.putExtra(Constants.EXTRA_SEND_COMMENT_TOPIC_ID, topicID);
                intent.putExtra(Constants.EXTRA_SEND_COMMENT_FLAG, flag);
                startActivityForResult(intent, REQ_SEND_COMMENT);
                break;
            }
            case R.id.btn_to_top: {
                osv_scroll.getRefreshableView().smoothScrollTo(0, 0);
                break;
            }
            case R.id.ibtn_switch: {
                int tag = ibtn_switch.getDrawable().getLevel();
                if (tag == 1) {
                    setEmojicoVisiable(false);
                    setSoftKeyboardVisiable(true);
                    ibtn_switch.getDrawable().setLevel(0);
                } else {
                    boolean isOpen = imm.isActive();
                    setSoftKeyboardVisiable(false);
                    new Handler() {// 做个短暂的延时，否则会有点不好看
                        public void handleMessage(Message msg) {
                            setEmojicoVisiable(true);
                            ibtn_switch.getDrawable().setLevel(1);
                        }
                    }.sendEmptyMessageDelayed(0,
                            isOpen ? SOFT_KEYBOARD_DELAYED_TIME : 0);
                }
                break;
            }
            case R.id.vg_reply:
            case R.id.vg_reply_sub: {// 这个帖子容器的点击事件不是主贴的，而是评论的，因为它们ID都是一样的
                if (v.getTag() instanceof PostBaseEntity) {
                    PostBaseEntity data = (PostBaseEntity) v.getTag();
                    // 自己不能回复自己
                    if ((data.getUserId() != null && !data.getUserId().equals(
                            SharePreCacheHelper.getUserID(this)))
                            || Constants.TOPIC_SUPER_ADNIMISTRATOR) {
                        d("III_data",
                                "click id " + data.getId() + " level "
                                        + data.getPostLevel());
                        et_message.setHint(getString(R.string.detail_reply_what,
                                data.getNickname()));
                        showReply();
                    }
                }
                break;
            }
		/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		case R.id.vg_reply_more: {
			MPostResp parent = (MPostResp) v.getTag();
			int page = Integer.parseInt(v.getTag(R.id.tv_more).toString());
			ViewGroup container = (ViewGroup) v.getTag(R.id.vg_reply_more);
			if (parent == null) {
				w("III", "叫你爸过来领儿子。");
				break;
			}
			requestReplyList(page + 1, parent.getId(), v, v, container, GetPostList.GET_DATA_SERVER);
			break;
		}
		case R.id.btn_send: {
			MPostResp data = (MPostResp) btn_send.getTag();
			ViewGroup container = (ViewGroup) btn_send.getTag(R.id.btn_send);
			if (data == null || container == null) {
				d("III_error", "回复错误，快看逻辑 你是不是删了一些代码呀？");
				return;
			}
			String content = et_message.getText().toString().trim();
			if (content.equals("")) {
				Toast.makeText(this, R.string.detail_input_content,
						Toast.LENGTH_SHORT).show();
				return;
			}
			// Toast.makeText(this, "功能暂未完善", Toast.LENGTH_SHORT).show();
			requestReply(content, data, container);
			break;
		}
		case R.id.btn_edit: {
			mOptionDialog.dismiss();
			Intent intent = new Intent(this, ActivityPostEdit.class);
			intent.putExtra(Constants.EXTRA_EDIT_POST_IS_MODIFY, true);
			intent.putExtra(Constants.EXTRA_EDIT_POST_MODIFY_POST, postMain);
			startActivityForResult(intent, REQ_EDIT_POST);
			break;
		}
		case R.id.btn_delete: {
			mOptionDialog.dismiss();
			MPostResp data = (MPostResp) btn_delete.getTag();
			if (data == null) {
				d("III_error", "没有数据你让我怎么删？");
				return;
			}
			if (data.getPostLevel() == 0) {
				mDeleteDialog.show();
			} else {
				View removeView = (View) btn_delete.getTag(R.id.btn_delete);
				requestDeletePost(data.getId(), data.getPostLevel(), removeView);
			}
			break;
		}
		case R.id.btn_delete_enter: {
			mDeleteDialog.dismiss();
			MPostResp data = (MPostResp) btn_delete.getTag();
			View removeView = (View) btn_delete.getTag(R.id.btn_delete);
			requestDeletePost(data.getId(), data.getPostLevel(), removeView);
			break;
		}*/
            case R.id.btn_cancel: {
                mOptionDialog.dismiss();
                break;
            }
            case R.id.btn_cancel_delete: {
                mDeleteDialog.dismiss();
                break;
            }
            // add by kfir
		/* @@@@@@@@@@@@@@@@@@@@@@@@@@@
		case R.id.btn_top: {
			mOptionDialog.dismiss();
			boolean top = v.getTag() instanceof Boolean ? Boolean
					.parseBoolean(v.getTag().toString()) : false;
			if (top) {
				// requestTopRemove(topicID, v);
			} else {
				dispatchActivity(0);
			}
			break;
		}
		case R.id.btn_banner: {
			mOptionDialog.dismiss();
			int priority = v.getTag() instanceof Integer ? Integer.parseInt(v
					.getTag().toString()) : 0;
			if (priority > 0) {
				requestBannerRemove(priority, v);
			} else {
				dispatchActivity(1);
			}
			break;
		}*/
        }
    }
    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void dispatchActivity(int type) {
        TopItemParcel topParcel = new TopItemParcel();
        topParcel.setType(type);
        topParcel.setTopicId(postMain.getTopicId());
        topParcel.setPostId(postMain.getId());
        Intent topIntent = new Intent(ActivityPostBase.this, ActivityEditTopModel.class);
        topIntent.putExtra(Constants.EXTRA_EDIT_TOP_CONTENT, topParcel);
        startActivity(topIntent);
    }*/

    protected void onClickOption(View v) {
        hideReply();
        if (mAdapter.canModify(mAdapter.to(postMain))) {
            btn_edit.setVisibility(View.VISIBLE);
        } else {
            btn_edit.setVisibility(View.GONE);
        }
        btn_delete.setText(R.string.detail_delete_post);
        btn_delete.setTag(postMain);
        // add by kfir
        if (Permissions.canBannerOperate(getApplicationContext(), mAdapter.to(postMain))) {
            btn_banner.setVisibility(View.VISIBLE);
        } else {
            btn_banner.setVisibility(View.GONE);
        }
        if (Permissions.canPostCollectionOperate(getApplicationContext(), mAdapter.to(postMain))) {
            btn_top.setVisibility(View.VISIBLE);
        } else {
            btn_top.setVisibility(View.GONE);
        }
        mOptionDialog.show();
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.vg_reply:
            case R.id.vg_reply_sub: {
                boolean canDelete = v.getTag() instanceof PostBaseEntity;
                if (canDelete) {
                    PostBaseEntity data = (PostBaseEntity) v.getTag();
                    d("III_logic",
                            "data userid " + data.getUserId() + ", local userid "
                                    + SharePreCacheHelper.getUserID(this)
                                    + ", postLevel " + data.getPostLevel());
                } else {
                    break;
                }
                PostBaseEntity data = (PostBaseEntity) v.getTag();
                // add by kfir
                if (Permissions.canBannerOperate(getApplicationContext(), mAdapter.to(data))) {
                    btn_banner.setVisibility(View.VISIBLE);
                } else {
                    btn_banner.setVisibility(View.GONE);
                }
                if (Permissions.canPostCollectionOperate(getApplicationContext(), mAdapter.to(data))) {
                    btn_top.setVisibility(View.VISIBLE);
                } else {
                    btn_top.setVisibility(View.GONE);
                }

                if (mAdapter.canDelete(mAdapter.to(data))) {
                    switch (data.getPostLevel()) {
                        case 0:
                            if (mAdapter.canModify(mAdapter.to(data))) {
                                btn_edit.setVisibility(View.VISIBLE);
                            } else {
                                btn_edit.setVisibility(View.GONE);
                            }
                            btn_delete.setText(R.string.detail_delete_post);
                            break;
                        case 1:
                            btn_edit.setVisibility(View.GONE);
                            btn_delete.setText(R.string.detail_delete_comment);
                            break;
                        default:
                            btn_edit.setVisibility(View.GONE);
                            btn_delete.setText(R.string.detail_delete_reply);
                            break;
                    }
                    hideReply();
                    mOptionDialog.show();
                }
                break;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQ_SEND_COMMENT: {
                if (resultCode == RESULT_OK) {
                    PostBaseEntity resp = mAdapter.from(data
                            .getSerializableExtra(Constants.EXTRA_SEND_COMMENT_RESULT));
                    resp.setNickname(SharePreCacheHelper.getNickName(this));
                    resp.setCreateTime(System.currentTimeMillis());
                    topicID = resp.getTopicId();
                    // datas.addFirst(resp);

                    d("III", "post " + JsonUtils.toJson(resp));
                    // 总评论数 + 1
                    postMain.setCommentCount(postMain.getCommentCount() + 1);
                    tv_total_comment.setText(getString(
                            R.string.detail_what_total_reply,
                            postMain.getCommentCount() + ""));
                    tv_reply.setText(postMain.getCommentCount() + "");
                    addComment(vg_comments, true, resp);
                    tv_total_comment.setText(getString(
                            R.string.detail_what_total_reply, (postMain.getCommentCount())
                                    + ""));
                    if (tv_sofa.getVisibility() != View.GONE) {
                        tv_sofa.setVisibility(View.GONE);
                        vg_comment_end.setVisibility(View.VISIBLE);
                    }
                }
                break;
            }
            case REQ_EDIT_POST: {
                if (resultCode == RESULT_OK) {
                    // requestData(postID, 1, true, GetPostList.GET_DATA_SERVER);
                }
                break;
            }
        }
    }

    /**
     * 添加评论数据到UI
     *
     * @param container    添加评论的容器
     * @param isAddToFirst 是否添加到第一个位置
     * @param data         数据
     */
    protected void addComment(ViewGroup container, boolean isAddToFirst, final PostBaseEntity data) {
        // d("III", "haha " + data);
        if (data == null) {
            return;
        }
        d("III_logic", "addComment " + data.getId() + ", " + data.getCommentId());
        data.setPostLevel(1);
        LayoutInflater inflater = getLayoutInflater();
        View itemView = inflater.inflate(R.layout.item_post_comment, null);
        final ViewGroup vg_reply = (ViewGroup) itemView
                .findViewById(R.id.vg_reply);
        ImageView iv_header = (ImageView) itemView.findViewById(R.id.iv_header);
        TextView tv_nickname = (TextView) itemView
                .findViewById(R.id.tv_nickname);
        TextView tv_datetime = (TextView) itemView
                .findViewById(R.id.tv_datetime);
        TextView btn_agree = (TextView) itemView.findViewById(R.id.btn_agree);
        ViewGroup post_container = (ViewGroup) itemView
                .findViewById(R.id.post_container);
        final ViewGroup reply_container = (ViewGroup) itemView
                .findViewById(R.id.reply_container);

        vg_reply.setTag(data);
        vg_reply.setTag(R.id.reply_container, reply_container);
        vg_reply.setTag(R.id.btn_delete_post, itemView);
        vg_reply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 这个逻辑的注释还没有写，看吧
                ActivityPostBase.this.onClick(v);
                btn_send.setTag(v.getTag());// 需要回复的Post对象
                btn_send.setTag(R.id.btn_send, v.getTag(R.id.reply_container));// 该评论下装回复容器
                btn_send.setTag(R.id.vg_reply, REPLY_TO_COMMENT);// 代表我回复的是评论,并不是@谁谁谁
            }
        });
        vg_reply.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                btn_delete.setTag(v.getTag());
                btn_delete.setTag(R.id.btn_delete,
                        v.getTag(R.id.btn_delete_post));// 删除成功的话，需要被删除的View
                return ActivityPostBase.this.onLongClick(v);
            }
        });
        iv_header.setTag(data);
        iv_header.setOnClickListener(this);

        btn_agree.setTag(data);
        btn_agree.setTag(R.id.vg_like, btn_agree.getCompoundDrawables()[0]);
        btn_agree.setTag(R.id.btn_agree, btn_agree);
        btn_agree.setOnClickListener(this);// 上面2句是为了再点击的时候把参数传过去3

        String nickname = data.getNickname() == null ? data.getUserId() : data.getNickname();
        String datetime = TopicHelper.getTopicDateString(this,
                System.currentTimeMillis(), data.getCreateTime());
        String agree = data.getLikeCount() + "";
        boolean isAgree = data.isLike();
        String postContent = data.getContent();

        ImageLoader.getInstance().displayImage(
                TopicHelper.wrappeImagePath(data.getHeadUrl()), iv_header,
                options);
        // TopicHelper.setImageFromUrl(HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP
        // + data.getUserHeadIcon(), iv_header);
        tv_nickname.setText(nickname);
        tv_datetime.setText(datetime);
        // tv_agree.setText(agree);
        btn_agree.getCompoundDrawables()[0].setLevel(isAgree ? 1 : 0);
        btn_agree.setText(agree);
        if (postContent != null && !"".equals(postContent.trim())) {
            BBSTextBuilder tb = new BBSTextBuilderImpl(postContent);
            BBSViewBuilder vb = new BBSViewBuilderImpl(getApplicationContext(),
                    BBSViewBuilder.MODE_NORMAL, tb, mBBSViewGetter);
            vb.setDividerDrawableRes(R.drawable.divider_transparent);
            post_container.addView(vb.getContainer());
        }

        List<PostBaseEntity> comms = data.getComments();
        if (comms != null && comms.size() > 0) {
            for (int i = 0; i < comms.size(); i++) {
                PostBaseEntity comm = comms.get(i);
                if (comm == null)
                    continue;
                addReply(comm, true, -1, reply_container);
            }
            d("III_logic",
                    "评论 " + data.getNickname() + ", reply "
                            + data.getCommentCount());
            if (data.getCommentCount() > comms.size()) {
                View moreView = inflater.inflate(
                        R.layout.item_post_comment_reply_more, null);
                moreView.setTag(data);// 父贴
                moreView.setTag(R.id.tv_more, 0);// 当前的页数
                moreView.setTag(R.id.vg_loading, data.getCommentCount());// 总页数
                moreView.setTag(R.id.vg_reply_more, reply_container);// 父容器
                // moreView.setClickable(true); TODO more
                moreView.setOnClickListener(this);
                reply_container.addView(moreView);
            }
        } else {
            reply_container.setVisibility(View.GONE);
        }

        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        if (isAddToFirst) {
            container.addView(itemView, 0, params);
        } else {
            container.addView(itemView, params);
        }

    }

    /**
     * 添加回复数据到UI
     *
     * @param comm        回复数据
     * @param isAddToLast 是否添加到末尾
     * @param index       如果 isAddToLast = falae, 就将该条View添加到index的位置
     * @param container   添加回复的容器
     */
    protected void addReply(PostBaseEntity comm, boolean isAddToLast, int index,
                            ViewGroup container) {
        comm.setPostLevel(2);
        if (container.getVisibility() != View.VISIBLE) {
            container.setVisibility(View.VISIBLE);
        }
        View replyView = getLayoutInflater().inflate(
                R.layout.item_post_comment_reply, null);
        View subView = replyView.findViewById(R.id.vg_reply_sub);
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        TextView tv_reply = (TextView) replyView.findViewById(R.id.tv_reply);
        tv_reply.setMovementMethod(LinkMovementMethod.getInstance());
        tv_reply.setText(getReplyChars(
                new ClickUserSpan(comm.getUserId()),
                new ClickUserSpan(comm.getToUserId()), comm));

        subView.setTag(comm);
        subView.setTag(R.id.reply_container, container);
        subView.setTag(R.id.btn_delete_post, replyView);
        subView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityPostBase.this.onClick(v);
                btn_send.setTag(v.getTag());
                btn_send.setTag(R.id.btn_send, v.getTag(R.id.reply_container));
                btn_send.setTag(R.id.vg_reply, REPLY_TO_REPLY);
            }
        });

        subView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                btn_delete.setTag(v.getTag());
                btn_delete.setTag(R.id.btn_delete,
                        v.getTag(R.id.btn_delete_post));// 删除成功的话，需要被删除的View
                return ActivityPostBase.this.onLongClick(v);
            }
        });

        if (isAddToLast) {
            container.addView(replyView, params);
        } else {
            container.addView(replyView, index, params);
        }
    }

    private void registerBroadcast() {

    }

    /**
     * 请求关注用户
     *
     * @param data    数据
     * @param isFocus 关注
     * @param click   触发该事件的控件
     * @param text    关注成功需要改变的控件
     */
    private void requestFocus(final PostBaseEntity data, final boolean isFocus,
                              final View click, final TextView text) {
        if (data == null || data.getUserId() == null
                || "".equals(data.getUserId().trim())) {
            d("III", "没有UserID，不能请求关注。");
            return;
        }
        FollowUser fu = ReqFactory.buildInterface(this, FollowUser.class);
        FollowUserReq req = new FollowUserReq();
        req.setUserId(data.getUserId());
        req.setFollow(isFocus);
        if (click != null) {
            click.setClickable(false);
        }
        d("III_logic", "关注 userId " + data.getUserId() + ", follow " + isFocus);
        BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {
                if (click != null) {
                    click.setClickable(true);
                }
                if (resp != null && resp.isRequestSuccess()) {
                    data.setFocusUser(isFocus);
                    text.setText(isFocus ? R.string.detail_followed
                            : R.string.detail_follow);
                    d("III_data", "关注操作成功 focus " + isFocus + " ");
                } else {
                    d("III_data", "关注操作失败 focus " + isFocus + " "
                            + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        addCallback(node);
        fu.followUserAsync(req, node);
    }

    /**
     * 下拉刷新时记得调用这个方法哦 但是当然要下拉后等数据到了再调，否则数据还没到评论就一片空白了
     */
    protected void resetDatas() {
        d("III_logic", "清除已有数据");
        // datas.clear();
        vg_comments.removeAllViews();
    }

    /**
     * 设置用户信息以及主贴内容
     *
     * @param data   数据
     * @param status 暂时没卵用
     */
    protected void setUIFromData(PostBaseEntity data, int status) {
        if (data == null) {
            vg_loading.setVisibility(View.GONE);
            Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
            return;
        }

        vg_collection.setTag(data);
        vg_collection.setTag(R.id.vg_collection,
                tv_is_collection.getCompoundDrawables()[0]);
        d("III_logic",
                "setUIFromData " + data.getId() + " " + data.getTitle());
        String title = data.getTitle() == null ? "unknown" : data
                .getTitle();
        /* @@@@@@@@@@@@@@@@@@@@@@@@@
        String topic = getString(R.string.detail_topic,
                data.getTopicTitle() == null ? "unknown" : data.getTopicTitle());*/
        // String postTitle = (data.getTopicTitle() != null ? "[" +
        // data.getTopicTitle() + "]" : "") + title;
        String nickname = data.getNickname() == null || "".equals(data.getNickname().trim()) ? data.getUserId() : data.getNickname();
        String gender = Constants.getGenderString(getApplicationContext(), data.getGender());
        String level = "lv " + data.getUserLevel();
        String comment = getString(R.string.detail_what_total_reply,
                data.getCommentCount() + "");
        Constants.TOPIC_AD_IDS[0] = SharePreCacheHelper.getTopicIdAdv(this);
        boolean noShowTopic = TopicHelper.isSpecialTopic(data.getTopicId());

        if (btn_focus.getVisibility() == View.VISIBLE)
            btn_focus.setVisibility(data.getUserId().equals(
                    SharePreCacheHelper.getUserID(this)) ? View.INVISIBLE
                    : View.VISIBLE);
        btn_focus.setText(data.isFocusUser() ? R.string.detail_followed : R.string.detail_follow);
        btn_focus.setTag(data);
		/* @@@@@@@@@@@@@@@@@@@@@@@@@@
		mTitle.setCenterText(title, null);
		if (Permissions.canPostDelete(this, data)) {
			mTitle.setRightIcon(R.drawable.btn_more, this);
		}
		tv_topic.setText(topic);
		*/
        tv_title.setText(title);
        tv_nickname.setText(nickname);
        tv_datetime.setText(TopicHelper.getTopicDateString(this,
                System.currentTimeMillis(), data.getCreateTime()));
        tv_gender.setText(gender);
        tv_level.setText(level);
        tv_like.setText(data.getLikeCount() + "");
        tv_reply.setText(data.getCommentCount() + "");
        // mTitle.setCenterText(tv_title.getText().toString(), null);
        tv_total_comment.setText(comment);
        tv_is_collection.getCompoundDrawables()[0]
                .setLevel(data.isColl() ? 1 : 0);
        tv_is_like.getCompoundDrawables()[0].setLevel(data.isLike() ? 1 : 0);
        iv_header.setTag(data);
        iv_header.setOnClickListener(this);
        // TopicHelper.setImageFromUrl((data.getUserHeadIcon().startsWith("http")
        // ? "" : HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP) +
        // data.getUserHeadIcon(), iv_header);
        ImageLoader.getInstance().displayImage(
                TopicHelper.wrappeImagePath(data.getHeadUrl()), iv_header,
                options);
        vg_like.setTag(data);
        vg_like.setTag(R.id.vg_like, tv_is_like.getCompoundDrawables()[0]);
        vg_like.setTag(R.id.btn_agree, tv_like);
        setPreview(data.getLocalId() != 0 && data.getId() == 0);
        // d("III", "postContent:" + data.getPostContent());
        if (!mBBSTextBuilder.getString().equals(data.getContent())) {
            mBBSTextBuilder.setString(data.getContent());
            mBBSViewBuilder.notifyTextBuilderChanged();
            for (int i = 0; i < mBBSTextBuilder.size(); i++) {
                onBindView(i, mBBSTextBuilder.get(i), mBBSViewBuilder.get(i));
            }
            d("III", "not equals");
        } else {
            d("III", "post content equals");
        }
        // post_container.removeAllViews();
        // if (mBBSViewBuilder.getContainer() != null) {
        // post_container.addView(mBBSViewBuilder.getContainer());
        // }
		/* @@@@@@@@@@@@@@@@@
		tv_topic.setVisibility(noShowTopic ? View.GONE : View.VISIBLE);*/
        mHeadLayout.setVisibility(noShowTopic ? View.GONE : View.VISIBLE);// add
        // by
        // kfir
        vg_option.setVisibility(View.VISIBLE);
        if (vg_loading.getVisibility() != View.GONE) {
            vg_loading.setVisibility(View.GONE);
            vg_loading.startAnimation(AnimationUtils.loadAnimation(this,
                    android.R.anim.fade_out));
        }
        if (osv_scroll.getVisibility() != View.VISIBLE) {
            osv_scroll.setVisibility(View.VISIBLE);
            osv_scroll.startAnimation(AnimationUtils.loadAnimation(this,
                    android.R.anim.fade_in));
        }
        // osv_scroll.getRefreshableView().setVisibility(View.VISIBLE);
    }

    protected void onBindView(int index, BBSDescribeItem item, View itemView) {

    }

    /**
     * 子类重写的，发布新帖时，广播到的IntentFilter
     * 这本来是要支持异步发帖的，收到广播后，需要取消禁用状态
     *
     * @return
     */
    protected String getReleasedFilter() {
        return null;
    }

    /**
     * 显示回复文本框，并且隐藏分享...收藏...栏
     */
    protected void showReply() {
        if (vg_option.getVisibility() != View.GONE) {
            vg_option.setVisibility(View.GONE);
            vg_option.startAnimation(outOption);
        }
        if (vg_edit_msg.getVisibility() != View.VISIBLE) {
            vg_edit_msg.setVisibility(View.VISIBLE);
            vg_edit_msg.startAnimation(inEditMsg);
            inEditMsg.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    et_message.requestFocus();
                    setSoftKeyboardVisiable(true);
                    inEditMsg.setAnimationListener(null);
                }
            });
        }
        et_message.requestFocus();
        setSoftKeyboardVisiable(true);
    }

    /**
     * 隐藏回复文本框，并且显示分享...收藏...栏
     */
    protected void hideReply() {
        et_message.clearFocus();
        tv_like.requestFocus();
        setSoftKeyboardVisiable(false);
        setEmojicoVisiable(false);
        if (vg_option.getVisibility() != View.VISIBLE) {
            vg_option.setVisibility(View.VISIBLE);
            vg_option.startAnimation(inOption);
        }
        if (vg_edit_msg.getVisibility() != View.GONE) {
            vg_edit_msg.setVisibility(View.GONE);
            vg_edit_msg.startAnimation(outEditMsg);
        }
		/*
		 * boolean isOpen = imm.isActive() || fragmentEmoji.isVisible(); new
		 * android.os.Handler() {// 做个短暂的延时，否则会有点不好看 public void
		 * handleMessage(Message msg) { vg_option.setVisibility(View.VISIBLE);
		 * vg_option.startAnimation(inOption);
		 * vg_edit_msg.setVisibility(View.GONE);
		 * vg_edit_msg.startAnimation(outEditMsg); }
		 * }.sendEmptyMessageDelayed(0, isOpen ? SOFT_KEYBOARD_DELAYED_TIME :
		 * 0);
		 */
    }

    protected GetUserResp convertPostToUser(PostBaseEntity post) {
        GetUserResp user = new GetUserResp();
        user.setUserId(post.getUserId());
        user.setName(post.getNickname());
        user.setPortrait(post.getHeadUrl());
        user.setGender(post.getGender());
        user.setLevel(post.getUserLevel());
        user.setFollowsUser(post.isFocusUser());
        user.setEMUserId(post.getEmUserId());
        return user;
    }

    protected void setEmojicoVisiable(boolean visiable) {
        boolean isOpen = fragmentEmoji.isVisible();
        if (visiable) {
            // if(!isOpen) {
            getSupportFragmentManager().beginTransaction().show(fragmentEmoji)
                    .commit();
            // }
        } else {
            // if(isOpen) {
            getSupportFragmentManager().beginTransaction().hide(fragmentEmoji)
                    .commit();
            // }
        }
    }

    protected void setSoftKeyboardVisiable(boolean visiable) {
        boolean isOpen = imm.isActive();
        // if(isOpen != visiable) {
        // return;
        // }
        if (visiable) {
            // if(!isOpen) {
            imm.showSoftInput(et_message, InputMethodManager.SHOW_FORCED);
            // }
        } else {
            // if(isOpen) {
            imm.hideSoftInputFromWindow(et_message.getWindowToken(), 0);
            // }
        }
    }

    @Override
    public void onKeyboardChanged(int state) {
        // d("III", "state " + state);
        if (state == AndroidBugsSolution.OnKeyboardListener.SHOW) {
            setEmojicoVisiable(false);
            ibtn_switch.getDrawable().setLevel(0);
        } else {
            ibtn_switch.getDrawable().setLevel(0);
        }
        osv_scroll.getRefreshableView().scrollTo(0, scrollY);
    }

    private int startY;

    @Override
    public void onScrollChanged(int x, int y, int oldx, int oldy) {
        // setEmojicoVisiable(false);
        // setSoftKeyboardVisiable(false);
        // ibtn_switch.getDrawable().setLevel(0);
        scrollY = y;
        if (oldy == 0 && startY == 0) {
            startY = y;
        }
        if (y > vg_content.getHeight()) {
            if (btn_to_top.getVisibility() != View.VISIBLE) {
                btn_to_top.setVisibility(View.VISIBLE);
                btn_to_top.startAnimation(inToTop);
            }
        } else {
            if (btn_to_top.getVisibility() != View.GONE) {
                btn_to_top.setVisibility(View.GONE);
                btn_to_top.startAnimation(outToTop);
            }
        }

		/*
		 * synchronized (this) { if (vg_edit_msg.getVisibility() == View.VISIBLE
		 * && isScrollReply) { isScrollReply = true; hideReply(); } else if
		 * (imm.isActive()) { setSoftKeyboardVisiable(false); } }
		 */

        // if(osv_scroldsFinished()) {
        // isScrollReply = true;
        // // d("III", "isFinished " + osv_scroldsFinished());
        // }
        // d("III", "x " + x + ", y " + y + ", oldx " + oldx + ", oldy " + oldy
        // + ", isFinished ");
    }

    @Override
    public void scrollBottom() {
        d("III", "scroolBottom");
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        View view = getCurrentFocus();
        if (!(view instanceof EditText)) {
            return;
        }
        EmojiconsFragment.backspace((EditText) view);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        View view = getCurrentFocus();
        if (!(view instanceof EditText)) {
            return;
        }
        EmojiconsFragment.input((EditText) view, emojicon);
    }

    @Override
    protected void onDestroy() {
        BBSLinkManager.unregisterLink("action");
        super.onDestroy();
    }

    protected List<ButtonGroup> getOtherButtonGroups() {
        Resources res = this.getResources();
        List<ButtonGroup> groups = new ArrayList<ButtonGroup>(2);
        ButtonGroup g0 = new ButtonGroup(this);
        g0.add(new ButtonItem(this, R.id.btn_delete, res
                .getString(R.string.detail_delete_reply), res
                .getColor(R.color.test_primary), -1, true, this));
        groups.add(g0);

        ButtonGroup g1 = new ButtonGroup(this);
        g1.add(new ButtonItem(this, R.id.btn_cancel, res
                .getString(R.string.cancel), res
                .getColor(R.color.default_text_color), -1, true, this));
        groups.add(g1);

        return groups;
    }

    protected void setCommentFlag(int flag) {
        this.flag = flag;
    }

    protected void setConverter(PostBaseAdapter adapter) {
        this.mAdapter = adapter;
    }

    protected boolean isPreview() {
        return v_option_dis.getVisibility() == View.VISIBLE /*&&
                v_sofa_dis.getVisibility() == View.VISIBLE*/;
    }

    protected void setPreview(boolean preview) {
        int visibility = preview ? View.VISIBLE : View.GONE;
        if (v_option_dis.getVisibility() != visibility) {
            v_option_dis.setVisibility(visibility);
        }
        /*if (v_sofa_dis.getVisibility() != visibility) {
            v_sofa_dis.setVisibility(visibility);
        }*/
        if (preview) {
            osv_scroll.setMode(PullToRefreshBase.Mode.DISABLED);
        } else {
            osv_scroll.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
    }

    private void ensureConverter() {
        if (this.mAdapter == null) {
            throw new NullPointerException("The adapter cannot be null!");
        }
    }

    /**
     * >=2级的帖子的用户名的点击Span
     */
    class ClickUserSpan extends ClickableSpan {

        String userId;
        PostBaseEntity post;

        public ClickUserSpan(String userid) {
            this.userId = userid;
        }

        @Override
        public void onClick(View widget) {
            d("III_logic", "userid " + userId);
            // Toast.makeText(getApplicationContext(), "Click " +
            // comm.getUserName(), Toast.LENGTH_SHORT).show();
            if (SharePreCacheHelper.getUserID(getApplicationContext()).equals(userId))
                return;
            Intent intent = new Intent(ActivityPostBase.this, ActivityOthers.class);
            intent.putExtra(Constants.EXTRA_USER_OTHERS_USER_ID, userId);
            // intent.putExtra(Constants.EXTRA_USER_OTHERS_USER_CONTENT, convertPostToUser(post));
            startActivity(intent);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.test_primary));
            ds.setUnderlineText(false);
        }
    }

    /**
     * 为了ActivityPostBase的通用，只能这样了
     */
    public interface PostBaseAdapter {

        PostBaseEntity from(Serializable o);

        Serializable to(PostBaseEntity entity);

        boolean canDelete(Serializable o);

        boolean canModify(Serializable o);
    }

    class PostBaseEntity {
        private long id;
        private long localId;
        private long parentId;
        private long commentId;
        private long commentParentId;
        private String userId;
        private String emUserId;
        private int gender;
        private int userLevel;
        private String headUrl;
        private String nickname;
        private boolean isFocusUser;
        private String toUserId;
        private String toNickname;
        private String cover;
        private String title;
        private String introduction;
        private String content;
        private String topicTitle;
        private int postLevel;
        private int useCount;
        private int likeCount;
        private int collCount;
        private int browseCount;
        private int commentCount;
        private long topicId;
        private long createTime;
        private long updateTime;
        private boolean isUse;
        private boolean isColl;
        private boolean isLike;
        private float score;
        private String[] effects;
        private String[] parts;
        private List<PostBaseEntity> comments;

        public int getBrowseCount() {
            return browseCount;
        }

        public void setBrowseCount(int browseCount) {
            this.browseCount = browseCount;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getLocalId() {
            return localId;
        }

        public void setLocalId(long localId) {
            this.localId = localId;
        }

        public long getParentId() {
            return parentId;
        }

        public void setParentId(long parentId) {
            this.parentId = parentId;
        }

        public long getCommentId() {
            return commentId;
        }

        public void setCommentId(long commentId) {
            this.commentId = commentId;
        }

        public long getCommentParentId() {
            return commentParentId;
        }

        public void setCommentParentId(long commentParentId) {
            this.commentParentId = commentParentId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getEmUserId() {
            return emUserId;
        }

        public void setEmUserId(String emUserId) {
            this.emUserId = emUserId;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public int getUserLevel() {
            return userLevel;
        }

        public void setUserLevel(int userLevel) {
            this.userLevel = userLevel;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public boolean isFocusUser() {
            return isFocusUser;
        }

        public void setFocusUser(boolean isFocusUser) {
            this.isFocusUser = isFocusUser;
        }

        public String getToNickname() {
            return toNickname;
        }

        public void setToNickname(String toNickname) {
            this.toNickname = toNickname;
        }

        public String getToUserId() {
            return toUserId;
        }

        public void setToUserId(String toUserId) {
            this.toUserId = toUserId;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTopicTitle() {
            return topicTitle;
        }

        public void setTopicTitle(String topicTitle) {
            this.topicTitle = topicTitle;
        }

        public int getPostLevel() {
            return postLevel;
        }

        public void setPostLevel(int postLevel) {
            this.postLevel = postLevel;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }

        public int getUseCount() {
            return useCount;
        }

        public void setUseCount(int useCount) {
            this.useCount = useCount;
        }

        public int getCollCount() {
            return collCount;
        }

        public void setCollCount(int collCount) {
            this.collCount = collCount;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public long getTopicId() {
            return topicId;
        }

        public void setTopicId(long topicId) {
            this.topicId = topicId;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public boolean isUse() {
            return isUse;
        }

        public void setUse(boolean isUse) {
            this.isUse = isUse;
        }

        public boolean isColl() {
            return isColl;
        }

        public void setColl(boolean isColl) {
            this.isColl = isColl;
        }

        public boolean isLike() {
            return isLike;
        }

        public void setLike(boolean isLike) {
            this.isLike = isLike;
        }

        public float getScore() {
            return score;
        }

        public void setScore(float score) {
            this.score = score;
        }

        public String[] getEffects() {
            return effects;
        }

        public void setEffects(String[] effects) {
            this.effects = effects;
        }

        public String[] getParts() {
            return parts;
        }

        public void setParts(String[] parts) {
            this.parts = parts;
        }

        public List<PostBaseEntity> getComments() {
            return comments;
        }

        public void setComments(List<PostBaseEntity> comments) {
            this.comments = comments;
        }
    }
}
