package hu.webarticum.miniconnect.repl;

import java.util.regex.Pattern;

public class AnsiUtil {

    private static final Pattern ANSI_ESCAPE_PATTERN = Pattern.compile("\\e\\[[0-9;]*m");
    
    private static final Pattern CONTROL_CHAR_PATTERN = Pattern.compile("[\\x{0000}-\\x{0008}\\x{000E}-\\x{001F}]");
    
    private static final String PROMPT_START_ANSI = "\u001B[1;34m";

    private static final String PROMPT2_START_ANSI = "\u001B[0;33m";

    private static final String SUCCESS_START_ANSI = "\u001B[1;32m";

    private static final String ERROR_START_ANSI = "\u001B[1;31m";
    
    private static final String RESET_ANSI = "\u001B[0m";

    
    private AnsiUtil() {
        // utility class
    }
    

    public static CharSequence cleanAnsiText(CharSequence ansiText) {
        return ANSI_ESCAPE_PATTERN.matcher(ansiText).replaceAll("");
    }

    public static CharSequence escapeText(CharSequence text) {
        return CONTROL_CHAR_PATTERN.matcher(text).replaceAll("");
    }

    public static CharSequence formatAsPrompt(CharSequence promptText) {
        return PROMPT_START_ANSI + promptText + RESET_ANSI;
    }

    public static CharSequence formatAsPrompt2(CharSequence promptText) {
        return PROMPT2_START_ANSI + promptText + RESET_ANSI;
    }

    public static CharSequence formatAsSuccess(CharSequence promptText) {
        return SUCCESS_START_ANSI + promptText + RESET_ANSI;
    }

    public static CharSequence formatAsError(CharSequence promptText) {
        return ERROR_START_ANSI + promptText + RESET_ANSI;
    }

}
