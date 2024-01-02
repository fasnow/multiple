package org.fasnow.mutiple;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.5845.141 Safari/537.36";
    public static final String CONTENT_TYPE_URLENCODED = "application/x-www-form-urlencoded";
    public static String formatUrl(String url) throws Exception {
        StringBuilder urlBuilder = new StringBuilder();
        URL u = new URL(url);
        String proto = u.getProtocol();
        if(Objects.equals(proto, "")){
          throw new Exception("未知协议");
        }
        urlBuilder.append(u.getProtocol()).append("://");
        String userinfo = u.getUserInfo();
        if(!"".equals(userinfo) && userinfo!=null){
            urlBuilder.append(userinfo).append("@");
        }
        String host = u.getHost();
        if(Objects.equals(host, "")){
            throw new Exception("未知主机名");
        }else {
            urlBuilder.append(host);
        }
        int port = u.getPort();
        if(-1!=port){
            urlBuilder.append(":").append(port);
        }
        String path = u.getPath();
        if (path.endsWith("/")) {
            urlBuilder.append(path, 0, path.length() - 1);
        }else {
            urlBuilder.append(path);
        }
        return urlBuilder.toString();
    }

    public static Map<String,List<String>> getTargetUrls() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择文件");
        File selectedFile = fileChooser.showOpenDialog(null);
        List<String> items = new ArrayList<>();
        Map<String,List<String>> result = new HashMap<>();
        if (selectedFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmedLine = line.trim();
                    if (!trimmedLine.isEmpty()) {
                        items.add(trimmedLine);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.put(selectedFile.getAbsolutePath(),items);
            return result;
        }
        result.put("",items);
        return result;
    }

    public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    public static void setProxy() {
    }

    public static String getRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomStringBuilder = new StringBuilder(length);
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            randomStringBuilder.append(randomChar);
        }
        return randomStringBuilder.toString();
    }

    public static void setGlobalFocusTraversal(boolean focusTraversable, Node... nodes) {
        for (Node node : nodes) {
            node.setFocusTraversable(focusTraversable);

            if (node instanceof TabPane) {
                // 如果是 TabPane，则获取其所有 Tab，并递归设置 Tab 中的元素
                TabPane tabPane = (TabPane) node;
                for (Tab tab : tabPane.getTabs()) {
                    setGlobalFocusTraversal(focusTraversable, tab.getContent());
                }
            } else if (node instanceof Parent) {
                // 如果是 Parent，递归设置其子节点
                setGlobalFocusTraversal(focusTraversable, ((Parent) node).getChildrenUnmodifiable().toArray(new Node[0]));
            }
        }
    }
    public static String[] fields(String s) {
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

    public static String StringRepeat(String str,int i) {
        StringBuilder builder=new StringBuilder();
        for (int j = 0; j < i; j++) {
            builder.append(str);
        }
        return builder.toString();
    }

    public static List<String> getHostAddresses() throws SocketException {
        List<String> hostAddresses= new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();

            // 获取该网络接口上的所有IP地址
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();

                // 过滤掉IPv6地址
//                    if (!inetAddress.isLoopbackAddress() && !inetAddress.getHostAddress().contains(":")) {
//                System.out.println("网络接口: " + networkInterface.getDisplayName());
//                System.out.println("IP地址: " + inetAddress.getHostAddress());
                hostAddresses.add(inetAddress.getHostAddress());
                System.out.println();
//                    }
            }
        }
        return hostAddresses;
    }

    public static int versionCompare(String v1,String v2){
        ComparableVersion version1 = new ComparableVersion(v1);
        ComparableVersion version2 = new ComparableVersion(v2);
        int result = version1.compareTo(version2);
        return Integer.compare(result, 0);
    }
}

