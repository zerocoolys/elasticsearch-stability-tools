package com.ss.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    int IP_DATA_LENGTH = 100;
    List<String> SEARCH_ENGINE_DATA = new ArrayList<>(Arrays.asList("百度", "搜狗", "好搜", "必应"));
    List<String> SEARCH_KW_DATA = new ArrayList<>(Arrays.asList("百思", "best-ad.cn", "百思seo", "百思 推广", "百思 广告"));
    int LOC_PAGE_MAX_LENGTH = 4;
    List<String> ENTRANCE_PAGE = new ArrayList<>(Collections.singletonList("http://www.best-ad.cn/"));
    List<String> PAGE_VIEW = new ArrayList<>(Arrays.asList("http://www.best-ad.cn/Quick_vote.html",
            "http://www.best-ad.cn/seo.html", "http://www.best-ad.cn/search.html",
            "http://www.best-ad.cn/Wireless.html", "http://www.best-ad.cn/Eye.html",
            "http://sem.best-ad.cn/login", "http://sem.best-ad.cn/index",
            "http://sem.best-ad.cn/keyword_group", "http://sem.best-ad.cn/bidding/index",
            "http://sem.best-ad.cn/reportIndex", "http://sem.best-ad.cn/assistant/index"));

    /**
     * os
     */
    List<String> OS_DATA = new ArrayList<>(Arrays.asList("Linux", "Windows 7", "Windows 8"));
    /**
     * pm
     */
    List<String> PM_DATA = new ArrayList<>(Arrays.asList("0", "1"));
    /**
     * 新老访客数组初始化长度
     */
    int CT_INIT_LENGTH = 10000;

    /**
     * index初始数量
     */
    long ES_INDEX_START_COUNT = 1000 * 1000;

    /**
     * index增量
     */
    long ES_INDEX_INCREMENT = 1000 * 1000 * 2;
}
