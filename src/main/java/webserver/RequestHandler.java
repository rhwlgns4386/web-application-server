package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import java.util.Optional;
import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;
import db.DataBase;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            HttpRequest httpRequest = new HttpRequest(in);
            String url = httpRequest.getUrl();
            if ("/user/create".equals(url)) {

                User user = new User(getParam(httpRequest,"userId"),
                        getParam(httpRequest,"password"),getParam(httpRequest,"name")
                        ,getParam(httpRequest,"email"));
                log.debug("user : {}", user);
                DataBase.addUser(user);
                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos);
            } else if ("/user/login".equals(url)) {
                User user = DataBase.findUserById(getParam(httpRequest,"userId"));
                if (user != null) {
                    if (user.login(getParam(httpRequest,"password"))) {
                        DataOutputStream dos = new DataOutputStream(out);
                        response302LoginSuccessHeader(dos);
                    } else {
                        responseResource(out, "/user/login_failed.html");
                    }
                } else {
                    responseResource(out, "/user/login_failed.html");
                }
            } else if ("/user/list".equals(url)) {

                boolean logined=Boolean.parseBoolean(httpRequest.findHeader("Cookie").orElseGet(()->"false"));
                if (!logined) {
                    responseResource(out, "/user/login.html");
                    return;
                }

                Collection<User> users = DataBase.findAll();
                StringBuilder sb = new StringBuilder();
                sb.append("<table border='1'>");
                for (User user : users) {
                    sb.append("<tr>");
                    sb.append("<td>" + user.getUserId() + "</td>");
                    sb.append("<td>" + user.getName() + "</td>");
                    sb.append("<td>" + user.getEmail() + "</td>");
                    sb.append("</tr>");
                }
                sb.append("</table>");
                byte[] body = sb.toString().getBytes();
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length);
                responseBody(dos, body);
            } else if (url.endsWith(".css")) {
                responseCssResource(out, url);
            } else {
                responseResource(out, url);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private static String getParam(HttpRequest httpRequest,String key) {
        return httpRequest.findData(key).orElseThrow(() -> new RuntimeException("잘못된 요청"));
    }

    private void responseResource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void responseCssResource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200CssHeader(dos, body.length);
        responseBody(dos, body);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302LoginSuccessHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
