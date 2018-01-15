package com.mobodev.postparser;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.*;

import com.chimbori.crux.articles.Article;
import com.chimbori.crux.articles.ArticleExtractor;
import com.wuman.jreadability.Readability;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testParseHtml() throws Exception {
        final String content = new TextExtract().parse(readString("test_1.html"));
        System.out.println(content);
    }

    @Test
    public void testParseHtml2() throws Exception {
//        final String html = readString("test_1.html");
        Readability readability = new Readability(new URL("http://isunman.com/2016/05/04/crawls-pages-text-content/"), 15_000) {
            @Override
            protected void dbg(String msg) {
            }

            @Override
            protected void dbg(String msg, Throwable t) {
            }
        };
        readability.init();
        String cleanHtml = readability.outerHtml();
        System.out.println(cleanHtml);
    }

    @Test
    public void testParseHtml3() throws Exception {
        final Document document = Jsoup.parse(new URL("http://mp.weixin.qq.com/s/Klxairct9ld8Q5rfGBpSgg"), 15_000);
        final Element first = document.select("div#js_content").first();
        System.out.println(first.text());
    }

    @Test
    public void testParseHtml4() throws Exception {
        String url = "https://example.com/article.html";
//        String rawHTML = "<html><body><h1>This is an article</h1></body></html>";
        String rawHTML = readString("test_1.html");

        Article article = ArticleExtractor.with(url, rawHTML)
                .extractMetadata()
                .extractContent()  // If you only need metadata, you can skip `.extractContent()`
                .article();
        System.out.println(article.document.text());
    }

    private String readString(String filename) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(filename), StandardCharsets.UTF_8);
    }
}