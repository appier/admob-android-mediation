package com.appier.mediation.admob;

import com.appier.ads.Appier;


public class AppierAdapterConfiguration {

    public AppierAdapterConfiguration() {}

    public static String getMediationVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public static String getNetworkSdkVersion() {
        return Appier.getVersionName();
    }

    public static String getAdvertiserName() {
        return BuildConfig.APPIER_ADVERTISER_NAME;
    }
}