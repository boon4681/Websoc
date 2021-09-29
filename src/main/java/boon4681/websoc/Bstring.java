package boon4681.websoc;

import org.bukkit.ChatColor;

public class Bstring {
    public static String merger(Object... string){
        String result = "";
        try{
            for (Object s : string) {
                if (s instanceof String) result += s;
                else if(s instanceof ChatColor) result += s;
            }
            return result;
        }catch (Exception e) {
            error(e);
            return null;
        }
    }
    public static String make_error(Exception e){
        StackTraceElement[] stack = e.getStackTrace();
        String exception = "";
        for (StackTraceElement s : stack) {
            exception = exception + s.toString() + "\n\t\t";
        }
        return (exception);
    }
    public static void print(Object string){
        core.logger.sendMessage(merger(ChatColor.GOLD,"[WLME] ",string.toString()));
    }
    public static void error(Exception string){
        try{
            core.logger.sendMessage(merger(ChatColor.GOLD,"[WLME]"));
            string.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}