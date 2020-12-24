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
