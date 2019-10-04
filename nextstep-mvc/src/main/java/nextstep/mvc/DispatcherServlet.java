package nextstep.mvc;

import nextstep.mvc.exception.AdapterNotFoundException;
import nextstep.mvc.exception.HandlerNotFoundException;
import nextstep.mvc.tobe.adapter.HandlerAdapter;
import nextstep.mvc.tobe.view.ModelAndView;
import nextstep.mvc.tobe.view.View;
import nextstep.mvc.tobe.viewresolver.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@WebServlet(name = "dispatcher", urlPatterns = "/", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

    private List<HandlerMapping> handlerMappings;
    private List<HandlerAdapter> handlerAdapters;
    private List<ViewResolver> viewResolvers;

    public DispatcherServlet(List<HandlerMapping> handlerMappings,
                             List<HandlerAdapter> handlerAdapters,
                             List<ViewResolver> viewResolvers) {
        this.handlerMappings = handlerMappings;
        this.handlerAdapters = handlerAdapters;
        this.viewResolvers = viewResolvers;
    }

    @Override
    public void init() {
        for (HandlerMapping handlerMapping : handlerMappings) {
            handlerMapping.initialize();
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestUri = req.getRequestURI();
        logger.debug("Method : {}, Request URI : {}", req.getMethod(), requestUri);
        try {
            Object handler = findHandler(req);
            HandlerAdapter handlerAdapter = findAdapter(handler);
            ModelAndView mav = handlerAdapter.handle(req, resp, handler);
            View view = findView(mav);
            view.render(mav.getModel(), req, resp);
        } catch (Exception e) {
            logger.error("Exception : {}", e);
            throw new ServletException(e.getMessage());
        }
    }

    private Object findHandler(HttpServletRequest req) {
        return handlerMappings.stream()
                .map(handlerMapping -> handlerMapping.getHandler(req))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new HandlerNotFoundException("handler not found"));
    }

    private HandlerAdapter findAdapter(Object handler) {
        return handlerAdapters.stream()
                .filter(handlerAdapter -> handlerAdapter.isSupport(handler))
                .findFirst()
                .orElseThrow(() -> new AdapterNotFoundException("adapter not found"));
    }

    private View findView(ModelAndView mav) {
        return viewResolvers.stream()
                .filter(viewResolver -> viewResolver.supports(mav))
                .map(ViewResolver::resolve)
                .findFirst()
                .orElseThrow(() -> new RuntimeException());
    }
}