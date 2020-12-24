package com.appier.mediation.admob.ads;

import android.content.Context;
import android.os.Bundle;

import com.appier.ads.Appier;
import com.appier.ads.AppierBannerAd;
import com.appier.ads.AppierError;
import com.appier.mediation.admob.AppierAdUnitIdentifier;
import com.appier.mediation.admob.AppierPredictHandler;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

import org.json.JSONObject;


public class AppierBanner extends AppierBase implements CustomEventBanner, AppierBannerAd.EventListener {
    private CustomEventBannerListener mAdMobBannerEventListener;
    private AppierBannerAd mAppierBannerAd;

    @Override
    public void requestBannerAd(Context context,
                                CustomEventBannerListener customEventBannerListener,
                                String serverString,
                                AdSize adSize,
                                MediationAdRequest mediationAdRequest,
                                Bundle localExtras) {
        Appier.log("[Appier AdMob Mediation]", "AppierBanner.requestBannerAd()");

        mAdMobBannerEventListener = customEventBannerListener;
        JSONObject serverExtras = parseServerString(serverString);
        String adUnitId = getAdUnitId(localExtras, serverExtras);

        // Get predict result, and passback rapidly.
        String predictZoneId = AppierPredictHandler.getPredictZone(adUnitId);
        if (predictZoneId == null) {
            mAdMobBannerEventListener.onAdFailedToLoad(buildNoBidError());
            return;
        }

        String zoneId = getZoneId(localExtras, serverExtras, predictZoneId);
        int adUnitWidth = getAdWidth(localExtras, serverExtras, adSize.getWidth());
        int adUnitHeight = getAdHeight(localExtras, serverExtras, adSize.getHeight());

        mAppierBannerAd = new AppierBannerAd(context, new AppierAdUnitIdentifier(adUnitId), this);
        mAppierBannerAd.setAdDimension(adUnitWidth, adUnitHeight);
        mAppierBannerAd.setZoneId(zoneId);
        mAppierBannerAd.loadAd();
    }

    @Override
    public void onDestroy() {
        if (mAppierBannerAd != null) {
            mAppierBannerAd.destroy();
            mAppierBannerAd = null;
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onAdLoaded(AppierBannerAd appierBannerAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierBanner.onAdLoaded() (Custom Callback)");
        mAdMobBannerEventListener.onAdLoaded(appierBannerAd.getView());
    }

    @Override
    public void onAdNoBid(AppierBannerAd appierBannerAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierBanner.onAdNoBid() (Custom Callback)");
        mAdMobBannerEventListener.onAdFailedToLoad(buildNoBidError());
    }

    @Override
    public void onAdLoadFail(AppierError appierError, AppierBannerAd appierBannerAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierBanner.onAdLoadFail() (Custom Callback)");
        mAdMobBannerEventListener.onAdFailedToLoad(buildAdError(appierError));
    }

    @Override
    public void onViewClick(AppierBannerAd appierBannerAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierBanner.onViewClick() (Custom Callback)");
        mAdMobBannerEventListener.onAdClicked();
    }
}
