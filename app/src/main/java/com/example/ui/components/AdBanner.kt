package com.example.ui.components

import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ads.AdConfig
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    var isVisible by remember { mutableStateOf(true) }
    var adHeight by remember { mutableStateOf(0f) }
    
    if (!isVisible) {
        Box(modifier = modifier.fillMaxWidth().height(0.dp))
        return
    }
    
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(adHeight.dp),
        factory = { context ->
            val adView = AdView(context).apply {
                val widthDp = (context.resources.displayMetrics.widthPixels / context.resources.displayMetrics.density).toInt()
                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp))
                adUnitId = AdConfig.androidBannerId
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            
            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    isVisible = true
                    adHeight = adView.height.toFloat() / context.resources.displayMetrics.density
                }
                
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isVisible = false
                    adHeight = 0f
                }
                
                override fun onAdClicked() {
                    // Ad clicked
                }
                
                override fun onAdImpression() {
                    // Ad impression recorded
                }
            }
            
            adView.loadAd(AdRequest.Builder().build())
            adView
        },
        update = { adView ->
            // Handle ad view updates if needed
        }
    )
}