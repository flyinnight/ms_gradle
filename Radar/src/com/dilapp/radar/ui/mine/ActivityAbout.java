package com.dilapp.radar.ui.mine;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.mine.FragmentMineList.*;
import com.dilapp.radar.util.ABAppUtil;

import java.util.ArrayList;
import java.util.List;

import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 * Created by husj1 on 2015/11/11.
 */
public class ActivityAbout extends BaseFragmentActivity implements OnClickListener {

    private TitleView mTitle;
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mTitle = new TitleView(this, findViewById(TitleView.ID_TITLE));
        mTitle.setCenterText(R.string.setting_about, null);
        mTitle.setLeftIcon(R.drawable.btn_back, this);
        tv_version = findViewById_(R.id.tv_version);
        d("III", "A");
        FragmentMineList fgmt_list = (FragmentMineList) getSupportFragmentManager().findFragmentById(R.id.fgmt_list);
        fgmt_list.setGroups(genGroups());

        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            int code = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            tv_version.setText("V " + version + " " + code);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View weight) {
        super.onClick(weight);
        switch (weight.getId()) {
            case TitleView.ID_LEFT: {
                finish();
                break;
            }
        }
    }

    private List<MineGroup> genGroups() {
        OnClickListener l = this;
        List<MineGroup> groups = new ArrayList<MineGroup>(1);
        List<MineItem> items = new ArrayList<MineItem>(1);
        items.add(new MineItem(R.string.about_feedback, 0,
                R.string.about_feedback, false, true, l));
        // groups.add(new MineGroup(true, true, true, items));
        return groups;
    }
}
