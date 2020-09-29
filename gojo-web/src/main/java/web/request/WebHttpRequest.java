package web.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class WebHttpRequest extends HttpServletRequestWrapper {

    private Map<String, String[]> params = new HashMap<String, String[]>();

    @SuppressWarnings("unchecked")
    public WebHttpRequest(HttpServletRequest request) {
        super(request);
        params.putAll(request.getParameterMap());
    }

    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        return values == null || values.length == 0 ? null : values[0];
    }

    @Override
    public String[] getParameterValues(String key) {
        return params.get(key);
    }

    public void setParameter(String key, String value) {
        if (value == null)
            return;
        params.put(key, new String[]{value});
    }

    public void setParameter(String key, String[] value) {
        if (value == null)
            return;
        params.put(key, value);
    }

    public void setParameter(String key, Object value) {
        if (value == null)
            return;
        params.put(key, new String[]{String.valueOf(value)});
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Enumeration<String> enumeration = new WebEnumeration<String>(params.keySet().iterator());
        return enumeration;
    }

}