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
 * @see http://LanDenLabs.com/
 */

package com.landenlabs.all_remember;

import static utils.SysUtil.getNavController;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Main activity for All-Remember which demonstrates cell expansion and dividers.
 */
@SuppressWarnings("Convert2Lambda")
public class MainActivity extends AppCompatActivity {

    static final String TAG = "UiTest";

    androidx.appcompat.widget.Toolbar toolbar;
    NavController navSideController;
    NavController navBotController;
    String intentAction = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navSideController = getNavController(this, R.id.sideNavFragment);
        // navSideController = Navigation.findNavController(this, R.id.sideNavFragment);

        // Set up ActionBar
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationUI.setupActionBarWithNavController(this, navSideController, drawerLayout);

        // Set up side navigation draw menu
        NavigationView navSideView = findViewById(R.id.navSideView);
        @SuppressLint("RestrictedApi")
        NavigationMenuView navMenuView = (NavigationMenuView) navSideView.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));
        NavigationUI.setupWithNavController(navSideView, navSideController);

        // Setup shortcuts for Bottom Nav items.
        navSideView.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup bottomNavigationView = findViewById(R.id.bottomNavigation);
                if (bottomNavigationView != null) {
                    addOrExecuteShortcut(bottomNavigationView);
                }
            }
        });
        intentAction = getIntent() != null ? getIntent().getAction() : null;
        Log.d(TAG, String.format(Locale.US, "indent action=%s", intentAction));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate  menu, adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_side, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        return NavigationUI.navigateUp(
                Navigation.findNavController(this, R.id.sideNavFragment), drawerLayout);
    }

    /**
     * Add or execute short cut
     */
    @SuppressWarnings("ConstantConditions")
    private void addOrExecuteShortcut(@NonNull ViewGroup bottomNavGroup) {
        if (android.os.Build.VERSION.SDK_INT <android.os.Build.VERSION_CODES.N_MR1)
            return;

        Map<String, MenuItem> menus;
        Menu menu;

        if (bottomNavGroup instanceof BottomNavigationView) {
            BottomNavigationView bottomNavigationView = (BottomNavigationView)bottomNavGroup;
            menu = bottomNavigationView.getMenu();
        } else {
            PopupMenu popupMenu = new PopupMenu(this, bottomNavGroup.getRootView());
            getMenuInflater().inflate(R.menu.menu_bottom, popupMenu.getMenu());
            menu = popupMenu.getMenu();
        }

        int menuSize = menu.size();
        menus = new HashMap<>(menuSize);
        for (int menuIdx = 0; menuIdx < menuSize; menuIdx++) {
            MenuItem menuItem = menu.getItem(menuIdx);
            menus.put(menuItem.getTitle().toString(), menuItem);
        }

        ShortcutManager shortcutManager = this.getSystemService(ShortcutManager.class);
        navBotController = Navigation.findNavController(this, R.id.bottomNavFragment);
        Iterator<NavDestination> navIT =  navBotController.getGraph().iterator();
        List<ShortcutInfo> shortcutList = new ArrayList<>();
        while (navIT.hasNext()) {
            NavDestination navDestination = navIT.next();
            MenuItem menuItem = menus.get(navDestination.getLabel());
            if (menuItem != null) {
                Intent newTaskIntent = new Intent(this, MainActivity.class);
                newTaskIntent.setAction(navDestination.getLabel().toString());
                newTaskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                ShortcutInfo postShortcut
                        = new ShortcutInfo.Builder(this, navDestination.getLabel().toString())
                        .setShortLabel(navDestination.getLabel())
                        .setLongLabel(navDestination.getLabel())
                        .setIcon(Icon.createWithResource(this, R.drawable.tab_gridview))
                        .setIntent(newTaskIntent)
                        .build();
                shortcutList.add(postShortcut);

                if (navDestination.getLabel().equals(intentAction)) {
                    // Execute shortcut
                    navBotController.navigate(navDestination.getId());
                }
            }
        }

        shortcutManager.addDynamicShortcuts(shortcutList);
    }
}
