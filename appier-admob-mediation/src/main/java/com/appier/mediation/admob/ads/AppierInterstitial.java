package com.appier.mediation.admob.ads;


import android.content.Context;
import android.os.Bundle;

import com.appier.ads.Appier;
import com.appier.ads.AppierError;
import com.appier.ads.AppierInterstitialAd;
import com.appier.mediation.admob.AppierAdUnitIdentifier;
import com.appier.mediation.admob.AppierPredictHandler;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;

import org.json.JSONObject;


public class AppierInterstitial extends AppierBase implements CustomEventInterstitial, AppierInterstitialAd.EventListener {

    private CustomEventInterstitialListener mAdMobInterstitialEventListener;
    private AppierInterstitialAd mAppierInterstitialAd;

    @Override
    public void requestInterstitialAd(Context context,
                                      CustomEventInterstitialListener customEventInterstitialListener,
                                      String serverString,
                                      MediationAdRequest mediationAdRequest,
                                      Bundle localExtras) {
        Appier.log("[Appier AdMob Mediation]", "AppierInterstitial.requestInterstitialAd()");

        mAdMobInterstitialEventListener = customEventInterstitialListener;
        JSONObject serverExtras = parseServerString(serverString);
        String adUnitId = getAdUnitId(localExtras, serverExtras);

        // Get predict result, and passback rapidly.
        String predictZoneId = AppierPredictHandler.getPredictZone(adUnitId);
        if (predictZoneId == null) {
            customEventInterstitialListener.onAdFailedToLoad(buildNoBidError());
            return;
        }

        String zoneId = getZoneId(localExtras, serverExtras, predictZoneId);
        int adWidth = getAdWidth(localExtras, serverExtras);
        int adHeight = getAdHeight(localExtras, serverExtras);

        mAppierInterstitialAd = new AppierInterstitialAd(context, new AppierAdUnitIdentifier(adUnitId), this);
        mAppierInterstitialAd.setZoneId(zoneId);
        mAppierInterstitialAd.setAdDimension(adWidth, adHeight);
        mAppierInterstitialAd.loadAd();
        
    }

    @Override
    public void showInterstitial() {
        mAppierInterstitialAd.showAd();
    }

    @Override
    public void onDestroy() {
        if (mAppierInterstitialAd != null) {
            mAppierInterstitialAd.destroy();
            mAppierInterstitialAd = null;
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onAdLoaded(AppierInterstitialAd appierInterstitialAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierInterstitial.onAdLoaded() (Custom Callback)");
        mAppierInterstitialAd = appierInterstitialAd;
        mAdMobInterstitialEventListener.onAdLoaded();
    }

    @Override
    public void onAdNoBid(AppierInterstitialAd appierInterstitialAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierInterstitial.onAdNoBid() (Custom Callback)");
        mAdMobInterstitialEventListener.onAdFailedToLoad(buildNoBidError());
    }

    @Override
    public void onAdLoadFail(AppierError appierError, AppierInterstitialAd appierInterstitialAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierInterstitial.onAdLoadFail() (Custom Callback)");
        mAdMobInterstitialEventListener.onAdFailedToLoad(buildAdError(appierError));
    }

    @Override
    public void onViewClick(AppierInterstitialAd appierInterstitialAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierInterstitial.onViewClick() (Custom Callback)");
        mAdMobInterstitialEventListener.onAdClicked();
    }

    @Override
    public void onShown(AppierInterstitialAd appierInterstitialAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierInterstitial.onShown() (Custom Callback)");
        mAdMobInterstitialEventListener.onAdOpened();
    }

    @Override
    public void onShowFail(AppierError appierError, AppierInterstitialAd appierInterstitialAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierInterstitial.onShowFail() (Custom Callback)");
        mAdMobInterstitialEventListener.onAdFailedToLoad(buildAdError(AppierError.NETWORK_ERROR));
    }

    @Override
    public void onDismiss(AppierInterstitialAd appierInterstitialAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierInterstitial.onDismiss() (Custom Callback)");
        mAdMobInterstitialEventListener.onAdClosed();
    }
}
