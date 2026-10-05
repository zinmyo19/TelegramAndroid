package org.telegram.ui.Components;

import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;

// DZ TG Player TV: slim remote hint bar, TeleTV-style.
// Shown only on TV / non-touch devices so phone UX is never affected.
public class DzTvHintBar {

    public static boolean isTvMode(Context context) {
        if (context == null) {
            return false;
        }
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager != null && uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            return true;
        }
        // DZ TV build 12: also treat Leanback devices as TV (covers more TV boxes)
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
            return true;
        }
        return !context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN);
    }

    // gravity: Gravity.BOTTOM or Gravity.TOP depending on the screen layout
    public static void attachTo(ViewGroup parent, Context context, int gravity) {
        if (parent == null || context == null || !isTvMode(context)) {
            return;
        }
        TextView bar = new TextView(context);
        bar.setText("OK  Select      \u2190 \u2192 \u2191 \u2193  Navigate      BACK  Return");
        bar.setTextSize(13);
        bar.setTextColor(0xE6FFFFFF);
        bar.setTypeface(Typeface.DEFAULT_BOLD);
        bar.setGravity(Gravity.CENTER);
        bar.setSingleLine(true);
        bar.setBackgroundColor(0x99000000);
        int padH = AndroidUtilities.dp(6);
        bar.setPadding(padH, AndroidUtilities.dp(4), padH, AndroidUtilities.dp(4));
        bar.setFocusable(false);
        bar.setClickable(false);
        bar.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        parent.addView(bar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, gravity));
    }
}
