# Appier Mediation for AdMob Android SDK

This is Appier's official Android mediation repository for AdMob SDK. The latest updated documentation can be found [here](https://appier-publisher-services.readme.io/docs/admob-mediation-sdk).

Refer to [pmp-android-example](https://github.com/appier/pmp-android-sample) for sample integrations.

## Prerequisites

- Make sure you are using AdMob Android SDK version >= `17.0.0`
- Make sure your app's `API level >= 18`
- Make sure you have already configured line items on AdMob Web UI
	- `Class Name` field should be one of Appier's predefined class names
		- `com.appier.mediation.admob.ads.AppierNative` for native ads
		- `com.appier.mediation.admob.ads.AppierBanner` for banner ads
		- `com.appier.mediation.admob.ads.AppierInterstitial` for interstitial ads
	- `Parameter` field should follow the format `{ "zoneId": "<your_zone_id_from_appier>" }`

## Gradle Configuration

*AdMob Dependency*
Please add google to your repositories, and specify AdMob’s dependency
``` diff

  repositories {
      // ...
+     google()
  }

  dependencies {
      // AdMob SDK
+     implementation 'com.google.android.gms:play-services-ads:19.6.0'
  }
```

*Appier Dependencies*
Please add jcenter to your repositories, and specify Appier's dependencies
``` diff
  repositories {
      // ...
+     jcenter()
  }

  dependencies {
      // ...
+     implementation 'com.appier.android:ads-sdk:1.1.2'
+     implementation 'com.appier.android:admob-mediation:1.0.0'
  }
```

## Manifest Configuration

To prevent your app from crashing, following are the recommended manifest configurations.

``` xml
<manifest ...>
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

  <!--  Required for displaying floating window  -->
  <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

  <application
    ...
    android:networkSecurityConfig="@xml/network_security_config">

    <uses-library android:name="org.apache.http.legacy" android:required="false" />

    <!-- AdMob app ID -->
    <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
  </application>
</manifest>
```

## GDPR Consent (Recommended)

In consent to GDPR, we strongly suggest sending the consent status to our SDK via `Appier.setGDPRApplies()` so that we will not track users personal information. Without this configuration, Appier will not apply GDPR by default. Note that this will impact advertising performance thus impacting Revenue.

``` java
import com.appier.ads.Appier;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // ...
    Appier.setGDPRApplies(true);
  }
}
```

## Native Ads Integration

To render Appier's native ads via AdMob mediation, you need to provide `<your_ad_unit_id_from_admob>` and `<your_zone_id_from_appier>`. You can either pass through `localExtras` or `serverExtras`.

``` java
import com.appier.ads.common.AppierDataKeys;
import com.appier.mediation.admob.ads.AppierNative;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;


Bundle localExtras = new Bundle();
localExtras.putString(AppierDataKeys.AD_UNIT_ID_LOCAL, "<your_ad_unit_id_from_admob>");

// Inflate the layout
final UnifiedNativeAdView nativeAdView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.template_admob_native_ad, null);

AdLoader adLoader = new AdLoader.Builder(mContext, "<your_ad_unit_id_from_admob>")
        .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                populateUnifiedNativeAdView(unifiedNativeAd, nativeAdView);
                mAdContainer.addView(nativeAdView);
            }
        })
        .build();

// Load Ad
adLoader.loadAd(new AdRequest.Builder()
        .addCustomEventExtrasBundle(AppierNative.class, localExtras)
        .build());
```

The `template_admob_native_ad` is a xml template of `UnifiedNativeAdView`.
You can get more details on the [Native Ads Advanced](https://developers.google.com/admob/android/native/advanced).

```xml
<com.google.android.gms.ads.formats.UnifiedNativeAdView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    <LinearLayout
    android:orientation="vertical"
    ... >
        <LinearLayout
        android:orientation="horizontal"
        ... >
          <ImageView
           android:id="@+id/ad_app_icon"
           ... />
          <TextView
            android:id="@+id/ad_headline"
            ... />
         </LinearLayout>


         // Other assets such as image or media view, call to action, etc follow.
         ...
    </LinearLayout>
</com.google.android.gms.ads.formats.UnifiedNativeAdView>
```

The `populateUnifiedNativeAdView` sets the text, images and the native ad, etc into the ad view. You can specify Appier native view through the method `getAdvertiser()` of AdMob native ad.
```java
import com.appier.mediation.admob.AppierAdapterConfiguration;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        adView.setVisibility(View.VISIBLE);

        // when the advertiser name is `Appier`, the ad is provided by Appier.
        if (nativeAd.getAdvertiser().equals(AppierAdapterConfiguration.getAdvertiserName())) {
            // ...
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.setNativeAd(nativeAd);
        }
    }
```

## Banner Ads Integration

To render Appier's banner ads via AdMob mediation, you need to specify the width and height of ad unit to load ads with suitable sizes. You can either pass through `localExtras` or `serverExtras`.

``` java
import com.appier.ads.common.AppierDataKeys;
import com.appier.mediation.admob.ads.AppierBanner;
import com.google.android.gms.ads.AdView;

// ...
Bundle localExtras = new Bundle();
localExtras.putInt(AppierDataKeys.AD_WIDTH_LOCAL, 300);
localExtras.putInt(AppierDataKeys.AD_HEIGHT_LOCAL, 250);
localExtras.putString(AppierDataKeys.AD_UNIT_ID_LOCAL, "<your_ad_unit_id_from_admob>");

AdView adView = getView().findViewById(R.id.admob_banner_ad);

// Load Ad
adView.loadAd(new AdRequest.Builder()
        .addCustomEventExtrasBundle(AppierBanner.class, localExtras)
        .build());
```

You also need to define the `adSize` and `adUnitId` otherwise the ad would not display correctly.

``` xml
<com.google.android.gms.ads.AdView
    android:id="@+id/admob_banner_ad"
    android:layout_width="300dp"
    android:layout_height="250dp"
    ads:adSize="MEDIUM_RECTANGLE"
    ads:adUnitId="<your_ad_unit_id_from_admob>">
</com.google.android.gms.ads.AdView>
```

## Interstitial Ads Integration

To render Appier's interstitial ads via AdMob mediation, you need to specify the width and height of ad unit to load ads with suitable sizes. You can either pass through `localExtras` or `serverExtras`.

``` java
import com.appier.ads.common.AppierDataKeys;
import com.google.android.gms.ads.InterstitialAd;

// ...
Bundle localExtras = new Bundle();
localExtras.put(AppierDataKeys.AD_WIDTH_LOCAL, 320);
localExtras.put(AppierDataKeys.AD_HEIGHT_LOCAL, 480);
localExtras.putString(AppierDataKeys.AD_UNIT_ID_LOCAL, "<your_ad_unit_id_from_admob>");
InterstitialAd interstitialAd = new InterstitialAd(context);

interstitialAd.setAdUnitId("<your_ad_unit_id_from_admob>");

// Load Ad
mInterstitialAd.loadAd(new AdRequest.Builder()
        .addCustomEventExtrasBundle(AppierInterstitial.class, mLocalExtras)
        .build());
```

## Predict Ads
Predict mode provides a function to do the Ad response prediction before real AdMob line items are triggered. It is recommended to do the prediction at the previous activity/user view before rendering ads. For details, you could contact our support.

Refer to [pmp-android-example](https://github.com/appier/pmp-android-sample) for sample integrations.

### How to predict Ads
We recommend to do the prediction at the previous activity/user view before rendering ads.
``` java
import com.appier.ads.AppierPredictor;
import com.appier.mediation.admob.AppierAdUnitIdentifier;
import com.appier.mediation.admob.AppierPredictHandler;

public class MainActivity extends AppCompatActivity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       // ...
       AppierPredictor predictor = new AppierPredictor(
           getContext(),
           new AppierPredictHandler(getContext())
       );
        // Predict by the ad unit id. It is recommended to do the prediction
        // at the previous activity/user view before rendering ads.
       predictor.predictAd(new AppierAdUnitIdentifier("<your_ad_unit_id_from_admob>"));
}
```
