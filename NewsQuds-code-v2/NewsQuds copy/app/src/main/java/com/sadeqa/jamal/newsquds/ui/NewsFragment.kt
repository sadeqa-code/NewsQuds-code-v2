package com.sadeqa.jamal.newsquds.ui

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.sadeqa.jamal.newsquds.R
import com.sadeqa.jamal.newsquds.api.Process
import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment : Fragment(R.layout.fragment_news) {

    val args: NewsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val news = args.news
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(news.url!!)
        }

        fab.setOnClickListener {
            Process.insertNewsToFirebase(news)
            Snackbar.make(view, "News saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }
}