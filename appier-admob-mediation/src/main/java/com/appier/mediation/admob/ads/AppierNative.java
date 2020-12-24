package com.appier.mediation.admob.ads;

import android.content.Context;
import android.os.Bundle;

import com.appier.ads.Appier;
import com.appier.ads.AppierError;
import com.appier.ads.AppierNativeAd;
import com.appier.ads.common.ImageLoader;
import com.appier.mediation.admob.AppierAdUnitIdentifier;
import com.appier.mediation.admob.AppierNativeAdMapper;
import com.appier.mediation.admob.AppierPredictHandler;
import com.google.android.gms.ads.mediation.NativeMediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventNative;
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener;

import org.json.JSONException;
import org.json.JSONObject;


public class AppierNative extends AppierBase implements CustomEventNative, AppierNativeAd.EventListener {

    private CustomEventNativeListener mAdMobNativeEventListener;
    private Context mContext;
    private AppierNativeAd mAppierNativeAd;

    @Override
    public void requestNativeAd(Context context,
                                CustomEventNativeListener customEventNativeListener,
                                String serverString,
                                NativeMediationAdRequest nativeMediationAdRequest,
                                Bundle localExtras) {
        Appier.log("[Appier AdMob Mediation]", "AppierNative.requestNativeAd()");

        mContext = context;
        mAdMobNativeEventListener = customEventNativeListener;
        JSONObject serverExtras = parseServerString(serverString);
        String adUnitId = getAdUnitId(localExtras, serverExtras);

        // Get predict result, and passback rapidly.
        String predictZoneId = AppierPredictHandler.getPredictZone(adUnitId);
        if (predictZoneId == null) {
            mAdMobNativeEventListener.onAdFailedToLoad(buildNoBidError());
            return;
        }

        String zoneId = getZoneId(localExtras, serverExtras, predictZoneId);

        mAppierNativeAd = new AppierNativeAd(context, new AppierAdUnitIdentifier(adUnitId), this);
        mAppierNativeAd.setZoneId(zoneId);
        mAppierNativeAd.loadAdWithExternalCache();
    }

    @Override
    public void onDestroy() {
        if (mAppierNativeAd != null) {
            mAppierNativeAd.destroy();
            mAppierNativeAd = null;
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onAdLoaded(AppierNativeAd appierNativeAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierNative.onAdLoaded() (Custom Callback)");
        mAppierNativeAd = appierNativeAd;
        ImageLoader imageLoader = new ImageLoader(mContext);
        imageLoader.batchLoadImages(appierNativeAd.getCacheableImageUrls(), new ImageLoader.OnBatchImageEventListener() {
            @Override
            public void onBatchImageLoadedAndCached() {
                try {
                    mAdMobNativeEventListener.onAdLoaded(new AppierNativeAdMapper(mContext, mAppierNativeAd, mAdMobNativeEventListener));
                } catch (JSONException e) {
                    mAdMobNativeEventListener.onAdFailedToLoad(buildAdError(AppierError.INVALID_JSON));
                }
            }

            @Override
            public void onBatchImageLoadFail() {
                mAdMobNativeEventListener.onAdFailedToLoad(buildAdError(AppierError.NETWORK_ERROR));
            }
        });
    }

    @Override
    public void onAdNoBid(AppierNativeAd appierNativeAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierNative.onAdNoBid() (Custom Callback)");
        mAdMobNativeEventListener.onAdFailedToLoad(buildNoBidError());
    }

    @Override
    public void onAdLoadFail(AppierError appierError, AppierNativeAd appierNativeAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierNative.onAdLoadFail() (Custom Callback)");
        mAdMobNativeEventListener.onAdFailedToLoad(buildAdError(appierError));
    }

    @Override
    public void onAdShown(AppierNativeAd appierNativeAd) {
        Appier.log("[Appier AdMob Mediation]", "AppierNative.onAdShown() (Custom Callback)");
        mAdMobNativeEventListener.onAdImpression();
    }

    @Override
    public void onImpressionRecorded(AppierNativeAd appierNativeAd) {
    }

    @Override
    public void onImpressionRecordFail(AppierError appierError, AppierNativeAd appierNativeAd) {
    }

    @Override
    public void onAdClick(AppierNativeAd appierNativeAd) {
        /*
         * Since AdMob click events are controlled by `UnifiedNativeAdMapper`,
         * therefore we handle click event at `AppierNativeAdMapper`.
         * The `onAdClick` method would not be used.
         */

    }

    @Override
    public void onAdClickFail(AppierError appierError, AppierNativeAd appierNativeAd) {
    }
}
