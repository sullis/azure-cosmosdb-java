/*
 * The MIT License (MIT)
 * Copyright (c) 2018 Microsoft Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.microsoft.azure.cosmosdb.internal.directconnectivity;

import com.microsoft.azure.cosmosdb.internal.HttpConstants;
import com.microsoft.azure.cosmosdb.rx.internal.Strings;
import io.reactivex.netty.protocol.http.client.HttpRequestHeaders;
import io.reactivex.netty.protocol.http.client.HttpResponseHeaders;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

    private static Logger log = LoggerFactory.getLogger(HttpUtils.class);

    public static String urlEncode(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("failed to encode {}", url, e);
            throw new IllegalArgumentException("failed to encode url " + url, e);
        }
    }

    public static URI toURI(String uri) {
        try {
            return new URI(uri);
        } catch (Exception e) {
            log.error("failed to parse {}", uri, e);
            throw new IllegalArgumentException("failed to parse uri " + uri, e);
        }
    }

    public static Map<String, String> asMap(HttpResponseHeaders headers) {
        if (headers == null) {
            return new HashMap<>();
        }

        HashMap<String, String> map = new HashMap<>(headers.names().size());
        for (Map.Entry<String, String> entry : headers.entries()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public static Map<String, String> asMap(HttpRequestHeaders headers) {
        HashMap<String, String> map = new HashMap<>();
        if (headers == null) {
            return map;
        }
        for (Map.Entry<String, String> entry : headers.entries()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public static String getDateHeader(Map<String, String> headerValues) {
        if (headerValues == null) {
            return StringUtils.EMPTY;
        }

        // Since Date header is overridden by some proxies/http client libraries, we support
        // an additional date header 'x-ms-date' and prefer that to the regular 'date' header.
        String date = headerValues.get(HttpConstants.HttpHeaders.X_DATE);
        if (Strings.isNullOrEmpty(date)) {
            date = headerValues.get(HttpConstants.HttpHeaders.HTTP_DATE);
        }

        return date != null ? date : StringUtils.EMPTY;
    }
}
