package com.softpo.teaencyclopedia.help;

public class Constants {
	public static final String HEADLINE_URL = "http://sns.maimaicha.com/api?apikey=b4f4ee31a8b9acc866ef2afb754c33e6&format=json&method=news.getHeadlines";
	public static final String BASE_URL = "http://sns.maimaicha.com/api?apikey=b4f4ee31a8b9acc866ef2afb754c33e6&format=json&method=news.getListByType";
	public static final String HEADERIMAGE_URL = "http://sns.maimaicha.com/api?apikey=b4f4ee31a8b9acc866ef2afb754c33e6&format=json&method=news.getSlideshow";
	public static final String CONTENT_URL = "http://sns.maimaicha.com/api?apikey=b4f4ee31a8b9acc866ef2afb754c33e6&format=json&method=news.getNewsContent&id=";
	//显示详情，不是url，而是具体的数据，webView可以展示url，还可以展示具体数据（html）
	
	//搜索url
	public static final String SEARCH_URL = "http://sns.maimaicha.com/api?apikey=b4f4ee31a8b9acc866ef2afb754c33e6&format=json&method=news.searcListByTitle&search=";
	
	//推荐 url，跟搜索类似
	public static final String SUGGEST_URL = "http://sns.maimaicha.com/api?apikey=b4f4ee31a8b9acc866ef2afb754c33e6&format=json&method=news.searcListByTitle&search=";

	public static final String HEADLINE_TYPE = "&rows=15&page=";// 头条
	public static final String CYCLOPEDIA_TYPE = "&type=16&rows=15&page=";// 百科
	public static final String CONSULT_TYPE = "&type=52&rows=15&page=";// 资讯
	public static final String OPERATE_TYPE = "&type=53&rows=15&page=";// 经营
	public static final String DATA_TYPE = "&type=54&rows=15&page=";// 数据

	public static final int STATE = 0;
	//public static final int STATE1 = 1;
	//public static final int STATE2 = 2;
}
