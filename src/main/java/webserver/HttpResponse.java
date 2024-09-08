package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private DataOutputStream dos;
    private Map<String,String> responseHeaders=new HashMap<>();
    public HttpResponse(OutputStream outputStream) {
        dos = new DataOutputStream(outputStream);
    }

    public void forward(String url) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        this.responseHeaders.put("Content-Length",body.length+"");
        if(url.endsWith(".html")){
            addContentType("text/html;charset=utf-8");
        }else if(url.endsWith(".css")){
            addContentType("text/css;charset=utf-8");
        }
        write200Body(body);
    }

    private void addContentType(String value) {
        this.responseHeaders.put("Content-Type",value);
    }

    public void write200Body(byte[] body) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        writeResponseHeader();
        responseBody(dos, body);
    }

    public void sendRedirect(String url) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
        this.responseHeaders.put("Location",url);
        writeResponseHeader();
    }

    private void writeResponseHeader() throws IOException {
        for (String s : responseHeaders.keySet()) {
            dos.writeBytes(String.format("%s: %s \r\n",s,responseHeaders.get(s)));
        }
        dos.writeBytes("\r\n");
    }

    public void addHeader(String key,String value){
        responseHeaders.put(key,value);
    }

    private void responseBody(DataOutputStream dos, byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.writeBytes("\r\n");
        dos.flush();
    }

}
