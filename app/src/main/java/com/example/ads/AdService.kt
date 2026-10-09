package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle
import java.util.UUID
import java.util.concurrent.TimeUnit

class AdService(context: Context) {
    private val appContext = context.applicationContext
    private val analytics = FirebaseAnalytics.getInstance(appContext)
    
    // Ad instances
    private var interstitial: InterstitialAd? = null
    private var rewarded: RewardedAd? = null
    private var appOpenAd: AppOpenAd? = null
    
    // State tracking
    private var lastInterstitialAt = 0L
    private var completedLevels = 0
    private var isShowingFullScreen = false
    private var lastAppOpenAt = 0L
    private var isAppOpenLoading = false
    
    // Rewarded ad type tracking
    private var currentRewardedType: String? = null
    private var currentRewardedSessionId: String? = null
    
    // Callbacks for rewarded ads
    private var onRewardedEarned: ((RewardItem) -> Unit)? = null
    private var onRewardedUnavailable: (() -> Unit)? = null

    fun initialize() {
        MobileAds.initialize(appContext) {
            loadInterstitial()
            loadRewarded()
            loadAppOpen()
        }
    }

    // ========== INTERSTITIAL ADS ==========
    
    fun loadInterstitial() {
        if (interstitial != null) return
        logEvent("ad_requested", mapOf("type" to "interstitial"))
        InterstitialAd.load(appContext, AdConfig.androidInterstitialId, AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitial = ad
                logEvent("ad_loaded", mapOf("type" to "interstitial"))
                // ad.paidEventListener = OnPaidEventListener { adValue -> logPaidEvent("interstitial", adValue) }
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        isShowingFullScreen = false
                        interstitial = null
                        loadInterstitial()
                    }
                    override fun onAdFailedToShowFullScreenContent(error: AdError) {
                        isShowingFullScreen = false
                        interstitial = null
                        logEvent("ad_failed", mapOf("type" to "interstitial", "error" to error.message))
                        loadInterstitial()
                    }
                    override fun onAdShowedFullScreenContent() {
                        logEvent("ad_shown", mapOf("type" to "interstitial"))
                    }
                    override fun onAdClicked() {
                        logEvent("ad_clicked", mapOf("type" to "interstitial"))
                    }
                }
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitial = null
                logEvent("ad_failed", mapOf("type" to "interstitial", "error" to error.message))
            }
        })
    }

    fun showInterstitialIfEligible(activity: Activity, onContinue: () -> Unit) {
        completedLevels++
        val now = System.currentTimeMillis()
        val eligible = completedLevels % AdConfig.interstitialEveryNLevels == 0 && 
                       now - lastInterstitialAt >= AdConfig.minimumInterstitialCooldownMs
        val ad = interstitial
        
        if (!eligible || ad == null || isShowingFullScreen) { 
            onContinue() 
            if (ad == null) loadInterstitial() 
            return 
        }
        
        isShowingFullScreen = true
        interstitial = null
        lastInterstitialAt = now
        ad.show(activity)
        onContinue()
    }

    fun isInterstitialAvailable(): Boolean = interstitial != null && !isShowingFullScreen
    
    fun canShowInterstitial(): Boolean {
        val now = System.currentTimeMillis()
        return completedLevels % AdConfig.interstitialEveryNLevels == 0 && 
               now - lastInterstitialAt >= AdConfig.minimumInterstitialCooldownMs &&
               interstitial != null && !isShowingFullScreen
    }

    // ========== REWARDED ADS ==========
    
    fun loadRewarded() {
        if (rewarded != null) return
        logEvent("ad_requested", mapOf("type" to "rewarded"))
        RewardedAd.load(appContext, AdConfig.androidRewardedId, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewarded = ad
                logEvent("ad_loaded", mapOf("type" to "rewarded"))
                // ad.paidEventListener = OnPaidEventListener { adValue -> logPaidEvent("rewarded", adValue) }
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        isShowingFullScreen = false
                        rewarded = null
                        loadRewarded()
                    }
                    override fun onAdFailedToShowFullScreenContent(error: AdError) {
                        isShowingFullScreen = false
                        rewarded = null
                        logEvent("ad_failed", mapOf("type" to "rewarded", "error" to error.message))
                        loadRewarded()
                        onRewardedUnavailable?.invoke()
                    }
                    override fun onAdShowedFullScreenContent() {
                        logEvent("ad_shown", mapOf("type" to "rewarded"))
                    }
                    override fun onAdClicked() {
                        logEvent("ad_clicked", mapOf("type" to "rewarded"))
                    }
                }
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                rewarded = null
                logEvent("ad_failed", mapOf("type" to "rewarded", "error" to error.message))
            }
        })
    }

    fun isRewardedAvailable(): Boolean = rewarded != null && !isShowingFullScreen

    fun showRewarded(
        activity: Activity,
        rewardType: String,
        onEarnedReward: (RewardItem) -> Unit,
        onUnavailable: () -> Unit
    ) {
        val ad = rewarded ?: run { 
            logEvent("ad_unavailable", mapOf("type" to "rewarded", "rewardType" to rewardType))
            onUnavailable() 
            loadRewarded() 
            return 
        }
        if (isShowingFullScreen) return
        
        isShowingFullScreen = true
        currentRewardedType = rewardType
        currentRewardedSessionId = UUID.randomUUID().toString()
        rewarded = null
        onRewardedEarned = onEarnedReward
        onRewardedUnavailable = onUnavailable
        
        ad.show(activity, OnUserEarnedRewardListener { reward ->
            logEvent("reward_earned", mapOf("rewardType" to rewardType, "amount" to reward.amount.toInt()))
            onRewardedEarned?.invoke(reward)
        })
    }

    // Convenience methods for specific reward types
    fun showRewardedForCoins(activity: Activity, onEarnedReward: () -> Unit, onUnavailable: () -> Unit) {
        showRewarded(activity, "coins", { _ -> onEarnedReward() }, onUnavailable)
    }

    fun showRewardedForLife(activity: Activity, onEarnedReward: () -> Unit, onUnavailable: () -> Unit) {
        showRewarded(activity, "life", { _ -> onEarnedReward() }, onUnavailable)
    }

    fun showRewardedForHint(activity: Activity, onEarnedReward: () -> Unit, onUnavailable: () -> Unit) {
        showRewarded(activity, "hint", { _ -> onEarnedReward() }, onUnavailable)
    }

    fun showRewardedForUndo(activity: Activity, onEarnedReward: () -> Unit, onUnavailable: () -> Unit) {
        showRewarded(activity, "undo", { _ -> onEarnedReward() }, onUnavailable)
    }

    fun showRewardedForContinue(activity: Activity, onEarnedReward: () -> Unit, onUnavailable: () -> Unit) {
        showRewarded(activity, "continue", { _ -> onEarnedReward() }, onUnavailable)
    }

    fun showRewardedForBonusReward(activity: Activity, onEarnedReward: () -> Unit, onUnavailable: () -> Unit) {
        showRewarded(activity, "bonus_reward", { _ -> onEarnedReward() }, onUnavailable)
    }

    fun showRewardedForDailyBonus(activity: Activity, onEarnedReward: () -> Unit, onUnavailable: () -> Unit) {
        showRewarded(activity, "daily_bonus", { _ -> onEarnedReward() }, onUnavailable)
    }

    // ========== APP OPEN ADS ==========
    
    fun loadAppOpen() {
        if (appOpenAd != null || isAppOpenLoading) return
        isAppOpenLoading = true
        logEvent("ad_requested", mapOf("type" to "app_open"))
        AppOpenAd.load(appContext, AdConfig.androidAppOpenId, AdRequest.Builder().build(), object : AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                isAppOpenLoading = false
                logEvent("ad_loaded", mapOf("type" to "app_open"))
                // ad.paidEventListener = OnPaidEventListener { adValue -> logPaidEvent("app_open", adValue) }
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        appOpenAd = null
                        loadAppOpen()
                    }
                    override fun onAdFailedToShowFullScreenContent(error: AdError) {
                        appOpenAd = null
                        logEvent("ad_failed", mapOf("type" to "app_open", "error" to error.message))
                        loadAppOpen()
                    }
                    override fun onAdShowedFullScreenContent() {
                        logEvent("ad_shown", mapOf("type" to "app_open"))
                    }
                    override fun onAdClicked() {
                        logEvent("ad_clicked", mapOf("type" to "app_open"))
                    }
                }
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                isAppOpenLoading = false
                appOpenAd = null
                logEvent("ad_failed", mapOf("type" to "app_open", "error" to error.message))
            }
        })
    }

    fun showAppOpenIfEligible(activity: Activity, onContinue: () -> Unit) {
        val now = System.currentTimeMillis()
        val eligible = now - lastAppOpenAt >= AdConfig.appOpenCooldownMs
        
        if (!eligible || appOpenAd == null || isShowingFullScreen) { 
            onContinue() 
            return 
        }
        
        isShowingFullScreen = true
        lastAppOpenAt = now
        appOpenAd?.show(activity)
        appOpenAd = null
        loadAppOpen()
        onContinue()
    }

    fun isAppOpenAvailable(): Boolean = appOpenAd != null && !isShowingFullScreen

    // ========== ANALYTICS ==========
    
    private fun logEvent(name: String, params: Map<String, Any>) {
        try {
            val bundle = Bundle().apply {
                params.forEach { (key, value) ->
                    when (value) {
                        is String -> putString(key, value)
                        is Int -> putInt(key, value)
                        is Long -> putLong(key, value)
                        is Double -> putDouble(key, value)
                        is Float -> putFloat(key, value)
                        else -> putString(key, value.toString())
                    }
                }
            }
            analytics.logEvent(name, bundle)
        } catch (e: Exception) {
            Log.w("AdService", "Analytics logging failed", e)
        }
    }
    
    private fun logPaidEvent(adType: String, adValue: AdValue) {
        logEvent("ad_paid", mapOf(
            "type" to adType,
            "valueMicros" to adValue.valueMicros,
            "currencyCode" to adValue.currencyCode
        ))
    }

    fun dispose() {
        interstitial = null
        rewarded = null
        appOpenAd = null
    }

    // Expose session ID for server validation
    fun getCurrentRewardedSessionId(): String? = currentRewardedSessionId
}