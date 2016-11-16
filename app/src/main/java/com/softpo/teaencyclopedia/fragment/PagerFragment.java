package com.softpo.teaencyclopedia.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.softpo.teaencyclopedia.DetailActivity;
import com.softpo.teaencyclopedia.R;
import com.softpo.teaencyclopedia.adapter.MyListViewAdapter;
import com.softpo.teaencyclopedia.help.ChaBaikeJsonHelper;
import com.softpo.teaencyclopedia.help.ChaBaikeSQLiteOpenHelper;
import com.softpo.teaencyclopedia.help.Constants;
import com.softpo.teaencyclopedia.help.HttpURLConnHelper;
import com.softpo.teaencyclopedia.help.NetworkHelper;
import com.softpo.teaencyclopedia.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PagerFragment extends Fragment implements XListView.IXListViewListener {
	private static final String TAG = "PagerFragment";
	private XListView xListView_dummy;
	private TextView text_fragment_emptyinfo;
	private ImageView imageView_fragment_backtotop;//返回顶部
	private int index = 0;
	private int page = 1;
	private Handler handler = new Handler();
	private List<Map<String, Object>> newsList = null;
	private MyListViewAdapter newsAdapter = null;
	private String url = "";
	private ChaBaikeSQLiteOpenHelper dbHelper;
	private String newsid = "";
	private String time = "";
	private String title = "";
	private String nickname = "";
	private String source = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		index = bundle.getInt("tabindex");
		newsList = new ArrayList<Map<String, Object>>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dummy, null);
		imageView_fragment_backtotop = (ImageView) view
				.findViewById(R.id.imageView_fragment_backtotop);
		xListView_dummy =  (XListView) view
				.findViewById(R.id.xListView_dummy);
		xListView_dummy.setPullLoadEnable(true);
		xListView_dummy.setPullRefreshEnable(true);
		text_fragment_emptyinfo = (TextView) view
				.findViewById(R.id.text_fragment_emptyinfo);

		switch (index) {
		case 1:
			url = Constants.BASE_URL + Constants.CYCLOPEDIA_TYPE;
			break;
		case 2:
			url = Constants.BASE_URL + Constants.CONSULT_TYPE;
			break;
		case 3:
			url = Constants.BASE_URL + Constants.OPERATE_TYPE;
			break;
		case 4:
			url = Constants.BASE_URL + Constants.DATA_TYPE;
			break;
		}

		if (newsAdapter == null) {
			newsAdapter = new MyListViewAdapter(getActivity(), newsList);
		}
		xListView_dummy.setAdapter(newsAdapter);
		xListView_dummy.setEmptyView(text_fragment_emptyinfo);

		if (NetworkHelper.isNetworkConnected(getActivity())) {
			// 网络加载ListView中的数据
			new MyThread().start();
		}
		
		xListView_dummy.setXListViewListener(this);
		// 给ListView增加监听器
		xListView_dummy
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {
						dbHelper = new ChaBaikeSQLiteOpenHelper(getActivity());
						Map<String, Object> map = newsList.get(position);
						newsid = (String) map.get("id");
						title = (String) map.get("title");
						time = (String) map.get("create_time");
						nickname = (String) map.get("nickname");
						source = (String) map.get("source");
						
						//查询一下数据库中的历史记录表 看看该id所对应的数据是否已经存在
						int num = dbHelper.selectCount(
								"select count(title) from tb_history where newsid=?",new String[] { newsid });
						if (num != 0) {
							//已经添加了 不做处理
							Log.i("TAG", "该条数据已经添加!");
						} else {
							String sql = "insert into tb_history(newsid , title , source,nickname,create_time) values(?,?,?,?,?)";
							dbHelper.execData(sql, new Object[] { newsid,
									title, source, nickname, time });
							Log.i("TAG", "添加到历史访问记录成功！");
						} 
						
						// 单击后跳转到内容页面
						Intent intent = new Intent(getActivity(),
								DetailActivity.class);
						Bundle bundle = new Bundle();
						bundle.putBoolean("isFavorite", false);
						bundle.putString("id", newsList.get(position).get("id")
								.toString());
						bundle.putString("title",
								newsList.get(position).get("title").toString());
						bundle.putString("nickname", newsList.get(position)
								.get("nickname").toString());
						bundle.putString("create_time", newsList.get(position)
								.get("create_time").toString());
						bundle.putString("source",
								newsList.get(position).get("source").toString());
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
		xListView_dummy.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
										   int position, long id) {
//				Toast.makeText(getActivity(), "position="+position, Toast.LENGTH_SHORT).show();
//				CustomDialog customDialog = new CustomDialog(getActivity(), xListView_dummy, position, newsAdapter,newsList);
//				customDialog.showDialog();
				return true;
			}
		});
		imageView_fragment_backtotop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				xListView_dummy.setSelectionFromTop(0, 0);
			}
		});
		return view;
	}

	// 下拉刷新列表数据
	public void fillListView(String jsonString) {
		if (page == 1) {
			newsList.clear();
		}
		newsList.addAll(ChaBaikeJsonHelper.parseJsonToList(jsonString));
		newsAdapter.notifyDataSetChanged();
		// xListView_dummy.setSelectionFromTop(newsList.size(), 0);
	}

	public void clickButton(View view) {
		switch (view.getId()) {
		case R.id.imageView_fragment_backtotop:
			xListView_dummy.setSelectionFromTop(0, 0);
			break;
		}
	}

	class MyThread extends Thread implements Runnable {
		@Override
		public void run() {
			super.run();
			Log.i(TAG, "--网络加载文章json数据：" + index);
			final byte[] newsResult = HttpURLConnHelper.loadByteFromURL(url
					+ page);
			if (newsResult == null) {
				handler.post(new Runnable() {
					public void run() {
						text_fragment_emptyinfo
								.setText(R.string.prompt_network_connecttimeout);
					}
				});
			} else {
				handler.post(new Runnable() {
					@Override
					public void run() {
						fillListView(new String(newsResult));
						text_fragment_emptyinfo.setText("");

					}
				});
			}
		}
	}

	@Override
	public void onRefresh() {
		page = 1;
		new MyThread().start();
		xListView_dummy.stopRefresh();
		
	}

	@Override
	public void onLoadMore() {
		page++;
		new MyThread().start();
		xListView_dummy.stopLoadMore();
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dbHelper!=null) {
			dbHelper.close();
		}
	}
}
