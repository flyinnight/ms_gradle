package com.dilapp.radar.ui.found;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.ui.BaseActivity;

public class SearchTopicActivity extends BaseActivity {
	private TextView btn_select = null;
	private AutoCompleteTextView et_search = null;
	private ListView lv_auto_tips = null;
	private ArrayAdapter<String> adapter = null;
	public List<String> suggest;
	public final int REQUEST_CODE = 133;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_skin);
		btn_select = (TextView) findViewById(R.id.btn_search);
		et_search = (AutoCompleteTextView) findViewById(R.id.et_search_content);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(et_search, InputMethodManager.SHOW_FORCED);
		suggest = new ArrayList<String>();
		et_search.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable editable) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String newText = s.toString();
				new SearchTopic().execute(newText);
			}

		});

		btn_select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(REQUEST_CODE);
				SearchTopicActivity.this.finish();
			}
		});
	}

	class SearchTopic extends AsyncTask<String, String, String> {
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			adapter = new ArrayAdapter<String>(getApplicationContext(),
					R.layout.search_list, suggest);
			et_search.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected String doInBackground(String... key) {
			String newText = key[0];
			newText = newText.trim();
			newText = newText.replace(" ", "+");
//			 suggest.add(SuggestKey);
			requestSeacher();
			return null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		setResult(REQUEST_CODE);
		SearchTopicActivity.this.finish();
		super.onBackPressed();
	}
	
	private void requestSeacher(){
		//TODO:后台搜索
	}
}
