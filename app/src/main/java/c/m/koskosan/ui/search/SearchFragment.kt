package c.m.koskosan.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import c.m.koskosan.data.entity.LocationEntity
import c.m.koskosan.databinding.FragmentSearchBinding
import c.m.koskosan.ui.detail.DetailActivity
import c.m.koskosan.util.Constants
import c.m.koskosan.util.gone
import c.m.koskosan.util.invisible
import c.m.koskosan.util.visible
import c.m.koskosan.vo.ResponseState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by viewModel()
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // appbar title setup
        (activity as AppCompatActivity?)?.setSupportActionBar(binding.toolbarSearch)

        // initialize recyclerview adapter
        searchAdapter = SearchAdapter { locationResponse ->
            val detailActivityIntent =
                Intent(requireActivity(), DetailActivity::class.java).apply {
                    putExtra(Constants.UID, locationResponse.uid)
                }
            startActivity(detailActivityIntent)
        }

        // initialize firebase data and save to local db
        initializeDataAndSaveToLocalDb()

        // initialize searching
        initializeSearching()

        // swipe to refresh function
        binding.searchSwipeRefreshLayout.setOnRefreshListener {
            binding.searchSwipeRefreshLayout.isRefreshing = false
            initializeDataAndSaveToLocalDb()
        }
    }

    // initialize firebase data and save to local db
    private fun initializeDataAndSaveToLocalDb() {
        searchViewModel.getAllLocation().observe(viewLifecycleOwner, { response ->
            if (response != null) when (response) {
                is ResponseState.Error -> showErrorStateView() // error state
                is ResponseState.Loading -> showLoadingStateView() // loading state
                is ResponseState.Success -> {
                    // success state
                    showSuccessStateView()

                    // save to local db
                    val locationEntity: ArrayList<LocationEntity> = arrayListOf()

                    response.data?.forEach { item ->
                        locationEntity.add(
                            LocationEntity(
                                uid = item.uid.toString(),
                                photoURL = item.photo?.first().toString(),
                                nameLocation = item.name.toString()
                            )
                        )
                    }

                    searchViewModel.saveAllLocation(locationEntity)
                }
            }
        })
    }

    // initialize searching
    private fun initializeSearching() {
        // unfocused search view state
        if (!binding.svSearch.isFocused) showLoadingStateView()

        // search view text listener
        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(keyword: String?): Boolean {
                when (binding.svSearch.isFocused) {
                    true -> showLoadingStateView()
                    false -> {
                        searchViewModel.getSearchKeyword(keyword.toString())
                        searchViewModel.searchContent.observe(viewLifecycleOwner, { response ->
                            if (response != null) when (response) {
                                is ResponseState.Error -> showErrorStateView() // error state view
                                is ResponseState.Loading -> showErrorStateView() // loading state view, for this case show the empty box for no result search
                                is ResponseState.Success -> {
                                    // if searching result is null or empty show error state (empty box animation)
                                    if (response.data.isNullOrEmpty()) {
                                        showErrorStateView()
                                    } else {
                                        // success state
                                        showSuccessStateView()

                                        // add search result to recyclerview
                                        searchAdapter.submitList(response.data)

                                        binding.rvSearchLocation.adapter = searchAdapter
                                        binding.rvSearchLocation.setHasFixedSize(true)
                                    }

                                }
                            }
                        })
                    }
                }
                return true
            }
        })
    }

    // handle success state of view
    private fun showSuccessStateView() {
        binding.animLoading.gone()
        binding.animError.gone()
        binding.searchLayout.visible()
    }

    // handle error state of view
    private fun showErrorStateView() {
        binding.animError.visible()
        binding.animLoading.gone()
        binding.searchLayout.invisible()
    }

    // handle loading state of view
    private fun showLoadingStateView() {
        binding.searchLayout.invisible()
        binding.animLoading.visible()
        binding.animError.gone()
    }
}