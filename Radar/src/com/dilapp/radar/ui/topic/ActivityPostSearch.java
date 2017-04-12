package com.dilapp.radar.ui.topic;

import static com.dilapp.radar.textbuilder.utils.L.d;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.SearchCallBack;
import com.dilapp.radar.domain.SearchCallBack.PostSearchReq;
import com.dilapp.radar.domain.SearchCallBack.PostSearchResp;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.util.AndroidBugsSolution;
import com.dilapp.radar.util.SerializableUtil;
import com.dilapp.radar.view.DividerItemDecoration;
import com.dilapp.radar.view.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

public class ActivityPostSearch extends BaseActivity implements
		OnClickListener, TextWatcher, OnKeyListener,
		AdapterView.OnItemClickListener,
		PullToRefreshBase.OnRefreshListener2<ListView> {

	private final static int MODE_LOADING = 1;
	private final static int MODE_INPUT_HISTORY = 2;
	private final static int MODE_SEARCH_RESULT = 3;
	private final static int MODE_NO_INPUT_HISTORY = 4;
	private final static int MODE_NO_SEARCH_RESULT = 5;

	private EditText et_taste_name;
	private TextView btn_cancel;
	private TextView tv_not_history;
	private TextView tv_not_post;
	private ImageButton btn_clear_input;
	private RecyclerView rv_input_history;
	private HistoryAdapter rvAdapter;
	private ListView lv_post;
	private PullToRefreshListView ptr_post;
	private PostAdapter postAdapter;
	private View vg_waiting;

	private String name = "post";
	private int size = 10;
	private String[] arrSearch = new String[1];
	// private LinkedList<String> mHistory;

	private InputMethodManager imm;
	private String searchStr;
	private int page;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_search);
		AndroidBugsSolution.assistActivity(this, null);

		Context context = getApplicationContext();

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		btn_cancel = findViewById_(R.id.btn_cancel);
		btn_clear_input = findViewById_(R.id.btn_clear_input);
		tv_not_history = findViewById_(R.id.tv_not_history);
		tv_not_post = findViewById_(R.id.tv_not_post);
		et_taste_name = findViewById_(R.id.et_taste_name);
		et_taste_name.addTextChangedListener(this);
		rv_input_history = findViewById_(R.id.rv_input_history);
		rv_input_history.setLayoutManager(new LinearLayoutManager(context));
		ptr_post = findViewById_(R.id.lv_post);
		ptr_post.setOnRefreshListener(this);
		lv_post = ptr_post.getRefreshableView();
		postAdapter = new PostAdapter(this,
				getLayoutInflater());
		postAdapter.setItemViewClickable(false);
		lv_post.setAdapter(postAdapter);
		lv_post.setOnItemClickListener(this);
		vg_waiting = findViewById(R.id.vg_waiting);
		et_taste_name.setTag(R.id.et_taste_name, 0);
		et_taste_name.setOnKeyListener(this);

		File ser = new File(getCacheDir(), name + "_history.ser");
		LinkedList<String> history = SerializableUtil
				.readSerializableObject(ser.getAbsolutePath());
		d("III", "read list " + history);
		if (history == null)
			history = new LinkedList<String>();

		rvAdapter = new HistoryAdapter(context, history);
		rv_input_history.setAdapter(rvAdapter);
		initRecyclerView(rv_input_history);
		if (history != null && history.size() > 0) {
			tv_not_history.setVisibility(View.GONE);
		} else {
			tv_not_history.setVisibility(View.VISIBLE);
		}

		ViewCompat.setTransitionName(et_taste_name, "share:et_taste_name");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			// 将选择的项置顶
			// if (resultCode == RESULT_OK) {
			// 保存历史记录到文件
			// saveHistory();
			// Intent data = null;
			// if (isInputState()) {
			// data = new Intent();
			// data.putExtra(Constants.RESULT_INPUT_HISTORY_TEXT,
			// et_taste_name.getText().toString());
			// }
			// setResult(resultCode, data);
			// if ("".equals(et_taste_name.getText().toString())) {
			ActivityCompat.finishAfterTransition(this);
			/*
			 * } else { finish(); }
			 */
			break;
		case R.id.btn_clear_input:
			et_taste_name.setText("");
			setInputState(false);
			btn_clear_input.setVisibility(View.GONE);
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			if (!"".equals(et_taste_name.getText().toString().trim())) {
				startSearchPost();
				return true;
			}
		}
		return false;
	}

	public void requestSearchPost(final int page, String text, final View click) {

		if (click != null) {
			click.setClickable(false);
		}
		arrSearch[0] = text;
		SearchCallBack sc = ReqFactory.buildInterface(this,
				SearchCallBack.class);
		PostSearchReq req = new PostSearchReq();
		req.setStartNo(page);
		req.setPostParam(arrSearch);
		d("III_data", "search post page " + page + ", text " + text);
		BaseCall<PostSearchResp> call = new BaseCall<PostSearchResp>() {
			@Override
			public void call(PostSearchResp resp) {
				ptr_post.onRefreshComplete();
				if (click != null) {
					click.setClickable(true);
				}
				if (resp != null && resp.isRequestSuccess()) {
					ptr_post.setMode(PullToRefreshBase.Mode.BOTH);
					ActivityPostSearch.this.page = resp.getPageNo();
					if (page == 1) {
						// 第一页， 清除原数据
						postAdapter.getList().clear();
					}
					if (resp.getDatas() != null && resp.getDatas().size() != 0) {
						// 有数据的话显示数据
						postAdapter.getList().addAll(resp.getDatas());
						postAdapter.notifyDataSetChanged();
					}
					if (page == 1) {// 这个要在填充数据之后设置
						ensureUIMode(MODE_SEARCH_RESULT);
					}
					/*
					 * else { ensureUIMode(MODE_NO_SEARCH_RESULT); }
					 */
					d("III_data",
							"搜索成功 page "
									+ resp.getPageNo()
									+ "/"
									+ resp.getTotalPage()
									+ ", size "
									+ (resp.getDatas() != null ? resp
											.getDatas().size() : -1));
				} else if (resp != null
						&& "query posts failed".equals(resp.getMessage())) {
					if (page == 1) {
                        postAdapter.getList().clear();
                        postAdapter.notifyDataSetChanged();
						ensureUIMode(MODE_NO_SEARCH_RESULT);
					} else {
						ptr_post.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					}
					d("III_data", "搜索成功 没有帖子 page " + page);
				} else {
					ensureUIMode(MODE_NO_SEARCH_RESULT);
					d("III_data", "搜索失败 "
							+ (resp != null ? resp.getMessage() : null));
				}
			}
		};
		addCallback(call);
		sc.PostSearchAsync(req, call);
	}

	private void setUIMode(int mode) {
		boolean load_vis = false;
		boolean hist_vis = false;
		boolean nohi_vis = false;
		boolean sear_vis = false;
		boolean nose_vis = false;

		switch (mode) {
		case MODE_LOADING:
			load_vis = true;
			break;
		case MODE_INPUT_HISTORY:
			hist_vis = true;
			break;
		case MODE_SEARCH_RESULT:
			sear_vis = true;
			break;
		case MODE_NO_INPUT_HISTORY:
			nohi_vis = true;
			break;
		case MODE_NO_SEARCH_RESULT:
			nose_vis = true;
			break;
		}

		if (vg_waiting.getVisibility() != (load_vis ? View.VISIBLE : View.GONE)) {
			vg_waiting.setVisibility((load_vis ? View.VISIBLE : View.GONE));
		}
		if (rv_input_history.getVisibility() != (hist_vis ? View.VISIBLE
				: View.GONE)) {
			rv_input_history
					.setVisibility((hist_vis ? View.VISIBLE : View.GONE));
		}
		if (tv_not_history.getVisibility() != (nohi_vis ? View.VISIBLE
				: View.GONE)) {
			tv_not_history.setVisibility((nohi_vis ? View.VISIBLE : View.GONE));
		}
		if (ptr_post.getVisibility() != (sear_vis ? View.VISIBLE : View.GONE)) {
			ptr_post.setVisibility((sear_vis ? View.VISIBLE : View.GONE));
		}
		if (tv_not_post.getVisibility() != (nose_vis ? View.VISIBLE : View.GONE)) {
			tv_not_post.setVisibility((nose_vis ? View.VISIBLE : View.GONE));
		}
	}

	private void ensureUIMode(int mode) {
		if (mode == MODE_INPUT_HISTORY) {
			if (rvAdapter.getList() == null || rvAdapter.getList().size() <= 1) {
				mode = MODE_NO_INPUT_HISTORY;
			}
		}
		if (mode == MODE_SEARCH_RESULT) {
			if (postAdapter.getList() == null
					|| postAdapter.getList().size() == 0) {
				mode = MODE_NO_SEARCH_RESULT;
			}
		}
		setUIMode(mode);
	}

	private void startSearchPost() {
		String text = et_taste_name.getText().toString().trim();
		if (text.equals(searchStr)) {// 如果与上一次一致
			ensureUIMode(MODE_SEARCH_RESULT);
			// page =
			// Integer.parseInt(et_taste_name.getTag(R.id.et_taste_name).toString())
			// + 1;
			return;
		}
		imm.hideSoftInputFromWindow(et_taste_name.getWindowToken(), 0);
		rvAdapter.history.remove(text);
		rvAdapter.history.addFirst(text);
		rvAdapter.notifyDataSetChanged();
		/*
		 * ((HistoryAdapter) rv_input_history.getAdapter()).history.addFirst(new
		 * Bean(TYPE_NORMAL, text));
		 */
		if (!text.equals(searchStr)) {
			setUIMode(MODE_LOADING);
		}
		page = 1;// 默认拿第1页
		searchStr = text;
		requestSearchPost(page, searchStr, null);
	}

	private void saveHistory() {
		LinkedList<String> list = rvAdapter.getList();
		d("III", "Adapter List " + list);
		// String text = et_taste_name.getText().toString();
		// if (list.contains(text)) {
		// list.remove(text);
		// }
		// list.addFirst(text);
		// }
		// 保留 size 以内的历史记录，去掉其它的
		if (size >= 0 && size < list.size()) {
			for (int i = size; i < list.size(); i++) {
				list.remove(i--);
			}
		}
		d("III", "save " + name + "_history.ser size " + list);
		File ser = new File(getCacheDir(), name + "_history.ser");
		SerializableUtil.writeSerializableObject(ser.getAbsolutePath(), list);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// 监听文本框文本的变化
		// 判断是否显示“叉叉”，
		// 显示“确定”还是“取消”
		if ("".equals(s.toString().trim())) {
			s.clear();
		}
		if ("".equals(s.toString().trim())/* && isInputState() */) {
			setInputState(false);
		} else /* if (!isInputState()) */{
			setInputState(true);
		}
		// Log.i(this.getClass().getSimpleName(), "as: " + s);
	}

	private void initRecyclerView(RecyclerView view) {

		// measureView(view);
		// 因为RecyclerView的特殊性
		// 用代码生成一下分割线
		final int dividerHeight = getResources().getDimensionPixelSize(
				R.dimen.default_line_size);
		final int dividerColor = getResources().getColor(
				R.color.default_line_color);
		final Paint p = new Paint();
		p.setColor(dividerColor);
		p.setStyle(Style.FILL);
		Shape s = new Shape() {
			@Override
			public void draw(Canvas canvas, Paint paint) {
				canvas.drawRect(0, 0, getWidth(), getHeight(), p);
			}
		};
		// s.resize(view.getMeasuredWidth(), dividerHeight);// 其实这一句没用，放这里吧
		ShapeDrawable sd = new ShapeDrawable(s);
		sd.setIntrinsicHeight(dividerHeight);// 设置分割线的高度
		DividerItemDecoration divider = new DividerItemDecoration(
				getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
		divider.setDivider(sd);// 将分割线放到对象中
		view.addItemDecoration(divider);
		view.setItemAnimator(new DefaultItemAnimator());
		view.setHasFixedSize(true);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in,
				R.anim.out_from_bottom);
	}

	private void setInputState(boolean state) {
		if (state) {
			// resultCode = RESULT_OK;
			// btn_cancel.setText(R.string.confirm);
			btn_clear_input.setVisibility(View.VISIBLE);
		} else {
			// resultCode = RESULT_CANCELED;
			// btn_cancel.setText(R.string.cancel);
			btn_clear_input.setVisibility(View.GONE);
		}
	}

	/*
	 * private boolean isInputState() { return resultCode == RESULT_OK; }
	 */

	private LinkedList<Bean> genHistory(List<String> list) {
		if (list == null || list.size() == 0) {
			return new LinkedList<Bean>();
		}
		LinkedList<Bean> arr = new LinkedList<Bean>();
		for (int i = 0; i < list.size(); i++) {
			arr.add(new Bean(0, list.get(i)));
		}
		arr.add(new Bean(1, getString(R.string.taste_clear_history)));
		return arr;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestSearchPost(page = 1, searchStr, null);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestSearchPost(page, searchStr, null);
		// et_taste_name.setTag(text);
		// et_taste_name.setTag(R.id.et_taste_name, page);
		// requestSearchPost(page, text, btn_cancel);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		d("III", "posi:" + position);
		GetPostList.MPostResp item = postAdapter.getItem(position - 1);
		if (item == null) {
			return;
		}
		Intent intent = new Intent(this, ActivityPostDetail.class);
		// 貌似ChildFragment主动回调不到onActivityResult
		// id传到详情界面
		// intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, item.getId());
		intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
		startActivity(intent);

	}

	// private void measureView(View view) {
	// int width = View.MeasureSpec.makeMeasureSpec(0,
	// View.MeasureSpec.UNSPECIFIED);
	// int height = View.MeasureSpec.makeMeasureSpec(0,
	// View.MeasureSpec.UNSPECIFIED);
	//
	// view.measure(width, height);
	// }

	public class HistoryAdapter extends RecyclerView.Adapter<ViewHolder>
			implements OnClickListener {

		private static final int TYPE_NORMAL = 0;
		private static final int TYPE_CLEAR = 1;
		private static final String CLEAR_STR = "*-*-:-&-@-!/']";
		private Context context;
		private LayoutInflater inflater;
		private LinkedList<String> history;

		public HistoryAdapter(Context context, final LinkedList<String> history) {
			this.context = context;
			this.history = history;
			this.inflater = LayoutInflater.from(context);
			if (this.history != null && this.history.size() != 0) {
				this.history.addLast(CLEAR_STR);
			} else {
				ensureUIMode(MODE_NO_INPUT_HISTORY);
			}
			registerAdapterDataObserver(new AdapterDataObserver() {
				@Override
				public void onItemRangeRemoved(int positionStart, int itemCount) {
					for (int i = positionStart; i < positionStart + itemCount; i++) {
						// ActivityPostSearch.this.onRemoveTasteHistory(i);
						if (getItemCount() == 1) {
							HistoryAdapter.this.history.remove(0);
							notifyItemRemoved(0);
							ensureUIMode(MODE_NO_INPUT_HISTORY);
						}
					}
				}

				@Override
				public void onItemRangeInserted(int positionStart, int itemCount) {
					List<String> h = HistoryAdapter.this.history;
					if (getItemViewType(h.size() - 1) != TYPE_CLEAR) {
						h.add(CLEAR_STR);
						notifyItemInserted(h.size() - 1);
					}
				}

				@Override
				public void onItemRangeChanged(int positionStart, int itemCount) {
					history.remove(CLEAR_STR);
					history.addLast(CLEAR_STR);
				}
			});
		}

		@Override
		public int getItemViewType(int position) {// 最后一个是清除历史记录
			return history != null && position < history.size()
					&& CLEAR_STR.equals(history.get(position)) ? TYPE_CLEAR
					: TYPE_NORMAL;
		}

		public LinkedList<String> getList() {
			LinkedList<String> newList = new LinkedList<String>(history);
			newList.remove(CLEAR_STR);
			return newList;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

			View itemView = inflater.inflate(R.layout.item_taste_history,
					parent, false);
			itemView.setOnClickListener(this);
			// ViewHolder holder = new ViewHolder(itemView);
			return new ViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {

			switch (getItemViewType(position)) {
			case TYPE_CLEAR:
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.text1
						.getLayoutParams();
				params.addRule(RelativeLayout.CENTER_IN_PARENT);
				holder.button1.setVisibility(View.GONE);
				holder.text1.setText(R.string.taste_clear_history);
				break;
			default:
				String text = history.get(position);
				holder.text1.setText(text);
				if (holder.button1 != null) {
					holder.button1.setTag(text);
					holder.button1.setOnClickListener(this);
				}
				break;
			}
		}

		@Override
		public int getItemCount() {
			return history != null ? history.size() : 0;
		}

		@Override
		public void onClick(View v) {
			d("III", "click ");
			switch (v.getId()) {
			case android.R.id.button1: {// 右边哪把叉叉
				// 这个position不能使用上面的，需要这样获得才是正确的
				int position = history.indexOf(v.getTag());
				history.remove(position);
				// ActivityPostSearch.this.mHistory.remove(position);
				notifyItemRemoved(position);
				break;
			}
			default: {// 这个是ItemClick事件
				int position = rv_input_history.getChildAdapterPosition(v);
				// Bean bean = mHistory.get(position);
				if (getItemViewType(position) == TYPE_CLEAR) {
					for (int i = 0; i < history.size(); i++) {
						int p = (i--);
						this.history.remove(p);
						notifyItemRemoved(p);
					}
					history.clear();
					tv_not_history.setVisibility(View.VISIBLE);
					break;
				}
				et_taste_name.setText(history.get(position));
				Selection.setSelection(et_taste_name.getText(), et_taste_name
						.getText().length());
				startSearchPost();
				// if (BuildConfig.DEBUG) {
				// ActivityPostSearch.this.onClick(btn_cancel);
				// }
				break;
			}
			}
		}
	}

	@Override
	protected void onDestroy() {
		saveHistory();
		super.onDestroy();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		TextView text1;
		ImageButton button1;

		public ViewHolder(View itemView) {
			super(itemView);
			text1 = (TextView) itemView.findViewById(android.R.id.text1);
			button1 = (ImageButton) itemView.findViewById(android.R.id.button1);
		}
	}

	public class Bean {
		int type;
		String text;

		public Bean(int type, String text) {
			this.type = type;
			this.text = text;
		}

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// Log.i(this.getClass().getSimpleName(), "s: " + s + ", start: " +
		// start + ", count: " + count + ", before: " + before);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// Log.i(this.getClass().getSimpleName(), "bs: " + s + ", start: " +
		// start + ", count: " + count + ", after: " + after);
	}
}
