package com.dilapp.radar.ui.comm;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseFragment;
import com.viewpagerindicator.UnderlinePageIndicator;

public class FragmentTabs extends BaseFragment {
    private TabHost mTabHost;
    private UnderlinePageIndicator mUnderLineIndicator;

    private List<TabsPagerInfo> mTabsPagerInfos;

    @Override
    public void onCreateView(/*LayoutInflater inflater, */ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(/*inflater, */container, savedInstanceState);
        setContentView(R.layout.fragment_tabs);
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mUnderLineIndicator = findViewById(R.id.lpi_indicator);
        if (mTabsPagerInfos == null || mTabsPagerInfos.size() == 0) {
            findViewById(R.id.line1).setVisibility(View.GONE);
            findViewById(R.id.line2).setVisibility(View.GONE);
        }

        View[] indicatorViews = getIndicatorViews();

        TabHost.TabContentFactory tcf = new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                View v = mTabsPagerInfos.get(Integer.parseInt(tag)).contentView;
                // ViewUtils.measureView(v);
                return v;
            }
        };
        for (int i = 0; i < indicatorViews.length; i++) {
//            final View view = mTabsPagerInfos.get(i).contentView;
//			if (view == null) {
//				view = new View(mContext);
//			}
            mTabHost.addTab(mTabHost.newTabSpec("" + i)
                            .setContent(tcf).setIndicator(indicatorViews[i])
                    /*, view, null*/);
        }

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
		/*return getContentView();*/
    }

    public int getCurrentTab() {
        if (mTabHost != null) {
            return mTabHost.getCurrentTab();
        } else {
            return -1;
        }
    }

    public void setCurrentTab(int tab) {
        if (mTabHost != null) {
            mTabHost.setCurrentTab(tab);
        }
    }

    public void setTabsPagerInfos(List<TabsPagerInfo> tabsPagerRecyclerInfos) {
        this.mTabsPagerInfos = tabsPagerRecyclerInfos;
    }

    public void setLinesVisibility(int line1, int line2) {
        findViewById(R.id.line1).setVisibility(line1);
        findViewById(R.id.line2).setVisibility(line2);
    }

    public TabHost getTabHost() {
        return mTabHost;
    }

    public UnderlinePageIndicator getUnderLineIndicator() {
        return mUnderLineIndicator;
    }

    private View[] getIndicatorViews() {
        if (mTabsPagerInfos == null) {
            return new View[0];
        }
        View[] indicators = new View[mTabsPagerInfos.size()];
        for (int i = 0; i < indicators.length; i++) {
            TabsPagerInfo info = mTabsPagerInfos.get(i);
            if (info == null) {
                continue;
            }
            indicators[i] = buldIndicatorView(info);
        }
        return indicators;
    }

    private View buldIndicatorView(TabsPagerInfo bean) {
        View view = mInflater.inflate(R.layout.layout_indicator_view, null);
        if (bean.getIconRes() != 0) {
            ImageView icon = (ImageView) view.findViewById(android.R.id.icon1);
            icon.setImageResource(bean.getIconRes());
            icon.setVisibility(View.VISIBLE);
        } else {
            view.findViewById(android.R.id.icon1).setVisibility(View.GONE);
        }
        if (bean.getTextRes() != 0) {
            TextView text = (TextView) view.findViewById(android.R.id.text1);
            text.setText(bean.getTextRes());
            text.setVisibility(View.VISIBLE);
        } else {
            view.findViewById(android.R.id.text1).setVisibility(View.GONE);
        }
        return view;
    }

    public static class TabsPagerInfo {
        private int iconRes;
        private int textRes;
        private View contentView;

        public TabsPagerInfo() {
        }

        public TabsPagerInfo(int iconRes, int textRes, View content) {
            this.iconRes = iconRes;
            this.textRes = textRes;
            this.contentView = content;
        }

        public int getIconRes() {
            return iconRes;
        }

        public void setIconRes(int iconRes) {
            this.iconRes = iconRes;
        }

        public int getTextRes() {
            return textRes;
        }

        public void setTextRes(int textRes) {
            this.textRes = textRes;
        }

        public View getContentView() {
            return contentView;
        }

        public void setContentView(View content) {
            this.contentView = content;
        }

    }

}
