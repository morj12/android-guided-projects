package com.example.wildrunning.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.wildrunning.R
import com.example.wildrunning.activity.LoginActivity.Companion.userEmail
import com.example.wildrunning.activity.SportType
import com.example.wildrunning.data.Records
import com.example.wildrunning.data.Run
import com.example.wildrunning.utils.Animator
import com.example.wildrunning.utils.FirestoreClient
import com.example.wildrunning.utils.MyLayoutManager
import com.example.wildrunning.utils.StorageClient

class RunAdapter(private val runsList: MutableList<Run>) :
    RecyclerView.Adapter<RunAdapter.MyViewHolder>() {

    private lateinit var context: Context
    private var minimized = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.card_run, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val run = runsList[position]

        MyLayoutManager.setLinearLayoutHeight(holder.lyDataRunBody, 0)
        holder.lyDataRunBodyContainer.translationY = -200f
        holder.ivHeaderOpenClose.setOnClickListener {
            if (minimized) {
                MyLayoutManager.setLinearLayoutHeight(holder.lyDataRunBody, 600)
                Animator.animateViewOfFloat(holder.lyDataRunBodyContainer, "translationY", 0f, 300L)
                holder.ivHeaderOpenClose.rotation = 180f
                minimized = false
            } else {
                holder.lyDataRunBodyContainer.translationY = -200f
                MyLayoutManager.setLinearLayoutHeight(holder.lyDataRunBody, 0)
                holder.ivHeaderOpenClose.rotation = 0f
                minimized = true
            }
        }

        val day = run.date?.subSequence(8, 10)
        val month = run.date?.subSequence(5, 7)
        val year = run.date?.subSequence(0, 4)
        val date = context.getString(R.string.datePlaceHolder, day, month, year)
        holder.tvDate.text = date
        holder.tvHeaderDate.text = date

        holder.tvStartTime.text = run.startTime?.subSequence(0, 5)
        holder.tvDurationRun.text = run.duration
        holder.tvHeaderDuration.text = run.duration!!.subSequence(0, 5).toString() + "HH"

        if (run.intervalMode != null) {
            var details = "${run.intervalDuration}min. ("
            details += "${run.runningTime}/${run.walkingTime})"
            holder.tvIntervalRun.text = details
        } else
            MyLayoutManager.setLinearLayoutHeight(holder.lyIntervalRun, 0)

        holder.tvDistanceRun.text = run.distance.toString()
        holder.tvHeaderDistance.text = run.distance.toString() + "KM"

        holder.tvMaxUnevennessRun.text = run.maxAltitude.toString()
        holder.tvMinUnevennessRun.text = run.minAltitude.toString()

        holder.tvAvgSpeedRun.text = run.avgSpeed.toString()
        holder.tvHeaderAvgSpeed.text = run.avgSpeed.toString() + "KM/H"
        holder.tvMaxSpeedRun.text = run.maxSpeed.toString()

        holder.ivMedalDistance.setImageResource(0)
        holder.ivMedalAvgSpeed.setImageResource(0)
        holder.ivMedalMaxSpeed.setImageResource(0)
        holder.ivHeaderMedalDistance.setImageResource(0)
        holder.ivHeaderMedalAvgSpeed.setImageResource(0)
        holder.ivHeaderMedalMaxSpeed.setImageResource(0)

        when (Records.RecordMedal.valueOf(run.medalDistance!!)) {
            Records.RecordMedal.GOLD -> {
                holder.ivMedalDistance.setImageResource(R.drawable.medalgold)
                holder.ivHeaderMedalDistance.setImageResource(R.drawable.medalgold)
                holder.tvMedalDistanceTitle.setText(R.string.CardMedalDistance)
            }
            Records.RecordMedal.SILVER -> {
                holder.ivMedalDistance.setImageResource(R.drawable.medalsilver)
                holder.ivHeaderMedalDistance.setImageResource(R.drawable.medalsilver)
                holder.tvMedalDistanceTitle.setText(R.string.CardMedalDistance)
            }
            Records.RecordMedal.BRONZE -> {
                holder.ivMedalDistance.setImageResource(R.drawable.medalbronze)
                holder.ivHeaderMedalDistance.setImageResource(R.drawable.medalbronze)
                holder.tvMedalDistanceTitle.setText(R.string.CardMedalDistance)
            }
            Records.RecordMedal.NONE -> {}
        }

        when (Records.RecordMedal.valueOf(run.medalAvgSpeed!!)) {
            Records.RecordMedal.GOLD -> {
                holder.ivMedalAvgSpeed.setImageResource(R.drawable.medalgold)
                holder.ivHeaderMedalAvgSpeed.setImageResource(R.drawable.medalgold)
                holder.tvMedalAvgSpeedTitle.setText(R.string.CardMedalAvgSpeed)
            }
            Records.RecordMedal.SILVER -> {
                holder.ivMedalAvgSpeed.setImageResource(R.drawable.medalsilver)
                holder.ivHeaderMedalAvgSpeed.setImageResource(R.drawable.medalsilver)
                holder.tvMedalAvgSpeedTitle.setText(R.string.CardMedalAvgSpeed)
            }
            Records.RecordMedal.BRONZE -> {
                holder.ivMedalAvgSpeed.setImageResource(R.drawable.medalbronze)
                holder.ivHeaderMedalAvgSpeed.setImageResource(R.drawable.medalbronze)
                holder.tvMedalAvgSpeedTitle.setText(R.string.CardMedalAvgSpeed)
            }
            Records.RecordMedal.NONE -> {}
        }

        when (Records.RecordMedal.valueOf(run.medalMaxSpeed!!)) {
            Records.RecordMedal.GOLD -> {
                holder.ivMedalMaxSpeed.setImageResource(R.drawable.medalgold)
                holder.ivHeaderMedalMaxSpeed.setImageResource(R.drawable.medalgold)
                holder.tvMedalMaxSpeedTitle.setText(R.string.CardMedalMaxSpeed)
            }
            Records.RecordMedal.SILVER -> {
                holder.ivMedalMaxSpeed.setImageResource(R.drawable.medalsilver)
                holder.ivHeaderMedalMaxSpeed.setImageResource(R.drawable.medalsilver)
                holder.tvMedalMaxSpeedTitle.setText(R.string.CardMedalMaxSpeed)
            }
            Records.RecordMedal.BRONZE -> {
                holder.ivMedalMaxSpeed.setImageResource(R.drawable.medalbronze)
                holder.ivHeaderMedalMaxSpeed.setImageResource(R.drawable.medalbronze)
                holder.tvMedalMaxSpeedTitle.setText(R.string.CardMedalMaxSpeed)
            }
            Records.RecordMedal.NONE -> {}
        }

        holder.ivSportSelected.setImageResource(
            when (SportType.valueOf(run.sport!!)) {
                SportType.Running -> R.mipmap.running
                SportType.RollerSkate -> R.mipmap.rollerskate
                SportType.Bike -> R.mipmap.bike
            }
        )

        if (run.lastImage != "") {
            StorageClient.getImage(run, holder, ::failureCallback)
        }

        holder.tvDelete.setOnClickListener {
            val id = (userEmail + run.date + run.startTime)
                .replace("/", "")
                .replace(":", "")

            val currentRun = Run().apply {
                distance = run.distance
                avgSpeed = run.avgSpeed
                maxSpeed = run.maxSpeed
                duration = run.duration
                locationEnabled = run.locationEnabled
                this.date = run.date
                startTime = run.startTime
                user = run.user
                sport = run.sport
            }

            FirestoreClient.deleteRun(
                id,
                SportType.valueOf(currentRun.sport!!),
                holder.lyDataRunHeader,
                currentRun
            )

            runsList.removeAt(position)
            notifyItemRemoved(position)
        }

    }

    private fun failureCallback(e: Exception) {
        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount() = runsList.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lyDataRunHeader: LinearLayout = itemView.findViewById(R.id.lyDataRunHeader)
        val tvHeaderDate: TextView = itemView.findViewById(R.id.tvHeaderDate)
        val tvHeaderDuration: TextView = itemView.findViewById(R.id.tvHeaderDuration)
        val tvHeaderDistance: TextView = itemView.findViewById(R.id.tvHeaderDistance)
        val tvHeaderAvgSpeed: TextView = itemView.findViewById(R.id.tvHeaderAvgSpeed)
        val ivHeaderMedalDistance: ImageView = itemView.findViewById(R.id.ivHeaderMedalDistance)
        val ivHeaderMedalAvgSpeed: ImageView = itemView.findViewById(R.id.ivHeaderMedalAvgSpeed)
        val ivHeaderMedalMaxSpeed: ImageView = itemView.findViewById(R.id.ivHeaderMedalMaxSpeed)
        val ivHeaderOpenClose: ImageView = itemView.findViewById(R.id.ivHeaderOpenClose)

        val lyDataRunBody: LinearLayout = itemView.findViewById(R.id.lyDataRunBody)
        val lyDataRunBodyContainer: LinearLayout =
            itemView.findViewById(R.id.lyDataRunBodyContainer)

        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvStartTime: TextView = itemView.findViewById(R.id.tvStartTime)

        val tvDurationRun: TextView = itemView.findViewById(R.id.tvDurationRun)
        val lyIntervalRun: LinearLayout = itemView.findViewById(R.id.lyIntervalRun)
        val tvIntervalRun: TextView = itemView.findViewById(R.id.tvIntervalRun)

        val tvDistanceRun: TextView = itemView.findViewById(R.id.tvDistanceRun)
        val tvMaxUnevennessRun: TextView = itemView.findViewById(R.id.tvMaxUnevennessRun)
        val tvMinUnevennessRun: TextView = itemView.findViewById(R.id.tvMinUnevennessRun)

        val tvAvgSpeedRun: TextView = itemView.findViewById(R.id.tvAvgSpeedRun)
        val tvMaxSpeedRun: TextView = itemView.findViewById(R.id.tvMaxSpeedRun)

        val ivMedalDistance: ImageView = itemView.findViewById(R.id.ivMedalDistance)
        val tvMedalDistanceTitle: TextView = itemView.findViewById(R.id.tvMedalDistanceTitle)
        val ivMedalAvgSpeed: ImageView = itemView.findViewById(R.id.ivMedalAvgSpeed)
        val tvMedalAvgSpeedTitle: TextView = itemView.findViewById(R.id.tvMedalAvgSpeedTitle)
        val ivMedalMaxSpeed: ImageView = itemView.findViewById(R.id.ivMedalMaxSpeed)
        val tvMedalMaxSpeedTitle: TextView = itemView.findViewById(R.id.tvMedalMaxSpeedTitle)

        val ivSportSelected: ImageView = itemView.findViewById(R.id.ivSportSelected)

        val ivPicture: ImageView = itemView.findViewById(R.id.ivPicture)
        val lyPicture: LinearLayout = itemView.findViewById(R.id.lyPicture)
        val tvDelete: TextView = itemView.findViewById(R.id.tvDelete)
    }
}