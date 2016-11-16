package com.softpo.teaencyclopedia.help;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChaBaikeJsonHelper {

	// 解析头条广告信息的json，将广告信息的数据存入集合
	public static List<Map<String, String>> parseAdsJsonToList(String jsonString) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			JSONObject object = new JSONObject(jsonString);
			JSONArray array_data = object.getJSONArray("data");
			for (int i = 0; i < array_data.length(); i++) {
				JSONObject object_data = array_data.getJSONObject(i);
				Map<String, String> map = new HashMap<String, String>();
				map.put("title", object_data.getString("title"));
				map.put("image", object_data.getString("image"));
				list.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	// 解析新闻信息的json，将新闻信息的数据存入集合
	public static List<Map<String, Object>> parseJsonToList(String jsonString) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			JSONObject object = new JSONObject(jsonString);
			JSONArray array_data = object.getJSONArray("data");
			for (int i = 0; i < array_data.length(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				JSONObject object_data = array_data.getJSONObject(i);
				map.put("id", object_data.getString("id"));
				map.put("title", object_data.getString("title"));
				String source = object_data.optString("source");
				if (source != null) {
					map.put("source", object_data.getString("source"));
				} else {
					map.put("source", "");
				}
				String nickname = object_data.optString("nickname");
				if (nickname != null) {
					map.put("nickname", object_data.getString("nickname"));
				} else {
					map.put("nickname", "");
				}
				String create_time = object_data.optString("create_time");
				if (create_time != null) {
					map.put("create_time", object_data.getString("create_time"));
				} else {
					map.put("create_time", "");
				}
				String wap_thumb = object_data.optString("wap_thumb");
				if (wap_thumb != null) {
					map.put("wap_thumb", object_data.getString("wap_thumb"));
				} else {
					map.put("wap_thumb", "");
				}
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
