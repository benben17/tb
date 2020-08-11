package com.spacex.tb.parm;

public class Headers {
    private String Authorization;
    private String XTenantId;
    private String XTenantType;

    public String getAuthorization() {
        return Authorization;
    }

    public void setAuthorization(String authorization) {
        Authorization = authorization;
    }

    public String getXTenantId() {
        return XTenantId;
    }

    public void setXTenantId(String XTenantId) {
        this.XTenantId = XTenantId;
    }

    public String getXTenantType() {
        return XTenantType;
    }

    public void setXTenantType(String XTenantType) {
        this.XTenantType = XTenantType;
    }
}
