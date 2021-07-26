package com.example.retrofitroom_taipeigarbagetruck.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.retrofitroom_taipeigarbagetruck.R
import com.example.retrofitroom_taipeigarbagetruck.databinding.FragmentGarbageTruckBinding

class GarbageTruckFragment : Fragment() {

    /**
     *  Lazily initialize our [GarbageTruckViewModel]
     */
    private val viewModel : GarbageTruckViewModel by lazy {
        ViewModelProvider(this).get(GarbageTruckViewModel::class.java)
    }

    /**
     * 使用數據綁定擴展佈局，將其生命週期所有者設置為 GarbageTruckFragment
     * 開啟Data Binding 觀察LiveData，並設置RecyclerView 與 adapter。 */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentGarbageTruckBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        // 允許數據綁定使用此 Fragment 的生命週期觀察 LiveData
        binding.lifecycleOwner = this

        // Giving the binding access to the GarbageTruckViewModel
        // 允許數據綁定使用此 Fragment 的生命週期觀察 LiveData
        /**
         *  需先在 layout 中 加上 <data></data>
         *  <data>
                <variable
                    name="viewModel"
                    type="com.example.retrofitroom_taipeigarbagetruck.ui.GarbageTruckViewModel" />
             </data>

         *  android:text="@{viewModel.response}"
         */
        binding.viewModel = viewModel

        // Sets the adapter of the recycler RecyclerView with clickHandler lambda that
        // tells the viewModel when our property is clicked
        binding.recycler.adapter = GarbageTruckAdapter(GarbageTruckAdapter.OnClickListener{
            viewModel.displayPropertyDetails(it)
        })

        // Observe the navigateToSelectedProperty LiveData and Navigate when it isn't null
        // After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
        // for another navigation event.
        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, Observer {
            if (null != it){
                this.findNavController().navigate(
                    GarbageTruckFragmentDirections.actionToGarbageTruckDetailFragment(it))
                viewModel.displayPropertyDetailsComplete()
            }
        })

        return binding.root

    }
}