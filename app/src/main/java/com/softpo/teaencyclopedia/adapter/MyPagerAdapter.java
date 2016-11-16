package com.softpo.teaencyclopedia.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MyPagerAdapter extends PagerAdapter {
	private List<View> list = null;
	private Context context;

	public MyPagerAdapter(List<View> list, Context context) {
		super();
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if(list.size()==0){
			return null;
		}
		container.addView(list.get(position));
//		list.get(position).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(context, "Position:"+position, Toast.LENGTH_SHORT).show();
//				
//			}
//		});
		return list.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(list.get(position));
	}
}
