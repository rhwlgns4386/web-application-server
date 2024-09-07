package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {

    private Map<String,String> headerStore;
    private Map<String,String> params;
    private String url;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        String line = br.readLine();
        if (line == null) {
            return;
        }

        String[] tokens = getTokens(line);
        this.url=getDefaultUrl(tokens);
        this.headerStore=parseHeader(br);
        this.params=parsePrams(tokens, br);
    }

    private static String[] getTokens(String line) {
        return line.split(" ");
    }

    private Map<String,String> parsePrams(String[] tokens, BufferedReader br) throws IOException {
        if(HttpMethod.POST.isSameAs(tokens[0])){
            String body = IOUtils.readData(br, Integer.parseInt(headerStore.getOrDefault("Content-Length","0")));
            return HttpRequestUtils.parseQueryString(body);
        }else if(HttpMethod.GET.isSameAs(tokens[0])){
          return HttpRequestUtils.parseQueryString(url);
        }
        return new HashMap<>();
    }

    private Map<String,String> parseHeader(BufferedReader br) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        String line;
        while (!(line= br.readLine()).equals("")) {
            String[] keyValue = line.split(":");

            if(keyValue.length==0){
                continue;
            }

            map.put(keyValue[0].trim(),keyValue[1].trim());
        }
        return map;
    }

    private String getDefaultUrl(String[] tokens) {
        String url = tokens[1];
        if (url.equals("/")) {
            url = "/index.html";
        }
        return url;
    }

    public Optional<String> findHeader(String header){
        if(headerStore.containsKey(header.trim()))Optional.of(headerStore.get(header.trim()));
        return Optional.empty();
    }

    public String getUrl(){
        return this.url;
    }

    public Optional<String> findData(String key){
        if(params.containsKey(key))return Optional.of(params.get(key));
        return Optional.empty();
    }

    private enum HttpMethod{
        GET("GET"),
        POST("POST");

        private String value;

        HttpMethod(String value) {
            this.value = value;
        }

        public boolean isSameAs(String value){
            return this.value.equals(value.trim().toUpperCase());
        }
    }
}
