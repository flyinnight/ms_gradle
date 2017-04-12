package com.dilapp.radar.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dilapp.radar.R;

/**
 * Created by husj1 on 2015/5/29.
 */
public class FragmentDeveloping extends BaseFragment implements View.OnClickListener {
    private String titleText;
    private TitleView mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_developing);
        setCacheView(true);

        if(getArguments() != null) {
            titleText = getArguments().getString("titleText");
        } else {
            titleText = "";
        }
        ViewGroup title = findViewById(TitleView.ID_TITLE);
        mTitle = new TitleView(mContext, title);
        mTitle.setCenterText(titleText, null);

        View click = findViewById(R.id.view_click);
        click.setClickable(true);
        click.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (getActivity() instanceof ActivityTabs) {
            ((ActivityTabs) getActivity()).setCurrentTab(1);
        }
    }
}
