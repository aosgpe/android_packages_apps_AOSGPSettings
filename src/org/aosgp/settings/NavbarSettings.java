/*
 * Copyright (C) 2019 AOSGP E
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

package org.aosgp.settings;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.ContentResolver;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManagerGlobal;
import android.view.IWindowManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;

import java.util.List;
import java.util.ArrayList;

/**
 * Settings screen for Navbar.
 */
public class NavbarSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener, Indexable {


    private SwitchPreference mNavbarToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.navbar_settings);
        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();

        mNavbarToggle = (SwitchPreference) findPreference("navigation_bar_enabled");
        boolean enabled = Settings.Secure.getIntForUser(
                resolver, Settings.Secure.NAVIGATION_BAR_VISIBLE,
                getActivity().getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar) ? 1 : 0,
                UserHandle.USER_CURRENT) == 0;
        mNavbarToggle.setChecked(enabled);
        mNavbarToggle.setOnPreferenceChangeListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNavbarToggle) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putIntForUser(getActivity().getContentResolver(),
                    Settings.Secure.NAVIGATION_BAR_VISIBLE, !value ? 1 : 0,
                    UserHandle.USER_CURRENT);
	    Settings.System.putIntForUser(getActivity().getContentResolver(),
            	    Settings.System.USE_BOTTOM_GESTURE_NAVIGATION, value ? 1 : 0,
		    UserHandle.USER_CURRENT);
            mNavbarToggle.setChecked(!value);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.NIGHT_DISPLAY_SETTINGS;
    }

    /**
     * For search
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.navbar_settings;
                    result.add(sir);

                    return result;
                }
            };
}
