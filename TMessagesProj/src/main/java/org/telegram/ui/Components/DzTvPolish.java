package org.telegram.ui.Components;

import android.content.Context;

import org.telegram.ui.ActionBar.Theme;

// DZ TG Player TV build 14: TeleShield-style polish, TV-mode only.
// Central place for the OLED Midnight + teal theme overrides, chat-list
// restyle constants, and subtle TV-safe animations. Phones are untouched:
// every entry point is gated by DzTvHintBar.isTvMode().
public class DzTvPolish {

    // TeleShield palette: cyans/teal on near-black
    public static final int TEAL = 0xFF00E5CC;
    public static final int BG_BLACK = 0xFF000000;   // pure OLED black
    public static final int BG_MIDNIGHT = 0xFF0B141F; // TeleTV midnight surface
    public static final int TEXT_PRIMARY = 0xFFFFFFFF;
    public static final int TEXT_SECONDARY = 0xFF94A3B8;
    public static final int BUBBLE_IN = 0xFF16202E;
    public static final int BUBBLE_OUT = 0xFF0E3A3C;  // subtle teal tint for outgoing

    private static boolean themeApplied = false;

    /**
     * Applies the TV theme overrides once per process. Safe to call repeatedly;
     * no-ops on phones and after the first TV application.
     */
    public static void applyTvTheme(Context context) {
        if (themeApplied || !DzTvHintBar.isTvMode(context)) {
            return;
        }
        themeApplied = true;

        // --- backgrounds: OLED pure black + midnight surfaces ---
        Theme.setColor(Theme.key_windowBackgroundWhite, BG_BLACK, false);
        Theme.setColor(Theme.key_windowBackgroundGray, BG_MIDNIGHT, false);
        Theme.setColor(Theme.key_actionBarDefault, BG_MIDNIGHT, false);
        Theme.setColor(Theme.key_actionBarDefaultTitle, TEXT_PRIMARY, false);
        Theme.setColor(Theme.key_actionBarDefaultSubtitle, TEXT_SECONDARY, false);
        Theme.setColor(Theme.key_dialogBackground, BG_MIDNIGHT, false);
        Theme.setColor(Theme.key_dialogTextBlack, TEXT_PRIMARY, false);

        // --- chat list: TeleShield-style rows ---
        Theme.setColor(Theme.key_chats_name, TEXT_PRIMARY, false);
        Theme.setColor(Theme.key_chats_message, TEXT_SECONDARY, false);
        Theme.setColor(Theme.key_chats_unreadCounter, TEAL, false);
        Theme.setColor(Theme.key_chats_unreadCounterText, BG_BLACK, false);
        Theme.setColor(Theme.key_chats_unreadCounterMuted, 0xFF334155, false);
        Theme.setColor(Theme.key_chats_menuItemText, TEXT_SECONDARY, false);
        Theme.setColor(Theme.key_listSelector, 0x1A00E5CC, false); // faint teal press

        // --- chat bubbles: dark in, teal-tinted out ---
        Theme.setColor(Theme.key_chat_inBubble, BUBBLE_IN, false);
        Theme.setColor(Theme.key_chat_outBubble, BUBBLE_OUT, false);
        Theme.setColor(Theme.key_chat_messageTextIn, TEXT_PRIMARY, false);
        Theme.setColor(Theme.key_chat_messageTextOut, TEXT_PRIMARY, false);

        // --- chat background: pure OLED black (kills the light-green classic
        // wallpaper in TV mode). Setting the wallpaper keys triggers
        // Theme.reloadWallpaper so the change applies immediately. ---
        Theme.setColor(Theme.key_chat_wallpaper, BG_BLACK, false);
        Theme.setColor(Theme.key_chat_wallpaper_gradient_to1, BG_BLACK, false);
        Theme.setColor(Theme.key_chat_wallpaper_gradient_to2, BG_BLACK, false);
        Theme.setColor(Theme.key_chat_wallpaper_gradient_to3, BG_BLACK, false);

        // --- message input panel + misc chat chrome: dark ---
        Theme.setColor(Theme.key_chat_messagePanelBackground, BG_MIDNIGHT, false);
        Theme.setColor(Theme.key_chat_messagePanelText, TEXT_PRIMARY, false);
        Theme.setColor(Theme.key_chat_messagePanelHint, TEXT_SECONDARY, false);
        Theme.setColor(Theme.key_actionBarDefaultIcon, TEXT_PRIMARY, false);
    }

    /** TV chat-list row heights (roomier TeleShield-style rows). */
    public static int dialogRowHeightDefault() {
        return 78;
    }

    public static int dialogRowHeightThreeLines() {
        return 84;
    }
}
