package webserver;

import db.DataBase;
import java.io.IOException;
import java.util.Collection;
import model.User;

public class UserListController implements Controller{

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        boolean logined=Boolean.parseBoolean(httpRequest.findHeader("Cookie").orElseGet(()->"false"));
        if (!logined) {
            httpResponse.forward("/user/login.html");
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
        httpResponse.write200Body(body);
    }
}
