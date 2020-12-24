# Appier Mediation for AdMob Android SDK

This is Appier's official Android mediation repository for AdMob SDK. The latest updated documentation can be found [here](https://appier-publisher-services.readme.io/docs/admob).

Refer to [pmp-android-example](https://github.com/appier/pmp-android-sample) for sample integrations.

## Prerequisites

- Make sure you are using AdMob Android SDK version `19.6.0`
- Make sure your app's `API level >= 18`
- Make sure you have already configured line items on AdMob Web UI
	- `Custom event class` field should be one of Appier's predefined class names
		- `com.appier.mediation.admob.ads.AppierNative` for native ads
		- `com.appier.mediation.admob.ads.AppierBanner` for banner ads
		- `com.appier.mediation.admob.ads.AppierInterstitial` for interstitial ads
	- `Custom event parameter` field should follow the format `{ "adUnitId": "<your_ad_unit_id_from_admob>", "zoneId": "<your_zone_id_from_appier>" }`

## Gradle Configuration

Please add jcenter to your repositories, and specify both AdMob’s dependencies and Appier's dependencies.

*AdMob Dependencies:*
``` diff

  repositories {
      // ...
+     jcenter()
  }

  dependencies {
      // AdMob SDK
+     implementation 'com.google.android.gms:play-services-ads:19.6.0'
  }
```

*Appier Dependencies:*
``` diff
  repositories {
      // ...
+     jcenter()
  }

  dependencies {
      // ...
+     implementation 'com.appier.android:ads-sdk:1.1.1'
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

In consent to GDPR, we strongly suggest sending the consent status to our SDK via `Appier.setGDPRApplies()` so that we will not track users personal information. Without this configuration, Appier will not apply GDPR by default. Note that this will impact Advertising performance thus impacting Revenue.

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
    android:background="@color/colorAdPlaceholder"
    ads:adSize="MEDIUM_RECTANGLE"
    ads:adUnitId="<your_ad_unit_id_from_admob>">
</com.google.android.gms.ads.AdView>
```

## Predict Ads
Predict mode provides a function to do the Ad response prediction before real AdMob line items are triggered. It is recommended to do the prediction at the previous activity/user view before rendering ads. For details, you could contact our support.

Refer to [pmp-android-example](https://github.com/appier/pmp-android-sample) for sample integrations.

### How to predict Ads
we recommend to do the prediction at the previous activity/user view before rendering ads.
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
