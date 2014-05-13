package com.djages.headline;

import android.app.Activity;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.customevent.CustomEventBanner;
import com.google.ads.mediation.customevent.CustomEventBannerListener;
import com.google.ads.mediation.customevent.CustomEventInterstitial;
import com.google.ads.mediation.customevent.CustomEventInterstitialListener;
import com.vpadn.ads.VpadnAd;
import com.vpadn.ads.VpadnAdListener;
import com.vpadn.ads.VpadnAdRequest;
import com.vpadn.ads.VpadnAdSize;
import com.vpadn.ads.VpadnBanner;
import com.vpadn.ads.VpadnInterstitialAd;


//TODO: 這class完整路徑需要註冊在admob mediation web上
public class VpadnCustomAd implements CustomEventBanner, CustomEventInterstitial {

	private VpadnBanner vpadnBanner = null;
	private VpadnInterstitialAd interstitialAd = null;

	@Override
	public void destroy() {
		if (vpadnBanner != null) {
			vpadnBanner.destroy();
			vpadnBanner = null;
		}
		if (interstitialAd != null) {
			interstitialAd.destroy();
			interstitialAd = null;
		}
	}

	/*
	 * 將admob AdSize轉換成 VpadnAdSize
	 */
	private VpadnAdSize getVpadnAdSizeByAdSize(AdSize adSize) {

		if (adSize.equals(AdSize.BANNER)) {
			return VpadnAdSize.BANNER;
		} else if (adSize.equals(AdSize.IAB_BANNER)) {
			return VpadnAdSize.IAB_BANNER;
		} else if (adSize.equals(AdSize.IAB_LEADERBOARD)) {
			return VpadnAdSize.IAB_LEADERBOARD;
		} else if (adSize.equals(AdSize.IAB_MRECT)) {
			return VpadnAdSize.IAB_MRECT;
		} else if (adSize.equals(AdSize.IAB_WIDE_SKYSCRAPER)) {
			return VpadnAdSize.IAB_WIDE_SKYSCRAPER;
		} else if (adSize.equals(AdSize.SMART_BANNER)) {
			return VpadnAdSize.SMART_BANNER;
		}

		boolean isAutoHeight = false;
		boolean isFullWidth = false;
		if (adSize.isAutoHeight()) {
			isAutoHeight = true;
		}
		if (adSize.isFullWidth()) {
			isFullWidth = true;
		}

		if (isAutoHeight && isFullWidth) {
			return VpadnAdSize.SMART_BANNER;
		}

		if (isAutoHeight && !isFullWidth) {
			return new VpadnAdSize(adSize.getWidth(), VpadnAdSize.AUTO_HEIGHT);
		}
		if (!isAutoHeight && isFullWidth) {
			return new VpadnAdSize(VpadnAdSize.FULL_WIDTH, adSize.getHeight());
		}

		if (adSize.isCustomAdSize()) {
			return new VpadnAdSize(adSize.getWidth(), adSize.getHeight());
		}

		return VpadnAdSize.SMART_BANNER;
	}

	/*
	 * 將admob的 MediationAdRequest轉換成 VpadnAdRequest
	 */
	private VpadnAdRequest getVpadnAdRequestByMediationAdRequest(MediationAdRequest request) {

		VpadnAdRequest adRequest = new VpadnAdRequest();
		if (request.getBirthday() != null) {
			adRequest.setBirthday(request.getBirthday());
		}
		if (request.getAgeInYears() != null) {
			adRequest.setAge(request.getAgeInYears());
		}

		if (request.getKeywords() != null) {
			adRequest.setKeywords(request.getKeywords());
		}

		if (request.getGender() != null) {
			if (request.getGender().equals(AdRequest.Gender.FEMALE)) {
				adRequest.setGender(VpadnAdRequest.Gender.FEMALE);
			} else if (request.getGender().equals(AdRequest.Gender.MALE)) {
				adRequest.setGender(VpadnAdRequest.Gender.MALE);
			} else {
				adRequest.setGender(VpadnAdRequest.Gender.UNKNOWN);
			}
		}

		return adRequest;
	}

	@Override
	public void requestBannerAd(final CustomEventBannerListener listener, final Activity activity, String label, String serverParameter, AdSize adSize,
			MediationAdRequest request, Object customEventExtra) {

		if (vpadnBanner != null) {
			vpadnBanner.destroy();
			vpadnBanner = null;
		}

		VpadnAdRequest adRequest = getVpadnAdRequestByMediationAdRequest(request);

		// TODO:請將Vpadn的 bannerID 設定在admob的mediation web上 由serverParameeter帶進來
		vpadnBanner = new VpadnBanner(activity, serverParameter, getVpadnAdSizeByAdSize(adSize), "TW");
		vpadnBanner.setAdListener(new VpadnAdListener() {

			@Override
			public void onVpadnDismissScreen(VpadnAd arg0) {
				listener.onDismissScreen();
			}

			@Override
			public void onVpadnFailedToReceiveAd(VpadnAd arg0, VpadnAdRequest.VpadnErrorCode arg1) {
				listener.onFailedToReceiveAd();
			}

			@Override
			public void onVpadnLeaveApplication(VpadnAd arg0) {
				listener.onLeaveApplication();
			}

			@Override
			public void onVpadnPresentScreen(VpadnAd arg0) {
				listener.onPresentScreen();
			}

			@Override
			public void onVpadnReceiveAd(VpadnAd arg0) {
				listener.onReceivedAd(vpadnBanner);
			}

		});

		vpadnBanner.loadAd(adRequest);
	}

	@Override
	public void requestInterstitialAd(final CustomEventInterstitialListener listener, Activity activity, String label, String serverParameter,
			MediationAdRequest request, Object customEventExtra) {

		// TODO:請將Vpadn的 interstitialBannerID 設定在admob的mediation web上 由serverParameeter帶進來
		interstitialAd = new VpadnInterstitialAd(activity, serverParameter, "TW");
		interstitialAd.setAdListener(new VpadnAdListener() {

			@Override
			public void onVpadnDismissScreen(VpadnAd arg0) {
				if (interstitialAd != null) {
					interstitialAd.destroy();
					interstitialAd = null;
				}
				listener.onDismissScreen();
			}

			@Override
			public void onVpadnFailedToReceiveAd(VpadnAd arg0, VpadnAdRequest.VpadnErrorCode arg1) {
				if (interstitialAd != null) {
					interstitialAd.destroy();
					interstitialAd = null;
				}
				listener.onFailedToReceiveAd();
			}

			@Override
			public void onVpadnLeaveApplication(VpadnAd arg0) {
				listener.onLeaveApplication();
			}

			@Override
			public void onVpadnPresentScreen(VpadnAd arg0) {
				listener.onPresentScreen();
			}

			@Override
			public void onVpadnReceiveAd(VpadnAd arg0) {
				listener.onReceivedAd();
			}

		});
		interstitialAd.loadAd(new VpadnAdRequest());

	}

	@Override
	public void showInterstitial() {
		if (interstitialAd != null && interstitialAd.isReady()) {
			interstitialAd.show();
		}

	}
}
