package com.bg.check.ui;

import android.text.TextUtils;

public class Utils {


    public static final String replaceVoiceChar(String words) {
        if (TextUtils.isEmpty(words)) {
            return null;
        }

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length(); i++) {
            char c = words.charAt(i);
            switch (c) {
            case '0':
                c = '��';
                break;
            case '1':
                c = '��';
                break;
            case '2':
                c = '��';
                break;
            case '3':
                c = '��';
                break;
            case '4':
                c = '��';
                break;
            case '5':
                c = '��';
                break;
            case '6':
                c = '��';
                break;
            case '7':
                c = '��';
                break;
            case '8':
                c = '��';
                break;
            case '9':
                c = '��';
                break;
            }

            builder.append(c);
        }

        return builder.toString();
    }

}
