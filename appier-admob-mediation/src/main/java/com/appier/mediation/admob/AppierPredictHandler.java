package com.appier.mediation.admob;

import android.content.Context;

import com.appier.ads.Appier;
import com.appier.ads.AppierError;
import com.appier.ads.AppierPredictCache;
import com.appier.ads.AppierPredictor;
import com.appier.ads.common.ImageLoader;

import java.util.List;


public class AppierPredictHandler implements AppierPredictor.EventListener {
    private Context mContext;
    private ImageLoader mImageLoader;

    public AppierPredictHandler(Context context) {
        mContext = context;
        mImageLoader = new ImageLoader(mContext);
    }

    /*
     * If predict result is null, it means the predict feature is not activated.
     * For details, please contact our support team.
     * If the zone id list is empty, it means Appier doesn't bid the user,
     * so the bid request would be passback.
     * If the zone id list has values. we would take the first zone and prefetch
     * the media resources, the bid result would be load rapidly when the real bid
     * request is triggered.
     */
    public static String getPredictZone(String adUnitId) {
        List<String> zoneIds = AppierPredictCache.getInstance().getPredictResult(new AppierAdUnitIdentifier(adUnitId));
        if (zoneIds == null) {
            return "";                      // feature is not activated
        } else if (zoneIds.size() == 0) {
            return null;                    // Appier no bid
        }
        return zoneIds.get(0);              // Appier bid
    }

    @Override
    public void onPredictSuccess(final String adUnitId, List<String> prefetchList) {
        Appier.log("[Appier AdMob Mediation]", "[Predict Mode]", "successfully predict ad:", adUnitId);
        mImageLoader.batchLoadImages(prefetchList, new ImageLoader.OnBatchImageEventListener() {
            @Override
            public void onBatchImageLoadedAndCached() {
                Appier.log("[Appier AdMob Mediation]", "[Predict Mode]", "successfully cache images for ad:", adUnitId);
            }

            @Override
            public void onBatchImageLoadFail() {
                Appier.log("[Appier AdMob Mediation]", "[Predict Mode]", "failed to cache images for ad:", adUnitId);
            }
        });
    }

    @Override
    public void onPredictFailed(String adUnitId, AppierError error) {
        Appier.log("[Appier AdMob Mediation]", "[Predict Mode]", "predict ad", adUnitId, " failed:", error);
    }
}
