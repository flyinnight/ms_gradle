package com.dilapp.radar.ui.found;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Permissions;
import com.dilapp.radar.ui.mine.FragmentMineList;
import com.dilapp.radar.util.ReleaseUtils;

public class TopicMangerFragment extends BaseFragment implements
		OnClickListener {
	private final int JOIN_TOPIC = 1;
	private final int MY_CREATE_TOPIC = 2;
	private final int CREATE_TOPIC = 3;
	private final int MY_CREATE_TOPIC_REQUEST_CODE = 111;
	private final int MY_JOIN_TOPIC_REQUEST_CODE = 112;
	private final int CREATE_NEW_TOPIC_REQUEST_CODE = 112;
	private FragmentMineList fragmentMineList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentMineList = new FragmentMineList();
		fragmentMineList.setGroups(genGroups());
	}

	@Override
	public void onCreateView(ViewGroup container,
			Bundle savedInstanceState) {
		setContentView(genRootView());
		setCacheView(true);
		getChildFragmentManager().beginTransaction().add(R.id.fragment_container, fragmentMineList, "fragmentMineList").commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.string.my_join_topic:
			Intent intent = new Intent(getActivity(), MyJoinTopic.class);
			startActivityForResult(intent, MY_JOIN_TOPIC_REQUEST_CODE);
			break;
		case R.string.my_create_topic:
			Intent jump_created_topic = new Intent(getActivity(),
					MyCreatedTopicActivity.class);
			startActivityForResult(jump_created_topic,
					MY_CREATE_TOPIC_REQUEST_CODE);
			break;
		case R.string.create_topic:
			// if(1 == 1) break;
			//add by kfir
			if(!Permissions.isAdminUser(getActivity()) && !ReleaseUtils.DEBUG_REMOTE_MODE){
				Toast.makeText(getActivity(), "非管理员暂停新建话题", Toast.LENGTH_SHORT).show();
			}else{
				Intent jump_create = new Intent(getActivity(), ActivityTopicEdit.class);
				startActivityForResult(jump_create, CREATE_NEW_TOPIC_REQUEST_CODE);
			}
			break;
		default:
			break;
		}
	}

	private ViewGroup genRootView() {
		FrameLayout fl = new FrameLayout(mContext);
		fl.setId(R.id.fragment_container);
		return fl;
	}


	private List<FragmentMineList.MineGroup> genGroups() {
		OnClickListener l = this;
		List<FragmentMineList.MineGroup> groups = new ArrayList<FragmentMineList.MineGroup>(3);

		// 第一组，只有一个个人资料
		List<FragmentMineList.MineItem> is1 = new ArrayList<FragmentMineList.MineItem>(1);
		is1.add(new FragmentMineList.MineItem(R.string.my_join_topic,
				R.drawable.mine_collect_topic, R.string.my_join_topic, false,
				true, l));
		is1.add(new FragmentMineList.MineItem(R.string.my_create_topic,
				R.drawable.mine_create_topic, R.string.my_create_topic, false,
				true, l));
		groups.add(new FragmentMineList.MineGroup(true, true, true, is1));

		// 第三组，有 设置
		List<FragmentMineList.MineItem> is3 = new ArrayList<FragmentMineList.MineItem>(1);
		is3.add(new FragmentMineList.MineItem(R.string.create_topic,
				R.drawable.mine_new_create, R.string.create_topic, false,
				true, l));
		groups.add(new FragmentMineList.MineGroup(true, true, true, is3));
		return groups;
	}
}
