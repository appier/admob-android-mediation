package com.appier.mediation.admob;

import com.appier.ads.common.AdUnitIdentifier;


public class AppierAdUnitIdentifier extends AdUnitIdentifier {
    public AppierAdUnitIdentifier(String adUnitId) { super(adUnitId); }

    @Override
    public String build() { return "admob_" + getAdUnitId(); }
}
