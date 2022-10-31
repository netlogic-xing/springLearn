package netlogic.demo.spring.web.view;

import netlogic.demo.spring.web.base.ViewRenderer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TemplateViewRenderer implements ViewRenderer {
    private String defaultViewType = "thymeleaf";

    @Override
    public void render(String viewPath, HttpServletRequest req, HttpServletResponse resp, Object model) {
        try {
            req.getRequestDispatcher(viewPath+".html").include(req, resp);
        } catch (ServletException | IOException e) {
            throw new ViewRenderException(e);
        }
    }
}
