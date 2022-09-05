package com.ym.projectManager.model.templateWrap.order;

import com.ym.projectManager.model.Item;

public class SelectItemsFromDB {

    private boolean selected = false;
    private Item item;

    public SelectItemsFromDB() {
    }

    public boolean isSelected() {
        return selected;
    }

    public Item getItem() {
        return item;
    }

    public SelectItemsFromDB(boolean selected, Item item) {
        this.selected = selected;
        this.item = item;
    }

    public SelectItemsFromDB(Item item) {
        this.item = item;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setItem(Item item) {
        this.item = item;
    }

}
