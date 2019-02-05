package com.hyperlocal.app.ui.home;

import com.hyperlocal.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ${Umesh} on 05-04-2018.
 */

public class SideMenuDataModel {

    private List<SideMenuItemModel> arrayList = new ArrayList<>();

    public List<SideMenuItemModel> getArrayList() {
        arrayList.add(new SideMenuItemModel("Ask",R.drawable.ask_a,false));
        arrayList.add(new SideMenuItemModel("Activity",R.drawable.activity,false));
        arrayList.add(new SideMenuItemModel("Invite",R.drawable.invite,false));
        arrayList.add(new SideMenuItemModel("Give",R.drawable.give,false));
        arrayList.add(new SideMenuItemModel("Spread the word",R.drawable.about,false));
        arrayList.add(new SideMenuItemModel("About",R.drawable.about_a,false));
        arrayList.add(new SideMenuItemModel("Report Abuse",R.drawable.report,false));
       // arrayList.add(new SideMenuItemModel("Logout",R.drawable.report,false));
        return arrayList;
    }
}
