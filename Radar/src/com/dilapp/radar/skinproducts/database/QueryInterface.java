package com.dilapp.radar.skinproducts.database;

import java.util.ArrayList;

public interface QueryInterface{
	public abstract void failure(Exception e);
	public abstract void onSucess(ArrayList<SkinProduct> produts);
}
