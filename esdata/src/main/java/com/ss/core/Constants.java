package com.ss.core;

import java.util.*;

/**
 * Created by dolphineor on 2015-5-28.
 */
public interface Constants {

    String ES_INDEX = "_index";
    String ES_REMOTE = "remote";
    String ES_REGION = "region";
    String ES_CITY = "city";
    String ES_ISP = "isp";
    String ES_CURR_ADDRESS = "loc";
    String ES_UNIX_TIME = "utime";
    String ES_OS = "os";
    String ES_CT = "ct";
    String ES_T = "t";         // trackId
    String ES_TT = "tt";       // 访问次数标识符
    String ES_VID = "vid";     // 访客唯一标识符
    String ES_UCV = "_ucv";    // 访客数(UV)区分标识符
    String ES_RF = "rf";
    String ES_SE = "se";       //搜索引擎
    String ES_KW = "kw";       // 搜索词
    String ES_RF_TYPE = "rf_type";     // 1. 直接访问, 2. 搜索引擎, 3. 外部链接
    String ES_DOMAIN = "dm";
    String ES_ENTRANCE = "entrance";   // 入口页面
    String ES_PM = "pm";// 访问终端（0.PC,1.手机）
    String ES_DT = "dt";// 浏览页面的当前时间

    /**
     * IP数据
     */
    int IP_DATA_LENGTH = 100;

    /**
     * 搜索引擎数据
     */
    int SEARCH_ENGINE_DATA_LENGTH = 4;
    List<String> SEARCH_ENGINE_DATA = new ArrayList<>(Arrays.asList("百度", "搜狗", "好搜", "必应"));

    /**
     * 搜索词数据
     */
    int SEARCH_KW_DATA_LENGTH = 5;
    List<String> SEARCH_KW_DATA = new ArrayList<>(Arrays.asList("百思", "best-ad.cn", "百思seo", "百思 推广", "百思 广告"));

    /**
     * 搜索引擎时的rf数据
     */
    Map<String, String> RF_SE_DATA = new HashMap<String, String>() {
        {
            put("00", "http://www.baidu.com/baidu?&ie=utf-8&word=%E7%99%BE%E6%80%9D");
            put("01", "http://www.baidu.com/s?ie=UTF-8&wd=best-ad.cn");
            put("02", "http://www.baidu.com/s?ie=UTF-8&wd=%E7%99%BE%E6%80%9Dseo");
            put("03", "http://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&tn=baidu&wd=%E7%99%BE%E6%80%9D%20%E6%8E%A8%E5%B9%BF&rsv_pq=c5d3b06600058cdb&rsv_t=77845%2FEGmbyezBGXNF1xn%2BzW9vy8IT0YXxKZ3bCwBsbUOS3QDDKGp0ikLfY&rsv_enter=0&rsv_n=2&rsv_sug3=1&inputT=490&rsv_sug4=490");
            put("04", "http://www.baidu.com/baidu?&ie=utf-8&word=%E7%99%BE%E6%80%9D%20%E5%B9%BF%E5%91%8A");
            put("10", "http://www.sogou.com/web?query=%E7%99%BE%E6%80%9D&ie=utf8&_ast=1433318912&_asf=null&w=01029901&p=40040100&dp=1&cid=&cid=&sut=1021&sst0=1433318913293&lkt=0%2C0%2C0");
            put("11", "http://www.sogou.com/web?query=best-ad.cn&_asf=www.sogou.com&_ast=&w=01019900&p=40040100&ie=utf8&sut=4918&sst0=1433318881722&lkt=0%2C0%2C0");
            put("12", "http://www.sogou.com/web?query=%E7%99%BE%E6%80%9Dseo&ie=utf8&_ast=1433318912&_asf=null&w=01029901&p=40040100&dp=1&cid=&cid=&sut=12625&sst0=1433318928842&lkt=0%2C0%2C0");
            put("13", "http://www.sogou.com/web?query=%E7%99%BE%E6%80%9D+%E5%B9%BF%E5%91%8A&ie=utf8&_ast=1433318912&_asf=null&w=01029901&p=40040100&dp=1&cid=&cid=&sut=2076&sst0=1433318949546&lkt=0%2C0%2C0");
            put("14", "http://www.sogou.com/web?query=%E7%99%BE%E6%80%9D+%E5%B9%BF%E5%91%8A&ie=utf8&_ast=1433318912&_asf=null&w=01029901&p=40040100&dp=1&cid=&sut=884&sst0=1433318890781&lkt=0%2C0%2C0");
            put("20", "http://www.haosou.com/s?src=360chrome_newtab_search&ie=utf-8&q=%E7%99%BE%E6%80%9D");
            put("21", "http://www.haosou.com/s?ie=utf-8&src=360chrome_toolbar_search&q=best-ad.cn");
            put("22", "http://www.haosou.com/s?psid=76d16ee13477437d8b0c03cc99859624&q=%E7%99%BE%E6%80%9Dseo&src=srp&fr=360chrome_toolbar_search");
            put("23", "http://www.haosou.com/s?src=360chrome_newtab_search&ie=utf-8&q=%E7%99%BE%E6%80%9D%20%E6%8E%A8%E5%B9%BF");
            put("24", "http://www.haosou.com/s?ie=utf-8&src=360chrome_toolbar_search&q=%E7%99%BE%E6%80%9D+%E5%B9%BF%E5%91%8A");
            put("30", "http://cn.bing.com/search?q=%E7%99%BE%E6%80%9D&go=%E6%8F%90%E4%BA%A4&qs=n&form=QBRE&pq=%E7%99%BE%E6%80%9D&sc=8-2&sp=-1&sk=&cvid=f509faaf032546c1b3a47673d4acc89b");
            put("31", "http://cn.bing.com/search?q=best-ad.cn&go=%E6%8F%90%E4%BA%A4&qs=n&form=QBLH&pq=best-ad.cn&sc=0-0&sp=-1&sk=&cvid=b20046d9e5484970be3e5182620b06fb");
            put("32", "http://cn.bing.com/search?q=%E7%99%BE%E6%80%9Dseo&go=%E6%8F%90%E4%BA%A4&qs=n&form=QBRE&pq=%E7%99%BE%E6%80%9Dseo&sc=0-5&sp=-1&sk=&cvid=2c384f93bf0b43408425e9df7dc44347");
            put("33", "http://cn.bing.com/search?q=%E7%99%BE%E6%80%9D+%E6%8E%A8%E5%B9%BF&go=%E6%8F%90%E4%BA%A4&qs=n&form=QBRE&pq=%E7%99%BE%E6%80%9D+%E6%8E%A8%E5%B9%BF&sc=0-0&sp=-1&sk=&cvid=77020776b25746978032692389b4776b");
            put("34", "http://cn.bing.com/search?q=%E7%99%BE%E6%80%9D+%E5%B9%BF%E5%91%8A&go=%E6%8F%90%E4%BA%A4&qs=n&form=QBLH&pq=%E7%99%BE%E6%80%9D+%E5%B9%BF%E5%91%8A&sc=0-0&sp=-1&sk=&cvid=b20046d9e5484970be3e5182620b06fb");
        }
    };

    /**
     * 外部链接数据
     */
    int EXTERNAL_LINK_PAGE_LENGTH = 3;
    List<String> EXTERNAL_LINK_PAGE = new ArrayList<>(Arrays.asList("http://www.neitui.me/j/376031",
            "http://127.0.0.1:8000/page/heatmap.html",
            "http://www.lagou.com/jobs/633836.html?source=search"));

    /**
     * 访问页面数据
     */
    int LOC_PAGE_MAX_LENGTH = 4;
    List<String> ENTRANCE_PAGE = new ArrayList<>(Collections.singletonList("http://www.best-ad.cn/"));
    List<String> PAGE_VIEW = new ArrayList<>(Arrays.asList("http://www.best-ad.cn/Quick_vote.html",
            "http://www.best-ad.cn/seo.html", "http://www.best-ad.cn/search.html",
            "http://www.best-ad.cn/Wireless.html", "http://www.best-ad.cn/Eye.html",
            "http://sem.best-ad.cn/login", "http://sem.best-ad.cn/index",
            "http://sem.best-ad.cn/keyword_group", "http://sem.best-ad.cn/bidding/index",
            "http://sem.best-ad.cn/reportIndex", "http://sem.best-ad.cn/assistant/index"));

    /**
     * os数据
     */
    int OS_PC_DATA_LENGTH = 2;
    int OS_PHONE_DATA_LENGTH = 2;
    List<String> OS_PC_DATA = new ArrayList<>(Arrays.asList("Linux", "Windows 7", "Windows 8"));
    List<String> OS_PHONE_DATA = new ArrayList<>(Arrays.asList("iPhone", "Android", "symbian"));

    /**
     * pm数据
     */
    int PM_DATA_LENGTH = 2;
    List<String> PM_DATA = new ArrayList<>(Arrays.asList("0", "1"));

    /**
     * 新老访客数组初始化长度
     */
//    int CT_INIT_LENGTH = 10000;
    int CT_INIT_LENGTH = 4000;

    /**
     * 最近index初始数量
     */
//    long ES_INDEX_BASE_COUNT = 1000 * 1000;
    long ES_INDEX_BASE_COUNT = 1000 * 1000;

    /**
     * index增量
     */
//    long ES_INDEX_INCREMENT = 1000 * 1000 * 2;
    long ES_INDEX_INCREMENT = 0;
}

