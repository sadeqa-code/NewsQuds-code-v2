package com.sadeqa.jamal.newsquds.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sadeqa.jamal.newsquds.R
import com.sadeqa.jamal.newsquds.adapter.NewsAdapter
import com.sadeqa.jamal.newsquds.api.Process
import com.sadeqa.jamal.newsquds.model.News
import com.sadeqa.jamal.newsquds.model.Source
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    //lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("news", it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                Process.deleteNewsToFirebase(requireContext(),article)
                Snackbar.make(view, "Successfully deleted news", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        Process.insertNewsToFirebase(
                            article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }
       // Process.getAllNews(requireContext()).observe(viewLifecycleOwner, Observer { articles ->
       //     newsAdapter.differ.submitList(articles)
       // })

        var  news : ArrayList<News> = ArrayList<News>()
        Firebase.firestore.collection(Process.QUADS)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(Process.TAG, "${document.id} => ${document.data}")
                    news.add((News(
                       0,
                        document.data.get("author") as String,
                        document.data.get("content") as String,
                        document.data.get("description") as String,
                        document.data.get("publishedAt") as String,
                        Source("1",document.data.get("source") as String),
                        document.data.get("source") as String,
                        document.data.get("title") as String,
                        document.data.get("url") as String,
                        document.data.get("urlToImage") as String,
                    )))
                }
                Log.d(Process.TAG, "news size ${news.size}")
                newsAdapter.differ.submitList(news)
            }
            .addOnFailureListener { exception ->
                Log.w(Process.TAG, "Error getting documents.", exception)
            }

    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}