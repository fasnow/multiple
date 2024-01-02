package org.fasnow.mutiple.model.redis;

import org.fasnow.mutiple.Utils;
import org.fasnow.mutiple.ui.controller.MainController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//执行redis任意命令
class ModuleCommand implements ProtocolCommand {

    private final byte[] raw;

    public ModuleCommand(String alt) {
        raw = SafeEncoder.encode(alt);
    }
    public ModuleCommand() {
        raw = SafeEncoder.encode("");
    }

    @Override
    public byte[] getRaw() {
        return raw;
    }
}
public class Redis {
    public static final String PLUGIN_NAME = "redis";
    private static MainController mainController;
    private String target;
    private Jedis jedis;

    public void connect(String host, int port, String password, int timeout) throws Exception {
        jedis = new Jedis(host, port, timeout);
        if (!"".equals(password)) jedis.auth(password);
    }

    public Jedis getJedis() throws Exception  {
        return jedis;
    }

    public boolean isConnected()  {
        if (jedis == null) {
            return false;
        }
        return jedis.isConnected();
    }

    public void close() {
        if (jedis != null) jedis.close();
    }

    public static void setMainController(MainController controller) {
        mainController = controller;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getInfo() throws Exception {
        return jedis.info();
    }

    public String getDir() throws Exception  {
        List<String> result = jedis.configGet("dir");
        return result.size() > 1 ? result.get(1) : "";
    }

    public String getDBFilename() throws Exception  {
        List<String> result = jedis.configGet("dbfilename");
        return result.size() > 1 ? result.get(1) : "";
    }

    public String setDir(String dir) throws Exception  {
        return jedis.configSet("dir", dir);
    }

    public String setDBFilename(String dbFilename) throws Exception  {
        return jedis.configSet("dbfilename", dbFilename);
    }

    public String setKey(String key, String value) throws Exception  {
        return jedis.set(key, value);
    }

    public long delKey(String key) throws Exception  {
        return jedis.del(key);
    }

    public String save() throws Exception {
        return jedis.save();
    }

    public String exec(String cmd) throws Exception {
        String[] fields = Utils.fields(cmd);
        String command = fields[0].toUpperCase();
        Object response = jedis.sendCommand(new ModuleCommand(command),Arrays.copyOfRange(fields, 1, fields.length));
        if (response instanceof List) {
            List<Object> objList = (List) response;
            //类似 config get dir 结果
            if(objList.size()==2 && objList.get(1) instanceof byte[]){
                return new String((byte[])objList.get(1));
            }

            //类似 command 结果
            return covert((List<Object>)response,0);
        } else if (response instanceof byte[]) {
            //类似 config set dir 结果
           return new String((byte[]) response);
        } else if (response ==null) {
            //类似 get a 结果返回nil
            return "";
        } else {
            Collections.singletonList(new String((byte[]) response));
        }
        return "";
    }

    private String covert(List<Object> list,int depth){
        if(null==list||list.size()==0)return "";
        StringBuilder builder = new StringBuilder();
        String spaces = Utils.StringRepeat("    ",depth);
        for (Object obj:list) {
            if(obj instanceof  Long){
                builder.append(obj).append("\n");
            }else if (obj instanceof byte[]){
                builder.append(new String((byte[]) obj)).append("\n");
            }else if (obj instanceof List){
                builder.append(covert((List<Object>)obj,depth+1));
            }
        }
        return builder.toString();
    }

    public String getVersion(String redisInfo){
        String[] lines = redisInfo.split("\\r?\\n");
        for (String line : lines) {
            if (line.startsWith("# Server") ||line.startsWith("redis_version") ) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    return parts[1].trim();
                }
            }
        }
        return "";
    }

    public String getVersion() throws Exception {
        String redisInfo = getInfo();
        return getVersion(redisInfo);
    }

    public String getOsName(String redisInfo){
        String[] lines = redisInfo.split("\\r?\\n");
        for (String line : lines) {
            if (line.startsWith("os") ) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    return parts[1].trim();
                }
            }
        }
        return "";
    }

    public String getOsName() throws Exception {
        String redisInfo = getInfo();
        return getVersion(redisInfo);
    }
}
