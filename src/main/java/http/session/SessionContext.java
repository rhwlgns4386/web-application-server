package http.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionContext {

    private static final Map<String,HttpSession> store=new ConcurrentHashMap<>();

    public static HttpSession getSession(String key){
        if(!store.containsKey(key)){
            store.put(key,new HttpSession(key));
        }
        return store.get(key);
    }

    public static void addSession(HttpSession httpSession){
        store.put(httpSession.getId(),httpSession);
    }
}
