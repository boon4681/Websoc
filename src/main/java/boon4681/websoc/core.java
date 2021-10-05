package boon4681.websoc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.InetSocketAddress;

public class core extends JavaPlugin {
    public static ConsoleCommandSender logger;
    public static JavaPlugin This;
    public static sock serv;
    public static Bfile config;
    private Thread serverRun;
    @Override
    public void onEnable() {
        This = this;
        logger = This.getServer().getConsoleSender();
        config = new Bfile(This.getDataFolder(),"config.yml");
        if(config.load()!=null){
            Config configManager = new Config(config);
            configManager.makeExists();
            config.load();
            serverRun = null;
            serv = new sock(new InetSocketAddress("0.0.0.0", 3515));
            serverRun = new Thread(new Runnable() {
                @Override
                public void run() {
                    serv.run();
                }
            });
            serverRun.start();
            Filter f = new log(serv);
            ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(f);
        }
    }

    @Override
    public void onDisable() {
        try {
            serv.stop();
            serverRun = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}