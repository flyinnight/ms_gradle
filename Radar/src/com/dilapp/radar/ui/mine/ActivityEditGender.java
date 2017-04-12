package com.dilapp.radar.ui.mine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.TitleView;

public class ActivityEditGender extends BaseActivity implements OnClickListener, OnItemClickListener{

	private Context mContext;
	private TitleView mTitle;
	
	private ListView listView;  
    private List<String> mData;  
    private int checkGender ;
    //record the current checked radio number  
    private int checkedIndex = -1;
    private InfoEditAdapter mAdapter ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_gender);
		init_view();
		
	}

	private void init_view() {
		mContext = this;
		checkGender = getIntent().getIntExtra("gender", 1);
		View title = findViewById(R.id.vg_title);
		mTitle = new TitleView(this, title);
		mTitle.setLeftText(R.string.cancel, this);
		mTitle.setCenterText(R.string.change_gender, null);
		mTitle.setRightText(R.string.save, this);
		listView = findViewById_(R.id.mine_edit_list);
		mData = new ArrayList<String>();
		mData.add(getResources().getString(R.string.man));
		mData.add(getResources().getString(R.string.woman));
		mData.add(getResources().getString(R.string.keep_secret));
		mAdapter = new InfoEditAdapter(mContext, mData);
		mAdapter.setData(checkGender);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.vg_left:
			finish();
			break;
		case R.id.vg_right:
			if(checkedIndex >= 0){
				SharePreCacheHelper.setGender(this, checkedIndex + 1);
				setResult(RESULT_OK);
				finish();
			}else{
				Toast.makeText(getApplicationContext(), "未作任何选择", Toast.LENGTH_SHORT).show();
			}
		break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		ListView lv = (ListView) parent;  
//		Slog.d("onItemClick  : "+position+"  "+id);
        if(checkedIndex != position){   
        {  //定位到现在处于点击状态的radio 
            int childId = checkedIndex - lv.getFirstVisiblePosition();    
            if(childId >= 0){  //如果checked =true的radio在显示的窗口内，改变其状态为false  
                View item = lv.getChildAt(childId);    
                if(item != null){    
                    RadioButton rb = (RadioButton)item.findViewById(R.id.check_node);    
                    if(rb != null)
                    rb.setChecked(false);    
                }    
            }  
            //将当前点击的radio的checked变为true  
            RadioButton rb1 = (RadioButton)view.findViewById(R.id.check_node);    
            if(rb1 != null)    
            rb1.setChecked(true); 
            checkedIndex = position;  
        }  
        } 
	}

}
