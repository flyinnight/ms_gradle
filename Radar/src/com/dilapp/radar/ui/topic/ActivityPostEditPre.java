package com.dilapp.radar.ui.topic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.TopicListCallBack.*;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.Permissions;
import com.dilapp.radar.ui.TitleView;

/**
 * 添加预置话题 用户初次使用时或者发帖时需要添加的界面
 *
 * @author john
 */
public class ActivityPostEditPre extends BaseFragmentActivity implements OnClickListener, FragmentTopicChoice.OnCheckedListener {

    // private final static int REQ_RELEASE_POST = 5;

    private TitleView mTitle;
    private FragmentTopicChoice mFTopicChoice;

    private boolean isReorderToFront;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ActivityPostEdit.BROADCAST_FINISH.equals(intent.getAction())) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit_pre);
        registerFinishListener();
        isReorderToFront = getIntent().getBooleanExtra(Constants.EXTRA_EDIT_POST_PRE_REORDER_TO_FRONT, false);

        Context context = getApplicationContext();

        mTitle = new TitleView(context, findViewById(TitleView.ID_TITLE));
        mTitle.setLeftText(R.string.cancel, this);
        mTitle.setCenterText(R.string.choice_title, null);

        mFTopicChoice = new FragmentTopicChoice();// 都使用默认的参数
        Bundle args = new Bundle();
        args.putBoolean(FragmentTopicChoice.ARG_IS_SEARCH, false);
        mFTopicChoice.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFTopicChoice, "mFTopicChoice").commit();
        mFTopicChoice.setOnCheckedListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case TitleView.ID_LEFT: {
                if (isReorderToFront) {
                    startReleasePostActivity(null);
                    // overridePendingTransition(R.anim.in_from_right, R.anim.out_from_right);
                } else {
                    finish();
                }
                break;
            }
        }
    }

    @Override
    public void onChecked(MTopicResp data) {
//		String mTag = data.getExtras().getString("tag");
//		String mTopicId = data.getExtras().getString("TopicId");
        // intent.putExtra("tag", mTag);
    	if (Permissions.canPostRelease(getApplicationContext(), data.getTopicId())) {
    		startReleasePostActivity(data);
    	} else {
    		Toast.makeText(getApplicationContext(), R.string.topic_create_no_right, Toast.LENGTH_LONG).show();
    	}
        
        // 这个界面还不能finish掉
        // finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isReorderToFront = intent.getBooleanExtra(Constants.EXTRA_EDIT_POST_PRE_REORDER_TO_FRONT, false);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_RELEASE_POST) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                setResult(resultCode, data);
                finish();
            }
        }
    }*/

    @Override
    protected void onDestroy() {
        unregisterFinishListener();
        super.onDestroy();
    }

    private void startReleasePostActivity(MTopicResp data) {
        Intent intent = new Intent(getApplicationContext(), ActivityPostEdit.class);
        intent.putExtra(Constants.EXTRA_EDIT_POST_TOPIC, data);

        if(isReorderToFront) {
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        } else {
            if (getIntent().getExtras() != null) {
                intent.putExtras(getIntent().getExtras());
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        }
        startActivity(intent);
    }

    private void registerFinishListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActivityPostEdit.BROADCAST_FINISH);
        registerReceiver(receiver, filter);
    }

    private void unregisterFinishListener() {
        unregisterReceiver(receiver);
    }
}
