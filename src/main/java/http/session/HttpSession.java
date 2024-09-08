package http.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private String id;
    private Map<String,Object> attributes=new HashMap<>();

    public HttpSession(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setAttributes(String key,Object item){
        attributes.put(key,item);
    }

    public Object getAttributes(String key){
        return attributes.get(key);
    }

    public void removeAttributes(String key){
        attributes.remove(key);
    }

    public void invalidate(){
        attributes.clear();
    }

}
