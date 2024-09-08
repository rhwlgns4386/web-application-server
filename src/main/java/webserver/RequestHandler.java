package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;

import java.util.HashMap;
import java.util.Map;
import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Map<String,Controller> handlerMapper=new HashMap<>();
    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        handlerMapper.put("/user/create",new UserCreateController());
        handlerMapper.put("/user/login",new UserLoginController());
        handlerMapper.put("/user/list",new UserListController());
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = new HttpResponse(out);
            String url = httpRequest.getUrl();
            if(handlerMapper.containsKey(url)){
                Controller controller = handlerMapper.get(url);
                controller.service(httpRequest,httpResponse);
            } else if (url.endsWith(".css")) {
                httpResponse.forward(url);
                return;
            }
            httpResponse.forward(url);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
