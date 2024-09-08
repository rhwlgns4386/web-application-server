package webserver;

import db.DataBase;
import java.io.IOException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCreateController implements Controller{

    private static final Logger log = LoggerFactory.getLogger(UserCreateController.class);
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        User user = new User(getParam(httpRequest,"userId"),
                getParam(httpRequest,"password"),getParam(httpRequest,"name")
                ,getParam(httpRequest,"email"));
        log.debug("user : {}", user);
        DataBase.addUser(user);

        httpResponse.sendRedirect("/index.html");
    }
}
