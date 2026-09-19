package com.threemsystems.rentmanager;

public class Config {
    private static final Config INSTANCE = new Config();
    // HTTPS is available on this host but the certificate expired in 2023; keep HTTP until it is renewed.
    private final String SERVER_URL = "http://3modernsystems.co.ke/threepmobileserver/";

    private Config() {}

    public static Config getInstance() {
        return INSTANCE;
    }

    public String getSERVERURL() {
        return SERVER_URL;
    }
}
