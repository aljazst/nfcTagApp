package com.aljazs.nfcTagApp.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.common.Animation

inline fun FragmentManager.extTransaction(actionBody: FragmentTransaction.() -> Unit) {
    beginTransaction().apply(actionBody).commit()
}

fun FragmentManager.replace(
    fragmentClass: Fragment,
    @Animation.AnimationType animation: Int,
    containerId: Int,
    addToBackStack: Boolean = false
) {
    if (!isDestroyed) {
        extTransaction {
            when (animation) {
                Animation.LEFT -> setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_left, R.anim.slide_to_right)
                Animation.RIGHT -> setCustomAnimations(
                    R.anim.slide_from_right,
                    R.anim.slide_to_left,
                    R.anim.slide_from_right,
                    R.anim.slide_to_left
                )
            }

            var fragment = findFragmentByTag(fragmentClass.TAG)
            if (fragment == null) {
                fragment = fragmentClass
                if (addToBackStack) {
                    addToBackStack(fragmentClass.TAG)
                }
            }
            setReorderingAllowed(true)
            replace(containerId, fragment, fragmentClass.TAG)
        }
    }

    /* if (!isDestroyed) {
         popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
         extTransaction {
             when(animation) {
                 Animation.LEFT -> setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right)
                 Animation.RIGHT -> setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left)
             }
             replace(containerId, fragment, fragment.TAG)

         }
     }*/
}