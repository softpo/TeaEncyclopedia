package com.softpo.teaencyclopedia.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softpo.teaencyclopedia.R;
import com.softpo.teaencyclopedia.help.AsyncTaskHelper;
import com.softpo.teaencyclopedia.help.ImageDownloadHelper;

import java.util.List;
import java.util.Map;

//自定义主页面中ListView的适配器
public class MyListViewAdapter extends BaseAdapter {
	private List<Map<String, Object>> list = null;
	private Context context = null;
	private ImageDownloadHelper imageDownloadHelper;
	private AsyncTaskHelper asyncTaskHelper;

	public MyListViewAdapter(Context context, List<Map<String, Object>> list) {
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
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_listview_fragment, parent, false);
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

		// 开始加载图片
		String imgUrl = list.get(position).get("wap_thumb").toString();
		//如果图片不存在,取消图片所占空间
		if (imgUrl.equals("") || imgUrl == null) {
			mHolder.imageView_fragment_wap_thumb.setVisibility(View.GONE);
		} else {
			mHolder.imageView_fragment_wap_thumb.setTag(imgUrl);

			// 当版本号高于11时，使用带LruCache的类异步加载图片
			if (Build.VERSION.SDK_INT >= 11) {
				// 做法1：使用自己封装的异步加载图片工具类——考虑内存缓存和文件缓存
				if (imageDownloadHelper == null) {
					imageDownloadHelper = new ImageDownloadHelper();
				}
				imageDownloadHelper.myDownloadImage(context, imgUrl,
						mHolder.imageView_fragment_wap_thumb,
						new ImageDownloadHelper.OnImageDownloadListener() {
							@Override
							public void onImageDownload(Bitmap bitmap,
									String imgUrl) {
								if (bitmap != null) {
									mHolder.imageView_fragment_wap_thumb
											.setImageBitmap(bitmap);
								}
							}
						});
			} else {
				// 做法2：不考虑内存缓存和文件缓存
				if (asyncTaskHelper == null) {
					asyncTaskHelper = new AsyncTaskHelper();
				}
				asyncTaskHelper.downloadData(imgUrl,
						new AsyncTaskHelper.OnDataDownloadListener() {
							@Override
							public void onDataDownload(byte[] result) {
								if (result != null) {
									Bitmap bm = BitmapFactory.decodeByteArray(
											result, 0, result.length);
									if (bm != null) {
										mHolder.imageView_fragment_wap_thumb.setImageBitmap(bm);
									}
								}
							}
						});
			}
		}
		
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
