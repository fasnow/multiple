package org.fasnow.mutiple.app;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Log {
    public static String getFormatDate(){
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentTime.format(formatter);
    }
    public static String formatStdoutWithPlugin(String pluginName, String msg){
        return String.format("[%s] [info] [%s] %s\n\n",getFormatDate(),pluginName,msg);
    }

    public static String formatStdout(boolean passed, String targetUrl, String vulName, String featureField){
        return String.format("[%s] [info] [%s] %s%s\n\n",
                getFormatDate(),
                passed?"+":"-",
                targetUrl+(passed?" 存在 ":" 不存在 ")+vulName,
                passed && !"".equals(featureField)?"\n\t"+featureField:""
        );
    }

    public static String formatStdout(String msg){
        msg = msg.replaceAll("\n","\n\t");
        String breakSep = "";
        if(msg.length()>2 && msg.substring(0,msg.length()-2).contains("\n")){
            breakSep="\n\t";
        }
        return String.format("[%s] [info] %s\n\n",
                getFormatDate(),
                breakSep+msg
        );
    }


    public static String formatStderrWithPlugin(String pluginName, Exception e){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String tmp = stringWriter.toString();
        String out = tmp.substring(0,tmp.length()-1);
        return String.format("[%s] [error] [%s]\n\t%s\n",getFormatDate(),pluginName,
                out.replace("\n", "\n\t")
        );
    }

    public static String formatStderr(Exception e){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String tmp = stringWriter.toString();
        String out = tmp.substring(0,tmp.length()-1);
        return String.format("[%s] [error]\n\t%s\n",getFormatDate(),
                out.replace("\n", "\n\t")
        );
    }
    private static String[] fields(String s) {
        // 使用正则表达式匹配带引号的参数
        Pattern pattern = Pattern.compile("\"[^\"]*\"|\\S+");
        Matcher matcher = pattern.matcher(s);

        // 使用 StringBuilder 临时存储匹配到的参数
        StringBuilder resultBuilder = new StringBuilder();

        // 将匹配到的参数添加到 StringBuilder
        while (matcher.find()) {
            resultBuilder.append(matcher.group()).append(",");
        }

        // 移除最后一个逗号并分割字符串返回
        String result = resultBuilder.toString().replaceAll(",$", "");
        return result.split(",");
    }
}
