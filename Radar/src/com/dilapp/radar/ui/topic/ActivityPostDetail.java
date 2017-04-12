package com.dilapp.radar.ui.topic;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.AddPostViewCount;
import com.dilapp.radar.domain.AddPostViewCount.AddPostViewCountReq;
import com.dilapp.radar.domain.Banner;
import com.dilapp.radar.domain.Banner.DeleteBannerReq;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.DeletePostTopic;
import com.dilapp.radar.domain.DeletePostTopic.DeletePostReq;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.LikeDislikePost;
import com.dilapp.radar.domain.LikeDislikePost.LikeDislikePostReq;
import com.dilapp.radar.domain.PostCollection;
import com.dilapp.radar.domain.PostCollection.DeleteCollectionReq;
import com.dilapp.radar.domain.PostDetailsCallBack;
import com.dilapp.radar.domain.PostDetailsCallBack.MFollowPostResp;
import com.dilapp.radar.domain.PostDetailsCallBack.MPostDetailReq;
import com.dilapp.radar.domain.PostDetailsCallBack.MReplyResp;
import com.dilapp.radar.domain.PostOperation;
import com.dilapp.radar.domain.PostOperation.StoreupPostReq;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.domain.PostReleaseCallBack.PostReleaseReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.SolutionCollectApply;
import com.dilapp.radar.domain.SolutionCollectApply.StoreupSolutionReq;
import com.dilapp.radar.domain.impl.PostReleaseCallBackAsyncImpl;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.Permissions;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.admin.ActivityEditTopModel;
import com.dilapp.radar.ui.admin.TopItemParcel;
import com.dilapp.radar.ui.found.ActivityTopicDetail;
import com.dilapp.radar.ui.topic.ActivityPostBase.*;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.UmengUtils;
import com.dilapp.radar.view.CustomScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.dilapp.radar.textbuilder.utils.L.d;
import static com.dilapp.radar.textbuilder.utils.L.i;
import static com.dilapp.radar.textbuilder.utils.L.w;

/**
 * Created by husj1 on 2015/7/6.
 */
public class ActivityPostDetail extends ActivityPostBase implements PostBaseAdapter {
	private final static boolean LIKE_SUCCESS_TOAST = false;
	private final static boolean COLL_SUCCESS_TOAST = false;
	private TitleView mTitle;

	private TextView tv_topic;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_detail);
		setConverter(this);
		initView();
		mTitle = new TitleView(context, findViewById(TitleView.ID_TITLE));
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		tv_topic = findViewById_(R.id.tv_topic);

		if (postMain.getId() == 0 && postMain.getLocalId() != 0) {
			// 留在下面做
		} else {
			requestData(postID, currPage, true, GetPostList.GET_DATA_LOCAL);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			finish();
			break;
		case TitleView.ID_RIGHT:
			onClickOption(v);
			break;
		case R.id.tv_topic: {
			Intent intent = new Intent(this, ActivityTopicDetail.class);
			intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, topicID);
			i("topicID:", "------" + topicID);
			startActivity(intent);
			break;
		}
		case R.id.vg_collection: {
			if ((v.getTag() == null)) {
				d("III_logic", "没有Post, 不能收藏操作");
				break;
			}
			PostBaseEntity data = (PostBaseEntity) v.getTag();
			requestCollection(data, !data.isColl(),
					(Drawable) v.getTag(R.id.vg_collection), v);
			break;
		}
		case R.id.btn_agree:
		case R.id.vg_like: {
			if ((v.getTag() == null)) {
				d("III_logic", "没有Post, 不能点赞");
				break;
			}
			PostBaseEntity data = (PostBaseEntity) v.getTag();
			/*
			 * if (data.isLike()) { Toast.makeText(this, R.string.detail_liked,
			 * Toast.LENGTH_SHORT).show(); break; }
			 */
			requestLike(data, !data.isLike(),
					(Drawable) v.getTag(R.id.vg_like), v,
					(TextView) v.getTag(R.id.btn_agree));
			break;
		}
		case R.id.vg_reply_more: {
			PostBaseEntity parent = (PostBaseEntity) v.getTag();
			int page = Integer.parseInt(v.getTag(R.id.tv_more).toString());
			ViewGroup container = (ViewGroup) v.getTag(R.id.vg_reply_more);
			if (parent == null) {
				w("III", "叫你爸过来领儿子。");
				break;
			}
			requestReplyList(page + 1, parent.getId(), v, v, container, GetPostList.GET_DATA_SERVER);
			break;
		}
		case R.id.btn_send: {
			PostBaseEntity data = (PostBaseEntity) v.getTag();
			ViewGroup container = (ViewGroup) btn_send.getTag(R.id.btn_send);
			if (data == null || container == null) {
				d("III_error", "回复错误，快看逻辑 你是不是删了一些代码呀？");
				return;
			}
			String content = et_message.getText().toString().trim();
			if (content.equals("")) {
				Toast.makeText(this, R.string.detail_input_content,
						Toast.LENGTH_SHORT).show();
				return;
			}
			// Toast.makeText(this, "功能暂未完善", Toast.LENGTH_SHORT).show();
			requestReply(content, data, container);
			break;
		}
		case R.id.btn_edit: {
			mOptionDialog.dismiss();
			Intent intent = new Intent(this, ActivityPostEdit.class);
			intent.putExtra(Constants.EXTRA_EDIT_POST_IS_MODIFY, true);
			intent.putExtra(Constants.EXTRA_EDIT_POST_MODIFY_POST, mAdapter.to(postMain));
			startActivityForResult(intent, REQ_EDIT_POST);
			break;
		}
		case R.id.btn_delete: {
			mOptionDialog.dismiss();
			PostBaseEntity data = (PostBaseEntity) v.getTag();
			if (data == null) {
				d("III_error", "没有数据你让我怎么删？");
				return;
			}
			if (data.getPostLevel() == 0) {
				mDeleteDialog.show();
			} else {
				View removeView = (View) v.getTag(R.id.btn_delete);
				requestDeletePost(data.getId(), data.getPostLevel(), removeView);
			}
			break;
		}
		case R.id.btn_delete_enter: {
			mDeleteDialog.dismiss();
			PostBaseEntity data = (PostBaseEntity) btn_delete.getTag();
			View removeView = (View) btn_delete.getTag(R.id.btn_delete);
			requestDeletePost(data.getId(), data.getPostLevel(), removeView);
			break;
		}
		// add by kfir
		case R.id.btn_top: {
			mOptionDialog.dismiss();
			boolean top = v.getTag() instanceof Boolean ? Boolean
					.parseBoolean(v.getTag().toString()) : false;
			if (top) {
				requestTopRemove(topicID, v);
			} else {
				dispatchActivity(0);
			}
			break;
		}
		case R.id.btn_banner: {
			mOptionDialog.dismiss();
			int priority = v.getTag() instanceof Integer ? Integer.parseInt(v
					.getTag().toString()) : 0;
			if (priority > 0) {
				requestBannerRemove(priority, v);
			} else {
				dispatchActivity(1);
			}
			break;
		}
		}
	}

	private void dispatchActivity(int type) {
		TopItemParcel topParcel = new TopItemParcel();
		topParcel.setType(type);
		topParcel.setTopicId(postMain.getTopicId());
		topParcel.setPostId(postMain.getId());
		Intent topIntent = new Intent(ActivityPostDetail.this,
				ActivityEditTopModel.class);
		topIntent.putExtra(Constants.EXTRA_EDIT_TOP_CONTENT, topParcel);
		startActivity(topIntent);
	}

	/*@Override
	public boolean onLongClick(View v) {
		return super.onLongClick(v);
	}*/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQ_EDIT_POST: {
			if (resultCode == RESULT_OK) {
				requestData(postID, 1, true, GetPostList.GET_DATA_SERVER);
			}
			break;
		}
		}
	}


	/**
	 * 请求回复帖子
	 * 
	 * @param content
	 *            回复的内容
	 * @param data
	 *            回复的对象
	 * @param container
	 *            回复成功后,添加回复的容器
	 */
	private void requestReply(final String content, final PostBaseEntity data,
			final ViewGroup container) {
		final Context context = getApplicationContext();
		PostReleaseCallBack prcb = new PostReleaseCallBackAsyncImpl(this);
		// 准备请求参数
		PostReleaseReq req = new PostReleaseReq();
		req.setPostContent(content);
		req.setPostLevel(2);
		req.setTopicId(data.getTopicId());
		req.setPostTitle("");
		req.setParentId(data.getId());
		if (data.getPostLevel() == 2) {
			req.setParentId(data.getParentId());
			req.setToUserId(data.getUserId());
			// req.setToUserName(data.getUserName());
		}
		d("III_data", "topicID " + data.getTopicId() + ", id " + data.getId()
				+ ", toUserId " + data.getUserId());
		UmengUtils.onEventPostReply(this, "" + data.getTopicId());
		BaseCall<MPostResp> node = new BaseCall<MPostResp>() {
			@Override
			public void call(MPostResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					resp.setPostContent(content);
					resp.setUserName(SharePreCacheHelper.getNickName(context));
					if (data.getPostLevel() == 2) {
						resp.setToUserId(data.getToUserId());
						resp.setToUserName(data.getNickname());
					} else if(data.getPostLevel() == 1) {
						List<PostBaseEntity> comms = data.getComments();
						if (comms == null) {
							comms = new ArrayList<PostBaseEntity>(1);
							data.setComments(comms);
						}
						comms.add(mAdapter.from(resp));
					}
					// 数据拿到后清空按钮上的数据
					et_message.setText("");
					btn_send.setTag(null);
					btn_send.setTag(R.id.btn_send, null);
					hideReply();// 隐藏回复栏
					addReply(mAdapter.from(resp), false, 0, container);// 添加到回复中 TODO here
														// 这个0要改掉
					Toast.makeText(context, R.string.detail_reply_success,
							Toast.LENGTH_SHORT).show();
				} else {
					d("III_data", "msg "
							+ ((resp != null) ? resp.getMessage() : "null"));
					Toast.makeText(context, R.string.detail_reply_failure,
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		addCallback(node);
		prcb.createPostAsync(req, node);
	}

	/**
	 * 请求数据
	 * 
	 * @param postId
	 *            帖子ID
	 * @param page
	 *            第几页
	 * @param clear
	 *            是否清楚老数据
	 */
	private void requestData(final long postId, int page, final boolean clear, int type) {
		if (postId == 0) {
			d("III_logic", " postId 为0，无法执行");
			return;
		}
		if (totalPage != -1 && page > totalPage && !clear) {
			d("III_logic", "当前页 " + page + ", 总页数 " + totalPage
					+ ", 最后一页了，无法加载");
			Toast.makeText(this, R.string.detail_data_finish,
					Toast.LENGTH_SHORT).show();
			return;
		}
		PostDetailsCallBack inter = ReqFactory.buildInterface(this,
				PostDetailsCallBack.class);
		MPostDetailReq bean = new MPostDetailReq();
		bean.setPageNo(page);
		bean.setPostId(postId);
		bean.setUpdateTime(postMain.getUpdateTime());
		d("III_logic", " load datas page " + page + " postId " + postId
				+ ", token " + HttpConstant.TOKEN);
		BaseCall<PostDetailsCallBack.MPostDetailResp> node = new BaseCall<PostDetailsCallBack.MPostDetailResp>() {
			@Override
			public void call(PostDetailsCallBack.MPostDetailResp resp) {
				osv_scroll.onRefreshComplete();
				if (resp != null && resp.isRequestSuccess()) {
					if (clear) {
						resetDatas();
					}
					if (resp.getTotalPage() <= resp.getPageNo()) {// 数据拉完了
						vg_comment_end.setVisibility(View.VISIBLE);
						osv_scroll
								.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					} else {// 还有数据可拉
						vg_comment_end.setVisibility(View.GONE);
						osv_scroll.setMode(PullToRefreshBase.Mode.BOTH);
					}
					totalPage = resp.getTotalPage();
					currPage = resp.getPageNo() + 1;
					if (resp.getResp() != null && resp.getResp().size() > 0) {
						// datas.addAll(resp.getResp());
						d("III_data",
								"查询成功 " + resp.getPageNo() + ", "
										+ resp.getTotalPage() + ", "
										+ resp.getResp().size());
						for (int i = 0; i < resp.getResp().size(); i++) {
							MFollowPostResp b = resp.getResp().get(i);
							if (b.isMain()) {
								if (b.getTopicTitle() == null) {// 为了兼容帖子详情接口没有TopicTitle属性
									b.setTopicTitle(postMain.getTopicTitle());
								}
								postMain = mAdapter.from(b);
								topicID = b.getTopicId();
								if (clear) {
									setUIFromData(postMain, 0);
								}
							} else {
								addComment(vg_comments, false, mAdapter.from(b));
							}
						}
					} else {
						d("III_data", "查询成功 但是没有数据，艹");
					}
					// 一条评论都没有
					if (resp.getPageNo() <= 1
							&& (resp.getResp() == null || resp.getResp().size() <= 1)) {
						vg_comment_end.setVisibility(View.GONE);
						tv_sofa.setVisibility(View.VISIBLE);
					} else {
						tv_sofa.setVisibility(View.GONE);
					}
					if (!addBrowseCount) {
						requestAddBrowseCount(postId);
						addBrowseCount = true;
					}
				} else {
					w("III_data", "查询失败 "
							+ (resp != null ? resp.getMessage() : "null"));
					Toast.makeText(getApplicationContext(),
							R.string.detail_get_data_filure, Toast.LENGTH_SHORT)
							.show();
				}
			}
		};
		addCallback(node);
		inter.getPostDetailsByTypeAsync(bean, node, type);
	}

	/**
	 * 请求收藏
	 * 
	 * @param data
	 *            需要收藏的帖子
	 * @param isColl
	 *            true为收藏，false为取消收藏
	 * @param drawable
	 *            需要改变样式的Drawable
	 * @param view
	 *            触发该事件的View，最好给我，否则用户会重复点，我可以去重复哦
	 */
	private void requestCollection(final PostBaseEntity data, final boolean isColl,
			final Drawable drawable, final View view) {
		PostOperation cp = ReqFactory.buildInterface(this, PostOperation.class);
		StoreupPostReq req = new StoreupPostReq();
		req.setPostId(data.getId());
		req.setStoreUp(isColl);
		d("III_logic", "收藏 postId " + data.getId() + ", is " + isColl);
		if (view != null) {
			view.setClickable(false);
		}
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					if (drawable != null) {
						drawable.setLevel(isColl ? 1 : 0);
					}
					data.setColl(isColl);

					if (COLL_SUCCESS_TOAST) {
						Toast.makeText(
								getApplicationContext(),
								isColl ? R.string.detail_collection_success
										: R.string.detail_discollection_success,
								Toast.LENGTH_SHORT).show();
					}
					// 发送广播更新UI数据
					Intent intent = new Intent(Constants.FOUND_TOPIC_COLLECT);
					context.sendBroadcast(intent);
					/*if (data.isSelectedToSolution()) {
						requestCollectionPlan(data.getId(), isColl);
					}*/
				} else {
					if (drawable != null) {
						drawable.setLevel(!isColl ? 1 : 0);
					}
					// data.setStoreUp(!isColl);
					w("III_data", "收藏失败 "
							+ (resp != null ? resp.getMessage() : "null"));
					Toast.makeText(
							getApplicationContext(),
							isColl ? R.string.detail_collection_failure
									: R.string.detail_discollection_failure,
							Toast.LENGTH_SHORT).show();
				}

				if (view != null) {
					view.setClickable(true);
				}
			}
		};
		addCallback(node);
		cp.storeupPostAsync(req, node);
	}

	/**
	 * 请求点赞
	 * 
	 * @param data
	 *            请求点赞帖子
	 * @param isLike
	 *            点赞，一般为true
	 * @param drawable
	 *            需要改变样式的Drawable
	 * @param view
	 *            触发该事件的View，最好给我，否则用户会重复点，我可以去重复哦
	 * @param tv
	 *            需要显示点赞数据的TextView，我可以在点赞成功后帮你+1哦
	 */
	private void requestLike(final PostBaseEntity data, final boolean isLike,
			final Drawable drawable, final View view, final TextView tv) {

		LikeDislikePost ldp = ReqFactory.buildInterface(this,
				LikeDislikePost.class);
		LikeDislikePostReq req = new LikeDislikePostReq();
		req.setPostId(data.getId());
		req.setLike(isLike);
		d("III_logic", "点赞 postId " + data.getId() + ", like " + isLike);

		if (view != null) {
			view.setClickable(false);
		}
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					if (drawable != null) {
						drawable.setLevel(isLike ? 1 : 0);
					}
					if (tv != null) {
						try {
							tv.setText(""
									+ (Integer
											.parseInt(tv.getText().toString()) + (isLike ? 1
											: -1)));
						} catch (Exception e) {
						}
					}
					data.setLikeCount(data.getLikeCount() + (isLike ? 1 : -1));
					data.setLike(isLike);
					if (isLike) {
						view.startAnimation(AnimationUtils.loadAnimation(
								getApplicationContext(), R.anim.like_post));
					}
					// post.setLike(isColl);
					if (LIKE_SUCCESS_TOAST) {
						Toast.makeText(
								getApplicationContext(),
								isLike ? R.string.detail_like_success
										: R.string.detail_cancel_success,
								Toast.LENGTH_SHORT).show();
					}
					// 点赞成功的话，按钮就不让点了
				} else {
					if (drawable != null) {
						drawable.setLevel(!isLike ? 1 : 0);
					}
					// post.setLike(!isColl);
					w("III_data", "操作失败 "
							+ (resp != null ? resp.getMessage() : "null"));
					Toast.makeText(
							getApplicationContext(),
							isLike ? R.string.detail_like_failure
									: R.string.detail_cancel_failure,
							Toast.LENGTH_SHORT).show();
				}
				if (view != null) {
					view.setClickable(true);
				}

			}
		};
		addCallback(node);
		ldp.likePostAsync(req, node);
	}

	private void requestDeletePost(long postId, final int postLevel,
			final View removeView) {
		// TODO requestDeletePost
		showWaitingDialog((AsyncTask) null);
		DeletePostTopic dpt = ReqFactory.buildInterface(this,
				DeletePostTopic.class);
		DeletePostReq req = new DeletePostReq();
		req.setPostId(postId);
		req.setPostLevel(postLevel);
		d("III_logic", "delete post id " + postId + ", level " + postLevel);
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {

				if (resp != null && resp.isRequestSuccess()) {
					if (removeView != null && removeView.getParent() != null) {
						((ViewGroup) removeView.getParent())
								.removeView(removeView);
					}

					if (postLevel == 1) {
						// 总评论数 - 1
						postMain.setCommentCount(postMain.getCommentCount() - 1);
						tv_total_comment.setText(getString(
								R.string.detail_what_total_reply,
								postMain.getCommentCount() + ""));
						tv_reply.setText(postMain.getCommentCount() + "");

						if (vg_comments.getChildCount() == 0) {
							if (tv_sofa.getVisibility() != View.VISIBLE) {
								tv_sofa.setVisibility(View.VISIBLE);
							}
							if (vg_comment_end.getVisibility() != View.GONE) {
								vg_comment_end.setVisibility(View.GONE);
							}
						}
					} else if (postLevel == 0) {
						setResult(RESULT_FIRST_USER);
						finish();
					}
					d("III_logic", "删除成功");
					Toast.makeText(getApplicationContext(),
							R.string.detail_delete_success, Toast.LENGTH_SHORT)
							.show();
				} else {
					d("III_logic", "删除失败");
					Toast.makeText(getApplicationContext(),
							R.string.detail_delete_failure, Toast.LENGTH_SHORT)
							.show();
				}
				dimessWaitingDialog();
			}
		};
		addCallback(node);
		dpt.deletePostAsync(req, node);
	}

	private void requestAddBrowseCount(long postId) {
		AddPostViewCount apvc = ReqFactory.buildInterface(this,
				AddPostViewCount.class);
		AddPostViewCountReq req = new AddPostViewCountReq();
		req.setPostId(postId);
		req.setViewCount(1);
		apvc.addPostViewCountAsync(req, null);
	}

	/**
	 * 请求回复列表
	 * 
	 * @param page
	 *            第几页
	 * @param post
	 *            父贴的ID
	 * @param click
	 *            触发此请求的按钮
	 * @param more
	 *            “显示更多”控件
	 * @param container
	 *            容器
	 */
	private void requestReplyList(int page, long post, final View click,
			final View more, final ViewGroup container, int type) {
		if (post == 0) {
			d("III_logic", " postId 为0，无法执行");
			return;
		}
		int totalPage = Integer.parseInt(more.getTag(R.id.vg_loading)
				.toString());
		if (totalPage != -1 && page > totalPage) {
			d("III_logic", "当前页 " + page + ", 总页数 " + totalPage
					+ ", 最后一页了，无法加载");
			Toast.makeText(this, R.string.detail_data_finish,
					Toast.LENGTH_SHORT).show();
			return;
		}
		PostDetailsCallBack inter = ReqFactory.buildInterface(this,
				PostDetailsCallBack.class);
		MPostDetailReq req = new MPostDetailReq();
		req.setPageNo(page);
		req.setPostId(post);

		// 显示加载
		more.findViewById(R.id.vg_loading).setVisibility(View.VISIBLE);
		more.findViewById(R.id.tv_more).setVisibility(View.GONE);
		if (click != null) {
			click.setClickable(false);
		}
		d("III", "replys " + post + ", page " + page);
		BaseCall<MReplyResp> node = new BaseCall<MReplyResp>() {
			@Override
			public void call(MReplyResp resp) {
				more.findViewById(R.id.vg_loading).setVisibility(View.GONE);
				more.findViewById(R.id.tv_more).setVisibility(View.VISIBLE);
				if (click != null) {
					click.setClickable(true);
				}
				if (resp != null && resp.isRequestSuccess()) {
					if (more.getParent() != null) {
						((ViewGroup) more.getParent()).removeView(more);
					}
					if (resp.getPageNo() == 1) {// 当前页为 1，把View清楚一遍
						container.removeAllViews();
					}
					more.setTag(R.id.tv_more, resp.getPageNo());// 当前的页数
					more.setTag(R.id.vg_loading, resp.getTotalPage());// 总页数
					// more
					d("III", "获取列表成功 "
							+ (resp.getComment() != null ? resp.getComment()
									.size() : 0) + ", " + resp.getPageNo()
							+ "/" + resp.getTotalPage());
					if (resp.getComment() != null) {
						final int size = resp.getComment().size();
						for (int i = 0; i < size; i++) {
							MPostResp reply = resp.getComment().get(i);
							if (reply == null)
								continue;
							addReply(mAdapter.from(reply), true, -1, container);
						}
						if (resp.getPageNo() < resp.getTotalPage()) {
							// 不是最后一页
							container.addView(more);
						}
					}
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.detail_get_data_filure, Toast.LENGTH_SHORT)
							.show();
					d("III", "获取列表失败");
				}
			}
		};
		addCallback(node);
		inter.getReplyByTypeAsync(req, node, type);
	}

	private void requestCollectionPlan(long postId, boolean focus) {
		d("III_request", "收藏护肤方案 " + postId + ", is " + focus);
		SolutionCollectApply sca = ReqFactory.buildInterface(this,
				SolutionCollectApply.class);
		StoreupSolutionReq req = new StoreupSolutionReq();
		req.setPostId(postId);
		req.setSelectedSolution(focus);
		sca.storeupPostAsSolutionAsync(req, null/*
												 * new BaseCall<BaseResp>() {
												 * 
												 * @Override public void
												 * call(BaseResp resp) { if
												 * (resp != null &&
												 * resp.isRequestSuccess()) {
												 * d("III_request", "收藏护肤方案成功");
												 * } else { d("III_request",
												 * "收藏护肤方案失败 " + (resp != null ?
												 * resp.getMessage() : null)); }
												 * } }
												 */);
	}

	private void requestTopRemove(long topicId, final View top) {
		if (topicId <= 0) {
			w("III_request", "topicId " + topicId + ", 你这是几个意思?");
			return;
		}

		showWaitingDialog((AsyncTask) null);
		PostCollection pc = ReqFactory.buildInterface(this,
				PostCollection.class);

		DeleteCollectionReq dcr = new DeleteCollectionReq();
		dcr.setTopicId(topicId);
		d("III_request", "移除精选贴 topicId " + topicId);
		BaseCall<BaseResp> call = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				dimessWaitingDialog();
				if (resp != null && resp.isRequestSuccess()) {
					if (top != null) {
						top.setTag(false);
						if (top instanceof TextView) {
							((TextView) top).setText(R.string.edit_post_top);
						}
					}
					setResult(RESULT_FIRST_USER, getIntent());
					finish();
					Toast.makeText(ActivityPostDetail.this,
							R.string.edit_post_remove_success,
							Toast.LENGTH_SHORT).show();
					d("III_request", "移除精选成功");
				} else {
					Toast.makeText(
							ActivityPostDetail.this,
							getString(R.string.edit_post_remove_failure)
									+ " msg "
									+ (resp != null ? resp.getMessage() : null),
							Toast.LENGTH_SHORT).show();
					w("III_request",
							"移除精选失败 "
									+ (resp != null ? resp.getMessage() : null));
				}
			}
		};
		addCallback(call);
		pc.deletePostCollectionAsync(dcr, call);
	}

	private void requestBannerRemove(final int priority, final View banner) {
		if (priority <= 0) {
			w("III_request", "priority " + priority + ", 你这是几个意思?");
			return;
		}
		Banner b = ReqFactory.buildInterface(this, Banner.class);
		DeleteBannerReq dbr = new DeleteBannerReq();
		dbr.setPriority(priority);
		d("III_request", "移除Banner priority " + priority);
		BaseCall<BaseResp> call = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					if (banner != null) {
						banner.setTag(0);
						if (banner instanceof TextView) {
							((TextView) banner)
									.setText(R.string.edit_post_banner);
						}
					}
					setResult(RESULT_FIRST_USER, getIntent());
					finish();
					d("III_request", "Banner 移除成功");
					Toast.makeText(
							ActivityPostDetail.this,
							getString(R.string.edit_post_remove_success)
									+ "priority " + priority,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(
							ActivityPostDetail.this,
							getString(R.string.edit_post_remove_failure)
									+ " msg "
									+ (resp != null ? resp.getMessage() : null),
							Toast.LENGTH_SHORT).show();
					w("III_request",
							"Banner 移除失败 "
									+ (resp != null ? resp.getMessage() : null));
				}
			}
		};
		addCallback(call);
		b.deleteBannerAsync(dbr, call);

	}

	/**
	 * 设置用户信息以及主贴内容
	 * 
	 * @param data
	 *            数据
	 * @param status
	 *            暂时没卵用
	 */
	@Override
	protected void setUIFromData(PostBaseEntity data, int status) {
		if (data == null) {
			vg_loading.setVisibility(View.GONE);
			Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
			return;
		}
		super.setUIFromData((data), status);
		String title = data.getTitle() == null ? "unknown" : data
				.getTitle();
		String topic = getString(R.string.detail_topic,
				data.getTopicTitle() == null ? "unknown" : data.getTopicTitle());
		boolean noShowTopic = TopicHelper.isSpecialTopic(data.getTopicId());
		mTitle.setCenterText(title, null);
		if (isPreview()) {
			mTitle.setRightIcon(null, this);
		} else if (mAdapter.canDelete(mAdapter.to(data))) {
			mTitle.setRightIcon(R.drawable.btn_more, this);
		}
		tv_topic.setText(topic);
		tv_topic.setVisibility(noShowTopic ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onPullDownToRefresh(
			PullToRefreshBase<CustomScrollView> refreshView) {
		d("III_logic", "下拉触发，开始流程,代号： down");
		requestData(postID, 1, true, GetPostList.GET_DATA_SERVER);
	}

	@Override
	public void onPullUpToRefresh(
			PullToRefreshBase<CustomScrollView> refreshView) {
		d("III_logic", "上拉触发，开始流程,代号： up");
		requestData(postID, currPage, false, GetPostList.GET_DATA_SERVER);
	}

	@Override
	public PostBaseEntity from(Serializable o) {
		MPostResp resp = (MPostResp) o;
		if (resp == null) return null;

		PostBaseEntity entity = new PostBaseEntity();
		entity.setId(resp.getId());
		entity.setLocalId(resp.getLocalPostId());
		entity.setParentId(resp.getPid());
		entity.setUserId(resp.getUserId());
		entity.setEmUserId(resp.getEMUserId());
		entity.setGender(resp.isGender());
		entity.setUserLevel(resp.getLevel());
		entity.setHeadUrl(resp.getUserHeadIcon());
		entity.setNickname(resp.getUserName());
		entity.setFocusUser(resp.isFollowsUser());
		entity.setToUserId(resp.getToUserId());
		entity.setToNickname(resp.getToUserName());
		entity.setTitle(resp.getPostTitle());
		entity.setContent(resp.getPostContent());
		entity.setTopicTitle(resp.getTopicTitle());
		entity.setPostLevel(resp.getPostLevel());
		entity.setLikeCount(resp.getLike());
		entity.setCollCount(resp.getStoreupNum());
		entity.setBrowseCount(resp.getPostViewCount());
		entity.setCommentCount(resp.getTotalFollows());
		entity.setTopicId(resp.getTopicId());
		entity.setCreateTime(resp.getCreateTime());
		entity.setUpdateTime(resp.getUpdateTime());
		entity.setColl(resp.isStoreUp());
		entity.setLike(resp.isLike());
		if (resp instanceof MFollowPostResp) {
			MFollowPostResp fresp = (MFollowPostResp) resp;
			List<MPostResp> comms = fresp.getComment();
			if (comms != null && comms.size() > 0) {
				List<PostBaseEntity> entites = new ArrayList<PostBaseEntity>(comms.size());
				for (int i = 0; i < comms.size(); i++) {
					entites.add(from(comms.get(i)));
				}
				entity.setComments(entites);
			}
		}
		return entity;
	}

	@Override
	public Serializable to(PostBaseEntity entity) {
		if (entity == null) return null;
		MPostResp resp = entity.getComments() != null ? new MFollowPostResp() : new MPostResp();
		resp.setId(entity.getId());
		resp.setLocalPostId(entity.getLocalId());
		resp.setPid(entity.getParentId());
		resp.setUserId(entity.getUserId());
		resp.setEMUserId(entity.getEmUserId());
		resp.setGender(entity.getGender());
		resp.setLevel(entity.getUserLevel());
		resp.setUserHeadIcon(entity.getHeadUrl());
		resp.setUserName(entity.getNickname());
		resp.setFollowsUser(entity.isFocusUser());
		resp.setToUserId(entity.getToUserId());
		resp.setToUserName(entity.getToNickname());
		resp.setPostTitle(entity.getTitle());
		resp.setPostContent(entity.getContent());
		resp.setTopicTitle(entity.getTopicTitle());
		resp.setPostLevel(entity.getPostLevel());
		resp.setLike(entity.getLikeCount());
		resp.setStoreupNum(entity.getCollCount());
		resp.setPostViewCount(entity.getBrowseCount());
		resp.setTotalFollows(entity.getCommentCount());
		resp.setTopicId(entity.getTopicId());
		resp.setCreateTime(entity.getCreateTime());
		resp.setUpdateTime(entity.getUpdateTime());
		resp.setStoreUp(entity.isColl());
		resp.setLike(entity.isLike());

		if (resp instanceof MFollowPostResp) {
			MFollowPostResp fresp = (MFollowPostResp) resp;
			List<PostBaseEntity> entities = entity.getComments();
			if (entities != null && entities.size() > 0) {
				List<MPostResp> posts = new ArrayList<MPostResp>(entities.size());
				for (int i = 0; i < entities.size(); i++) {
					posts.add((MPostResp) to(entities.get(i)));
				}
				fresp.setComment(posts);
			}
		}
		return resp;
	}

	@Override
	public boolean canDelete(Serializable o) {
		return Permissions.canPostDelete(this, (MPostResp) o);
	}

	@Override
	public boolean canModify(Serializable o) {
		return Permissions.canPostModify(this, (MPostResp) o);
	}
}
