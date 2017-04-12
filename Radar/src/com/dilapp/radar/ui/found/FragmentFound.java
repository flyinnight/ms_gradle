package com.dilapp.radar.ui.found;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.mine.FragmentMineList;
import com.dilapp.radar.ui.topic.ActivityCarePlanList;
import com.dilapp.radar.util.ReleaseUtils;
import com.dilapp.radar.wifi.AllKfirManager;

public class FragmentFound extends BaseFragment implements OnClickListener {
	private TitleView mTitle;
	private FragmentMineList mFragmentMineList;
	private RelativeLayout topic_all_layout;
	private RelativeLayout skin_scheme_layout;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		mFragmentMineList = new FragmentMineList();
		/*Toast.makeText(mContext, "Part of the function has not been developed",
				Toast.LENGTH_LONG).show();*/
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_found_layout);

//		FragmentManager cfm = getChildFragmentManager();
//		if (null == cfm.findFragmentByTag("foundList")
//				|| !mFragmentMineList.isInLayout()) {
//			mFragmentMineList.setGroups(genGroups());
//			getChildFragmentManager()
//					.beginTransaction()
//					.replace(R.id.fragment_container, mFragmentMineList,
//							"foundList").commit();
//		}
		init_view();
		return getContentView();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean haspaired = SharePreCacheHelper.getPairStatus(getActivity());
        if(haspaired && !ReleaseUtils.CAUSE_END_AFTER_SKINTEST){
        		AllKfirManager.getInstance(getActivity()).endSkinTest();
        }
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	private void init_view() {

		mTitle = new TitleView(mContext, findViewById(R.id.vg_toolbar));
		mTitle.setCenterText(R.string.main_found, null);
		topic_all_layout = findViewById(R.id.topic_all_layout);
		skin_scheme_layout = findViewById(R.id.skin_scheme_layout);
		topic_all_layout.setOnClickListener(this);
		skin_scheme_layout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 话题大全
		case R.id.topic_all_layout:
			Intent intent = new Intent(mContext, TopicAllActivity.class);
			startActivity(intent);
			break;
		// 护肤方案
		case R.id.skin_scheme_layout:
			Intent jump_plan = new Intent(mContext, ActivityCarePlanList.class);
			startActivity(jump_plan);
			break;
		default:
			break;
		}
	}
//
//	private ViewGroup getLayout() {
//		FrameLayout vg = new FrameLayout(mContext);
//		vg.setId(R.id.fragment_container);
//		return vg;
//	}

	private List<FragmentMineList.MineGroup> genGroups() {
		OnClickListener l = this;
		List<FragmentMineList.MineGroup> groups = new ArrayList<FragmentMineList.MineGroup>(3);

		// 第一组，
		List<FragmentMineList.MineItem> is1 = new ArrayList<FragmentMineList.MineItem>(2);
		is1.add(new FragmentMineList.MineItem(R.string.found_all_topic,
				R.drawable.found_topic_all, R.string.found_all_topic, false,
				true, l));
		is1.add(new FragmentMineList.MineItem(R.string.found_skin_plan,
				R.drawable.found_skin_scheme, R.string.found_skin_plan, false,
				true, l));
		groups.add(new FragmentMineList.MineGroup(true, true, true, is1));

		// 第2组，
		List<FragmentMineList.MineItem> is2= new ArrayList<FragmentMineList.MineItem>(1);
		is2.add(new FragmentMineList.MineItem(R.string.found_expert_school,
				R.drawable.found_expert_school, R.string.found_expert_school, false,
				true, l));
		groups.add(new FragmentMineList.MineGroup(true, true, true, is2));
		// 第3组
		List<FragmentMineList.MineItem> is3= new ArrayList<FragmentMineList.MineItem>(1);
		is3.add(new FragmentMineList.MineItem(R.string.found_radar_store,
				R.drawable.found_radar_store, R.string.found_radar_store, false,
				true, l));
		groups.add(new FragmentMineList.MineGroup(true, true, true, is3));
		return groups;
	}
}
