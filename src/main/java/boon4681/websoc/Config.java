package boon4681.websoc;

import java.util.Arrays;
import java.util.UUID;

public class Config {
    private Bfile config;
    private enum dconfig{
        Authorization("string", UUID.randomUUID().toString()),
        Port("string",3000),
        Timeout("string",1500)
        ;
        private class ifig{
            private String type;
            private Object Default;
            private String path;
            public ifig(String type,Object Default,String path){
                this.type = type;
                this.Default = Default;
                this.path = path;
            }
        }
        private ifig Ifig;
        dconfig(String type,Object Default) {
            this.Ifig = new ifig(type, Default,name().replace("__","."));
        }
        public static void makeExists(Bfile bfile){
            for (dconfig s : values()){
                if(!bfile.getData().contains(s.Ifig.path)){
                    bfile.set(s.Ifig.path,s.Ifig.Default);
                }
            }
        }
    }
    public Config(Bfile defaultConfig){
        this.config = defaultConfig;
    }
    public void makeExists(){
        dconfig.makeExists(this.config);
    }
}