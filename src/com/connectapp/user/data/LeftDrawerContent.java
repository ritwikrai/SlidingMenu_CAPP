package com.connectapp.user.data;

import java.util.ArrayList;

import com.connectapp.user.R;

public class LeftDrawerContent {

	// Items
	public static ArrayList<DrawerModel> getList() {
		ArrayList<DrawerModel> list = new ArrayList<DrawerModel>();
		list.add(new DrawerModel(0, "", "Home", R.string.material_icon_home));
		list.add(new DrawerModel(1, "", "UnSynced Data", R.string.material_icon_cloud_univ_first));
		list.add(new DrawerModel(2, "", "History", R.string.material_icon_state));
		list.add(new DrawerModel(3, "", "Privacy Policy", R.string.material_icon_info_box));
		list.add(new DrawerModel(4, "", "Logout", R.string.material_icon_logout));
		/*list.add(new DummyModel(4, "", "Guests", R.string.material_icon_account));
		list.add(new DummyModel(5, "", "Students", R.string.material_icon_account));*/
		return list;
	}

}
