package com.appier.mediation.admob;

import com.appier.ads.Appier;


public class AppierAdapterConfiguration {

    public AppierAdapterConfiguration() {}

    public String getMediationVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public String getNetworkSdkVersion() {
        return Appier.getVersionName();
    }

    public String getAdvertiserName() {
        return BuildConfig.APPIER_ADVERTISER_NAME;
    }
}