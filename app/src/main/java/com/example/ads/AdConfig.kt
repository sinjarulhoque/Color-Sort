package com.example.ads

import android.content.Context
import com.example.BuildConfig
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

enum class AdEnvironment { TEST, PRODUCTION }

object AdConfig {
    // Debug builds use Google test ads; release (Play) builds serve real ads.
    var environment: AdEnvironment = if (BuildConfig.DEBUG) AdEnvironment.TEST else AdEnvironment.PRODUCTION
        private set

    // Android Test IDs (Google provided)
    private const val testBanner = "ca-app-pub-3940256099942544/9214589741"
    private const val testInterstitial = "ca-app-pub-3940256099942544/1033173712"
    private const val testRewarded = "ca-app-pub-3940256099942544/5224354917"
    private const val testAppOpen = "ca-app-pub-3940256099942544/9257395921"
    private const val testNative = "ca-app-pub-3940256099942544/2247696110"

    // iOS Test IDs (Google provided)
    private const val iosTestBanner = "ca-app-pub-3940256099942544/2435281174"
    private const val iosTestInterstitial = "ca-app-pub-3940256099942544/4411468910"
    private const val iosTestRewarded = "ca-app-pub-3940256099942544/1712485313"
    private const val iosTestAppOpen = "ca-app-pub-3940256099942544/5575463023"
    private const val iosTestNative = "ca-app-pub-3940256099942544/3986624511"

    // Production IDs (AdMob: Color Sort Puzzle)
    private const val prodBanner = "ca-app-pub-5878631871852645/6542309223"
    private const val prodInterstitial = "ca-app-pub-5878631871852645/7918970077"
    private const val prodRewarded = "ca-app-pub-5878631871852645/6350737539"
    private const val prodAppOpen = "ca-app-pub-5878631871852645/4811617124"
    private const val prodNative = "REPLACE_WITH_YOUR_NATIVE_ID" // Native format not used in app

    // iOS Production IDs
    private const val iosProdBanner = "REPLACE_WITH_YOUR_IOS_BANNER_ID"
    private const val iosProdInterstitial = "REPLACE_WITH_YOUR_IOS_INTERSTITIAL_ID"
    private const val iosProdRewarded = "REPLACE_WITH_YOUR_IOS_REWARDED_ID"
    private const val iosProdAppOpen = "REPLACE_WITH_YOUR_IOS_APP_OPEN_ID"
    private const val iosProdNative = "REPLACE_WITH_YOUR_IOS_NATIVE_ID"

    val androidBannerId: String
        get() = if (environment == AdEnvironment.TEST) testBanner else prodBanner

    val androidInterstitialId: String
        get() = if (environment == AdEnvironment.TEST) testInterstitial else prodInterstitial

    val androidRewardedId: String
        get() = if (environment == AdEnvironment.TEST) testRewarded else prodRewarded

    val androidAppOpenId: String
        get() = if (environment == AdEnvironment.TEST) testAppOpen else prodAppOpen

    val androidNativeId: String
        get() = if (environment == AdEnvironment.TEST) testNative else prodNative

    val iosBannerId: String
        get() = if (environment == AdEnvironment.TEST) iosTestBanner else iosProdBanner

    val iosInterstitialId: String
        get() = if (environment == AdEnvironment.TEST) iosTestInterstitial else iosProdInterstitial

    val iosRewardedId: String
        get() = if (environment == AdEnvironment.TEST) iosTestRewarded else iosProdRewarded

    val iosAppOpenId: String
        get() = if (environment == AdEnvironment.TEST) iosTestAppOpen else iosProdAppOpen

    val iosNativeId: String
        get() = if (environment == AdEnvironment.TEST) iosTestNative else iosProdNative

    // Configurable ad frequencies
    var interstitialEveryNLevels = 3
    var minimumInterstitialCooldownMs = 120_000L // 2 minutes
    var appOpenCooldownMs = 600_000L // 10 minutes

    // Rewarded ad daily limits
    val rewardedDailyLimits = mapOf(
        "coins" to 5,
        "life" to 3,
        "hint" to 5,
        "undo" to 5,
        "continue" to 1,
        "bonus_reward" to 1,
        "daily_bonus" to 1
    )

    // Device IDs that should always receive test ads (from logcat "Use RequestConfiguration...").
    private val testDeviceIds = listOf(
        "5A6C4DC4407AFCE2E718D6574A7AE08C" // V2437 - 16
    )

    fun initialize(context: Context) {
        MobileAds.initialize(context) {}
        // In TEST mode, register known devices so they reliably get test ads and avoid
        // the "add test device" warning. Google test ad unit IDs still work on any device
        // that can reach Google's servers.
        if (environment == AdEnvironment.TEST) {
            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
        }
    }

    fun setEnvironment(env: AdEnvironment) {
        environment = env
    }

    fun isTestMode(): Boolean = environment == AdEnvironment.TEST
}