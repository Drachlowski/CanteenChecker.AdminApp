package com.example.canteenchecker.adminapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.canteenchecker.adminapp.CanteenCheckerApplication
import com.example.canteenchecker.adminapp.R
import com.example.canteenchecker.adminapp.api.AdminApiFactory
import com.example.canteenchecker.adminapp.core.CanteenReview
import com.example.canteenchecker.adminapp.databinding.ActivityReviewsBinding
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

class ReviewsActivity : AppCompatActivity() {

    companion object {
        fun intent(context: Context) = Intent(context, ReviewsActivity::class.java)
    }

    private lateinit var binding: ActivityReviewsBinding

    private val reviewsAdapter = ReviewsAdapter(this, lifecycleScope)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Reviews"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        binding.rcvReviews.adapter = reviewsAdapter

        binding.srlSwipeRefreshLayout.setOnRefreshListener { updateReviews() }

        updateReviews()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun updateReviews() = lifecycleScope.launch {
        var authenticationToken = (application as CanteenCheckerApplication).authenticationToken
        if (authenticationToken == null) {
            authenticationToken = ""
        }
        binding.srlSwipeRefreshLayout.isRefreshing = true

        reviewsAdapter.displayReviews(
            AdminApiFactory
                .createAdminApi()
                .getCanteenReviews(authenticationToken)
                .getOrElse {
                    Toast.makeText(this@ReviewsActivity, "Cannot load reviews - please try again", Toast.LENGTH_SHORT).show()
                    emptyList()
                }
        )
        binding.srlSwipeRefreshLayout.isRefreshing = false

    }


    private class ReviewsAdapter(
        private val context: Context,
        private val lifecycleScope: LifecycleCoroutineScope)
        : RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

        private class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val txvCreator: TextView = itemView.findViewById(R.id.txvCreator)
            val rtbRating: RatingBar = itemView.findViewById(R.id.rtbRating)
            val txvCreationDate: TextView = itemView.findViewById(R.id.txvCreationDate)
            val txvRemark: TextView = itemView.findViewById(R.id.txvRemark)
            val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        }

        private var reviews = emptyList<CanteenReview>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.run {
            val review = reviews[position]
            txvCreator.text = review.creator
            rtbRating.rating = review.rating.toFloat()
            txvCreationDate.text = LocalDateTime.parse(
                review.creationDate,
                DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                    .optionalStart()
                    .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                    .optionalEnd()
                    .toFormatter()
            ).format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            )
            txvRemark.text = review.remark

            btnDelete.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Delete Review")
                    .setMessage("Do you really want to delete this review?")
                    .setPositiveButton("Yes") { dialog, _ ->
                            deleteReview(review)
                            dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }

        override fun getItemCount(): Int = reviews.size

        fun displayReviews(reviews: List<CanteenReview>) {
            this.reviews = reviews
            notifyDataSetChanged()
        }

        private fun deleteReview(review: CanteenReview) {
            lifecycleScope.launch {
                val authenticationToken = (context.applicationContext as CanteenCheckerApplication).authenticationToken
                if (authenticationToken.isNullOrEmpty()) {
                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                AdminApiFactory.createAdminApi().deleteReview(authenticationToken, review.id)
                    .onSuccess {
                        Toast.makeText(context, "Review deleted", Toast.LENGTH_SHORT).show()
                        reviews = reviews.filter { it.id != review.id }
                        notifyDataSetChanged()
                    }
                    .onFailure {
                        Toast.makeText(context, "Failed to delete review", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

}