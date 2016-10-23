package com.whitegems.memowords.model;

/**
 * Created by andreypaunov on 2016-06-29.
 */
public class DrawerItem
{
    private int imageResourceId;
    private String itemName;

    // Constructor when image is present
    public DrawerItem(int imageResourceId, String itemName)
    {
        this.imageResourceId = imageResourceId;
        this.itemName = itemName;
    }

    public DrawerItem(String itemName)
    {
        this.itemName = itemName;
    }

    public int getImageResourceId()
    {
        return imageResourceId;
    }

    public String getItemName()
    {
        return itemName;
    }
}