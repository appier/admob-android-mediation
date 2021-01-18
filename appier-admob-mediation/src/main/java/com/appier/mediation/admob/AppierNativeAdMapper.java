package com.appier.mediation.admob;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;

import com.appier.ads.Appier;
import com.appier.ads.AppierNativeAd;
import com.appier.ads.common.BrowserUtil;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdAssetNames;
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper;
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AppierNativeAdMapper extends UnifiedNativeAdMapper {

    private final Context mContext;
    private final AppierNativeAd mNativeAd;
    private final CustomEventNativeListener mEventNativeListener;
    private final BrowserUtil mBrowserUtil;
    private final View.OnClickListener mAdClickListener;
    private final View.OnClickListener mPrivacyInfoClickListener;
    private final View.OnAttachStateChangeListener mAttachStateChangeListener;

    public AppierNativeAdMapper(Context context,
                                AppierNativeAd nativeAd,
                                CustomEventNativeListener eventNativeListener) throws JSONException {
        mContext = context;
        mNativeAd = nativeAd;
        mEventNativeListener = eventNativeListener;
        mBrowserUtil = new BrowserUtil(mContext);

        setAdvertiser(AppierAdapterConfiguration.getAdvertiserName());
        setHasVideoContent(false);
        setHeadline(nativeAd.getTitle());
        setBody(nativeAd.getText());
        setCallToAction(nativeAd.getCallToActionText());
        setIcon(new NativeImage(nativeAd.getIconImageUrl()));

        final List<NativeAd.Image> images = new ArrayList<>(2);
        images.add(new NativeImage(nativeAd.getPrivacyInformationIconImageUrl()));
        images.add(new NativeImage(nativeAd.getMainImageUrl()));

        setImages(images);

        if (Appier.getBrowserAgent() == Appier.BrowserAgent.NATIVE) {
            mBrowserUtil.disableInternalBrowser();
        } else {
            mBrowserUtil.enableInternalBrowser();
        }

        mAdClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean isOpened = mBrowserUtil.tryToOpenUrl(mNativeAd.getClickDestinationUrl());
                    if (isOpened) {
                        Appier.log("[Appier AdMob Mediation]", "AppierNative.onAdClick() (Custom Callback)");
                        mEventNativeListener.onAdClicked();
                    }
                } catch (JSONException ignored) {
                    Appier.log("[Appier AdMob Mediation]", "AppierNative.onAdClick() failed");
                }
            }
        };

        mPrivacyInfoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mBrowserUtil.tryToOpenUrl(mNativeAd.getPrivacyInformationIconClickThroughUrl());
                } catch (JSONException ignored) {

                }
            }
        };

        mAttachStateChangeListener = new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                if (!mNativeAd.impressionIsRecordingOrRecorded()) {
                    mNativeAd.makeImpressionTrackingRequest();
                }
                mEventNativeListener.onAdImpression();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {}
        };

        setOverrideClickHandling(true);
        setOverrideImpressionRecording(true);
    }

    @Override
    public void trackViews(View view, Map<String, View> map, Map<String, View> map1) {
        view.addOnAttachStateChangeListener(mAttachStateChangeListener);
        for (Map.Entry<String, View> entry: map.entrySet()) {
            if (entry.getKey().equals(UnifiedNativeAdAssetNames.ASSET_ADVERTISER)) {
                entry.getValue().setOnClickListener(mPrivacyInfoClickListener);
            } else {
                entry.getValue().setOnClickListener(mAdClickListener);
            }
        }
    }

    private class NativeImage extends NativeAd.Image {
        private final String mUri;

        NativeImage(String uri) {
            mUri = uri;
        }

        @Override
        public Drawable getDrawable() {
            Bitmap bitmap = Appier.getBitmapCache().getBitmap(mUri);
            if (bitmap != null) {
                return new BitmapDrawable(mContext.getResources(), bitmap);
            }
            return null;
        }

        @Override
        public Uri getUri() {
            return Uri.parse(mUri);
        }

        @Override
        public double getScale() {
            return 1.0;
        }
    }
}
