package hu.webarticum.miniconnect.repl;

import java.util.regex.Pattern;

public class AnsiUtil {

    private static final Pattern ANSI_ESCAPE_PATTERN = Pattern.compile("\\e\\[[0-9;]*m");
    
    private static final Pattern CONTROL_CHAR_PATTERN = Pattern.compile("[\\x{0000}-\\x{0008}\\x{000E}-\\x{001F}]");
    
    private static final String PROMPT_START_ANSI = "\u001B[1;34m";

    private static final String PROMPT2_START_ANSI = "\u001B[1m";

    private static final String SUCCESS_START_ANSI = "\u001B[1;32m";

    private static final String ERROR_START_ANSI = "\u001B[1;31m";

    private static final String WARNING_START_ANSI = "\u001B[1;33m";
    
    private static final String HEADER_START_ANSI = "\u001B[1m";

    private static final String PARAMETER_START_ANSI = "\u001B[3;33m";

    private static final String NUMBER_START_ANSI = "\u001B[1;96m";
    
    private static final String NONE_START_ANSI = "\u001B[3;90m";
    
    private static final String RESET_ANSI = "\u001B[0m";

    
    private AnsiUtil() {
        // utility class
    }
    

    public static String cleanAnsiText(CharSequence ansiText) {
        return ANSI_ESCAPE_PATTERN.matcher(ansiText).replaceAll("");
    }

    public static String escapeText(CharSequence text) {
        return CONTROL_CHAR_PATTERN.matcher(text).replaceAll("");
    }

    public static String formatAsPrompt(CharSequence promptText) {
        return PROMPT_START_ANSI + promptText + RESET_ANSI;
    }

    public static String formatAsPrompt2(CharSequence promptText) {
        return PROMPT2_START_ANSI + promptText + RESET_ANSI;
    }

    public static String formatAsSuccess(CharSequence promptText) {
        return SUCCESS_START_ANSI + promptText + RESET_ANSI;
    }

    public static String formatAsError(CharSequence promptText) {
        return ERROR_START_ANSI + promptText + RESET_ANSI;
    }

    public static String formatAsWarning(CharSequence promptText) {
        return WARNING_START_ANSI + promptText + RESET_ANSI;
    }

    public static String formatAsHeader(CharSequence promptText) {
        return HEADER_START_ANSI + promptText + RESET_ANSI;
    }

    public static String formatAsParameter(CharSequence promptText) {
        return PARAMETER_START_ANSI + promptText + RESET_ANSI;
    }

    public static String formatAsNumber(CharSequence promptText) {
        return NUMBER_START_ANSI + promptText + RESET_ANSI;
    }

    public static String formatAsNone(CharSequence promptText) {
        return NONE_START_ANSI + promptText + RESET_ANSI;
    }

}
