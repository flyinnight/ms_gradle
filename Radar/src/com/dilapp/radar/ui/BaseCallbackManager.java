package com.dilapp.radar.ui;

import java.util.ArrayList;
import java.util.List;

import com.dilapp.radar.domain.BaseCallNode;

public class BaseCallbackManager {
	
	protected List<BaseCallNode> mBaseCallList;
	
	public BaseCallbackManager(){
		mBaseCallList = new ArrayList<BaseCallNode>();
	}
	
	public void addCallbace(BaseCallNode node){
		if(node != null && !mBaseCallList.contains(node)){
			mBaseCallList.add(node);
		}
	}
	
	public void clearCallback(){
		for(BaseCallNode node : mBaseCallList){
			if(node != null){
				node.cancelCallBack(true);
			}
		}
		mBaseCallList.clear();
	}

}
