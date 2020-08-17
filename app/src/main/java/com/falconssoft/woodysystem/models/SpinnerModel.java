package com.falconssoft.woodysystem.models;

public class SpinnerModel {

    private String title;
    private boolean isChecked;

    public SpinnerModel() {
    }

    public SpinnerModel(String title, boolean isChecked) {
        this.title = title;
        this.isChecked = isChecked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
