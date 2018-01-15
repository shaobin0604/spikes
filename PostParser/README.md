# 微信公众号文章正文抽取

测试了以下几种方案：

- https://github.com/reorx/cx-extractor
- https://github.com/wuman/JReadability
- JSoup 这个最好

```java
final Document document = Jsoup.parse(new URL("http://mp.weixin.qq.com/s/Klxairct9ld8Q5rfGBpSgg"), 15_000);
final Element first = document.select("div#js_content").first();
System.out.println(first.text());
```