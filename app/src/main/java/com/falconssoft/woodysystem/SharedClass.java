package com.falconssoft.woodysystem;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class SharedClass {
    private Context context;

    public SharedClass(Context context) {
        this.context = context;
    }

    public void showSnackbar(View coordinatorLayout, String text, boolean imageType) {
        Snackbar snackbar;

        if (imageType) {
            snackbar = Snackbar.make(coordinatorLayout, Html.fromHtml("<font color=\"#3167F0\">" + text + "</font>"), Snackbar.LENGTH_SHORT);//Updated Successfully
            View snackbarLayout = snackbar.getView();
            TextView textViewSnackbar = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);//android.support.design.R.id.snackbar_text
            textViewSnackbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);
            textViewSnackbar.setCompoundDrawablePadding(5);
        } else {
            snackbar = Snackbar.make(coordinatorLayout, Html.fromHtml("<font color=\"#D11616\">" + text + "</font>"), Snackbar.LENGTH_SHORT);//Updated Successfully
            View snackbarLayout = snackbar.getView();
            TextView textViewSnackbar = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);//android.support.design.R.id.snackbar_text
            textViewSnackbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_error, 0, 0, 0);
            textViewSnackbar.setCompoundDrawablePadding(5);
        }
        snackbar.show();
    }
}
