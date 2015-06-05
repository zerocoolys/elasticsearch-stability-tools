package com.nelo2.benchmark.utils;

import org.apache.commons.lang.StringUtils;
/**
 * 
 * @author TomDing
 * 计算索引的工具类
 */
public class IndexUtils {

	public static String[] calculateIndex(String prdfixName, int count) {

		String indexNames[];
		if (StringUtils.isBlank(prdfixName) || count == 0) {
			return null;
		}
		indexNames = new String[count];
		StringBuffer indexName = new StringBuffer(prdfixName);
		for (int i = 0; i < count;) {
			
			
			indexNames[i] = indexName.append("-").append(format(2,++i)).toString();
			indexName.delete(indexName.lastIndexOf("-"), indexName.length());
		}

		return indexNames;
	}
	
    private static String format(int length, int number) {
        String f = "%0" + length + "d";
        return String.format(f, number);
    }
	

	public static void main(String[] args) {
		String[] test = calculateIndex("vic-2015-06",100);
		
		for(String s : test) {
			System.out.println(s);
		}
	}
}
