package com.ss.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by dolphineor on 2015-5-28.
 */
public interface Constants {

    String ES_REMOTE = "remote";
    String ES_REGION = "region";
    String ES_CITY = "city";
    String ES_ISP = "isp";
    String ES_CURR_ADDRESS = "loc";
    String ES_UNIX_TIME = "utime";
    String ES_T = "t";         // trackId
    String ES_TT = "tt";       // 访问次数标识符
    String ES_VID = "vid";     // 访客唯一标识符
    String ES_UCV = "_ucv";    // 访客数(UV)区分标识符
    String ES_RF = "rf";
    String ES_SE = "se";
    String ES_KW = "kw";       // 搜索词
    String ES_RF_TYPE = "rf_type";     // 1. 直接访问, 2. 搜索引擎, 3. 外部链接
    String ES_DOMAIN = "dm";
    String ES_ENTRANCE = "entrance";   // 入口页面

    int IP_DATA_LENGTH = 100;
    int SEARCH_ENGINE_DATA_LENGTH = 4;
    List<String> SEARCH_ENGINE_DATA = new ArrayList<>(Arrays.asList("百度", "搜狗", "好搜", "必应"));
    List<String> ENTRANCE_PAGE = new ArrayList<>(Collections.singletonList("http://www.best-ad.cn/"));
    List<String> PAGE_VIEW = new ArrayList<>(Arrays.asList("http://www.best-ad.cn/Quick_vote.html",
            "http://www.best-ad.cn/seo.html", "http://www.best-ad.cn/search.html",
            "http://www.best-ad.cn/Wireless.html", "http://www.best-ad.cn/Eye.html",
            "http://sem.best-ad.cn/login", "http://sem.best-ad.cn/index",
            "http://sem.best-ad.cn/keyword_group", "http://sem.best-ad.cn/bidding/index",
            "http://sem.best-ad.cn/reportIndex", "http://sem.best-ad.cn/assistant/index"));
}
