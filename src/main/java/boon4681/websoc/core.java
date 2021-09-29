package boon4681.websoc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class core extends JavaPlugin {
    public static ConsoleCommandSender logger;
    public static JavaPlugin This;
    public sock serv = new sock();
    public static Bfile config;
    @Override
    public void onEnable() {
        This = this;
        logger = This.getServer().getConsoleSender();
        config = new Bfile(This.getDataFolder(),"config.yml");
        if(config.load()!=null){
            Config configManager = new Config(config);
            configManager.makeExists();
            config.load();
            try {
                serv.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
            serv.init();
            Filter f = new log(serv);
            ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(f);
        }
    }

    @Override
    public void onDisable() {
        try {
            serv.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}