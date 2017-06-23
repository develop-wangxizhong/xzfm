package xzfm.util;

import xzfm.boot.httpclient.HttpClientHandler;

/**
 * Created by wangxizhong on 2017/6/23.
 */
public class HttpClientUtil extends ThreadLocalUtilHolder<HttpClientHandler> {
    @Override
    protected HttpClientHandler newInstance() {
        return new HttpClientHandler();
    }

    private static class HttpClientSingletonHolder {
        static HttpClientUtil instance = new HttpClientUtil();
    }

    public static HttpClientHandler getHttpClientInstance() {
        return HttpClientSingletonHolder.instance.get();
    }
}
