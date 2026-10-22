package com.allsitehub

import android.content.Context
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class AllSiteHubPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(AllSiteHubProvider())
    }
}
