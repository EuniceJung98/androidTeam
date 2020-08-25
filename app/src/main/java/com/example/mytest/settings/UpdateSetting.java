package com.example.mytest.settings;

import java.io.Serializable;

public class UpdateSetting implements Serializable {
    private int id;
    private String type;
    private String categoryName;
    private String assetName;

    public UpdateSetting() {}

    public UpdateSetting(int id, String type, String categoryName) {
        this.id = id;
        this.type = type;
        this.categoryName = categoryName;
    }

    public UpdateSetting(int id, String assetName) {
        this.id = id;
        this.assetName = assetName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    @Override
    public String toString() {
        return "UpdateSetting{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", assetName='" + assetName + '\'' +
                '}';
    }
}
