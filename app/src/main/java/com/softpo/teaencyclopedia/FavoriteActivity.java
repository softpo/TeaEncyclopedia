package com.softpo.teaencyclopedia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.softpo.teaencyclopedia.adapter.MyFavoriteListAdapter;
import com.softpo.teaencyclopedia.help.ChaBaikeSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoriteActivity extends Activity {
	private ListView listView_favorite_infolist;
	private TextView text_favorite_emptyinfo;
	private List<Map<String, Object>> list;
	private ChaBaikeSQLiteOpenHelper dbHelper;
	private MyFavoriteListAdapter adapter;
	private TextView title;//该界面显示标题
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);
		title = (TextView) findViewById(R.id.text_favorite_title);
		listView_favorite_infolist = (ListView) findViewById(R.id.listView_favorite_infolist);
		text_favorite_emptyinfo = (TextView) findViewById(R.id.text_favorite_emptyinfo);

		list = new ArrayList<Map<String, Object>>();
		dbHelper = new ChaBaikeSQLiteOpenHelper(this);
		type = getIntent().getIntExtra("type", 1);
		switch (type) {
		case 1:
			title.setText("我的收藏");
			list = dbHelper.selectList("select newsid,title , source,nickname,create_time from tb_favorites order by _id desc ",null);
			break;
		case 2:
			title.setText("历史访问记录");
			list = dbHelper.selectList("select newsid,title , source,nickname,create_time from tb_history order by _id desc ",null);
			break;
		}
		
		adapter = new MyFavoriteListAdapter(this, list);
		listView_favorite_infolist.setAdapter(adapter);
		listView_favorite_infolist.setEmptyView(text_favorite_emptyinfo);
		listView_favorite_infolist
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {
						Intent intent = new Intent(FavoriteActivity.this,
								DetailActivity.class);
						Bundle bundle = new Bundle();
						bundle.putBoolean("isFavorite", true);
						bundle.putString("id", list.get(position).get("newsid")
								.toString());
						bundle.putString("title",
								list.get(position).get("title").toString());
						bundle.putString("nickname",
								list.get(position).get("nickname").toString());
						bundle.putString("create_time",
								list.get(position).get("create_time")
										.toString());
						bundle.putString("source",
								list.get(position).get("source").toString());
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
		registerForContextMenu(listView_favorite_infolist);
	}

	public void clickButton(View view) {
		switch (view.getId()) {
		case R.id.imageView_favorite_back:
			finish();
			break;
		case R.id.imageView_favorite_home:
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		menu.setHeaderIcon(R.drawable.ic_logo);
		menu.setHeaderTitle(list.get(info.position).get("title").toString());
		getMenuInflater().inflate(R.menu.favorite_item_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final String newsid = list.get(info.position).get("newsid").toString();
		switch (item.getItemId()) {
		case R.id.action_delete://删除
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.ic_logo);
			builder.setTitle(R.string.prompt_alert);
			builder.setMessage(R.string.prompt_confirm_delete);
			builder.setPositiveButton(R.string.button_confirm,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							boolean flag = false;
							switch (type) {
							case 1:
								flag = dbHelper.execData("delete from tb_favorites where newsid=?",new String[] { newsid });
								if (flag) {
									list.clear();
									list.addAll(dbHelper
											.selectList(
													"select newsid , title , source,nickname,create_time from tb_favorites",
													null));
									adapter.notifyDataSetChanged();
									Toast.makeText(FavoriteActivity.this,R.string.prompt_delete_ok, Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(FavoriteActivity.this,R.string.prompt_delete_err, Toast.LENGTH_SHORT).show();
								}
								break;
							case 2:
								flag = dbHelper.execData("delete from tb_history where newsid=?",new String[] { newsid });
								if (flag) {
									list.clear();
									list.addAll(dbHelper
											.selectList(
													"select newsid , title , source,nickname,create_time from tb_history",
													null));
									adapter.notifyDataSetChanged();
									Toast.makeText(FavoriteActivity.this,R.string.prompt_delete_ok, Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(FavoriteActivity.this,R.string.prompt_delete_err, Toast.LENGTH_SHORT).show();
								}
								break;
							}
							
						}
					});
			builder.setNegativeButton(R.string.button_cancel, null);
			builder.create().show();
			break;
		case R.id.action_nothing:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbHelper != null) {
			dbHelper.destroy();
		}
	}

}
