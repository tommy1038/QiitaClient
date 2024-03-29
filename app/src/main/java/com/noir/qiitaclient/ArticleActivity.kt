package com.noir.qiitaclient

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.noir.qiitaclient.model.Article
import com.noir.qiitaclient.view.ArticleView

class ArticleActivity : AppCompatActivity() {

  companion object {

    private const val ARTICLE_EXTRA: String = "article"

    fun intent(context: Context, article: Article): Intent =
      Intent(context, ArticleActivity::class.java)
        .putExtra(ARTICLE_EXTRA, article)
  }

  private val articleView: ArticleView by lazy {
    findViewById(R.id.article_view) as ArticleView
  }

  private val webView: WebView by lazy {
    findViewById(R.id.web_view) as WebView
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_article)

    val article: Article = intent.getParcelableExtra(ARTICLE_EXTRA)
    articleView.setArticle(article)
    webView.loadUrl(article.url)
  }
}
