package com.kangogo.threepmanager;

/**
 * Created by Joel on 11/01/2021.
 */
public class Config {
    //JSON URL
    public static final String DATA_URL = "http://172.16.4.253/server/propertySpinner.php";

    //Tags used in the JSON String
    public static final String TAG_NAME = "property_name";

    
    public static final String JSON_ARRAY = "result";
	//Server url
	private final String SERVER_URL = "http://3modernsystems.com/threepmobileserver/";
	//private final String SERVER_URL = "http://3modernsystems.com/threepmobileserver_dev/";

public String getSERVERURL() {
    return SERVER_URL;
}

private static final Config ourInstance = new Config();
public static Config getInstance() {
      return ourInstance;
   }
public Config(){
	
}
}