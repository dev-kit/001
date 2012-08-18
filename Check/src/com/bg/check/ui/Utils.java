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
                c = '动';
                break;
            case '1':
                c = '邀';
                break;
            case '2':
                c = '二';
                break;
            case '3':
                c = '三';
                break;
            case '4':
                c = '四';
                break;
            case '5':
                c = '五';
                break;
            case '6':
                c = '六';
                break;
            case '7':
                c = '拐';
                break;
            case '8':
                c = '八';
                break;
            case '9':
                c = '九';
                break;
            }

            builder.append(c);
        }

        return builder.toString();
    }

}
