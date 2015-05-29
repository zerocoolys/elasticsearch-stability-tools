package com.ss.core;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by dolphineor on 2015-5-28.
 */
public class RandomDataReader implements Constants {

    private final static Random RANDOM = new Random();
    private final static URL IP_DATA_URL = ClassLoader.getSystemResource("ip.txt");
    private static List<String> ipLines = null;


    public static Map<String, String> getIpInfo() {
        if (ipLines == null) {
            synchronized (RandomDataReader.class) {
                if (ipLines == null) {
                    try {
                        ipLines = Files.readAllLines(Paths.get(IP_DATA_URL.getPath()));
                    } catch (IOException e) {
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

    public static String getSearchEngineInfo() {
        return SEARCH_ENGINE_DATA.get(RANDOM.nextInt(SEARCH_ENGINE_DATA_LENGTH));
    }
}
