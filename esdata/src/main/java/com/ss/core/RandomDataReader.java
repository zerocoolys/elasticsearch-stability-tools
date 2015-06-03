package com.ss.core;

import com.ss.tools.ConcurrentDateUtil;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by dolphineor on 2015-5-28.
 */
public class RandomDataReader implements Constants {

    private final static Random RANDOM = new Random();
    private final static URL IP_DATA_URL = ClassLoader.getSystemResource("ip.txt");
    private static List<String> ipLines = null;

    /**
     * 获取ip信息
     * ES_REMOTE
     * ES_REGION
     * ES_CITY
     * ES_ISP
     */
    public static Map<String, String> getIpInfo() {
        if (ipLines == null) {
            synchronized (RandomDataReader.class) {
                if (ipLines == null) {
                    try {
                        ipLines = Files.readAllLines(Paths.get(IP_DATA_URL.toURI()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Map<String, String> ipMap = new HashMap<>();
        String[] ipInfo = ipLines.get(RANDOM.nextInt(IP_DATA_LENGTH)).split(":");
        ipMap.put(ES_REMOTE, ipInfo[0]);
        ipMap.put(ES_REGION, ipInfo[1]);
        ipMap.put(ES_CITY, ipInfo[2]);
        ipMap.put(ES_ISP, ipInfo[3]);

        return ipMap;
    }

    /**
     * 获取loc以及utime信息
     */
    public static Map<String, Object> getLocAndUTimeInfo(Object accessIndex) {
        Map<String, Object> map = new HashMap<>();

        int pageCount = RANDOM.nextInt(LOC_PAGE_MAX_LENGTH) + 1;

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(ConcurrentDateUtil.parse(accessIndex.toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] locArray = new String[pageCount];
        String[] uTimeArray = new String[pageCount];
        int seed = RANDOM.nextInt(PAGE_VIEW.size() - LOC_PAGE_MAX_LENGTH + 1);
        for (int i = 0; i < pageCount; i++) {
            if (i == 0) {
                c.add(Calendar.HOUR, RANDOM.nextInt(24));
                c.add(Calendar.MINUTE, RANDOM.nextInt(60));
                c.add(Calendar.SECOND, RANDOM.nextInt(10) + 1);
                map.put(ES_DT, c.getTimeInMillis());
                locArray[i] = ENTRANCE_PAGE.get(0);
            } else {
                c.add(Calendar.SECOND, RANDOM.nextInt(10) + 1);
                locArray[i] = PAGE_VIEW.get(i + seed);
            }
            uTimeArray[i] = c.getTimeInMillis() + "";
        }
        map.put(ES_CURR_ADDRESS, locArray);
        map.put(ES_UNIX_TIME, uTimeArray);
        return map;
    }

    /**
     * 获取rf_type相关信息
     * rf.
     * se.
     */
    public static Map<String, Object> getRfTypeInfo() {
        Map<String, Object> rf = new HashMap<>();
        int rfType = RANDOM.nextInt(3) + 1;
        if (rfType == 1) {// 直接访问
            rf.put(ES_RF_TYPE, rfType);
            rf.put(ES_RF, "-");
            rf.put(ES_SE, "-");
            rf.put(ES_ENTRANCE, ENTRANCE_PAGE.get(0));
        } else if (rfType == 2) {// 搜索引擎
            rf.put(ES_RF_TYPE, rfType);
            int a = RANDOM.nextInt(SEARCH_ENGINE_DATA_LENGTH - 1);
            rf.put(ES_SE, SEARCH_ENGINE_DATA.get(a));
            int b = RANDOM.nextInt(SEARCH_KW_DATA_LENGTH - 1);
            rf.put(ES_KW, SEARCH_KW_DATA.get(b));
            rf.put(ES_RF, RF_SE_DATA.get("" + a + b));
        } else {// 外部链接
            rf.put(ES_RF_TYPE, rfType);
            rf.put(ES_DOMAIN, ENTRANCE_PAGE.get(0));
            rf.put(ES_ENTRANCE, ENTRANCE_PAGE.get(0));
            rf.put(ES_RF, EXTERNAL_LINK_PAGE.get(RANDOM.nextInt(EXTERNAL_LINK_PAGE_LENGTH - 1)));
        }
        return rf;
    }

    /**
     * 获取OS以及PM信息
     * os　操作系统
     * pm　访问终端(0 PC,1 手机)
     */
    public static Map<String, String> getOSAndPMInfo() {
        Map<String, String> os_pm_map = new HashMap<>();
        os_pm_map.put(ES_PM, PM_DATA.get(RANDOM.nextInt(PM_DATA_LENGTH - 1)));
        os_pm_map.put(ES_OS, getOSInfo(os_pm_map.get(ES_PM)));
        return os_pm_map;
    }

    private static String getOSInfo(String pmType) {
        if (pmType.equals("0")) {// PC
            return OS_PC_DATA.get(RANDOM.nextInt(OS_PC_DATA_LENGTH - 1));
        } else {
            return OS_PHONE_DATA.get(RANDOM.nextInt(OS_PHONE_DATA_LENGTH - 1));
        }
    }

    /**
     * 获取_INDEX信息
     */
    public static String getIndexInfo(List<String> indexes) {
        return indexes.get(RANDOM.nextInt(indexes.size()));
    }

}
