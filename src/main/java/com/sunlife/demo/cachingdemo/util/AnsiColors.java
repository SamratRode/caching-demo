package com.sunlife.demo.cachingdemo.util;

public class AnsiColors {
    
    // Reset
    public static final String RESET = "\u001B[0m";
    
    // Regular Colors
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    
    // Bold
    public static final String BLACK_BOLD = "\u001B[1;30m";
    public static final String RED_BOLD = "\u001B[1;31m";
    public static final String GREEN_BOLD = "\u001B[1;32m";
    public static final String YELLOW_BOLD = "\u001B[1;33m";
    public static final String BLUE_BOLD = "\u001B[1;34m";
    public static final String PURPLE_BOLD = "\u001B[1;35m";
    public static final String CYAN_BOLD = "\u001B[1;36m";
    public static final String WHITE_BOLD = "\u001B[1;37m";
    
    // Background
    public static final String BLACK_BACKGROUND = "\u001B[40m";
    public static final String RED_BACKGROUND = "\u001B[41m";
    public static final String GREEN_BACKGROUND = "\u001B[42m";
    public static final String YELLOW_BACKGROUND = "\u001B[43m";
    public static final String BLUE_BACKGROUND = "\u001B[44m";
    public static final String PURPLE_BACKGROUND = "\u001B[45m";
    public static final String CYAN_BACKGROUND = "\u001B[46m";
    public static final String WHITE_BACKGROUND = "\u001B[47m";
    
    // Emojis for cache status
    public static final String CACHE_HIT = "🟢";
    public static final String CACHE_MISS = "🟡";
    public static final String DB_FETCH = "🔴";
    public static final String REDIS_ICON = "📦";
    public static final String MEMCACHED_ICON = "💾";
    public static final String HAZELCAST_ICON = "⚡";
    
    private AnsiColors() {
        // Utility class
    }
    
    public static String colorize(String text, String color) {
        return color + text + RESET;
    }
    
    public static String cacheHit(String message) {
        return CACHE_HIT + " " + colorize(message, GREEN_BOLD);
    }
    
    public static String cacheMiss(String message) {
        return CACHE_MISS + " " + colorize(message, YELLOW_BOLD);
    }
    
    public static String dbFetch(String message) {
        return DB_FETCH + " " + colorize(message, RED_BOLD);
    }
    
    public static String redis(String message) {
        return REDIS_ICON + " " + colorize(message, BLUE_BOLD);
    }
    
    public static String memcached(String message) {
        return MEMCACHED_ICON + " " + colorize(message, PURPLE_BOLD);
    }
    
    public static String hazelcast(String message) {
        return HAZELCAST_ICON + " " + colorize(message, CYAN_BOLD);
    }
}
