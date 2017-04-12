package com.dilapp.radar.ui.mine;

import static com.dilapp.radar.textbuilder.utils.L.i;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.UpdateGetUser;
import com.dilapp.radar.domain.UpdateGetUser.GetUserResp;
import com.dilapp.radar.util.DateUtils;
import com.dilapp.radar.util.MineInfoUtils;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;

/**
 * 我的会话
 * 
 * @author Administrator
 * 
 */
public class MineConversationAdapter extends ArrayAdapter<EMConversation> {

	private static final String TAG = "MineConversationAdapter";
	private final int REQUEST_SUCCESS = 1110;
	private Context context;
	private LayoutInflater inflater;
	private List<EMConversation> conversationList;
	private List<EMConversation> copyConversationList;
	private GetUserResp mOthersInfoList = null;// 后台请求下来的所有数据
	private String portraitUrl, nickName, EMUserId;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REQUEST_SUCCESS:
				// portraitUrl = mOthersInfoList.getPortrait();
				// ViewHolder v = new ViewHolder();
				// if (!TextUtils.isEmpty(portraitUrl)) {
				// MineInfoUtils
				// .setImageFromUrl(
				// HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP
				// + mOthersInfoList.getPortrait(),
				// v.portrait);
				// Log.i(TAG, "portraitUrl:" + portraitUrl);
				// }
				break;
			default:
				break;
			}
		}
	};

	public MineConversationAdapter(Context context, int textViewResourceId,
			List<EMConversation> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.conversationList = objects;
		copyConversationList = new ArrayList<EMConversation>();
		copyConversationList.addAll(conversationList);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_chat_history, parent,
					false);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.unreadLabel = (TextView) convertView
					.findViewById(R.id.unread_msg_number);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.portrait = (ImageView) convertView
					.findViewById(R.id.portrait);
			holder.msgState = convertView.findViewById(R.id.msg_state);
			holder.list_item_layout = (RelativeLayout) convertView
					.findViewById(R.id.list_item_layout);
			convertView.setTag(holder);
		}
		if (position % 2 == 0) {
			holder.list_item_layout
					.setBackgroundResource(R.drawable.mm_listitem);
		} else {
			holder.list_item_layout
					.setBackgroundResource(R.drawable.mm_listitem_grey);
		}
		// 获取用户username或者群组groupid
		// 获取与此用户/群组的会话
		EMConversation conversation = getItem(position);
		if (conversation.getUnreadMsgCount() > 0) {
			// 显示与此用户的消息未读数
			holder.unreadLabel.setText(String.valueOf(conversation
					.getUnreadMsgCount()));
			holder.unreadLabel.setVisibility(View.VISIBLE);
		} else {
			holder.unreadLabel.setVisibility(View.INVISIBLE);
		}
		if (conversation.getMsgCount() != 0) {
			// 把最后一条消息的内容作为item的message内容
			EMMessage lastMessage = conversation.getLastMessage();
			holder.message.setText(getMessageDigest(lastMessage,
					this.getContext()));
			holder.time.setText(DateUtils.getTimestampString(new Date(
					lastMessage.getMsgTime())));
			if (lastMessage.direct == EMMessage.Direct.SEND
					&& lastMessage.status == EMMessage.Status.FAIL) {
				holder.msgState.setVisibility(View.VISIBLE);
			} else {
				holder.msgState.setVisibility(View.GONE);
			}
		}
		EMUserId = conversation.getUserName();
		if (!TextUtils.isEmpty(EMUserId)) {
			getOthersInfo(EMUserId, holder.name, holder.portrait);
		}
		i(TAG, "EMUserId:" + EMUserId);
		return convertView;
	}

	/**
	 * 根据消息内容和消息类型获取消息内容提示
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case TXT: // 文本消息
			TextMessageBody txtBody = (TextMessageBody) message.getBody();
			digest = txtBody.getMessage();
			break;
		case IMAGE:
			// ImageMessageBody imageBody = (ImageMessageBody)
			// message.getBody();
			digest = "[图片]";
			break;
		default:
			EMLog.e(TAG, "unknow type");
			return "";
		}

		return digest;
	}

	/**
	 * 根据环信ID(EMUserId)获取他人信息 包括头像和昵称
	 */
	private void getOthersInfo(final String EMUserId,
			final TextView tv_userName, final ImageView imageView) {
		UpdateGetUser mDetail = ReqFactory.buildInterface(context,
				UpdateGetUser.class);
		mDetail.getUserByEMIdAsync(EMUserId,
				new BaseCall<UpdateGetUser.GetUserResp>() {
					@Override
					public void call(GetUserResp resp) {
						if (resp != null && resp.isRequestSuccess()) {
							i(TAG, "resp:" + resp);
							mOthersInfoList = (GetUserResp) resp;
							if (mOthersInfoList != null) {
								portraitUrl = mOthersInfoList.getPortrait();
								nickName = mOthersInfoList.getName();
								if (TextUtils.isEmpty(nickName))
									tv_userName.setText(EMUserId, null);
								else
									tv_userName.setText(nickName, null);
								i(TAG, "portraitUrl:" + portraitUrl
										+ "EMUserId:" + EMUserId);
								MineInfoUtils.setImage(portraitUrl, imageView);
							}
						}
					}
				});
	}

	private static class ViewHolder {
		/** 和谁的聊天记录 */
		TextView name;
		/** 消息未读数 */
		TextView unreadLabel;
		/** 最后一条消息的内容 */
		TextView message;
		/** 最后一条消息的时间 */
		TextView time;
		/** 用户头像 */
		ImageView portrait;
		/** 最后一条消息的发送状态 */
		View msgState;
		/** 整个list中每一行总布局 */
		RelativeLayout list_item_layout;

	}
}
