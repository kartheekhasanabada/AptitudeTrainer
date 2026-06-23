package com.karth.aptitudetrainer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class UiKit {
    public static final int BG_TOP = Color.rgb(224, 242, 255);
    public static final int BG_BOTTOM = Color.rgb(227, 250, 241);
    public static final int INK = Color.rgb(18, 23, 31);
    public static final int MUTED = Color.rgb(86, 95, 109);
    public static final int HERMES_BLUE = Color.rgb(0, 113, 227);
    public static final int HERMES_PURPLE = Color.rgb(112, 92, 232);
    public static final int HERMES_GREEN = Color.rgb(0, 168, 120);
    public static final int LEARNING_GOLD = Color.rgb(255, 179, 64);
    public static final int LEARNING_PINK = Color.rgb(255, 90, 132);
    public static final int CODE_APT_CYAN = Color.rgb(0, 180, 216);
    public static final int DANGER = Color.rgb(222, 68, 90);

    private UiKit() {}

    public static GradientDrawable screenBackground() {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{
                Color.rgb(213, 236, 255),
                Color.rgb(244, 238, 255),
                Color.rgb(222, 250, 241),
                Color.rgb(255, 244, 224)
        });
        g.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        return g;
    }

    public static GradientDrawable glassCard() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.argb(214, 255, 255, 255));
        g.setStroke(dp(1), Color.argb(185, 255, 255, 255));
        g.setCornerRadius(dp(8));
        return g;
    }

    public static GradientDrawable subtleCard() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.argb(205, 255, 255, 255));
        g.setStroke(dp(1), Color.argb(105, 145, 165, 205));
        g.setCornerRadius(dp(8));
        return g;
    }

    public static GradientDrawable dashboardGlass() {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{
                Color.argb(232, 221, 239, 255),
                Color.argb(222, 232, 224, 255),
                Color.argb(220, 220, 250, 238)
        });
        g.setStroke(dp(1), Color.argb(190, 255, 255, 255));
        g.setCornerRadius(dp(8));
        return g;
    }

    public static GradientDrawable learningGlass(int accent) {
        int mixed = blend(accent, Color.WHITE, 0.58f);
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{
                Color.argb(225, 255, 255, 255),
                Color.argb(215, Color.red(mixed), Color.green(mixed), Color.blue(mixed))
        });
        g.setStroke(dp(1), Color.argb(150, 255, 255, 255));
        g.setCornerRadius(dp(8));
        return g;
    }

    public static GradientDrawable inputBackground() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.argb(190, 255, 255, 255));
        g.setStroke(dp(1), Color.argb(120, 170, 190, 220));
        g.setCornerRadius(dp(8));
        return g;
    }

    public static GradientDrawable pill(int color) {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{color, blend(color, Color.WHITE, 0.18f)});
        g.setCornerRadius(dp(999));
        return g;
    }

    public static GradientDrawable outlinePill() {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{
                Color.argb(222, 255, 255, 255),
                Color.argb(185, 224, 238, 255)
        });
        g.setStroke(dp(1), Color.argb(120, 0, 113, 227));
        g.setCornerRadius(dp(999));
        return g;
    }

    public static GradientDrawable dangerOutlinePill() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.argb(190, 255, 255, 255));
        g.setStroke(dp(1), Color.argb(150, Color.red(DANGER), Color.green(DANGER), Color.blue(DANGER)));
        g.setCornerRadius(dp(999));
        return g;
    }

    public static TextView text(Context c, String s, int sp, int color, boolean bold) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(responsiveSp(c, sp));
        t.setTextColor(color);
        t.setLineSpacing(4f, 1.12f);
        if (bold) t.setTypeface(Typeface.DEFAULT_BOLD);
        return t;
    }

    public static TextView poweredByLabel(Context c) {
        TextView t = new TextView(c);
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append("powered by ");
        int codeStart = sb.length();
        sb.append("{Code}");
        sb.setSpan(new ForegroundColorSpan(CODE_APT_CYAN), codeStart, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new StyleSpan(Typeface.BOLD), codeStart, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int aptStart = sb.length();
        sb.append(" Apt");
        sb.setSpan(new ForegroundColorSpan(CODE_APT_CYAN), aptStart, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new StyleSpan(Typeface.BOLD), aptStart, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        t.setText(sb);
        t.setTextSize(responsiveSp(c, 13));
        t.setTextColor(MUTED);
        t.setLetterSpacing(0f);
        return t;
    }

    public static ImageView appLogo(Context c) {
        ImageView logo = new ImageView(c);
        logo.setImageResource(R.drawable.codeapt_logo);
        logo.setAdjustViewBounds(true);
        int w = isWideScreen(c) ? dp(140) : dp(112);
        logo.setLayoutParams(new LinearLayout.LayoutParams(w, LinearLayout.LayoutParams.WRAP_CONTENT));
        logo.setContentDescription("Code Apt logo");
        return logo;
    }

    public static Button primaryButton(Context c, String s) {
        Button b = new Button(c);
        b.setText(s);
        b.setTextColor(Color.WHITE);
        b.setTextSize(responsiveSp(c, 15));
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setAllCaps(false);
        b.setPadding(dp(16), dp(12), dp(16), dp(12));
        if (Build.VERSION.SDK_INT >= 16) b.setBackground(pill(HERMES_BLUE));
        b.setMinHeight(dp(48));
        return b;
    }

    public static Button secondaryButton(Context c, String s) {
        Button b = new Button(c);
        b.setText(s);
        b.setTextColor(HERMES_BLUE);
        b.setTextSize(responsiveSp(c, 15));
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setAllCaps(false);
        b.setPadding(dp(16), dp(12), dp(16), dp(12));
        if (Build.VERSION.SDK_INT >= 16) b.setBackground(outlinePill());
        b.setMinHeight(dp(48));
        return b;
    }

    public static Button dangerButton(Context c, String s) {
        Button b = new Button(c);
        b.setText(s);
        b.setTextColor(DANGER);
        b.setTextSize(responsiveSp(c, 15));
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setAllCaps(false);
        b.setPadding(dp(16), dp(12), dp(16), dp(12));
        if (Build.VERSION.SDK_INT >= 16) b.setBackground(dangerOutlinePill());
        b.setMinHeight(dp(48));
        return b;
    }

    public static LinearLayout card(Context c) {
        LinearLayout card = new LinearLayout(c);
        card.setOrientation(LinearLayout.VERTICAL);
        int pad = contentPadding(c) - dp(2);
        card.setPadding(pad, pad, pad, pad);
        if (Build.VERSION.SDK_INT >= 16) card.setBackground(glassCard());
        if (Build.VERSION.SDK_INT >= 21) card.setElevation(dp(4));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(10), 0, dp(10));
        card.setLayoutParams(lp);
        return card;
    }

    public static LinearLayout metricCard(Context c, String value, String label, int accent) {
        LinearLayout box = new LinearLayout(c);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(12), dp(10), dp(12), dp(10));
        if (Build.VERSION.SDK_INT >= 16) box.setBackground(learningGlass(accent));
        TextView v = text(c, value, 24, accent, true);
        TextView l = text(c, label, 12, MUTED, true);
        box.addView(v);
        box.addView(l);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
        lp.setMargins(dp(4), dp(4), dp(4), dp(4));
        box.setLayoutParams(lp);
        return box;
    }

    public static LinearLayout learningTile(Context c, String title, String value, int accent) {
        LinearLayout box = new LinearLayout(c);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(14), dp(12), dp(14), dp(12));
        if (Build.VERSION.SDK_INT >= 16) box.setBackground(learningGlass(accent));
        if (Build.VERSION.SDK_INT >= 21) box.setElevation(dp(2));
        TextView titleView = text(c, title, 12, MUTED, true);
        TextView valueView = text(c, value, 19, accent, true);
        box.addView(titleView);
        box.addView(valueView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
        lp.setMargins(dp(4), dp(6), dp(4), dp(6));
        box.setLayoutParams(lp);
        return box;
    }

    public static void fadeSlideIn(View v, long delayMs) {
        v.setAlpha(0f);
        v.setTranslationY(dp(20));
        v.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delayMs)
                .setDuration(520)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public static void scalePopIn(View v, long delayMs) {
        v.setAlpha(0f);
        v.setScaleX(0.92f);
        v.setScaleY(0.92f);
        v.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(delayMs)
                .setDuration(460)
                .setInterpolator(new OvershootInterpolator(0.55f))
                .start();
    }

    public static void pulseOnce(View v) {
        v.animate().scaleX(1.025f).scaleY(1.025f).setDuration(110).withEndAction(() ->
                v.animate().scaleX(1f).scaleY(1f).setDuration(170).setInterpolator(new DecelerateInterpolator()).start()
        ).start();
    }

    public static void crossFadeContent(View container, Runnable updateContent) {
        container.animate().alpha(0f).translationX(dp(6)).setDuration(140).withEndAction(() -> {
            updateContent.run();
            container.setTranslationX(-dp(6));
            container.animate().alpha(1f).translationX(0f).setDuration(260).setInterpolator(new DecelerateInterpolator()).start();
        }).start();
    }

    public static int contentPadding(Context c) {
        float widthDp = screenWidthDp(c);
        if (widthDp < 340) return dp(14);
        if (widthDp > 600) return dp(32);
        if (widthDp > 420) return dp(24);
        return dp(20);
    }

    public static float responsiveSp(Context c, float base) {
        float widthDp = screenWidthDp(c);
        if (widthDp < 340) return base - 1f;
        if (widthDp > 600) return base + 2f;
        if (widthDp > 420) return base + 1f;
        return base;
    }

    public static boolean isWideScreen(Context c) {
        return screenWidthDp(c) > 420;
    }

    public static int screenWidthDp(Context c) {
        return (int) (c.getResources().getDisplayMetrics().widthPixels / c.getResources().getDisplayMetrics().density);
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
