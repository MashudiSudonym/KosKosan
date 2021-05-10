package c.m.koskosan.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import c.m.koskosan.data.model.LocationDistanceResponse
import c.m.koskosan.databinding.ItemLocationBinding
import c.m.koskosan.util.ViewUtilities.loadImageWithCoil


class HomeAdapter(private val onClick: (LocationDistanceResponse) -> Unit) :
    ListAdapter<LocationDistanceResponse, HomeAdapter.HomeViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val contents = getItem(position)
        holder.bind(contents)
    }

    class HomeViewHolder(
        itemView: ItemLocationBinding,
        onClick: (LocationDistanceResponse) -> Unit
    ) :
        RecyclerView.ViewHolder(itemView.root) {
        private val photoLayout = itemView.imgLocationPhoto
        private val nameLayout = itemView.tvLocationName
        private val addressLayout = itemView.tvLocationAddress
        private val distanceLayout = itemView.tvLocationDistance
        private val phoneLayout = itemView.tvLocationPhone
        private var currentLocation: LocationDistanceResponse? = null

        init {
            itemView.locationItemLayout.setOnClickListener {
                currentLocation?.let {
                    onClick(it)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(locationDistanceResponse: LocationDistanceResponse) {
            currentLocation = locationDistanceResponse

            // add data to widget view
            loadImageWithCoil(photoLayout, currentLocation?.photo?.first().toString())
            nameLayout.text = currentLocation?.name
            addressLayout.text = currentLocation?.address
            phoneLayout.text = currentLocation?.phone
            distanceLayout.text = "${currentLocation?.distance} KM"
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<LocationDistanceResponse>() {
        override fun areItemsTheSame(
            oldItem: LocationDistanceResponse,
            newItem: LocationDistanceResponse
        ): Boolean = oldItem == newItem


        override fun areContentsTheSame(
            oldItem: LocationDistanceResponse,
            newItem: LocationDistanceResponse
        ): Boolean = oldItem.uid == newItem.uid
    }
}