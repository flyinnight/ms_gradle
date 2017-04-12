package com.dilapp.radar.ui.found;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.TitleView;

/**
 * Created by husj1 on 2015/7/13.
 */
public class ActivityTopicAll extends BaseFragmentActivity implements View.OnClickListener{

    private TitleView mTitle;
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_all);

        mTitle = new TitleView(getApplicationContext(), findViewById(TitleView.ID_TITLE));
        mTitle.setCenterText(R.string.all_topic, null);
        mTitle.setLeftIcon(R.drawable.btn_back, this);
//        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
    }

    @Override
    public void onClick(View weight) {
        super.onClick(weight);
        switch (weight.getId()) {
            case TitleView.ID_LEFT: {
                finish();
            }
        }
    }
}
