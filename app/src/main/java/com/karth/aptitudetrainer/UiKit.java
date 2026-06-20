package com.karth.aptitudetrainer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class UiKit {
    public static final int BG_TOP = Color.rgb(235, 244, 255);
    public static final int BG_BOTTOM = Color.rgb(244, 238, 255);
    public static final int INK = Color.rgb(21, 27, 38);
    public static final int MUTED = Color.rgb(91, 102, 124);
    public static final int HERMES_BLUE = Color.rgb(42, 96, 255);
    public static final int HERMES_PURPLE = Color.rgb(126, 87, 255);
    public static final int HERMES_GREEN = Color.rgb(0, 190, 140);
    public static final int DANGER = Color.rgb(222, 68, 90);

    private UiKit() {}

    public static GradientDrawable screenBackground() {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{BG_TOP, Color.WHITE, BG_BOTTOM});
        g.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        return g;
    }

    public static GradientDrawable glassCard() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.argb(210, 255, 255, 255));
        g.setStroke(dp(1), Color.argb(135, 255, 255, 255));
        g.setCornerRadius(dp(28));
        return g;
    }

    public static GradientDrawable subtleCard() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.argb(238, 255, 255, 255));
        g.setStroke(dp(1), Color.argb(80, 120, 150, 210));
        g.setCornerRadius(dp(22));
        return g;
    }

    public static GradientDrawable pill(int color) {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{color, blend(color, Color.WHITE, 0.18f)});
        g.setCornerRadius(dp(999));
        return g;
    }

    public static GradientDrawable outlinePill() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.argb(215, 255, 255, 255));
        g.setStroke(dp(1), Color.argb(110, 42, 96, 255));
        g.setCornerRadius(dp(999));
        return g;
    }

    public static TextView text(Context c, String s, int sp, int color, boolean bold) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setLineSpacing(2f, 1.08f);
        if (bold) t.setTypeface(Typeface.DEFAULT_BOLD);
        return t;
    }

    public static Button primaryButton(Context c, String s) {
        Button b = new Button(c);
        b.setText(s);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setAllCaps(false);
        b.setPadding(dp(16), dp(10), dp(16), dp(10));
        if (Build.VERSION.SDK_INT >= 16) b.setBackground(pill(HERMES_BLUE));
        return b;
    }

    public static Button secondaryButton(Context c, String s) {
        Button b = new Button(c);
        b.setText(s);
        b.setTextColor(HERMES_BLUE);
        b.setTextSize(15);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setAllCaps(false);
        b.setPadding(dp(16), dp(10), dp(16), dp(10));
        if (Build.VERSION.SDK_INT >= 16) b.setBackground(outlinePill());
        return b;
    }

    public static LinearLayout card(Context c) {
        LinearLayout card = new LinearLayout(c);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        if (Build.VERSION.SDK_INT >= 16) card.setBackground(glassCard());
        if (Build.VERSION.SDK_INT >= 21) card.setElevation(dp(8));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(12), 0, dp(12));
        card.setLayoutParams(lp);
        return card;
    }

    public static LinearLayout metricCard(Context c, String value, String label, int accent) {
        LinearLayout box = new LinearLayout(c);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(14), dp(12), dp(14), dp(12));
        if (Build.VERSION.SDK_INT >= 16) box.setBackground(subtleCard());
        TextView v = text(c, value, 24, accent, true);
        TextView l = text(c, label, 12, MUTED, true);
        box.addView(v);
        box.addView(l);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
        lp.setMargins(dp(4), dp(4), dp(4), dp(4));
        box.setLayoutParams(lp);
        return box;
    }

    public static View spacer(Context c, int dp) {
        View v = new View(c);
        v.setLayoutParams(new LinearLayout.LayoutParams(1, dp(dp)));
        return v;
    }

    public static int dp(int v) {
        return (int) (v * android.content.res.Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }

    public static int blend(int a, int b, float amount) {
        int ar = Color.red(a), ag = Color.green(a), ab = Color.blue(a);
        int br = Color.red(b), bg = Color.green(b), bb = Color.blue(b);
        return Color.rgb((int)(ar + (br-ar)*amount), (int)(ag + (bg-ag)*amount), (int)(ab + (bb-ab)*amount));
    }
}
