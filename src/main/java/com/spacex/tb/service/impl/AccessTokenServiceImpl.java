package com.spacex.tb.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.spacex.tb.config.TbConfig;
import com.spacex.tb.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {
    public final Long  EXPIRES_IN = 12 * 3600 * 1000L;
    @Resource
    private TbConfig tbConfig;

    @Override
    public String appAccessToken() {
        String appId = tbConfig.getTBAppId();
        String appSecret = tbConfig.getTBappSecret();
        String TOKEN_APPID = appId;

        Algorithm algorithm = Algorithm.HMAC256(appSecret);
        long timestamp = System.currentTimeMillis();
        Date issuedAt = new Date(timestamp);
        Date expiresAt = new Date(timestamp + EXPIRES_IN);

        return JWT.create()
                .withClaim(TOKEN_APPID, appId)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }
}
