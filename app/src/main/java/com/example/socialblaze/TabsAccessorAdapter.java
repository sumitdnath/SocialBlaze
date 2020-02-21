package com.example.socialblaze;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter

{
    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i)
    {
        switch (i){

            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            case 3:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;
            case 4:
                AboutFragment aboutFragment = new AboutFragment();
                return aboutFragment;


                default:
                    return null;

        }
    }


    @Override
    public int getCount()
    {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position){

            case 0:

                return "Chat";
            case 1:

                return "Group";
            case 2:
                 return "Contact";
            case 3:
                return "Request";
            case 4:
                return "info";

            default:
                return null;

        }

    }
}
