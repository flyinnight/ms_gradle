package com.dilapp.radar.ui.topic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.GridLayout;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.PostReleaseCallBack.*;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityChoiceSkinCarePlan extends BaseFragmentActivity implements View.OnClickListener {

    private final static int REQ_RELEASE_POST = 5;

    private TitleView mTitle;

    private GridLayout gl_effects;
    private GridLayout gl_parts;

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
        setContentView(R.layout.activity_choice_skin_care_plan);

        registerFinishListener();
        mTitle = new TitleView(this, findViewById(TitleView.ID_TITLE));
        mTitle.setLeftIcon(R.drawable.btn_back, this);
        mTitle.setCenterText(R.string.plan_title, null);
        mTitle.setRightText(R.string.finish, this);

        gl_effects = findViewById_(R.id.gl_effects);
        gl_parts = findViewById_(R.id.gl_parts);

        PostReleaseReq data = (PostReleaseReq) getIntent().getSerializableExtra(Constants.EXTRA_EDIT_POST_PLAN_CONTENT);
        if(data != null) {
            if(data.getEffect() != null) {
                String[] effects = data.getEffect().split(",");
                setViewGroupCheckedByTags(gl_effects, effects, true);
            }
            if(data.getPart() != null) {
                String[] parts = data.getPart().split(",");
                setViewGroupCheckedByTags(gl_parts, parts, true);
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case TitleView.ID_LEFT: {
            Intent intent = new Intent(getApplicationContext(), ActivityPostEdit.class);
            // intent.putExtra(Constants.EXTRA_EDIT_POST_TOPIC, data);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra(Constants.EXTRA_EDIT_POST_IS_PLAN, false);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, R.anim.out_from_bottom);
            break;
        }
        case TitleView.ID_RIGHT: {
            String[] effects = getViewGroupCheckedTags(gl_effects);
            String[] parts = getViewGroupCheckedTags(gl_parts);
            // L.i("III", "effects " + L.array2String(effects) + " | parts " + L.array2String(parts));
            if(effects == null || effects.length == 0 || parts == null || parts.length == 0) {
                Toast.makeText(this, R.string.plan_not_choice, Toast.LENGTH_SHORT).show();
                break;
            }
            Intent intent = new Intent(getApplicationContext(), ActivityPostEdit.class);
            // intent.putExtra(Constants.EXTRA_EDIT_POST_TOPIC, data);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra(Constants.EXTRA_EDIT_POST_IS_PLAN, true);
            PostReleaseReq data = new PostReleaseReq();
            data.setEffect(unionArrays(effects, ","));
            data.setPart(unionArrays(parts, ","));
            data.setSelectedToSolution(true);
            intent.putExtra(Constants.EXTRA_EDIT_POST_PLAN_CONTENT, data);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, R.anim.out_from_bottom);
            break;
        }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_RELEASE_POST) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                // setResult(resultCode, data);
                // finish();
            }
        }
    }

    private void setViewGroupCheckedByTags(ViewGroup vg, String[] tags, boolean checked) {
        if(tags == null) {
            return;
        }
        List<String> list = new ArrayList<String>(Arrays.asList(tags));
        int childCount = vg.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = vg.getChildAt(i);
            if(!(view instanceof Checkable) || view.getTag() == null) continue;
            for (int j = 0; j < list.size(); j++) {
                String tag = list.get(j);
                if(tag == null) {
                    list.remove(j--);
                    continue;
                }
                if(tag.equals(view.getTag())) {
                    ((Checkable) view).setChecked(checked);
                    list.remove(j);
                    break;
                }
            }
        }
    }

    private String[] getViewGroupCheckedTags(ViewGroup vg) {
        List<String> tags = new ArrayList<String>();
        int childCount = vg.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = vg.getChildAt(i);
            if(view instanceof Checkable) {
                Checkable c = (Checkable) view;
                if(c.isChecked()) {
                    tags.add(view.getTag().toString());
                }
            }
        }
        return tags.toArray(new String[0]);
    }

    private String unionArrays(String[] arr, String unionStr) {
        if(arr == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if(i != arr.length - 1) {
                sb.append(unionStr);
            }
        }
        return sb.toString();
    }

    @Override
    protected void onDestroy() {
        unregisterFinishListener();
        super.onDestroy();
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
