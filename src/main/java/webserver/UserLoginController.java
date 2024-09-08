package webserver;

import db.DataBase;
import java.io.IOException;
import model.User;

public class UserLoginController implements Controller{

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        User user = DataBase.findUserById(getParam(httpRequest,"userId"));
        if (user != null) {
            if (user.login(getParam(httpRequest,"password"))) {
                httpResponse.addHeader("Set-Cookie","logined=true");
                httpResponse.sendRedirect("/index.html");
            } else {
                httpResponse.forward("/user/login_failed.html");
            }
        } else {
            httpResponse.forward( "/user/login_failed.html");
        }
    }
}
