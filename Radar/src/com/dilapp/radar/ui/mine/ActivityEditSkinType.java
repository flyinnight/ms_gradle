package com.dilapp.radar.ui.mine;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.Slog;

public class ActivityEditSkinType extends BaseActivity implements OnClickListener, OnItemClickListener{
	
	private TitleView mTitle;
	
	private ListView listView;  
    private List<String> mData;  
      
    //record the current checked radio number  
    private int checkedIndex = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_gender);
		View title = findViewById(R.id.vg_title);
		mTitle = new TitleView(this, title);
		mTitle.setLeftText(R.string.cancel, this);
		mTitle.setCenterText(R.string.change_skin_type, null);
		mTitle.setRightText(R.string.save, this);
		
		listView = findViewById_(R.id.mine_edit_list);
		mData = new ArrayList<String>();
		mData.add(getResources().getString(R.string.test_dry_skin));
		mData.add(getResources().getString(R.string.test_oil_skin));
		mData.add(getResources().getString(R.string.test_middle_skin));
		InfoEditAdapter mAdapter = new InfoEditAdapter(this, mData);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.vg_left:
			finish();
			break;
		case R.id.vg_right:
			if(checkedIndex >= 0){
				SharePreCacheHelper.setSkinType(this, checkedIndex + 1);
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
