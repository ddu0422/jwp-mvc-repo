package nextstep.mvc.tobe.adapter;

import nextstep.mvc.asis.Controller;
import nextstep.mvc.tobe.view.JspView;
import nextstep.mvc.tobe.view.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LegacyHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean isSupport(Object handler) {
        return handler instanceof Controller;
    }

    @Override
    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        ModelAndView mav = new ModelAndView();
        String viewName = ((Controller) handler).execute(req, resp);
        mav.setViewName(viewName);

        return mav;
    }
}