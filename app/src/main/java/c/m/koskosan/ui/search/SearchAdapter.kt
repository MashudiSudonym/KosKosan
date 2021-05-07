package c.m.koskosan.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import c.m.koskosan.R
import c.m.koskosan.data.entity.LocationEntity
import c.m.koskosan.data.model.LocationResponse
import c.m.koskosan.databinding.ItemSearchBinding
import c.m.koskosan.util.ViewUtilities.loadImageWithCoil
import com.bumptech.glide.Glide

class SearchAdapter(
    private val onClick: (LocationEntity) -> Unit
) :
    ListAdapter<LocationEntity, SearchAdapter.SearchViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val contents = getItem(position)
        holder.bind(contents)
    }

    class SearchViewHolder(itemView: ItemSearchBinding, onClick: (LocationEntity) -> Unit) :
        RecyclerView.ViewHolder(itemView.root) {
        private val photoLayout = itemView.imgLocationPhoto
        private val nameLayout = itemView.tvLocationName
        private var location: LocationEntity? = null

        init {
            itemView.searchItemLayout.setOnClickListener {
                location?.let {
                    onClick(it)
                }
            }
        }

        fun bind(locationEntity: LocationEntity) {
            location = locationEntity

            // add data to widget view
            loadImageWithCoil(photoLayout, location?.photoURL.toString())
            nameLayout.text = location?.nameLocation
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<LocationEntity>() {
        override fun areItemsTheSame(
            oldItem: LocationEntity,
            newItem: LocationEntity
        ): Boolean = oldItem == newItem


        override fun areContentsTheSame(
            oldItem: LocationEntity,
            newItem: LocationEntity
        ): Boolean = oldItem.uid == newItem.uid
    }
}