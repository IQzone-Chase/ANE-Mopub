/*
 * Copyright (c) 2011, MoPub Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'MoPub Inc.' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.mopub.mobileads;

import java.util.Map;

import android.content.Context;
import android.location.Location;

import com.millennialmedia.android.MMAd;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMException;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;
import com.millennialmedia.android.RequestListener;
import com.sticksports.nativeExtensions.mopub.MoPubExtension;

class MillennialBanner extends CustomEventBanner {
    private MMAdView mMillennialBanner;
    private CustomEventBannerListener mBannerListener;
    public static final String APID_KEY = "adUnitID";
    public static final String AD_WIDTH_KEY = "adWidth";
    public static final String AD_HEIGHT_KEY = "adHeight";

    @Override
    protected void loadBanner(Context context, CustomEventBannerListener customEventBannerListener,
                              Map<String, Object> localExtras, Map<String, String> serverExtras) {
        mBannerListener = customEventBannerListener;

        if (!extrasAreValid(serverExtras)) {
        	mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        	return;
        }
        String apid = serverExtras.get(APID_KEY);
        int width = Integer.parseInt(serverExtras.get(AD_WIDTH_KEY));
        int height = Integer.parseInt(serverExtras.get(AD_HEIGHT_KEY));

        MMSDK.initialize(context);

        mMillennialBanner = new MMAdView(context);
        mMillennialBanner.setApid(apid);
        mMillennialBanner.setWidth(width);
        mMillennialBanner.setHeight(height);

        Location location = (Location) localExtras.get("location");
        if (location != null) MMRequest.setUserLocation(location);

        mMillennialBanner.setMMRequest(new MMRequest());
        mMillennialBanner.setId(MMSDK.getDefaultAdId());
        AdViewController.setShouldHonorServerDimensions(mMillennialBanner);
        mMillennialBanner.setListener(new MillenialRequestListener());
        MoPubExtension.log("Fetching Millennial banner ad ...");
        mMillennialBanner.getAd();
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        try {
            Integer.parseInt(serverExtras.get(AD_WIDTH_KEY));
            Integer.parseInt(serverExtras.get(AD_HEIGHT_KEY));
        } catch (NumberFormatException e) {
            return false;
        }

        return serverExtras.containsKey(APID_KEY);
    }

    @Override
    protected void onInvalidate() {
    	if(mMillennialBanner != null) 
    		mMillennialBanner.setListener(null);
    	MoPubExtension.log("Millennial banner invalidated");
    }
    
    
    //////////////////////
    // REQUEST LISTENER //
    //////////////////////
    
	class MillenialRequestListener implements RequestListener {
       
		@Override
		public void MMAdOverlayClosed(MMAd ad) {
			MoPubExtension.log("Millennial banner overlay closed");
			mBannerListener.onBannerCollapsed();
		}

		@Override
		public void MMAdOverlayLaunched(MMAd ad) {
			MoPubExtension.log("Millennial banner overlay launched");
			mBannerListener.onBannerExpanded();
		}

		@Override
		public void MMAdRequestIsCaching(MMAd ad) {
			MoPubExtension.log("Millennial banner is caching");
		}

		@Override
		public void requestCompleted(MMAd ad) {
			MoPubExtension.log("Millennial banner request complete");
			mBannerListener.onBannerLoaded(mMillennialBanner);
		}

		@Override
		public void requestFailed(MMAd ad, MMException exception) {
			MoPubExtension.log("Millennial banner request failed : " + exception);
			exception.printStackTrace();
			mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
		}

		@Override
		public void onSingleTap(MMAd ad) {
			MoPubExtension.log("Millennial banner single tap");
			mBannerListener.onBannerClicked();
		}
    }
}
