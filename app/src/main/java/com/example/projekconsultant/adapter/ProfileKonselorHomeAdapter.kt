package com.example.projekconsultant.adapter

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.sideactivity.DeskripsiKonselorHome
import com.google.android.flexbox.FlexboxLayout

class ProfileKonselorHomeAdapter(private val profileList: List<ProfileKonselorHome>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.imageViewProfile)
        val profileName: TextView = itemView.findViewById(R.id.textViewName)
        val tags:FlexboxLayout = itemView.findViewById(R.id.tag_layout)
        val menyesuaikantexbox:LinearLayout = itemView.findViewById(R.id.menyesuaikanpanjangflexbox)
    }

    inner class ProfileViewHolderLast(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.imageViewProfile)
        val profileName: TextView = itemView.findViewById(R.id.textViewName)
        val tags:FlexboxLayout = itemView.findViewById(R.id.tag_layout)
        val menyesuaikantexbox:LinearLayout = itemView.findViewById(R.id.menyesuaikanpanjangflexbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            VIEW_TYPE_LAST ->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile_home_konselor_2, parent, false)
                ProfileViewHolderLast(view)
            }
            else ->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile_home_konselor, parent, false)
                ProfileViewHolder(view)
            }
        }
    }

    // Mengatur apakah tipe yang paling terakhir untuk tampilan peer konselor (foto beramai-ramai)
    companion object {
        private const val VIEW_TYPE_NORMAL = 0
        private const val VIEW_TYPE_LAST = 1
    }

    override fun getItemViewType(position: Int): Int {
        // Logika untuk menentukan tipe tampilan
        return if (position == profileList.size - 1) VIEW_TYPE_LAST else VIEW_TYPE_NORMAL
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val profile = profileList[position]

        when (holder) {
            is ProfileViewHolder -> {
                holder.profileName.text = profile.name
                holder.tags.removeAllViews()

                // Mengecek apakah hanya ada satu tag
                val isSingleTag = profile.tag.size == 1

                for (text in profile.tag) {
                    val textView = TextView(holder.tags.context).apply {
                        // Menentukan layoutParams berdasarkan jumlah tag
                        val layoutParams = if (isSingleTag) {
                            // Setel lebar ke MATCH_PARENT jika hanya ada satu kata
                            FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.MATCH_PARENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
                        } else {
                            // Setel lebar ke WRAP_CONTENT jika ada lebih dari satu kata
                            FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
                        }

                        // Atur margin antar item
                        layoutParams.setMargins(8, 8, 8, 8)

                        this.layoutParams = layoutParams
                        setText(text)
                        textSize = 12f
                        background = ContextCompat.getDrawable(context, R.drawable.border_radius_2)
                        backgroundTintList = ContextCompat.getColorStateList(context, R.color.ijofigmapudar)
                        setPadding(16, 8, 16, 8)
                        gravity = Gravity.CENTER
                    }
                    holder.tags.addView(textView)
                }

                holder.profileImage.setImageResource(profile.imageResourceId)

                // Ketika klik tampilan konselor akan di arahkan ke Class deskripsi konselor
                holder.menyesuaikantexbox.setOnClickListener {
                    val intent = Intent(holder.itemView.context, DeskripsiKonselorHome::class.java)
                    intent.putExtra("namaKonselor", profile.name)
                    intent.putExtra("fotoKonselor", profile.imageResourceId)
                    intent.putExtra("descsingkatKonselor", profile.deskripsi)
                    intent.putExtra("tags", ArrayList(profile.tag))
                    holder.itemView.context.startActivity(intent)
                }
            }
            is ProfileViewHolderLast -> {
                holder.profileName.text = profile.name
                holder.tags.removeAllViews()

                // Mengecek apakah hanya ada satu tag
                val isSingleTag = profile.tag.size == 1

                for (text in profile.tag) {
                    val textView = TextView(holder.tags.context).apply {
                        // Menentukan layoutParams berdasarkan jumlah tag
                        val layoutParams = if (isSingleTag) {
                            // Setel lebar ke MATCH_PARENT jika hanya ada satu kata
                            FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.MATCH_PARENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
                        } else {
                            // Setel lebar ke WRAP_CONTENT jika ada lebih dari satu kata
                            FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
                        }

                        // Atur margin antar item
                        layoutParams.setMargins(8, 8, 8, 8)

                        this.layoutParams = layoutParams
                        setText(text)
                        textSize = 12f
                        background = ContextCompat.getDrawable(context, R.drawable.border_radius_2)
                        backgroundTintList = ContextCompat.getColorStateList(context, R.color.ijofigmapudar)
                        setPadding(16, 8, 16, 8)
                        gravity = Gravity.CENTER
                    }
                    holder.tags.addView(textView)
                }

                holder.profileImage.setImageResource(profile.imageResourceId)
            }
        }
    }

    override fun getItemCount(): Int = profileList.size
}



