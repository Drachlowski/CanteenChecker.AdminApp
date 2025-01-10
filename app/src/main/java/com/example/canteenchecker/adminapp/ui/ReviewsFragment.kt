package com.example.canteenchecker.adminapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.canteenchecker.adminapp.CanteenCheckerApplication
import com.example.canteenchecker.adminapp.api.AdminApiFactory
import com.example.canteenchecker.adminapp.databinding.FragmentReviewsBinding
import kotlinx.coroutines.launch

class ReviewsFragment : Fragment() {

    companion object {
        fun FragmentTransaction.addReviewsFragment(@IdRes containerViewId: Int) : FragmentTransaction {
            return add(containerViewId, ReviewsFragment::class.java, null)
        }
    }

    private lateinit var binding: FragmentReviewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddReview.setOnClickListener{
            startActivity(ReviewsActivity.intent(requireActivity()))
        }

        updateReviews()
    }

    fun updateReviews() = lifecycleScope.launch {
        val authenticationToken = (requireActivity().application as CanteenCheckerApplication).authenticationToken?: ""

        AdminApiFactory.createAdminApi().getCanteenStatistics(authenticationToken)
            .onFailure {
                binding.txvAverageRating.text = null
                binding.rtbAverageRating.rating = 0f
                binding.txvTotalRatings.text = null
                binding.prbRatingsOne.max = 1
                binding.prbRatingsOne.progress = 0
                binding.prbRatingsTwo.max = 1
                binding.prbRatingsTwo.progress = 0
                binding.prbRatingsThree.max = 1
                binding.prbRatingsThree.progress = 0
                binding.prbRatingsFour.max = 1
                binding.prbRatingsFour.progress = 0
                binding.prbRatingsFive.max = 1
                binding.prbRatingsFive.progress = 0
            }
            .onSuccess {
                binding.txvAverageRating.text = String.format("%.1f", it.averageRating)
                binding.rtbAverageRating.rating = it.averageRating
                binding.txvTotalRatings.text = it.totalRatings.toString()
                binding.prbRatingsOne.max = it.totalRatings
                binding.prbRatingsOne.progress = it.countOneStar
                binding.prbRatingsTwo.max = it.totalRatings
                binding.prbRatingsTwo.progress = it.countTwoStars
                binding.prbRatingsThree.max = it.totalRatings
                binding.prbRatingsThree.progress = it.countThreeStars
                binding.prbRatingsFour.max = it.totalRatings
                binding.prbRatingsFour.progress = it.countFourStars
                binding.prbRatingsFive.max = it.totalRatings
                binding.prbRatingsFive.progress = it.countFiveStars
            }
    }
}