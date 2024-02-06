package com.example.vibe.utils

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.example.vibe.R

fun setMenuToFavoritesFragment(
    fragmentActivity: FragmentActivity,
    viewLifecycleOwner: LifecycleOwner,
    searchSong: (SearchView) -> Unit)
{
    fragmentActivity.addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.options_menu_search, menu)

            val searchItem = menu.findItem(R.id.search_text)
            val searchView = searchItem?.actionView as SearchView

            searchView.isSubmitButtonEnabled = true
            searchSong(searchView)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return false
        }

    }, viewLifecycleOwner)
}