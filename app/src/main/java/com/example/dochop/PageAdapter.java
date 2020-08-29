package com.example.dochop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Represents the Fragment Page Adapter implementation that represents each page as a Fragment that
 * is persistently kept in the fragment manager as long as the user can return to the page.
 * PageAdapter inherits from FragmentPagerAdapter.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class PageAdapter extends FragmentPagerAdapter {

    /**
     * Number of tabs in the tabbed activity (2 in this case).
     */
    private int numberOfTabs;

    /**
     * Bundle object that carries the details of the user that is logged in.
     */
    Bundle bundle;

    /**
     * Constructor of the PageAdapter.
     *
     * @param fm           Object which is used to create transactions for adding, removing or
     *                     replacing fragments.
     * @param numberOfTabs Int that contains the number of tabs (2 in this case).
     * @param bundle       Bundle object that carries the details of the user that is logged in.
     */
    public PageAdapter(FragmentManager fm, int numberOfTabs, Bundle bundle) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
        this.bundle = bundle;
    }

    /**
     * Returns the selected fragment based on what the user has tabbed ('DOCTOR' or 'AMBULANCE").
     *
     * @param position Position on the tab that the user has tapped on.
     * @return Returns either the patientGPHistory or PatientAmbulanceHistory fragment, based on
     * user selection.
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PatientGpHistory patientGPHistory = new PatientGpHistory();
                patientGPHistory.setArguments(bundle);
                return patientGPHistory;
            case 1:
                PatientAmbulanceHistory patientAmbulanceHistory = new PatientAmbulanceHistory();
                patientAmbulanceHistory.setArguments(bundle);
                return patientAmbulanceHistory;
            default:
                return null;
        }
    }

    /**
     * Getter method that returns the number of tabs.
     *
     * @return The number of tabs.
     */
    @Override
    public int getCount() {
        return numberOfTabs;
    }

    /**
     * Called when the host view is attempting to determine if an item's position has changed.
     *
     * @param object Object representing an item, previously returned by a call to
     *               instantiateItem(View, int).
     * @return POSITION_NONE, indicating that the item is no longer present in the adapter.
     */
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
