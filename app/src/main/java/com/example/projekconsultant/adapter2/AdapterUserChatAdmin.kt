package com.example.projekconsultant.adapter2

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterUserChatAdmin(private val messages: List<Any>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ADMIN = 0
        private const val TYPE_USER = 1
        private const val TYPE_DATE = 2

        private const val TYPE_REMINDER = 20 // Tipe pesan reminder
        private const val TYPE_REMINDER_APPROVED = 21
        private const val TYPE_REMINDER_NEED_APPROVED = 22
        private const val TYPE_REMINDER_RESCHEDULE = 23

    }

        override fun getItemViewType(position: Int): Int {
            val message = messages?.get(position)
            return when {
                message is Message -> {
                    if (message.sender == "user") TYPE_USER else TYPE_ADMIN
                }
                message is MessageReminder ->  {
                    when (message.statusReminder) {
                        "approved" -> TYPE_REMINDER_APPROVED
                        "needApproved" -> TYPE_REMINDER_NEED_APPROVED
                        "reschedule" -> TYPE_REMINDER_RESCHEDULE
                        else -> TYPE_REMINDER // Default untuk status lain
                    }
                }
                else -> TYPE_DATE // For date entries, make sure you have the data type correctly identified.
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                TYPE_USER -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mybubblechat, parent, false)
                    UserViewHolder(view)
                }
                TYPE_ADMIN -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notmybubblechat, parent, false)
                    AdminViewHolder(view)
                }
                TYPE_REMINDER_NEED_APPROVED -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reminderchatuser, parent, false)
                    ReminderViewHolder(view)
                }
                TYPE_REMINDER_APPROVED -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reminderchatuser_2, parent, false)
                    ReminderViewHolder(view)
                }
                TYPE_REMINDER_RESCHEDULE -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reminderchatuser_3, parent, false)
                    ReminderViewHolder(view)
                }
                TYPE_DATE -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tanggalchat, parent, false)
                    BatasTanggalChatViewHolder(view)
                }
                else -> throw IllegalArgumentException("Invalid view type")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val message = messages?.get(position)
            when (holder) {
                is UserViewHolder -> {
                    if (message is Message) holder.bind(message)
                }
                is AdminViewHolder -> {
                    if (message is Message) holder.bind(message)
                }
                is ReminderViewHolder -> {
                    if (message is MessageReminder) holder.bind(message)
                }
                is BatasTanggalChatViewHolder ->{
                    if (message is MessageDate) holder.bind(message)
                }
            }
        }

        override fun getItemCount(): Int {
            return messages?.size ?: 0
        }

    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(message: MessageReminder) {
                if(message.statusReminder == "needApproved"){
                    itemView.findViewById<TextView>(R.id.tanggal).text = "Tanggal Pesanan ${message.tanggalPesan}"
                    itemView.findViewById<TextView>(R.id.layanan).text = "Layanan ${message.layanan}"
                    itemView.findViewById<TextView>(R.id.time).text = formatTimestamp(message.timestamp)
                    itemView.findViewById<TextView>(R.id.status).text = "Butuh Persetujuan."

                    val main = itemView.findViewById<LinearLayout>(R.id.butuhpersetujuanremindchat)

                }else if(message.statusReminder == "approved"){
                    itemView.findViewById<TextView>(R.id.tanggal).text = "Tanggal Pesanan ${message.tanggalPesan}"
                    itemView.findViewById<TextView>(R.id.layanan).text = "Layanan ${message.layanan}"
                    itemView.findViewById<TextView>(R.id.time).text = formatTimestamp(message.timestamp)
                    itemView.findViewById<TextView>(R.id.status).text = "Di Setujui.."

                    val main = itemView.findViewById<LinearLayout>(R.id.butuhpersetujuanremindchat)

                }else if(message.statusReminder == "reschedule"){
                    itemView.findViewById<TextView>(R.id.tanggal).text = "Reschedule ${message.tanggalPesan}"
                    itemView.findViewById<TextView>(R.id.layanan).text = "Layanan ${message.layanan}"
                    itemView.findViewById<TextView>(R.id.time).text = formatTimestamp(message.timestamp)

                }
            }

            private fun formatTimestamp(timestamp: Long): CharSequence? {
                val sdf = SimpleDateFormat("HH:mm", Locale("id", "ID"))

                return sdf.format(timestamp)
            }
        }

        class BatasTanggalChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(message: MessageDate) {
                itemView.findViewById<TextView>(R.id.textdate1).text = message.tanggal
            }
        }

        class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(message: Message) {
                itemView.findViewById<TextView>(R.id.textMessage).text = message.text
                itemView.findViewById<TextView>(R.id.time).text = formatTimestamp(message.timestamp)
            }

            private fun formatTimestamp(timestamp: Long): CharSequence? {
                val sdf = SimpleDateFormat("HH:mm", Locale("id", "ID"))
                return sdf.format(timestamp)
            }
        }

        class AdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(message: Message) {
                itemView.findViewById<TextView>(R.id.textMessage).text = message.text
                itemView.findViewById<TextView>(R.id.time).text = formatTimestamp(message.timestamp)
            }

            private fun formatTimestamp(timestamp: Long): CharSequence? {
                //   val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))

                val sdf = SimpleDateFormat("HH:mm", Locale("id", "ID"))
                return sdf.format(timestamp)
            }
        }
}









//override fun getItemViewType(position: Int): Int {
//        Log.d("CHAT ADAPTER", "${position}")
//        return (
//            if (messages[position].sender == "user") TYPE_USER else TYPE_ADMIN
//        )
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return if (viewType == TYPE_USER) {
//            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mybubblechat, parent, false)
//            UserViewHolder(view)
//        } else {
//            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notmybubblechat, parent, false)
//            AdminViewHolder(view)
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val message = messages[position]
//        if (holder is UserViewHolder) {
//            holder.bind(message)
//        } else if (holder is AdminViewHolder) {
//            holder.bind(message)
//        }
//    }
//
//    override fun getItemCount(): Int = messages.size
