package com.hyperlocal.app.ui.home;

/**
 * @Author ${Umesh} on 05-04-2018.
 */

public class SideMenuItemModel {
    private String itemName;
    private int icon;
    private boolean isItemClick;

    public boolean isItemClick() {
        return isItemClick;
    }

    public void setItemClick(boolean itemClick) {
        isItemClick = itemClick;
    }

    public SideMenuItemModel(String itemName, int icon,boolean isItemClick) {
        this.itemName = itemName;
        this.icon = icon;
        this.isItemClick = isItemClick;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
