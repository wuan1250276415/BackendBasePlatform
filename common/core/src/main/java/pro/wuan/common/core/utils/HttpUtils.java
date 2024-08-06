package pro.wuan.common.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * 提供通过HTTP协议获取内容的方法 <br/>
 * 所有提供方法中的params参数在内部不会进行自动的url encode，如果提交参数需要进行url encode，请调用方自行处理
 *
 * @author admin
 * @Description: HTTP请求代理工具
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);


    /**
     * 支持的Http method
     */
    private static enum HttpMethod {
        POST, DELETE, GET, PUT, HEAD
    }


    /**
     * 发送HttpPost请求
     *
     * @param strURL
     *            服务地址
     * @param params
     *            json字符串,例如: "{ \"id\":\"12345\" }" ;其中属性名必须带双引号<br/>
     * @return 成功:返回json字符串<br/>
     */
    public static String sendPost(String strURL, String params) {
        BufferedReader reader = null;
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("content-type", "application/json");// 设置发送数据的格式
            // connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式<br>、　　　　　　//因为要登陆才可以执行请求，所以这里要带cookie的header
            //connection.setRequestProperty("Cookie", "geli-session=41b6d86db1e97df5:-60813064:16d0571e3e8:-3a58377431669b192d08; c=58ydwry; u=58zqg8z; pcsuv=1482230765738.a.46958170; u4ad=4031lfgby; UM_distinctid=16be539fa796b9-099f04a8a5445d-4144032b-1fa400-16be539fa7a7d3; visitedfid=22035D16260D14152; gr_user_id=b2a6251c-dddb-4502-91f9-0b6f10ccb260; favCar=%E5%A5%A5%E8%BF%AAA3_9550%7C%E5%A5%A5%E8%BF%AAA8L_7%7C%E7%A6%8F%E5%85%8B%E6%96%AFActive_25101%7C%E8%BD%A9%E9%80%B8_3996%7C%E5%88%9B%E7%95%8C_24483; locationWap=%7B%22expires%22%3A1568884707863%2C%22city%22%3A%22%E5%93%88%E5%B0%94%E6%BB%A8%E5%B8%82%22%2C%22cityUser%22%3A%22%E5%93%88%E5%B0%94%E6%BB%A8%E5%B8%82%22%2C%22cityCode%22%3A%22230100%22%2C%22proCode%22%3A%22230000%22%2C%22cityCodeUser%22%3A%22230100%22%2C%22proCodeUser%22%3A%22230000%22%7D; AplocationWap=%7B%22regionId%22%3A187%2C%20%22regionName%22%3A%22%E5%93%88%E5%B0%94%E6%BB%A8%22%7D; pcLocate=%7B%22proCode%22%3A%22440000%22%2C%22pro%22%3A%22%E5%B9%BF%E4%B8%9C%E7%9C%81%22%2C%22cityCode%22%3A%22441900%22%2C%22city%22%3A%22%E4%B8%9C%E8%8E%9E%E5%B8%82%22%2C%22dataType%22%3A%22user%22%2C%22expires%22%3A1571888929008%7D; pcautoLocate=%7B%22proId%22%3A5%2C%22cityId%22%3A6%2C%22url%22%3A%22%2F%2Fwww.pcauto.com.cn%2Fqcbj%2Fdg%2F%22%2C%22dataTypeAuto%22%3A%22user%22%7D; PClocation=6; pcuvdata=lastAccessTime=1570869719999|visits=289; channel=9396");
            //connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            //一定要用BufferedReader 来接收响应， 使用字节来接收响应的方法是接收不到内容的
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
            out.append(params);
            out.flush();
            out.close();
            // 读取响应
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            String res = "";
            while ((line = reader.readLine()) != null) {
                res += line;
            }
            reader.close();
            return res;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "error"; // 自定义错误信息
    }


    /**
     * 发送HttpPost请求
     *
     * @param strURL
     *            服务地址
     * @param params
     *            json字符串,例如: "{ \"id\":\"12345\" }" ;其中属性名必须带双引号<br/>
     * @return 成功:返回json字符串<br/>
     */
    public static String sendPost2(String strURL, String params,String authorization) {
        BufferedReader reader = null;
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("content-type", "application/json");// 设置发送数据的格式
            connection.setRequestProperty("authorization", authorization);
            // connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式<br>、　　　　　　//因为要登陆才可以执行请求，所以这里要带cookie的header
            //connection.setRequestProperty("Cookie", "geli-session=41b6d86db1e97df5:-60813064:16d0571e3e8:-3a58377431669b192d08; c=58ydwry; u=58zqg8z; pcsuv=1482230765738.a.46958170; u4ad=4031lfgby; UM_distinctid=16be539fa796b9-099f04a8a5445d-4144032b-1fa400-16be539fa7a7d3; visitedfid=22035D16260D14152; gr_user_id=b2a6251c-dddb-4502-91f9-0b6f10ccb260; favCar=%E5%A5%A5%E8%BF%AAA3_9550%7C%E5%A5%A5%E8%BF%AAA8L_7%7C%E7%A6%8F%E5%85%8B%E6%96%AFActive_25101%7C%E8%BD%A9%E9%80%B8_3996%7C%E5%88%9B%E7%95%8C_24483; locationWap=%7B%22expires%22%3A1568884707863%2C%22city%22%3A%22%E5%93%88%E5%B0%94%E6%BB%A8%E5%B8%82%22%2C%22cityUser%22%3A%22%E5%93%88%E5%B0%94%E6%BB%A8%E5%B8%82%22%2C%22cityCode%22%3A%22230100%22%2C%22proCode%22%3A%22230000%22%2C%22cityCodeUser%22%3A%22230100%22%2C%22proCodeUser%22%3A%22230000%22%7D; AplocationWap=%7B%22regionId%22%3A187%2C%20%22regionName%22%3A%22%E5%93%88%E5%B0%94%E6%BB%A8%22%7D; pcLocate=%7B%22proCode%22%3A%22440000%22%2C%22pro%22%3A%22%E5%B9%BF%E4%B8%9C%E7%9C%81%22%2C%22cityCode%22%3A%22441900%22%2C%22city%22%3A%22%E4%B8%9C%E8%8E%9E%E5%B8%82%22%2C%22dataType%22%3A%22user%22%2C%22expires%22%3A1571888929008%7D; pcautoLocate=%7B%22proId%22%3A5%2C%22cityId%22%3A6%2C%22url%22%3A%22%2F%2Fwww.pcauto.com.cn%2Fqcbj%2Fdg%2F%22%2C%22dataTypeAuto%22%3A%22user%22%7D; PClocation=6; pcuvdata=lastAccessTime=1570869719999|visits=289; channel=9396");
            //connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            //一定要用BufferedReader 来接收响应， 使用字节来接收响应的方法是接收不到内容的
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
            out.append(params);
            out.flush();
            out.close();
            // 读取响应
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            String res = "";
            while ((line = reader.readLine()) != null) {
                res += line;
            }
            reader.close();
            return res;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "error"; // 自定义错误信息
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private static String invokeUrl(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String encoding, HttpMethod method) {
        //构造请求参数字符串
        StringBuilder paramsStr = null;
        if (params != null) {
            paramsStr = new StringBuilder();
            Set<Map.Entry> entries = params.entrySet();
            for (Map.Entry entry : entries) {
                String value = (entry.getValue() != null) ? (String.valueOf(entry.getValue())) : "";
                paramsStr.append(entry.getKey() + "=" + value + "&");
            }
            //只有POST方法才能通过OutputStream(即form的形式)提交参数
            if (method != HttpMethod.POST) {
                url += "?" + paramsStr.toString();
            }
        }
        if (method != HttpMethod.POST) {
            url = url.substring(0, url.length() - 1);
        }
        URL uUrl = null;
        HttpURLConnection conn = null;
        BufferedWriter out = null;
        BufferedReader in = null;
        try {
            //创建和初始化连接
            uUrl = new URL(url);
            conn = (HttpURLConnection) uUrl.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod(method.toString());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //设置连接超时时间
            conn.setConnectTimeout(connectTimeout);
            //设置读取超时时间
            conn.setReadTimeout(readTimeout);
            //指定请求header参数
            if (headers != null && headers.size() > 0) {
                Set<String> headerSet = headers.keySet();
                for (String key : headerSet) {
                    conn.setRequestProperty(key, headers.get(key));
                }
            }

            if (paramsStr != null && method == HttpMethod.POST) {
                //发送请求参数
                out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), encoding));
                out.write(paramsStr.toString());
                out.flush();
            }

            //接收返回结果
            StringBuilder result = new StringBuilder();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
            if (in != null) {
                String line = "";
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            }
            return result.toString();
        } catch (Exception e) {
            logger.error("调用接口[" + url + "]失败！请求URL：" + url + "，参数：" + params, e);
            //处理错误流，提高http连接被重用的几率
            try {
                byte[] buf = new byte[100];
                InputStream es = conn.getErrorStream();
                if (es != null) {
                    while (es.read(buf) > 0) {
                        ;
                    }
                    es.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //关闭连接
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    /**
     * POST方法提交Http请求，语义为“增加” <br/>
     * 注意：Http方法中只有POST方法才能使用body来提交内容
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static String post(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.POST);
    }

    /**
     * POST方法提交Http请求，语义为“增加” <br/>
     * 注意：Http方法中只有POST方法才能使用body来提交内容
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param headers        请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static String post(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.POST);
    }

    /**
     * GET方法提交Http请求，语义为“查询”
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static String get(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.GET);
    }

    /**
     * GET方法提交Http请求，语义为“查询”
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param headers        请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static String get(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.GET);
    }

    /**
     * PUT方法提交Http请求，语义为“更改” <br/>
     * 注意：PUT方法也是使用url提交参数内容而非body，所以参数最大长度收到服务器端实现的限制，Resin大概是8K
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static String put(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.PUT);
    }

    /**
     * PUT方法提交Http请求，语义为“更改” <br/>
     * 注意：PUT方法也是使用url提交参数内容而非body，所以参数最大长度收到服务器端实现的限制，Resin大概是8K
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param headers        请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static String put(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.PUT);
    }

    /**
     * DELETE方法提交Http请求，语义为“删除”
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static String delete(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.DELETE);
    }

    /**
     * DELETE方法提交Http请求，语义为“删除”
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param headers        请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static String delete(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.DELETE);
    }

    /**
     * HEAD方法提交Http请求，语义同GET方法  <br/>
     * 跟GET方法不同的是，用该方法请求，服务端不返回message body只返回头信息，能节省带宽
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static String head(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.HEAD);
    }

    /**
     * HEAD方法提交Http请求，语义同GET方法  <br/>
     * 跟GET方法不同的是，用该方法请求，服务端不返回message body只返回头信息，能节省带宽
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param headers        请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static String head(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.HEAD);
    }



}

