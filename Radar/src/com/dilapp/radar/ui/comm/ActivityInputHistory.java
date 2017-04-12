package com.dilapp.radar.ui.comm;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dilapp.radar.BuildConfig;
import com.dilapp.radar.R;
import static com.dilapp.radar.textbuilder.utils.L.*;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.util.AndroidBugsSolution;
import com.dilapp.radar.util.SerializableUtil;
import com.dilapp.radar.view.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 本地数据输入历史记录
 * Intent
 * @param {@link com.dilapp.radar.ui.Constants#EXTRA_INPUT_HISTORY_NAME} require   String
 * 该输入历史的数据集的名称。
 * @param {@link com.dilapp.radar.ui.Constants#EXTRA_INPUT_HISTORY_HINT} optional  String
 * 文本框的提示文字 EditText.Hint
 * @param {@link com.dilapp.radar.ui.Constants#EXTRA_INPUT_HISTORY_TEXT} optional  String
 * 文本框预显示的文字
 * @param {@link com.dilapp.radar.ui.Constants#EXTRA_INPUT_HISTORY_SIZE} optional  int
 * 历史记录的最大数量，默认20条
 *
 * @return {@link com.dilapp.radar.ui.Constants#RESULT_INPUT_HISTORY_TEXT} optional  String
 */
public class ActivityInputHistory extends BaseActivity
        implements
        OnClickListener, TextWatcher {

    public static final int MAX_HISTORY_SIZE = 20;
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_CLEAR = 1;

    private EditText et_taste_name;
    private TextView btn_cancel;
    private TextView tv_not_history;
    private ImageButton btn_clear_input;
    private RecyclerView list;

    private String name;
    private int size;
    private LinkedList<String> mHistory;

    private int resultCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_history);
        AndroidBugsSolution.assistActivity(this, null);

        Context context = getApplicationContext();

        btn_cancel = findViewById_(R.id.btn_cancel);
        btn_clear_input = findViewById_(R.id.btn_clear_input);
        tv_not_history = findViewById_(R.id.tv_not_history);
        et_taste_name = findViewById_(R.id.et_taste_name);
        et_taste_name.addTextChangedListener(this);
        list = findViewById_(android.R.id.list);

        list.setLayoutManager(new LinearLayoutManager(context));

        String text = getIntent().getStringExtra(Constants.EXTRA_INPUT_HISTORY_TEXT);
        if (text != null && !"".equals(text.trim())) {
            et_taste_name.setText(text);
            Selection.setSelection(et_taste_name.getText(), text.length());
        }
        String hint = getIntent().getStringExtra(Constants.EXTRA_INPUT_HISTORY_HINT);
        et_taste_name.setHint(hint == null ? "" : hint);
        name = getIntent().getStringExtra(Constants.EXTRA_INPUT_HISTORY_NAME);
        size = getIntent().getIntExtra(Constants.EXTRA_INPUT_HISTORY_SIZE, MAX_HISTORY_SIZE);
        File ser = new File(getCacheDir(), name + "_history.ser");
        mHistory = SerializableUtil.readSerializableObject(ser.getAbsolutePath());
        if (this.mHistory == null) this.mHistory = new LinkedList<String>();

        List<Bean> history = genHistory(this.mHistory);
        HistoryAdapter adapter = new HistoryAdapter(context, history);
        list.setAdapter(adapter);
        initRecyclerView(list);
        if (history != null && history.size() > 0) {
            tv_not_history.setVisibility(View.GONE);
        } else {
            tv_not_history.setVisibility(View.VISIBLE);
        }

        ViewCompat.setTransitionName(et_taste_name, "share:et_taste_name");
    }

    private void onRemoveTasteHistory(int index) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                // 将选择的项置顶
                if (resultCode == RESULT_OK) {
                    String text = et_taste_name.getText().toString();
                    if (this.mHistory.contains(text)) {
                        this.mHistory.remove(text);
                    }
                    this.mHistory.addFirst(text);
                }
                // 保留 size 以内的历史记录，去掉其它的
                if (size >= 0 && size < this.mHistory.size()) {
                    for (int i = size; i < this.mHistory.size(); i++) {
                        this.mHistory.remove(i--);
                    }
                }
                // 保存历史记录到文件
                saveHistory();
                Intent data = null;
                if (isInputState()) {
                    data = new Intent();
                    data.putExtra(Constants.RESULT_INPUT_HISTORY_TEXT, et_taste_name.getText().toString());
                }
                setResult(resultCode, data);
                if ("".equals(et_taste_name.getText().toString())) {
                    ActivityCompat.finishAfterTransition(this);
                } else {
                    finish();
                }
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

    private void saveHistory() {
        d("III", "save " + name + "_history.ser size " + this.mHistory);
        File ser = new File(getCacheDir(), name + "_history.ser");
        SerializableUtil.writeSerializableObject(ser.getAbsolutePath(), this.mHistory);
    }

    @Override
    public void afterTextChanged(Editable s) {
        // 监听文本框文本的变化
        // 判断是否显示“叉叉”，
        // 显示“确定”还是“取消”
        if ("".equals(s.toString().trim())) {
            s.clear();
        }
        if ("".equals(s.toString().trim()) && isInputState()) {
            setInputState(false);
        } else if (!isInputState()) {
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
        overridePendingTransition(android.R.anim.fade_in, R.anim.out_from_bottom);
    }

    private void setInputState(boolean state) {
        if (state) {
            resultCode = RESULT_OK;
            btn_cancel.setText(R.string.confirm);
            btn_clear_input.setVisibility(View.VISIBLE);
        } else {
            resultCode = RESULT_CANCELED;
            btn_cancel.setText(R.string.cancel);
            btn_clear_input.setVisibility(View.GONE);
        }
    }

    private boolean isInputState() {
        return resultCode == RESULT_OK;
    }

    private List<Bean> genHistory(List<String> list) {
        if(list == null || list.size() == 0) {
            return new ArrayList<Bean>(0);
        }
        List<Bean> arr = new ArrayList<Bean>(list.size());
        for (int i = 0; i < list.size(); i++) {
            arr.add(new Bean(0, list.get(i)));
        }
        arr.add(new Bean(1, getString(R.string.taste_clear_history)));
        return arr;
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

        private Context context;
        private LayoutInflater inflater;
        private List<Bean> history;

        public HistoryAdapter(Context context, List<Bean> history) {
            this.context = context;
            this.history = history;
            this.inflater = LayoutInflater.from(context);
            registerAdapterDataObserver(new AdapterDataObserver() {
                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    for (int i = positionStart; i < positionStart + itemCount; i++) {
                        ActivityInputHistory.this.onRemoveTasteHistory(i);
                        if (getItemCount() == 1) {
                            HistoryAdapter.this.history.remove(0);
                            notifyItemRemoved(0);
                            tv_not_history.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    List<Bean> h = HistoryAdapter.this.history;
                    if (h.get(h.size() - 1).type != TYPE_CLEAR) {
                        h.add(new Bean(1,
                                getString(R.string.taste_clear_history)));
                    }
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return history.get(position).type;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = inflater.inflate(R.layout.item_taste_history,
                    parent, false);
            ViewHolder holder = new ViewHolder(itemView);
            switch (viewType) {
                case TYPE_CLEAR:
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.text1
                            .getLayoutParams();
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    holder.button1.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            itemView.setOnClickListener(this);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Bean bean = history.get(position);
            String text = bean.text;
            holder.text1.setText(text);
            if (holder.button1 != null) {
                holder.button1.setTag(bean);
                holder.button1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 这个position不能使用上面的，需要这样获得才是正确的
                        int position = history.indexOf(v.getTag());
                        HistoryAdapter.this.history.remove(position);
                        ActivityInputHistory.this.mHistory.remove(position);
                        notifyItemRemoved(position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return history != null ? history.size() : 0;
        }

        @Override
        public void onClick(View v) {
            int position = list.getChildAdapterPosition(v);
            // Bean bean = mHistory.get(position);
            if (getItemViewType(position) == TYPE_CLEAR) {
                for (int i = 0; i < history.size(); i++) {
                    int p = (i--);
                    this.history.remove(p);
                    notifyItemRemoved(p);
                }
                ActivityInputHistory.this.mHistory.clear();
                tv_not_history.setVisibility(View.VISIBLE);
                return;
            }
            et_taste_name.setText(history.get(position).text);
            Selection.setSelection(et_taste_name.getText(), et_taste_name
                    .getText().length());
            if (BuildConfig.DEBUG) {
                ActivityInputHistory.this.onClick(btn_cancel);
            }
        }
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
