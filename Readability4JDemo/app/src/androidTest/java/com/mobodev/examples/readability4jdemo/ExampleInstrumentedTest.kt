package com.mobodev.examples.readability4jdemo

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import net.dankito.readability4j.Article
import net.dankito.readability4j.Readability4J

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.net.URL


fun Article.toS(): String {
    return "Article{uri=$uri, title=$title, byline=$byline, excerpt=$excerpt, direction=$dir, length: $length, textContent=$textContent}"
}

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    companion object {
        const val TAG = "ExampleInstrumentedTest"
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.mobodev.examples.readability4jdemo", appContext.packageName)
    }

    @Test
    fun readability4j_should_parse_postlight() {
        val url = "https://postlight.com/trackchanges/mercury-goes-open-source"
        val article = parseToArticle(url)

        Log.v(TAG, "article: ${article.toS()}")

        assertEquals("Mercury Goes Open Source! — Postlight — Digital Product Studio", article.title)
        assertEquals("It’s my pleasure to announce that today, Postlight is open-sourcing the Mercury Web Parser. Written in JavaScript and running on both Node and in the ...", article.excerpt)
    }

    @Test
    fun readability4j_should_parse_nyt() {
        val url = "https://www.nytimes.com/2019/06/03/world/europe/trump-uk-visit-may.html"
        val article = parseToArticle(url)

        Log.v(TAG, "nyt article: ${article.toS()}")
    }

    @Test
    fun readability4j_should_parse_cnn() {
        val url = "https://www.cnn.com/2019/06/26/politics/mexico-father-daughter-dead-rio-grande-wednesday/index.html"
        val article = parseToArticle(url)

        Log.v(TAG, "cnn article: ${article.toS()}")
    }

    @Test
    fun readability4j_should_parse_medium() {

    }

    private fun parseToArticle(url: String): Article {
        val html = URL(url).readText()
        return Readability4J(url, html).parse()
    }
}
