package com.softpo.teaencyclopedia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softpo.teaencyclopedia.R;

import java.util.List;
import java.util.Map;

//自定义主页面中ListView的适配器
public class MyFavoriteListAdapter extends BaseAdapter {
	private List<Map<String, Object>> list = null;
	private Context context = null;

	public MyFavoriteListAdapter(Context context, List<Map<String, Object>> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder mHolder;
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_fragment, parent, false);
			mHolder.text_fragment_title = (TextView) convertView
					.findViewById(R.id.text_fragment_title);
			mHolder.text_fragment_source = (TextView) convertView
					.findViewById(R.id.text_fragment_source);
			mHolder.text_fragment_nickname = (TextView) convertView
					.findViewById(R.id.text_fragment_nickname);
			mHolder.text_fragment_create_time = (TextView) convertView
					.findViewById(R.id.text_fragment_create_time);
			mHolder.imageView_fragment_wap_thumb = (ImageView) convertView
					.findViewById(R.id.imageView_fragment_wap_thumb);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		mHolder.text_fragment_title.setText(list.get(position).get("title")
				.toString());
		mHolder.text_fragment_source.setText(list.get(position).get("source")
				.toString());
		mHolder.text_fragment_nickname.setText(list.get(position)
				.get("nickname").toString());
		mHolder.text_fragment_create_time.setText(list.get(position)
				.get("create_time").toString());
		mHolder.imageView_fragment_wap_thumb.setVisibility(View.GONE);
		return convertView;
	}

	class ViewHolder {
		private TextView text_fragment_title;
		private TextView text_fragment_source;
		private TextView text_fragment_nickname;
		private TextView text_fragment_create_time;
		private ImageView imageView_fragment_wap_thumb;
	}
}
