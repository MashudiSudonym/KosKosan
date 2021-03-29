package c.m.koskosan.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import c.m.koskosan.R
import c.m.koskosan.databinding.FragmentTransactionBinding
import c.m.koskosan.util.gone
import c.m.koskosan.util.invisible
import c.m.koskosan.util.visible
import c.m.koskosan.vo.ResponseState
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransactionFragment : Fragment() {

    private val transactionViewModel: TransactionViewModel by viewModel()
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        val view = binding.root

        // app bar title setup
        (activity as AppCompatActivity?)?.setSupportActionBar(binding.toolbarTransaction)
        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            title = getString(R.string.transaction)
        }

        // initialize get transaction data
        initializeGetTransactionData()

        // swipe refresh function
        binding.transactionSwipeRefreshView.setOnRefreshListener {
            binding.transactionSwipeRefreshView.isRefreshing = false
            initializeGetTransactionData()
        }
        return view
    }

    // initialize get transaction data
    private fun initializeGetTransactionData() {
        transactionViewModel.getUserOrderByUid().observe(viewLifecycleOwner, { response ->
            if (response != null) when (response) {
                is ResponseState.Error -> showErrorStateView() // error state
                is ResponseState.Loading -> showLoadingStateView() // loading state
                is ResponseState.Success -> {
                    // success state
                    showSuccessStateView()

                    // TODO: show data to recyclerview
                }
            }
        })
    }

    // handle success state of view
    private fun showSuccessStateView() {
        binding.animLoading.gone()
        binding.animError.gone()
        binding.transactionLayout.visible()
    }

    // handle error state of view
    private fun showErrorStateView() {
        binding.animError.visible()
        binding.animLoading.gone()
        binding.transactionLayout.invisible()
    }

    // handle loading state of view
    private fun showLoadingStateView() {
        binding.transactionLayout.invisible()
        binding.animLoading.visible()
        binding.animError.gone()
    }

}