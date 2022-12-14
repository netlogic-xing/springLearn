package netlogic.demo.spring.web.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import netlogic.demo.spring.web.base.ViewRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JsonViewRenderer implements ViewRenderer {
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void render(String viewPath, HttpServletRequest req, HttpServletResponse resp, Object model) {
        resp.setContentType("application/json");
        try {
            objectMapper.writeValue(resp.getWriter(), model);
        } catch (IOException e) {
            throw new ViewRenderException(e);
        }
    }
}
