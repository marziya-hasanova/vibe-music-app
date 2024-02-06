package com.example.vibe.utils

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.example.vibe.R
import com.example.vibe.presentation.ui.viewmodels.MusicViewModel

fun setMenuHomeFragment(
    fragmentActivity: FragmentActivity,
    musicViewModel: MusicViewModel,
    viewLifecycleOwner: LifecycleOwner)
{
    fragmentActivity.addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.options_menu_search, menu)

            val searchItem = menu.findItem(R.id.search_text)
            val searchView = searchItem?.actionView as SearchView

            searchView.isSubmitButtonEnabled = true
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    musicViewModel.getResults(query.toString())
                    searchView.clearFocus()
                    searchView.onActionViewCollapsed()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        }


        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return false
        }

    }, viewLifecycleOwner)
}