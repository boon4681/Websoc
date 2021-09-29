package boon4681.websoc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class sock {
    ServerSocket serv;
    Thread cons;
    String consoles;
    private void runThread(){
        if(cons!=null)cons.stop();
        cons = new Thread(new Runnable() {
            @Override
            public void run() {
                listen();
            }
        });
        cons.start();
    }
    public void init(){
        try {
            int port = Integer.parseInt(core.config.getData().get("Port").toString());
            serv = new ServerSocket(port);
            runThread();
            core.logger.sendMessage(Bstring.merger(ChatColor.GREEN,"[Websoc] Socket is Ready"));
        } catch (IOException e) {
            core.logger.sendMessage(Bstring.merger(ChatColor.RED,"[Websoc] Error ", Bstring.make_error(e)));
            e.printStackTrace();
        }
    }
    private class response{
        private PrintWriter out;
        response(PrintWriter out){
            this.out = out;
        }
        public void send(String a){
            core.logger.sendMessage(a);
            out.print(a);
            out.flush();
        }
        public void send(ChatColor color,String a){
            core.logger.sendMessage(Bstring.merger(color,a));
            out.print(a);
            out.flush();
        }
    }
    public void console(String a){
        this.consoles = a;
    }
    private void receive() throws Exception{
        Socket connection = serv.accept();
        Thread timeout;
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        PrintWriter outToClient = new PrintWriter(connection.getOutputStream());
        timeout = new Thread(() -> {
            try{
                Thread.sleep(Integer.parseInt(core.config.getData().get("Timeout").toString()));
                connection.close();
                inFromClient.close();
                outToClient.close();
            }catch (Exception e){
                core.logger.sendMessage(Bstring.merger(ChatColor.RED,"[Websoc] Thread Error ", Bstring.make_error(e)));
            }
            Thread.currentThread().stop();
        });
        timeout.start();
        response res = new response(outToClient);
        String client_in = inFromClient.lines().collect(Collectors.joining("\r\n"));
        Matcher match = Pattern.compile("Authorization: (.*)").matcher(client_in);
        String ip = (((InetSocketAddress) connection.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
        if(match.find()){
            if(match.group(1).equals(core.config.getData().get("Authorization"))){
                Matcher command = Pattern.compile("Command: (.*)").matcher(client_in);
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
                    outToClient.flush();
                }else{
                    res.send(ChatColor.RED,Bstring.merger("[Websoc] ",connection.getInetAddress(),"Command not Found"));
                }
            }else res.send(ChatColor.RED,Bstring.merger("[Websoc] ",connection.getInetAddress(),"Authorization Error"));
        }else{
            res.send(ChatColor.RED,Bstring.merger("[Websoc] ",connection.getInetAddress(),"Authorization Error"));
        }
        connection.close();
        inFromClient.close();
        outToClient.close();
    }
    private void listen() {
        try{
            while (true){
                receive();
            }
        }catch (Exception e){
            core.logger.sendMessage(Bstring.merger(ChatColor.RED,"[Websoc] Thread Error ", Bstring.make_error(e)));
        }
    }
    public void stop() throws IOException {
        if(serv!=null){
            cons.stop();
            serv.close();
            core.logger.sendMessage(Bstring.merger(ChatColor.GOLD,"[Websoc] Socket closed"));
        }
    }
}
