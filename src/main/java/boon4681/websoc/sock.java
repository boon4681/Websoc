package boon4681.websoc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class sock extends WebSocketServer {
    String consoles;
    public void console(String a){
        this.consoles = a;
    }
    public sock(InetSocketAddress address){
        super(address);
        setReuseAddr(true);
    }
    private class response{
        private WebSocket out;
        response(WebSocket out){
            this.out = out;
        }
        public void send(String a){
            core.logger.sendMessage(a);
            out.send(a);
        }
        public void send(ChatColor color,String a){
            core.logger.sendMessage(Bstring.merger(color,a));
            out.send(a);
        }
    }
    @Override
    public void onOpen(WebSocket conn, ClientHandshake clientHandshake) {
        conn.send("Websoc Welcome!");
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket conn, String s) {
        response res = new response(conn);
        String ip = (((InetSocketAddress) conn.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
        Matcher match = Pattern.compile("Authorization: (.*)").matcher(s);
        if(match.find()){
            if(match.group(1).equals(core.config.getData().get("Authorization"))){
                Matcher command = Pattern.compile("Command: (.*)").matcher(s);
                if(command.find()){
                    String c = command.group(1);
                    ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();
                    core plugin = (core) Bukkit.getPluginManager().getPlugin("Websoc");
                    core.logger.sendMessage(Bstring.merger(ChatColor.GOLD,"[Websoc] Execute Command -> ",c," From ",ip));
                    try {
                        @SuppressWarnings("unused")
                        boolean success = Bukkit.getScheduler().callSyncMethod(plugin, () -> Bukkit.dispatchCommand(sender, c)).get();
                        res.send(ChatColor.GREEN,Bstring.merger("[Websoc] ","Success Command Execution -> ",c,"\n",this.consoles));
                    } catch (Exception e) {
                        core.logger.sendMessage(Bstring.merger(ChatColor.RED,"[Websoc] ",ip," Error Command Execution -> ",c,"\n", Bstring.make_error(e)));
                        res.send(ChatColor.RED,Bstring.merger("[Websoc] ","Error Command Execution -> ",c));
                    }
                }else{
                    res.send(ChatColor.RED,Bstring.merger("[Websoc] ",ip," Command not Found"));
                }
            }else res.send(ChatColor.RED,Bstring.merger("[Websoc] ",ip," Authorization Error"));
        }else{
            res.send(ChatColor.RED,Bstring.merger("[Websoc] ",ip," Authorization Error"));
        }
    }

    @Override
    public void onError(WebSocket conn, Exception e) {

    }

    @Override
    public void onStart() {
        core.logger.sendMessage(Bstring.merger(ChatColor.GREEN,"[Websoc] Socket is Ready"));
    }
}
