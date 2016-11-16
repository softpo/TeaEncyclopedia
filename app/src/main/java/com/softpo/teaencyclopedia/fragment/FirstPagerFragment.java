package com.softpo.teaencyclopedia.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softpo.teaencyclopedia.DetailActivity;
import com.softpo.teaencyclopedia.MainActivity;
import com.softpo.teaencyclopedia.R;
import com.softpo.teaencyclopedia.adapter.MyListViewAdapter;
import com.softpo.teaencyclopedia.adapter.MyPagerAdapter;
import com.softpo.teaencyclopedia.help.AsyncTaskHelper;
import com.softpo.teaencyclopedia.help.ChaBaikeJsonHelper;
import com.softpo.teaencyclopedia.help.ChaBaikeSQLiteOpenHelper;
import com.softpo.teaencyclopedia.help.Constants;
import com.softpo.teaencyclopedia.help.CustomDialog;
import com.softpo.teaencyclopedia.help.HttpURLConnHelper;
import com.softpo.teaencyclopedia.help.ImageDownloadHelper;
import com.softpo.teaencyclopedia.help.MyViewPager;
import com.softpo.teaencyclopedia.help.NetworkHelper;
import com.softpo.teaencyclopedia.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FirstPagerFragment extends Fragment implements XListView.IXListViewListener {
	private XListView xListView_dummy;
	// private ListView listView_fragment_infolist;
	private MyViewPager viewPager_firstfragment_ads;
	private ViewPager mViewPager;
	private TextView text_ads_desc;
	private TextView text_fragment_emptyinfo;
	private LinearLayout layout_ads_container;
	private ImageView imageView_fragment_backtotop;
	private MyListViewAdapter newsAdapter = null;
	private ImageDownloadHelper imageDownloadHelper = null;
	private AsyncTaskHelper asyncTaskHelper = null;
	private List<Map<String, Object>> newsList = null;
	private ChaBaikeSQLiteOpenHelper dbHelper;
	private String newsid = "";
	private String time = "";
	private String title = "";
	private String nickname = "";
	private String source = "";
	private List<Map<String, String>> adsTotalList = new ArrayList<>();
	private List<View> adsImageList = null;
	private ImageView[] dots = null;
	private int page = 1;
	// ViewPager展示当前视图的位置
	private int viewPagerPosition = 0;
	private int myFirstVisibleItem;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
//				viewPager_firstfragment_ads.setCurrentItem(viewPagerPosition);
				mViewPager.setCurrentItem(viewPagerPosition);
				// 当滑动到最后一页时，偷偷跳转至第0页，不加滑动效果
				if (viewPagerPosition >= adsImageList.size()) {
					viewPagerPosition = 0;
//					viewPager_firstfragment_ads.setCurrentItem(viewPagerPosition, false);
					mViewPager.setCurrentItem(viewPagerPosition, false);
				}
				viewPagerPosition++;
				break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		newsList = new ArrayList<Map<String, Object>>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dummy, null);
		imageView_fragment_backtotop = (ImageView) view
				.findViewById(R.id.imageView_fragment_backtotop);
		xListView_dummy = (XListView) view.findViewById(R.id.xListView_dummy);
		xListView_dummy.setPullLoadEnable(true);
		xListView_dummy.setPullRefreshEnable(true);
		text_fragment_emptyinfo = (TextView) view
				.findViewById(R.id.text_fragment_emptyinfo);

		// 初始化广告条
		View adsView = View.inflate(getActivity(), R.layout.ads_listview, null);
		layout_ads_container = (LinearLayout) adsView
				.findViewById(R.id.layout_ads_container);
//		viewPager_firstfragment_ads = (MyViewPager) adsView
//				.findViewById(R.id.viewPager_firstfragment_ads);
		mViewPager = (ViewPager) adsView
				.findViewById(R.id.viewPager);

		//解决两个ViewPager嵌套滑动冲突问题
		mViewPager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				((MainActivity) getContext()).getViewPager_main()
						.requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		text_ads_desc = (TextView) adsView.findViewById(R.id.text_ads_desc);

		initDots();// 初始化右下角所有的点图片

		// 每次返回到该页面必须重新实例化，否则会出现集合下标越界
		adsTotalList.clear();
		Log.i("TAG", "广告条的长度为："+adsTotalList.size());
		adsImageList = new ArrayList<View>();
		// 初始化头部viewpager中的三张图片
		for (int i = 0; i < dots.length; i++) {
			ImageView imageView = new ImageView(getActivity());
			// 设置数据未加载出来前的默认显示的图片
			imageView.setBackgroundResource(R.drawable.ic_launcher);
			// 使图片填充整个ImageView
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			adsImageList.add(imageView);
		}
		// 为viewPager设置数据源
//		viewPager_firstfragment_ads.setAdapter(new MyPagerAdapter(adsImageList,
//				getActivity()));
		mViewPager.setAdapter(new MyPagerAdapter(adsImageList,
				getActivity()));

		// 给viewpager设置页面滑动监听
		mViewPager
				.setOnPageChangeListener(new OnPageChangeListener() {
					@Override
					public void onPageSelected(int arg0) {
						if(adsTotalList.size() == 0)
							return;
						if (adsTotalList.get(arg0) != null ) {
							for (int i = 0; i < adsTotalList.size(); i++) {
								dots[i].setEnabled(true);
							}
							dots[arg0].setEnabled(false);
							text_ads_desc.setText(adsTotalList.get(arg0).get(
									"title"));
						}
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
					}
				});

		// 将广告ViewPager添加到xListView的Header中
		xListView_dummy.addHeaderView(adsView);
		
		//开启轮播
		startRecycle();

		// 获取广告下方的ListView的信息
		if (newsAdapter == null) {
			newsAdapter = new MyListViewAdapter(getActivity(), newsList);
		}
		xListView_dummy.setAdapter(newsAdapter);
		xListView_dummy.setEmptyView(text_fragment_emptyinfo);

		if (NetworkHelper.isNetworkConnected(getActivity())) {
			// 网络加载广告图片
			new Thread(new Runnable() {
				@Override
				public void run() {
					// Log.i(TAG, "--访问网络，开始获取广告json数据");

					final byte[] adsResult = HttpURLConnHelper
							.loadByteFromURL(Constants.HEADERIMAGE_URL);

					if (adsResult == null) {
						handler.post(new Runnable() {
							public void run() {
								Toast.makeText(getActivity(),
										R.string.prompt_network_error,
										Toast.LENGTH_SHORT).show();
							}
						});
					} else {
						// 将子线程获取到的广告图片信息返回给主线程UI
						handler.post(new Runnable() {
							@Override
							public void run() {
								// 获取头条广告信息及广告图片
								// Log.i(TAG, "--网络获取到广告信息，准备json解析");

								adsTotalList = ChaBaikeJsonHelper
										.parseAdsJsonToList(new String(
												adsResult));
								// 当版本号高于11时，使用带LruCache的类异步加载图片
								if (Build.VERSION.SDK_INT >= 11) {

									if (imageDownloadHelper == null) {
										imageDownloadHelper = new ImageDownloadHelper();
									}
									for (int i = 0; i < adsImageList.size(); i++) {
										String imageUrl = adsTotalList.get(i)
												.get("image");
										adsImageList.get(i).setTag(imageUrl);
										final ImageView imgView = (ImageView) adsImageList
												.get(i);
										imageDownloadHelper
												.myDownloadImage(
														getActivity(),
														imageUrl,
														(ImageView) adsImageList
																.get(i),
														new ImageDownloadHelper.OnImageDownloadListener() {
															@Override
															public void onImageDownload(
																	Bitmap bitmap,
																	String imgUrl) {
																imgView.setImageBitmap(bitmap);
															}
														});
									}
								} else {
									// Toast.makeText(getActivity(),
									// R.string.prompt_nocache,
									// Toast.LENGTH_SHORT).show();
									if (asyncTaskHelper == null) {
										asyncTaskHelper = new AsyncTaskHelper();
									}
									for (int i = 0; i < adsImageList.size(); i++) {
										String imageUrl = adsTotalList.get(i)
												.get("image");
										final ImageView imgView = (ImageView) adsImageList
												.get(i);
										asyncTaskHelper.downloadData(imageUrl,
												new AsyncTaskHelper.OnDataDownloadListener() {
													@Override
													public void onDataDownload(
															byte[] result) {
														imgView.setImageBitmap(BitmapFactory
																.decodeByteArray(
																		result,
																		0,
																		result.length));
													}
												});
									}
								}
								text_ads_desc.setText(adsTotalList.get(0).get(
										"title"));
							}
						});
					}
				}
			}).start();

			// 网络加载ListView中的数据
			new MyThread().start();

		} else {
			Toast.makeText(getActivity(), R.string.prompt_network_error,
					Toast.LENGTH_SHORT).show();
		}
		// 给XListView设置刷新监听
		xListView_dummy.setXListViewListener(this);

		// 给xListView增加监听器
		xListView_dummy.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				
				dbHelper = new ChaBaikeSQLiteOpenHelper(getActivity());
				Map<String, Object> map = newsList.get(position-2);
				newsid = (String) map.get("id");
				title = (String) map.get("title");
				time = (String) map.get("create_time");
				nickname = (String) map.get("nickname");
				source = (String) map.get("source");
				//查询一下数据库中的历史记录表 看看该id所对应的数据是否已经存在
				int number = dbHelper.selectCount(
						"select count(title) from tb_history where newsid=?",new String[] { newsid });
				if (number != 0) {
					//已经添加了 不做处理
					Log.i("TAG", "该条数据已经添加!");
				} else {
					String sql = "insert into tb_history(newsid , title , source,nickname,create_time) values(?,?,?,?,?)";
					dbHelper.execData(sql, new Object[] { newsid,
							title, source, nickname, time });
					Log.i("TAG", "添加到历史访问记录成功！");
				} 
				
				// 单击后跳转到内容页面
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				Bundle bundle = new Bundle();
				int num = position - 2;
				bundle.putBoolean("isFavorite", false);
				bundle.putString("id", newsList.get(num).get("id").toString());
				bundle.putString("title", newsList.get(num).get("title")
						.toString());
				bundle.putString("nickname", newsList.get(num).get("nickname")
						.toString());
				bundle.putString("create_time",
						newsList.get(num).get("create_time").toString());
				bundle.putString("source", newsList.get(num).get("source")
						.toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		//长点击事件
		xListView_dummy.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
										   int position, long id) {
				Toast.makeText(getActivity(), "position="+position, Toast.LENGTH_SHORT).show();
				CustomDialog customDialog = new CustomDialog(getActivity(), xListView_dummy, newsAdapter,newsList,view,position);
				customDialog.showDialog();
				return true;
			}
		});
		
		imageView_fragment_backtotop.setOnClickListener(new OnClickListener() {
			@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
			@Override
			public void onClick(View v) {
				// 单击时返回顶部
				xListView_dummy.setSelectionFromTop(0, 0);
			}
		});
		return view;
	}

	// 初始化头条广告ViewPager中的三个点
	private void initDots() {
		dots = new ImageView[3];
		for (int i = 0; i < dots.length; i++) {
			dots[i] = (ImageView) layout_ads_container.getChildAt(i);
			dots[i].setEnabled(true);
			dots[i].setTag(i);
			// dots[i].setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// viewPager_firstfragment_ads.setCurrentItem((Integer) v
			// .getTag());
			// }
			// });
		}
		dots[0].setEnabled(false);
	}

	// 下拉刷新列表数据
	public void fillListView(String jsonString) {
		if (page == 1) {
			newsList.clear();
		}
		newsList.addAll(ChaBaikeJsonHelper.parseJsonToList(jsonString));
		newsAdapter.notifyDataSetChanged();
	}

	class MyThread extends Thread implements Runnable {
		@Override
		public void run() {
			super.run();
			// Log.i(TAG, "--网络加载文章json数据：0");
			final byte[] newsResult = HttpURLConnHelper
					.loadByteFromURL(Constants.HEADLINE_URL
							+ Constants.HEADLINE_TYPE + page);
			if (newsResult == null) {
				handler.post(new Runnable() {
					public void run() {
						text_fragment_emptyinfo
								.setText(R.string.prompt_network_connecttimeout);
					}
				});
			} else {
				// 将子线程获取到的茶信息返回给主线程UI
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

	// 上拉刷新
	@Override
	public void onRefresh() {
		page = 1;
		new MyThread().start();
		xListView_dummy.stopRefresh();
		// 转换时间为字符串
//		Calendar calendar = Calendar.getInstance();
//		xListView_dummy.setRefreshTime(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        String date = simpleDateFormat.format(new Date(System.currentTimeMillis()));
//        xListView_dummy.setRefreshTime(date);
        
        xListView_dummy.setRefreshTime(new java.util.Date().toLocaleString());
	}

	// 下拉刷新，加载更多数据
	@Override
	public void onLoadMore() {

		page++;
		new MyThread().start();
		xListView_dummy.stopLoadMore();
	}
	//开启轮播
	private void startRecycle() {
		// 定时器,为了实现自动轮播
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(1);

			}
		}, 0, 3000);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dbHelper != null) {
			dbHelper.close();
		}
	}

	
}
