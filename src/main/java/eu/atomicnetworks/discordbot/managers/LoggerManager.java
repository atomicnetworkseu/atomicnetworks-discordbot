package eu.atomicnetworks.discordbot.managers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Kacper Mura
 * Copyright (c) 2021 atomicnetworks ✨
 * This code is available under the MIT License.
 *
 */
public class LoggerManager {

    public LoggerManager() {
        
    }
    
    public void sendInfo(String text) {
        System.out.println("[" + getTimestamp(System.currentTimeMillis()) +" | INFO] " + text);
    }
    
    public void sendWarning(String text) {
        System.out.println("[" + getTimestamp(System.currentTimeMillis()) +" | WARNING] " + text);
    }
    
    public void sendError(String text) {
        System.out.println("[" + getTimestamp(System.currentTimeMillis()) +" | ERROR] " + text);
    }
    
    public void sendDebug(String text) {
        System.out.println("[" + getTimestamp(System.currentTimeMillis()) +" | DEBUG] " + text);
    }
    
    private String getTimestamp(Long timemillies) {
        Date date = new Date(timemillies);
        DateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = format.format(date);
        return formatted;
    }
    
}
