package com.example.dpfoodapp.ui.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dpfoodapp.databinding.ActivitySearchBinding
import com.example.dpfoodapp.model.data.Meal
import com.example.dpfoodapp.model.modelsearch.SearchTable
import com.example.dpfoodapp.roomdb.database.SearchDatabase
import com.example.dpfoodapp.ui.adapter.HistoryAdapter
import com.example.dpfoodapp.ui.adapter.SearchAdapter
import com.example.dpfoodapp.ui.viewmodel.FoodViewModel
import com.example.dpfoodapp.ui.viewmodel.HistoryViewModel
import com.example.dpfoodapp.utils.NetworkClass
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val emptyList: ArrayList<Meal> by lazy { ArrayList<Meal>() }
    private val foodViewModel: FoodViewModel by lazy { FoodViewModel() }
    private val searchDBViewModel: HistoryViewModel by lazy {
        HistoryViewModel(
            SearchDatabase.getInstanceSearch(
                this
            )
        )
    }
    private val searchAdapter: SearchAdapter by lazy { SearchAdapter() }
    private val historyAdapter: HistoryAdapter by lazy { HistoryAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        searchTextListener()
        searchLiveObserver()

        receiveLaunchdata()

        //swipe delete
        swipeDeleteUndo()

        historyDataApply()

    }

    private fun historyDataApply() {
        lifecycleScope.launchWhenCreated {
            searchDBViewModel.searchDetails.collectLatest {
                if (it.isEmpty()) {
                    binding.placeholder.visibility = View.VISIBLE
                    binding.progressbar.visibility = View.GONE
                    binding.loadingGifMeals.visibility = View.GONE
                } else {
                    historyAdapter.differ.submitList(it)
                    historyAdapter.notifyDataSetChanged()
                    binding.historyRecycle.adapter = historyAdapter
                    binding.placeholder.visibility = View.GONE
                    binding.homeTitle.visibility = View.VISIBLE
                    binding.historyRecycle.layoutManager =
                        LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
                }
            }
        }
    }

    private fun swipeDeleteUndo() {
        val itemTouchHelper = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val recentStrore = historyAdapter.differ.currentList[position]
                searchDBViewModel.deleteTask(recentStrore)
                Snackbar.make(
                    binding.historyRecycle,
                    "Deleted " + "${historyAdapter.differ.currentList[position].strCategory}",
                    Snackbar.LENGTH_SHORT
                )
                    .setAction("Undo", View.OnClickListener {
                        searchDBViewModel.insertTask(recentStrore)
                    }).show()
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.historyRecycle)
    }

    private fun receiveLaunchdata() {
        historyAdapter.searchDataReceive { data ->
            val receivehistory = Meal(
                data.dateModified,
                data.idMeal,
                data.strMeal,
                data.strCategory,
                data.strCreativeCommonsConfirmed,
                data.strDrinkAlternate,
                data.strImageSource,
                data.strIngredient1,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                data.strInstructions,
                data.strMeal,
                data.strMealThumb,
                "",
                "",
                ",",
                "",
                "",
                "",
                ",",
                ",",
                ",",
                "",
                "",
                "",
                "",
                ",",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                data.strYoutube
            )
            val intent = Intent()
            intent.putExtra("message", receivehistory)
            setResult(RESULT_OK, intent)
            finish()
        }


        searchAdapter.searchDataReceive { data ->

            val searchList = SearchTable(
                data.dateModified,
                data.idMeal,
                data.strMeal,
                data.strCategory,
                data.strCreativeCommonsConfirmed,
                data.strDrinkAlternate,
                data.strImageSource,
                data.strIngredient1,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                data.strInstructions,
                data.strMeal,
                data.strMealThumb,
                "",
                "",
                ",",
                "",
                "",
                "",
                ",",
                ",",
                ",",
                "",
                "",
                "",
                "",
                ",",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                data.strYoutube
            )
            searchDBViewModel.insertTask(searchList)
            val intent = Intent()
            intent.putExtra("message", data)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun searchLiveObserver() {
        lifecycleScope.launchWhenCreated {
            foodViewModel.searchResponseLive.collect() {
                when (it) {
                    is NetworkClass.Loading -> {
                        binding.progressbar.visibility = View.VISIBLE
                        binding.homeTitle.visibility = View.GONE
                        binding.searchRecycle.visibility = View.GONE
                        binding.historyRecycle.visibility = View.GONE
                        binding.loadingGifMeals.visibility = View.VISIBLE
                        binding.placeholder.visibility = View.GONE
                    }
                    is NetworkClass.Success -> {
                        binding.loadingGifMeals.visibility = View.GONE
                        binding.progressbar.visibility = View.GONE
                        binding.historyRecycle.visibility = View.GONE
                        binding.placeholder.visibility = View.GONE
                        binding.searchRecycle.visibility = View.VISIBLE
                        // SystemClock.sleep(400)
                        searchAdapter.differ.submitList(it.item as MutableList<Meal>)
                        //  searchAdapter.notifyDataSetChanged()
                    }
                    else -> {
                        binding.progressbar.visibility = View.GONE
                    }
                }
            }
        }
        binding.searchRecycle.adapter = searchAdapter
        binding.searchRecycle.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
    }

    private fun searchTextListener() {

        binding.searchname.addTextChangedListener(object : TextWatcher {
            private var countDownTimer: CountDownTimer? = null
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                countDownTimer?.cancel()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                countDownTimer = object : CountDownTimer(500, 1000) {
                    override fun onTick(millisUntilFinished: Long) {

                    }
                    override fun onFinish() {
                       Log.d("NewSearchActivity",s.toString())

                        if (s.toString().trim().isEmpty()) {
                            binding.historyRecycle.visibility = View.VISIBLE
                            binding.searchRecycle.visibility = View.GONE
                            binding.homeTitle.visibility = View.VISIBLE
                            binding.loadingGifMeals.visibility = View.GONE
                            binding.placeholder.visibility=View.GONE
                            binding.progressbar.visibility=View.GONE
                        } else {
                            foodViewModel.searchMeals(s.toString().trim())
                            binding.historyRecycle.visibility = View.GONE
                            binding.progressbar.visibility=View.VISIBLE
                            binding.homeTitle.visibility = View.GONE
                            binding.loadingGifMeals.visibility = View.VISIBLE
                        }

                    }
                }.start()
          /*      if (s.toString().trim().isEmpty()) {
                    SystemClock.sleep(500)
                    binding.historyRecycle.visibility = View.VISIBLE
                    binding.searchRecycle.visibility = View.GONE
                    binding.homeTitle.visibility = View.VISIBLE
                    binding.placeholder.visibility=View.GONE
                } else {
                    SystemClock.sleep(500)
                    foodViewModel.searchMeals(s.toString().trim())
                }*/
            }
            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
}