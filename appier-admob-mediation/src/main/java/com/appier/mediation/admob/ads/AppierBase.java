package com.appier.mediation.admob.ads;

import android.os.Bundle;

import com.appier.ads.Appier;
import com.appier.ads.AppierError;
import com.appier.ads.common.AppierDataKeys;
import com.google.android.gms.ads.AdRequest;

import org.json.JSONException;
import org.json.JSONObject;


public abstract class AppierBase {

    protected AppierBase() {}

    protected JSONObject parseServerString(String serverString) {
        try {
            return new JSONObject(serverString);
        } catch (Exception e) {
            Appier.log("[Appier AdMob Mediation]", "Parse server string error:", e.toString());
            return null;
        }
    }

    protected String getAdUnitId(Bundle localExtras, JSONObject serverExtras) {
        try {
            if (serverExtras != null && serverExtras.has(AppierDataKeys.AD_UNIT_ID_SERVER))
                return serverExtras.getString(AppierDataKeys.AD_UNIT_ID_SERVER);
        } catch (JSONException e) {
            Appier.log("[Appier AdMob Mediation]", "Server extras error:", e.toString());
        }

        if (localExtras != null && localExtras.containsKey(AppierDataKeys.AD_UNIT_ID_LOCAL)) {
            return localExtras.getString(AppierDataKeys.AD_UNIT_ID_LOCAL);
        }

        Appier.log("[Appier AdMob Mediation]", "Please add ad unit id in custom event parameters or bundle");
        return null;
    }

    protected String getZoneId(Bundle localExtras, JSONObject serverExtras, String zoneId) {
        if (zoneId != null && !zoneId.equals("")) {
            return zoneId;
        }

        try {
            if (serverExtras != null && serverExtras.has(AppierDataKeys.ZONE_ID_SERVER))
                return serverExtras.getString(AppierDataKeys.ZONE_ID_SERVER);
        } catch (JSONException e) {
            Appier.log("[Appier AdMob Mediation]", "Server extras error:", e.toString());
        }

        if (localExtras != null && localExtras.containsKey(AppierDataKeys.ZONE_ID_LOCAL)) {
            return localExtras.getString(AppierDataKeys.ZONE_ID_LOCAL);
        }

        Appier.log("[Appier AdMob Mediation]", "Please add zone id in custom event parameters or bundle");
        return null;
    }

    protected int getAdWidth(Bundle localExtras, JSONObject serverExtras) {
        return getAdWidth(localExtras, serverExtras, -1);
    }

    protected int getAdWidth(Bundle localExtras, JSONObject serverExtras, int adWidth) {
        try {
            if (serverExtras != null && serverExtras.has(AppierDataKeys.AD_WIDTH_SERVER))
                return serverExtras.getInt(AppierDataKeys.AD_WIDTH_SERVER);
        } catch (JSONException e) {
            Appier.log("[Appier AdMob Mediation]", "Server extras error:", e.toString());
        }

        if (localExtras != null && localExtras.containsKey(AppierDataKeys.AD_WIDTH_LOCAL)) {
            return localExtras.getInt(AppierDataKeys.AD_WIDTH_LOCAL);
        }

        if (adWidth == -1) {
            Appier.log("[Appier AdMob Mediation]", "Please add ad width in custom event parameters or bundle");
        }
        return adWidth;
    }

    protected int getAdHeight(Bundle localExtras, JSONObject serverExtras) {
        return getAdHeight(localExtras, serverExtras, -1);
    }

    protected int getAdHeight(Bundle localExtras, JSONObject serverExtras, int adHeight) {
        try {
            if (serverExtras != null && serverExtras.has(AppierDataKeys.AD_HEIGHT_SERVER))
                return serverExtras.getInt(AppierDataKeys.AD_HEIGHT_SERVER);
        } catch (JSONException e) {
            Appier.log("[Appier AdMob Mediation]", "Server extras error:", e.toString());
        }

        if (localExtras != null && localExtras.containsKey(AppierDataKeys.AD_WIDTH_LOCAL)) {
            return localExtras.getInt(AppierDataKeys.AD_HEIGHT_LOCAL);
        }

        if (adHeight == -1) {
            Appier.log("[Appier AdMob Mediation]", "Please add ad height in custom event parameters or bundle");
        }
        return adHeight;
    }

    protected int buildNoBidError() {
        return AdRequest.ERROR_CODE_NO_FILL;
    }

    protected int buildAdError(AppierError appierError) {
        if (appierError == AppierError.NETWORK_ERROR || appierError == AppierError.WEBVIEW_ERROR) {
            return AdRequest.ERROR_CODE_NETWORK_ERROR;
        } else if (appierError == AppierError.BAD_REQUEST) {
            return AdRequest.ERROR_CODE_INVALID_REQUEST;
        } else if (appierError == AppierError.INTERNAL_SERVER_ERROR) {
            return AdRequest.ERROR_CODE_INTERNAL_ERROR;
        } else {
            return AdRequest.ERROR_CODE_INTERNAL_ERROR;
        }
    }
}
