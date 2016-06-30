package cc.springwind.mobileguard.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HeFan on 2016/6/30.
 */
public class TextCheckTool {

    /**
     * 判断字符串是否为纯数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
