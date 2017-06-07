package xzfm.common.boot.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wangxizhong on 2017/3/13.
 */
public class HttpClientHandler {
    private static PoolingHttpClientConnectionManager cm;
    private static String EMPTY_STR = "";
    private static String UTF_8 = "UTF-8";

    private static CloseableHttpClient client = null;

    private static void initHttpClientConnectionPool() {
        if (cm == null) {
            cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(50);// max connection by pool
            cm.setDefaultMaxPerRoute(5);// route max connection,default
        }
    }

    /**
     * get HttpClient from pool
     *
     * @return
     */
    private static CloseableHttpClient getHttpClient() {
        initHttpClientConnectionPool();
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * @param url
     * @return
     */
    public static Map httpGetRequest(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return getResult(httpGet);
    }

    public static Map httpGetRequest(String url, Map<String, Object> params) throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setPath(url);

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        uriBuilder.setParameters(pairs);

        HttpGet httpGet = new HttpGet(uriBuilder.build());
        return getResult(httpGet);
    }

    public static Map httpGetRequest(String url, Map<String, Object> headers, Map<String, Object> params)
            throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setPath(url);

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        uriBuilder.setParameters(pairs);

        HttpGet httpGet = new HttpGet(uriBuilder.build());
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return getResult(httpGet);
    }

    public static Map httpPostRequest(String url) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        return getResult(httpPost);
    }

    public static Map httpPostRequest(String url, Map<String, Object> params) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
        return getResult(httpPost);
    }

    public static Map httpPostRequest(String url, Map<String, Object> headers, Map<String, Object> params)
            throws IOException {
        HttpPost httpPost = new HttpPost(url);


        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
        return getResult(httpPost);
    }

    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
        ArrayList<NameValuePair> pairs = params.entrySet().stream().map(param -> new BasicNameValuePair(param.getKey(),
                String.valueOf(param.getValue()))).collect(Collectors.toCollection(ArrayList::new));

        return pairs;
    }

    /**
     * handler Http request
     *
     * @param request
     */
    private static Map getResult(HttpRequestBase request) {
        try {
            CloseableHttpClient httpClient = getHttpClient();
            RequestConfig requestConfig = RequestConfig.
                    custom().
                    setSocketTimeout(3000).
                    setConnectTimeout(3000).
                    setConnectionRequestTimeout(3000).build();//设置请求和传输超时时间

            request.setConfig(requestConfig);
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            String responseEntity = EntityUtils.toString(entity);
            Map responseMap = new HashMap();
            responseMap.put("code", response.getStatusLine().getStatusCode());
            responseMap.put("entity", responseEntity);
            responseMap.put("responseInfo", response.toString());
            response.close();
            return responseMap;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
