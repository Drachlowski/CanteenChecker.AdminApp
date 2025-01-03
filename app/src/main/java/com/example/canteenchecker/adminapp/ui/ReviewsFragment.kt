package com.example.canteenchecker.adminapp.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class ReviewsFragment : Fragment() {

    companion object {

        fun FragmentTransaction.addReviewsFragment(@IdRes containerViewId: Int) : FragmentTransaction {
            val bundle = Bundle()
            return add(containerViewId, ReviewsFragment::class.java, bundle)
        }
    }
}