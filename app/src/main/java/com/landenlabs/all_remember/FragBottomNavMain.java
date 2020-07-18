package com.landenlabs.all_remember;

/*
 * Copyright (C) 2019 Dennis Lang (landenlabs@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple [Fragment] subclass.
 */
public class FragBottomNavMain extends FragBottomNavBase
        /* implements  View.OnClickListener */ {

    private View root;
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.frag_bottom_nav_main, container, false);
        navController = Navigation.findNavController(requireActivity(), R.id.sideNavFragment);
        NavGraph bottomNavGraph = navController.getNavInflater().inflate(R.navigation.nav_bottom);
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (false) {
            // Setup custom bottom navigation
            /*
            RadioGroup navBar = root.findViewById(R.id.tabHolder);
            PopupMenu popupMenu = new PopupMenu(getContext(), null);
            getActivitySafe().getMenuInflater().inflate(R.menu.menu_bottom, popupMenu.getMenu());
            addTabBar(navBar, popupMenu.getMenu());
             */
        } else {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.bottomNavFragment);
            BottomNavigationView bottomNavigation = view.findViewById(R.id.bottomNavigation);
            NavigationUI.setupWithNavController(bottomNavigation, navController);
        }
    }

    /*
    @Override
    public void onClick(View view) {
        changePage(view.getId());
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean changePage(int id) {
        switch (id) {
            case R.id.bottomNavFragmentOne:
                navController.navigate(R.id.bottomNavFragmentOne);
                return true;
            case R.id.bottomNavFragmentTwo:
                navController.navigate(R.id.bottomNavFragmentTwo);
                return true;
            case R.id.bottomNavFragmentThree:
                navController.navigate(R.id.bottomNavFragmentThree);
                return true;
        }

        return false;
    }

    // ---------------------------------------------------------------------------------------------
    // Alternate bottom nav bar

    void addTabBar(@NonNull RadioGroup tabHolder, @NonNull Menu menu) {
        Context context = tabHolder.getContext();
        tabHolder.removeAllViews();
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;

        for (int idx = 0; idx < menu.size(); idx++) {
            RadioButton button = (RadioButton)getLayoutInflater().inflate(R.layout.tab_btn, null);
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
                if (tabBtnIcon != null) {
                    button.setCompoundDrawablesWithIntrinsicBounds(null, tabBtnIcon, null, null);
                }
                button.setOnClickListener(this);
                tabHolder.addView(button, lp);
            }
        }
    }
     */
}
