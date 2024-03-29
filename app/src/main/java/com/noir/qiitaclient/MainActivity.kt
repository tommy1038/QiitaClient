package com.noir.qiitaclient

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.noir.qiitaclient.client.ArticleClient
import com.noir.qiitaclient.model.Article
import com.noir.qiitaclient.model.User
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindToLifecycle
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : RxAppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val listView = findViewById(R.id.list_view) as ListView
    val progressBar = findViewById(R.id.progress_bar) as ProgressBar
    val queryEditText = findViewById(R.id.query_edit_text) as EditText
    val searchButton = findViewById(R.id.search_button) as Button

    val listAdapter = ArticleListAdapter(applicationContext)
//    listAdapter.articles = listOf(
//      dummyArticle("Kotlin入門", "たろう"),
//      dummyArticle("Java入門", "じろう")
//    )
    listView.adapter = listAdapter
    listView.setOnItemClickListener { adapterView, view, position, id ->
      val article = listAdapter.articles[position]
      ArticleActivity.intent(this, article).let { startActivity(it) }
    }

    val gson = GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .create()
    val retrofit = Retrofit.Builder()
      .baseUrl("https://qiita.com")
      .addConverterFactory(GsonConverterFactory.create(gson))
      .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
      .build()
    val articleClient = retrofit.create(ArticleClient::class.java)


    searchButton.setOnClickListener {
      progressBar.visibility = View.VISIBLE

      articleClient.search(queryEditText.text.toString())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doAfterTerminate {
          progressBar.visibility = View.GONE
        }
        .bindToLifecycle(this)
        .subscribe({
          queryEditText.text.clear()
          listAdapter.articles = it
          listAdapter.notifyDataSetChanged()
        }, {
          toast("エラー: $it")
        })
    }
  }

  // ダミー記事を生成するメソッド
  private fun dummyArticle(title: String, userName: String): Article {
    return Article(
      id = "", title = title, url = "https://kotlinlang.org/",
      user = User(id = "", name = userName, profileImageUrl = "")
    )
  }
}
