package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;

// DZ TG Player TV build 12: TeleTV-style navigation rail for TV devices.
// Vertical 84dp rail: logo on top, nav items (Chats/Contacts/Calls/Settings),
// TV badge at the bottom. D-pad UP/DOWN moves between items, RIGHT leaves
// the rail (host wires it to the dialog list), OK activates the item.
// Shown only in TV mode (see DzTvHintBar.isTvMode); phones are unaffected.
public class DzTvSidebar extends LinearLayout {

    public static final int DEST_CHATS = 0;
    public static final int DEST_CONTACTS = 1;
    public static final int DEST_CALLS = 2;
    public static final int DEST_SETTINGS = 3;

    public interface OnNavigateListener {
        void onNavigate(int destination);
    }

    public interface OnMoveRightListener {
        void onMoveRight();
    }

    // TeleTV OLED Midnight palette (accent unified to DZ teal #00E5CC)
    private static final int BG = 0xFF0B141F;
    private static final int TEAL = 0xFF00E5CC;
    private static final int TEXT_SECONDARY = 0xFF94A3B8;

    private static final int[] ICONS = {
            R.drawable.dz_tv_ic_chats,
            R.drawable.dz_tv_ic_contacts,
            R.drawable.dz_tv_ic_calls,
            R.drawable.dz_tv_ic_settings
    };
    private static final String[] LABELS = {"Chats", "Contacts", "Calls", "Settings"};

    private OnNavigateListener navigateListener;
    private OnMoveRightListener moveRightListener;
    private final FrameLayout[] itemBoxes = new FrameLayout[4];
    private final ImageView[] itemIcons = new ImageView[4];
    private final TextView[] itemLabels = new TextView[4];
    private final View[] itemRoots = new View[4];
    private int selected = DEST_CHATS;

    public DzTvSidebar(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setBackgroundColor(BG);
        int padV = AndroidUtilities.dp(16);
        setPadding(0, padV, 0, padV);

        // Top logo: teal-ringed circle with "DZ"
        FrameLayout logoWrap = new FrameLayout(context);
        int logoSize = AndroidUtilities.dp(46);
        GradientDrawable logoBg = new GradientDrawable();
        logoBg.setShape(GradientDrawable.OVAL);
        logoBg.setColor(0xFF142232);
        logoBg.setStroke(AndroidUtilities.dp(2), TEAL);
        TextView logoText = new TextView(context);
        logoText.setText("DZ");
        logoText.setTextColor(Color.WHITE);
        logoText.setTypeface(Typeface.DEFAULT_BOLD);
        logoText.setTextSize(16);
        logoText.setGravity(Gravity.CENTER);
        logoText.setBackground(logoBg);
        logoWrap.addView(logoText, new FrameLayout.LayoutParams(logoSize, logoSize, Gravity.CENTER));
        LayoutParams logoLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        logoLp.gravity = Gravity.CENTER_HORIZONTAL;
        logoLp.bottomMargin = AndroidUtilities.dp(8);
        addView(logoWrap, logoLp);

        TextView logoLabel = new TextView(context);
        logoLabel.setText("DZ TG");
        logoLabel.setTextColor(TEAL);
        logoLabel.setTypeface(Typeface.DEFAULT_BOLD);
        logoLabel.setTextSize(11);
        logoLabel.setGravity(Gravity.CENTER);
        LayoutParams logoLabelLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        logoLabelLp.gravity = Gravity.CENTER_HORIZONTAL;
        logoLabelLp.bottomMargin = AndroidUtilities.dp(12);
        addView(logoLabel, logoLabelLp);

        // Nav items (vertically centered group)
        LinearLayout itemsContainer = new LinearLayout(context);
        itemsContainer.setOrientation(VERTICAL);
        itemsContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutParams itemsLp = new LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1f);
        itemsLp.gravity = Gravity.CENTER_HORIZONTAL;
        addView(itemsContainer, itemsLp);

        for (int i = 0; i < 4; i++) {
            addItem(itemsContainer, i);
        }

        // Bottom TV badge
        TextView badge = new TextView(context);
        badge.setText("TV");
        badge.setTextColor(TEAL);
        badge.setTypeface(Typeface.DEFAULT_BOLD);
        badge.setTextSize(9);
        badge.setGravity(Gravity.CENTER);
        badge.setFocusable(false);
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setShape(GradientDrawable.RECTANGLE);
        badgeBg.setCornerRadius(AndroidUtilities.dp(6));
        badgeBg.setColor(0x801B2B3E);
        badge.setBackground(badgeBg);
        int bPadH = AndroidUtilities.dp(8);
        int bPadV = AndroidUtilities.dp(3);
        badge.setPadding(bPadH, bPadV, bPadH, bPadV);
        LayoutParams badgeLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        badgeLp.gravity = Gravity.CENTER_HORIZONTAL;
        addView(badge, badgeLp);

        updateSelection();
    }

    private void addItem(LinearLayout parent, int dest) {
        Context context = getContext();
        // Focusable root: teal focus ring (shared DZ TV drawable)
        LinearLayout item = new LinearLayout(context);
        item.setOrientation(VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setFocusable(true);
        item.setBackgroundResource(R.drawable.dz_tv_focus_highlight);
        int padV = AndroidUtilities.dp(5);
        item.setPadding(0, padV, 0, padV);

        // Inner 54dp rounded box: carries the selected state
        FrameLayout box = new FrameLayout(context);
        int boxSize = AndroidUtilities.dp(54);
        LinearLayout inner = new LinearLayout(context);
        inner.setOrientation(VERTICAL);
        inner.setGravity(Gravity.CENTER);
        ImageView icon = new ImageView(context);
        icon.setImageResource(ICONS[dest]);
        int iconSize = AndroidUtilities.dp(22);
        inner.addView(icon, new LinearLayout.LayoutParams(iconSize, iconSize));
        TextView label = new TextView(context);
        label.setText(LABELS[dest]);
        label.setTextSize(8);
        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        labelLp.topMargin = AndroidUtilities.dp(2);
        inner.addView(label, labelLp);
        box.addView(inner, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        item.addView(box, new LinearLayout.LayoutParams(boxSize, boxSize));

        final int d = dest;
        item.setOnClickListener(v -> {
            setSelected(d);
            if (navigateListener != null) {
                navigateListener.onNavigate(d);
            }
        });
        // D-pad RIGHT leaves the rail toward the dialog list (host wires the target)
        item.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (moveRightListener != null) {
                    moveRightListener.onMoveRight();
                    return true;
                }
            }
            return false;
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = AndroidUtilities.dp(5);
        lp.bottomMargin = AndroidUtilities.dp(5);
        parent.addView(item, lp);

        itemRoots[dest] = item;
        itemBoxes[dest] = box;
        itemIcons[dest] = icon;
        itemLabels[dest] = label;
    }

    public void setOnNavigateListener(OnNavigateListener l) {
        navigateListener = l;
    }

    public void setOnMoveRightListener(OnMoveRightListener l) {
        moveRightListener = l;
    }

    /** Returns the first item view so the host can wire nextFocusLeftId from the list. */
    public View getFirstItemView() {
        return itemRoots[0];
    }

    /** Sets explicit RIGHT focus target for every item (dialog list). */
    public void setNextFocusRightId(int id) {
        for (View v : itemRoots) {
            if (v != null) {
                v.setNextFocusRightId(id);
            }
        }
    }

    public void setSelected(int dest) {
        if (dest < 0 || dest > 3) {
            return;
        }
        boolean changed = dest != selected;
        selected = dest;
        updateSelection();
        // DZ TG Player TV build 14: subtle TeleShield-style selection pulse.
        // Fast and TV-safe (plain view alpha, no blur).
        if (changed && itemBoxes[dest] != null) {
            View box = itemBoxes[dest];
            box.animate().cancel();
            box.setAlpha(0.55f);
            box.animate().alpha(1f).setDuration(140).start();
        }
    }

    public int getSelected() {
        return selected;
    }

    private void updateSelection() {
        for (int i = 0; i < 4; i++) {
            boolean sel = i == selected;
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.RECTANGLE);
            bg.setCornerRadius(AndroidUtilities.dp(14));
            if (sel) {
                bg.setColor(0x5900E5CC); // teal at 35%
                bg.setStroke(AndroidUtilities.dp(2), TEAL);
            } else {
                bg.setColor(Color.TRANSPARENT);
            }
            if (itemBoxes[i] != null) {
                itemBoxes[i].setBackground(bg);
            }
            if (itemIcons[i] != null) {
                itemIcons[i].setColorFilter(sel ? TEAL : TEXT_SECONDARY);
            }
            if (itemLabels[i] != null) {
                itemLabels[i].setTextColor(sel ? Color.WHITE : TEXT_SECONDARY);
                itemLabels[i].setTypeface(sel ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            }
        }
    }
}
