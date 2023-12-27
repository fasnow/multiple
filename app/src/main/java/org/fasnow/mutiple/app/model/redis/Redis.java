package org.fasnow.mutiple.app.model.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.fasnow.mutiple.app.Utils;
import org.fasnow.mutiple.app.ui.controller.MainController;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandExecutor;
import org.redisson.config.Config;
import redis.clients.jedis.Connection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.util.SafeEncoder;

import javax.rmi.CORBA.Util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    public Jedis getJedis() {
        return jedis;
    }

    public boolean isConnected() {
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

    public String getInfo() {
        return jedis.info();
    }

    public String getDir() {
        List<String> result = jedis.configGet("dir");
        return result.size() > 1 ? result.get(1) : "";
    }

    public String getDBFilename() {
        List<String> result = jedis.configGet("dbfilename");
        return result.size() > 1 ? result.get(1) : "";
    }

    public String setDir(String dir) {
        return jedis.configSet("dir", dir);
    }

    public String setDBFilename(String dbFilename) {
        return jedis.configSet("dbfilename", dbFilename);
    }

    public String setKey(String key, String value) {
        return jedis.set(key, value);
    }

    public long delKey(String key) {
        return jedis.del(key);
    }

    public String save() {
        return jedis.save();
    }

    public String exec(String cmd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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

    public String getVersion(){
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

    public String getOsName(){
        String redisInfo = getInfo();
        return getVersion(redisInfo);
    }
}
