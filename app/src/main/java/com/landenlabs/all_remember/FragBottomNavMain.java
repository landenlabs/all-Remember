/*
 * Copyright (c) 2020 Dennis Lang (LanDen Labs) landenlabs@gmail.com
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Dennis Lang
 * @see https://LanDenLabs.com/
 */

package com.landenlabs.all_remember;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.TextViewCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple [Fragment] subclass.
 * @noinspection FieldCanBeLocal
 */
public class FragBottomNavMain extends FragBottomNavBase
         implements  View.OnClickListener  {

    private ViewGroup root;

    private final boolean useRadioBottomBar = true;
    private final int LAYOUT_RES = useRadioBottomBar ? R.layout.frag_bottom_rg_nav_main : R.layout.frag_bottom_nav_main;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        root = (ViewGroup)inflater.inflate(LAYOUT_RES, container, false);
        // NavController navSideController = Navigation.findNavController(requireActivity(), R.id.sideNavFragment);
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (useRadioBottomBar) {
            // Setup custom bottom navigation
        
            RadioGroup navBar = root.findViewById(R.id.tabHolder);
            PopupMenu popupMenu = new PopupMenu(requireActivity(), root);
            requireActivity().getMenuInflater().inflate(R.menu.menu_bottom, popupMenu.getMenu());
            addTabBar(navBar, popupMenu.getMenu());
           
        } else {
            // Setup bottom navigation
            // NavGraph bottomNavGraph = navController.getNavInflater().inflate(R.navigation.nav_bottom);
            NavController bottomNavController = Navigation.findNavController(requireActivity(), R.id.bottomNavFragment);
            BottomNavigationView bottomNavView = view.findViewById(R.id.bottomNavigation);
            NavigationUI.setupWithNavController(bottomNavView, bottomNavController);
        }
    }

  
    @Override
    public void onClick(View view) {
        changePage(view.getId());
    }

    private void changePage(int id) {
        NavController navBotController = Navigation.findNavController(requireActivity(), R.id.bottomNavFragment);
        navBotController.navigate(id);
    }

    private final ColorStateList colorGrey = new ColorStateList(
            new int[][]{ new int[]{}},
            new int[]{Color.GRAY }
    	);

    // ---------------------------------------------------------------------------------------------
    // Alternate bottom nav bar

    private void addTabBar(@NonNull RadioGroup tabHolder, @NonNull Menu menu) {
        tabHolder.removeAllViews();
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;

        for (int idx = 0; idx < menu.size(); idx++) {
            RadioButton button = (RadioButton)getLayoutInflater().inflate(R.layout.tab_btn, root, false);
            MenuItem item = menu.getItem(idx);
            Drawable tabBtnIcon = null;
            try {
                tabBtnIcon = item.getIcon();
            } catch (Exception ex) {
                Log.e(TAG, String.format("Failed to load icon for tab# %d, exception=%s",
                        idx, ex.getMessage()));
            }
            if (tabBtnIcon != null) {
                button.setId(item.getItemId());
                button.setText(item.getTitle());
                // button.setCompoundDrawableTintList(colorGrey);
                TextViewCompat.setCompoundDrawableTintList(button, colorGrey);
                button.setCompoundDrawablesWithIntrinsicBounds(null, tabBtnIcon, null, null);
                button.setOnClickListener(this);
                tabHolder.addView(button, lp);
            }
        }
    }
}
