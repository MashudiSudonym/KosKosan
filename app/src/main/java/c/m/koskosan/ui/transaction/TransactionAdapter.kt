package c.m.koskosan.ui.transaction

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import c.m.koskosan.R
import c.m.koskosan.data.model.OrderResponse
import c.m.koskosan.databinding.ItemLocationBinding
import c.m.koskosan.databinding.ItemTransactionBinding

import com.bumptech.glide.Glide

class TransactionAdapter(private val onClick: (OrderResponse) -> Unit) :
    ListAdapter<OrderResponse, TransactionAdapter.TransactionViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val contents = getItem(position)
        holder.bind(contents)
    }

    class TransactionViewHolder(itemView: ItemTransactionBinding, onClick: (OrderResponse) -> Unit) :
        RecyclerView.ViewHolder(itemView.root) {
        private val transactionDateLayout = itemView.tvTransactionDate
        private val transactionByLayout = itemView.tvTransactionBy
        private val transactionFromLayout = itemView.tvTransactionFrom
        private val transactionStatusLayout = itemView.tvTransactionStatus
        private var currentTransaction: OrderResponse? = null

        init {
            itemView.transactionItemLayout.setOnClickListener {
                currentTransaction?.let {
                    onClick(it)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(OrderResponse: OrderResponse) {
            currentTransaction = OrderResponse

            // add data to widget view
            transactionDateLayout.text = currentTransaction?.orderCreated
            transactionByLayout.text = currentTransaction?.userName
            transactionFromLayout.text = currentTransaction?.nameLocation
            transactionStatusLayout.text = when (currentTransaction?.orderStatus) {
                0 -> itemView.context.getString(R.string.waiting_status)
                1 -> itemView.context.getString(R.string.survey_status)
                2 -> itemView.context.getString(R.string.accept_status)
                3 -> itemView.context.getString(R.string.cancel_status)
                else -> itemView.context.getString(R.string.strip)
            }
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<OrderResponse>() {
        override fun areItemsTheSame(
            oldItem: OrderResponse,
            newItem: OrderResponse
        ): Boolean = oldItem == newItem


        override fun areContentsTheSame(
            oldItem: OrderResponse,
            newItem: OrderResponse
        ): Boolean = oldItem.uid == newItem.uid
    }
}