package com.nelo2.benchmark.utils;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.settings.Settings;


/**
 * 更改索引的setting信息
 */
import com.nelo2.benchmark.Logging;

public class UpdateIndexSettingUtil {

	public static UpdateSettingsResponse updateIndexSetting(Client client ,Settings settings,String indexname){
		 UpdateSettingsResponse updateSettingsResponse = null;
		try {
			updateSettingsResponse = client.admin().indices().prepareUpdateSettings(indexname).setSettings(settings).execute().actionGet();
		} catch (ElasticsearchException e) {
			Logging.err(e.getMessage());
		}
		 return updateSettingsResponse;
	}
	
	public static Settings getSettingConfig(){
		Builder  builder = ImmutableSettings.settingsBuilder();
		String settingconfig = CommonUtils.readProperties("settingconfig");
		if(StringUtils.isNotBlank(settingconfig)){
			String[] configs = settingconfig.split("\\|");
			for(String eachConfig : configs){
				String value = eachConfig.substring(eachConfig.indexOf(":")+1);
				if(value.equals("true")){
					builder.put(eachConfig.substring(0,eachConfig.indexOf(":")),true );
				}else if(value.equals("false")){
					builder.put(eachConfig.substring(0,eachConfig.indexOf(":")),false );
				}else{
					builder.put(eachConfig.substring(0,eachConfig.indexOf(":")),value );
				}
				
			}
		}
		
		return builder.build();
	}
	public static void main(String[] args) {
		/*String str = "refresh_interval:50s";
		System.out.println(str.substring(str.indexOf(":")+1));
		System.out.println(str.substring(0,str.indexOf(":")));*/
//		System.out.println( UpdateIndexSettingUtil.updateIndexSetting(CommonUtils.getNewClient(), getSettingConfig(), "access-2015-04-20"));
	}
	 
}
