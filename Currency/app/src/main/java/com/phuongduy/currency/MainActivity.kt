package com.phuongduy.currency

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.phuongduy.currency.databinding.ActivityMainBinding
import com.phuongduy.currency.list.ExchangeAdapter
import com.phuongduy.currency.paging.ExchangePagingSource
import com.phuongduy.currency.presentation.uimodel.CurrencyUiModel
import com.phuongduy.currency.presentation.viewmodel.main.MainViewModel
import com.phuongduy.currency.presentation.viewmodel.main.MainViewModel.Companion.PAGE_SIZE
import com.phuongduy.currency.presentation.viewmodel.main.MainViewModelImpl
import dagger.android.AndroidInjection
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainViewModel

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AndroidInjection.inject(this)

        viewModel = viewModelFactory.create(MainViewModelImpl::class.java)
        viewModel.setUp()

        setUpViews()
    }

    private fun setUpViews() {
        viewModel.currencyList.observe(this, {
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, it)
            viewBinding.currencySelector.setAdapter(adapter)
            viewBinding.currencySelector.setOnItemClickListener { parent, _, position, _ ->
                (parent?.getItemAtPosition(position) as? CurrencyUiModel)?.let { currencyUiModel ->
                    val amountText = viewBinding.inputAmount.text.toString()
                    if (amountText.isNotBlank()) {
                        viewModel.onDataInputted(currencyUiModel, amountText.toInt())
                    }
                }
            }
        })

        viewModel.isRefreshNeeded.observe(this, { isRefreshNeeded ->
            if (isRefreshNeeded) {
                val exchangeAdapter = ExchangeAdapter()
                viewBinding.exchangeList.adapter = exchangeAdapter
                viewBinding.exchangeList.layoutManager =
                    LinearLayoutManager(this@MainActivity, VERTICAL, false)
                viewBinding.exchangeList.addItemDecoration(DividerItemDecoration(this, VERTICAL))

                lifecycleScope.launch {
                    Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                        ExchangePagingSource(viewModel)
                    }.flow.collectLatest(exchangeAdapter::submitData)
                }
            }
        })

        viewBinding.inputAmount.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewBinding.inputAmount.text.toString().toInt().let(viewModel::onAmountInputted)
            }
            true
        }
    }
}