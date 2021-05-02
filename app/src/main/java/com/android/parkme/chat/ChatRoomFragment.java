package com.android.parkme.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.parkme.R;
import com.google.android.material.tabs.TabLayout;

public class ChatRoomFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean hasPermissions;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_view_pager, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = v.findViewById(R.id.tab);
        viewPager = v.findViewById(R.id.myviewpager);

        setupViewpPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewpPager(ViewPager viewPager) {
        ViewPagerAdapter viewpagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putBoolean("permission-wifi", hasPermissions);
        RaisedFragment raisedFragment = new RaisedFragment();
        ReceivedFragment receivedFragment = new ReceivedFragment();
        viewpagerAdapter.addfragment(raisedFragment, "Raised By Me");
        viewpagerAdapter.addfragment(receivedFragment, "Raised Against Me");
        viewPager.setAdapter(viewpagerAdapter);
    }

}
