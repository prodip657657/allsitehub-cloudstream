package com.allsitehub

import com.lagradost.cloudstream3.*
import java.net.URI
import java.util.Locale

internal data class SiteEntry(val category: String, val title: String, val url: String)

/** Displays website directory entries only; not a video streaming provider. */
class AllSiteHubProvider : MainAPI() {
    override var name = "AllSiteHub Website Directory"
    override var mainUrl = "https://www.allsitehub.site"
    override var lang = "en"
    override val supportedTypes = setOf(TvType.Others)
    override val hasMainPage = true
    override val hasQuickSearch = true
    override val hasDownloadSupport = false
    override val hasChromecastSupport = false
    override val sequentialMainPage = true

    private val categories = listOf(
        "Movies & Shows", "Anime", "Manga", "Live TV & Sports",
        "Paid", "Apps", "Download", "Games"
    )
    override val mainPage = mainPageOf(*categories.map { it to it }.toTypedArray())
    private var cache: List<SiteEntry>? = null
    private var cachedAt = 0L

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        if (page > 1) return newHomePageResponse(request, emptyList(), false)
        val results = catalog().filter { it.category == request.data }.map { it.toSearchResult() }
        return newHomePageResponse(request, results, false)
    }

    override suspend fun search(query: String): List<SearchResponse> =
        catalog().filter {
            it.title.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true) ||
            it.url.contains(query, ignoreCase = true)
        }.map { it.toSearchResult() }

    override suspend fun quickSearch(query: String): List<SearchResponse> = search(query)

    private fun SiteEntry.toSearchResult(): SearchResponse =
        newMovieSearchResponse(title, url, TvType.Others, false) {
            posterUrl = "https://www.google.com/s2/favicons?domain=" + URI(url).host + "&sz=128"
        }

    override suspend fun load(url: String): LoadResponse? {
        val site = catalog().firstOrNull { it.url == url } ?: return null
        return newMovieLoadResponse(site.title, site.url, TvType.Others, site.url) {
            plot = "WEBSITE DIRECTORY ONLY (NOT A VIDEO)\n\n" +
                "Category: " + site.category + "\nWebsite: " + site.url + "\n\n" +
                "Open the link in a web browser. CloudStream cannot browse all third-party pages or play arbitrary website videos."
            tags = listOf(site.category, "External website", "Not a stream")
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean = false

    private suspend fun catalog(): List<SiteEntry> {
        val now = System.currentTimeMillis()
        cache?.let { if (now - cachedAt < 30L * 60L * 1000L) return it }
        val result = try {
            val doc = app.get(mainUrl).document
            val found = linkedMapOf<String, SiteEntry>()
            var category: String? = null
            for (element in doc.select("h1, h2, h3, h4, a[href]")) {
                if (element.tagName() != "a") {
                    val heading = element.text().trim()
                    category = categories.firstOrNull {
                        heading.equals(it, ignoreCase = true) ||
                        heading.startsWith(it + " (", ignoreCase = true)
                    } ?: category
                    continue
                }
                val url = element.attr("abs:href").ifBlank { element.attr("href") }
                if (category == null || (!url.startsWith("https://") && !url.startsWith("http://"))) continue
                val host = try { URI(url).host?.lowercase(Locale.ROOT) } catch (_: Exception) { null } ?: continue
                if (host == "allsitehub.site" || host == "www.allsitehub.site" ||
                    host in setOf("discord.gg", "discord.com", "t.me", "reddit.com", "www.reddit.com")) continue
                val label = element.text().trim().let {
                    if (it.length in 3..45 && it.contains('.') && !it.contains(' ')) it else host
                }
                found.putIfAbsent(host, SiteEntry(category, label, url.substringBefore('#')))
            }
            found.values.toList().takeIf { it.size >= 40 } ?: EmbeddedCatalog.records
        } catch (_: Exception) {
            EmbeddedCatalog.records
        }
        cache = result
        cachedAt = now
        return result
    }
}
