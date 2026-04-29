package com.project.back_end.utils.outputhelpers;

/**
 * <p>Message Formatter based on ANSI colors and Visual style, and infographics to some degree.
 * This is aimed to enrich text based communication, bien sea throughout a CLI deliver mechanism
 * or Log or the kind of text means.
 * Its design as composite features behavior.
 *
 * @Author Samuel Camarena Caballer</p>
 * <p>[Atribuciones]
 * Fragmento de la Clase Ansi de Dain/Dainkaplan de Github)<br>
 * · info: https://gist.github.com/dainkaplan/4651352
 * @Author dain kaplan</p>
 *
 * Visual enhancement assess by Gemini
 * <p>
 */
public class MessageFormatter {
    
    public static final String RESET                = "\u001B[0m";
    
    public static final String BOLD                 = "\u001B[1m";
    public static final String ITALIC               = "\u001B[3m";
    public static final String UNDERLINE			= "\u001B[4m";
    public static final String BLINK				= "\u001B[5m";
    public static final String RAPID_BLINK			= "\u001B[6m";
    public static final String REVERSE_VIDEO		= "\u001B[7m";
    public static final String INVISIBLE_TEXT		= "\u001B[8m";
    
    public static final String BLACK				= "\u001B[30m";
    public static final String RED					= "\u001B[31m";
    public static final String GREEN				= "\u001B[32m";
    public static final String YELLOW				= "\u001B[33m";
    public static final String BLUE				    = "\u001B[34m";
    public static final String MAGENTA				= "\u001B[35m";
    public static final String CYAN				    = "\u001B[36m";
    public static final String WHITE				= "\u001B[37m";
    public static final String ORANGE				= "\u001B[38;2;220;120;0m";
    
    /**
     * Maven example of Console output building diagnostics.
     * [INFO]  T E S T S
     * [INFO] -------------------------------------------------------
     * [ERROR] Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
     */
    public static enum MessageHead {
        INFO { public String compose() { return wrapHead(blue(this.toString())); } },
        WARNING { public String compose() { return wrapHead(orange(this.toString())); } },
        CHECK { public String compose() { return wrapHead(green(this.toString())); } },
        SUCCESS { public String compose() { return wrapHead(green(this.toString())); } },
        FAIL { public String compose() { return wrapHead(orange(this.toString())); } },
        ERROR { public String compose() { return wrapHead(red(this.toString())); } },
        UNAUTHORIZED { public String compose() { return wrapHead(red(this.toString())); } };
        
        /*
           TODO: Formatting a width and left justify does not work in String.format nor printf?
          - [ERROR ] -> String.format("[%-6s] ", text);
          - [INFO  ]
          - [CHECK ]
        */
        private final int headLength = 6;
        
        public abstract String compose();
        
        private static String wrapHead(String head) {
            //return String.format("[%-6s]", head);
            return String.format("[" + head + "] ");
        }
    }
    
    /*
     *   Composite behavior functions
     */
    public static String bold(String text) {
        return BOLD + text + RESET;
    }
    
    public static String italic(String text) {
        return ITALIC + text + RESET;
    }
    
    public static String underline(String text) {
        return UNDERLINE + text + RESET;
    }
    
    public static String blink(String text) {
        return BLINK + text + RESET;
    }
    
    public static String reverse(String text) {
        return REVERSE_VIDEO + text + RESET;
    }
    
    public static String invisible(String text) {
        return INVISIBLE_TEXT + text + RESET;
    }
    
    public static String red(String text) {
        return RED + text + RESET;
    }
    
    public static String blue(String text) {
        return BLUE + text + RESET;
    }
    
    public static String green(String text) {
        return GREEN + text + RESET;
    }
    
    public static String orange(String text) {
        return ORANGE + text + RESET;
    }
}
