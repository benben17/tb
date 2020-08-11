package com.spacex.tb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class TbConfig {
    @Value("${tb.api.url}")
    public String TBApiUrl;

    @Value("${tb.app-id}")
    public String TBAppId;

    @Value("${tb.appSecret}")
    public String TBappSecret;

    public String getTBApiUrl() {
        return TBApiUrl;
    }

    public void setTBApiUrl(String TBApiUrl) {
        this.TBApiUrl = TBApiUrl;
    }

    public String getTBAppId() {
        return TBAppId;
    }

    public void setTBAppId(String TBAppId) {
        this.TBAppId = TBAppId;
    }

    public String getTBappSecret() {
        return TBappSecret;
    }

    public void setTBappSecret(String TBappSecret) {
        this.TBappSecret = TBappSecret;
    }
}
