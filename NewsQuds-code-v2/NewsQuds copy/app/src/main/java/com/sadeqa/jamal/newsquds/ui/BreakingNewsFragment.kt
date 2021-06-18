package com.sadeqa.jamal.newsquds.ui

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sadeqa.jamal.newsquds.MainActivity
import com.sadeqa.jamal.newsquds.QUERY_PAGE_SIZE

import com.sadeqa.jamal.newsquds.R
import com.sadeqa.jamal.newsquds.adapter.NewsAdapter
import com.sadeqa.jamal.newsquds.api.Process
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.item_error_message.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var newsAdapter: NewsAdapter
    var page = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        page = 1
        //viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("news", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        breakingNews("القدس")

        btnRetry.setOnClickListener {
          //  viewModel.getBreakingNews("القدس")
        }
    }

    fun breakingNews(countryCode: String){
        showProgressBar()
        Process.getBreakingNews(countryCode,page,{response ->
            hideProgressBar()
            hideErrorMessage()
            response?.let { newsResponse ->
                newsAdapter.differ.submitList(newsResponse.articles.toList())
                val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                isLastPage = page == totalPages
                if(isLastPage) {
                    rvBreakingNews.setPadding(0, 0, 0, 0)
                }
            }
        },{message ->
            hideProgressBar()
            message?.let { message ->
                Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG).show()
                showErrorMessage(message)
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage() {
        itemErrorMessage.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String) {
        itemErrorMessage.visibility = View.VISIBLE
        tvErrorMessage.text = message
        isError = true
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                breakingNews("القدس")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}








