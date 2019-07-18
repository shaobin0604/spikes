import com.github.ghostdogpr.readability4s.Readability

object Main {
  def main(args: Array[String]): Unit = {
    val url = "https://postlight.com/trackchanges/mercury-goes-open-source"
//    val htmlString = Http(url).asString.body

    val htmlString = requests.get(url).text


    val article = Readability(url, htmlString).parse()

    println(article)
  }
}
