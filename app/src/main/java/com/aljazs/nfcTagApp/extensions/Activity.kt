package com.aljazs.nfcTagApp.extensions

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.aljazs.nfcTagApp.R
import com.aljazs.nfcTagApp.common.Animation

fun AppCompatActivity.extReplaceFragmentWithAnimation(
    fragmentClass: Fragment, @Animation.AnimationType animation: Int,
    @IdRes frameId: Int,
    addToBackStack: Boolean = false,
    popBackStackInclusive: Boolean = false
) {

    supportFragmentManager.extTransaction {
        /** Clear stack and return user to first added fragment(Dashboard). Sample: A -> B -> C-> User press back button, goes directly to fragment A!
        If we would write supportFragmentManager.popBackStack(), it would behave like:  A -> B -> C->, user press back button, goes like C -> to B, back button -> to A!
        In other words: supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE) simple clears all back stack transactions saved in manager!
        And because we did not added first fragment to true(back stack), it is not cleared from transactions, and manager delegate user to it!
        Again, if we debug, it can be seen that back stack does not "save" fragments but instead it saves all transactions(A->B, B->A A-B-C...) */
        if (popBackStackInclusive) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        when (animation) {
            Animation.LEFT -> setCustomAnimations(
                R.anim.slide_from_left, R.anim.slide_to_right,
                R.anim.slide_from_left, R.anim.slide_to_right
            )
            Animation.RIGHT -> setCustomAnimations(
                R.anim.slide_from_right, R.anim.slide_to_left,
                R.anim.slide_from_right, R.anim.slide_to_left
            )
        }
        var fragment = supportFragmentManager.findFragmentByTag(fragmentClass.TAG)
        if (fragment == null) {
            fragment = fragmentClass
            if (addToBackStack) {
                addToBackStack(fragmentClass.TAG)
            }
        }
        /** For behavior compatibility, the reordering flag is not enabled by default.
         * It is required, however, to allow FragmentManager to properly execute your FragmentTransaction,
         * particularly when it operates on the back stack and runs animations and transitions.
         * Enabling the flag ensures that if multiple transactions are executed together,
         * any intermediate fragments (i.e. ones that are added and then immediately replaced)
         * do not go through lifecycle changes or have their animations or transitions executed.
         * Note that this flag affects both the initial execution of the transaction and reversing the transaction with popBackStack(). */

        setReorderingAllowed(true)
        replace(frameId, fragment, fragmentClass.TAG)
    }
}