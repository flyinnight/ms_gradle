package com.dilapp.radar.ui.mine;

import static com.dilapp.radar.textbuilder.utils.L.i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.chat.ChatActivity2;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.UpdateGetUser;
import com.dilapp.radar.domain.UpdateGetUser.GetUserResp;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.util.CollectionUtil;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

/**
 * 我的消息，最近聊天
 * 
 * @author Administrator
 * 
 */
public class MineMessageFragment extends BaseFragment implements
		OnItemClickListener {
	private final static String TAG = "MineMessageFragment";
	private static final int REQUEST_SUCCESS = 1211;
	private MineConversationAdapter adapter = null;
	private ListView listView;
	private RelativeLayout defult_layout;
	private TextView tv_defult;
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();
	private NewMessageBroadcastReceiver msgReceiver;
	private GetUserResp mOthersInfoList = null;// 后台请求下来的所有数据
	private String EMUserId;
	private boolean isClearConversation = false;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REQUEST_SUCCESS:
				if (mOthersInfoList != null) {
					String othersPortrait = mOthersInfoList.getPortrait();
					String userName = mOthersInfoList.getName();
					Intent intent = new Intent(getActivity(),
							ChatActivity2.class);
					intent.putExtra("EMuserID", EMUserId);
					intent.putExtra("othersPortrait", othersPortrait);
					intent.putExtra("userName", userName);
					startActivityForResult(intent, 1);
				}

				break;
			default:
				break;
			}
		}
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setContentView(R.layout.mine_conversation_history);
		setCacheView(true);
		// 只有注册了广播才能接收到新消息，目前离线消息，在线消息都是走接收消息的广播（离线消息目前无法监听，在登录以后，接收消息广播会执行一次拿到所有的离线消息）
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		getActivity().registerReceiver(msgReceiver, intentFilter);
		EMChat.getInstance().setAppInited();
		initView();
		return getContentView();
	}

	private void initView() {
		listView = findViewById(R.id.listView);
		defult_layout = findViewById(R.id.defult_layout);
		tv_defult = findViewById(R.id.tv_defult);
		listView.setOnItemClickListener(this);

		getMessageList();

		/**
		 * 获取当前聊天对象的未读消息数（传入对方userId）
		 * 
		 * 仅供测试
		 */
		// String userId = "yuanb4";
		// conversation = EMChatManager.getInstance().getConversation(userId);
		// int messageCount = conversation.getUnreadMsgCount();
		// int messageAllCount = conversation.getMsgCount();
		// Log.i("MineMessageFragment", userId + "--unReadMessageCount:"
		// + messageCount);
		// Log.i("MineMessageFragment", "--messageAllCount:" + messageAllCount);

	}

	/**
	 * 获取会话列表
	 */
	private void getMessageList() {
		if (adapter != null) {
			adapter.clear();
		} else if (conversationList != null) {
			conversationList.clear();
		}
		conversationList.addAll(loadConversationsWithRecentChat());
		if (!CollectionUtil.isEmpty(conversationList)) {
			if (defult_layout.getVisibility() == View.VISIBLE)
				defult_layout.setVisibility(View.GONE);
			adapter = new MineConversationAdapter(getActivity(), 1,
					conversationList);
			// 设置adapter
			listView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			Log.i(TAG, "conversationList:--==" + conversationList.size());
		} else {
			defult_layout.setVisibility(View.VISIBLE);
			tv_defult.setText("您还没有收到任何消息");
		}
	}

	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 注销广播
			// abortBroadcast();
			// 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
			String msgId = intent.getStringExtra("msgid");
			// 发送方
			String username = intent.getStringExtra("from");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(username);
			// 如果是群聊消息，获取到group id
			// if (message.getChatType() == ChatType.GroupChat) {
			// username = message.getTo();
			// }
			if (!username.equals(username)) {
				// 消息不是发给当前会话，return
				return;
			}
			if (adapter == null) {
				getMessageList();
			} else
				adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 获取所有会话
	 * 
	 * @param context
	 * @return +
	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		// 过滤掉messages size为0的conversation
		/**
		 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
		 * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
		 */
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					sortList.add(new Pair<Long, EMConversation>(conversation
							.getLastMessage().getMsgTime(), conversation));
				}
			}
		}
		try {
			// Internal is TimSort algorithm, has bug
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(
			List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList,
				new Comparator<Pair<Long, EMConversation>>() {
					@Override
					public int compare(final Pair<Long, EMConversation> con1,
							final Pair<Long, EMConversation> con2) {

						if (con1.first == con2.first) {
							return 0;
						} else if (con2.first > con1.first) {
							return 1;
						} else {
							return -1;
						}
					}

				});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		EMConversation conversation = adapter.getItem(position);
		EMUserId = conversation.getUserName();
		// Intent intent = new Intent(getActivity(),
		// ChatActivity2.class);
		// intent.putExtra("EMuserID", EMUserId);

		// //仅适用测试环境，EMuserId就是userId
		// intent.putExtra("userId", EMUserId);

		// startActivityForResult(intent, 1);
		if (!TextUtils.isEmpty(EMUserId)) {
			getOthersInfo(EMUserId);
		}
		Log.i(TAG, "conversation:" + conversation.getUserName());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			isClearConversation = data.getBooleanExtra("isClearConversation",
					false);
			if (isClearConversation) {
				getMessageList();
			} else
				adapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(msgReceiver);
		super.onDestroy();
	}

	/**
	 * 根据环信获取他人信息
	 */
	private void getOthersInfo(String EMUserId) {
		UpdateGetUser mDetail = ReqFactory.buildInterface(getActivity(),
				UpdateGetUser.class);
		mDetail.getUserByEMIdAsync(EMUserId,
				new BaseCall<UpdateGetUser.GetUserResp>() {
					@Override
					public void call(GetUserResp resp) {
						if (resp != null && resp.isRequestSuccess()) {
							i(TAG, "resp:" + resp);
							mOthersInfoList = (GetUserResp) resp;
							if (mOthersInfoList != null) {
								mHandler.sendEmptyMessage(REQUEST_SUCCESS);
							}
						}
					}
				});
	}
}
