package com.example.stopwatch.utils;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Toast;

public class Utils {
    public enum PadPlacement {
        START,
        END
    }

    public static String padString (String string, String with, PadPlacement padPlacement) {
        if (padPlacement == null) padPlacement = PadPlacement.START;

        if (padPlacement == PadPlacement.START) return with + string;
        else return string + with;
    }

    public static String padWith0 (String string, PadPlacement padPlacement) {
        return Utils.padString(string, "0", padPlacement);
    }

    public static int getColorFromThemeAttribute(Context context, int attribute) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attribute, typedValue, true);

        return typedValue.data;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void copyToClipBoard(Context context, String label, String text) {
        ClipboardManager clipboard =  (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard.hasPrimaryClip()) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

            if (text.equals(item.getText().toString())) {
                Toast.makeText(context, "Item already copied to clipboard.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        clipboard.setPrimaryClip(ClipData.newPlainText(label, text));
        Toast.makeText(context, "Copies to clipboard.", Toast.LENGTH_SHORT).show();
    }
}
