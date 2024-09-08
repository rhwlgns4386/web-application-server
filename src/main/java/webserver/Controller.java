package webserver;

import java.io.IOException;

public interface Controller {
    void service(HttpRequest httpRequest,HttpResponse httpResponse) throws IOException;

    default String getParam(HttpRequest httpRequest,String key) {
        return httpRequest.findData(key).orElseThrow(() -> new RuntimeException("잘못된 요청"));
    }
}
