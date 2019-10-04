package nextstep.mvc.tobe.viewresolver;

import nextstep.mvc.tobe.view.JspView;
import nextstep.mvc.tobe.view.ModelAndView;
import nextstep.mvc.tobe.view.View;
import nextstep.mvc.tobe.viewresolver.exception.ViewNameNotInitializedException;

public class JspViewResolver implements ViewResolver {
    private String viewName;

    @Override
    public boolean supports(ModelAndView mav) {
        if (mav.isViewNameExist()) {
            this.viewName = mav.getViewName();
            return true;
        }

        return false;
    }

    @Override
    public View resolve() {
        checkInit();
        return new JspView(viewName);
    }

    private void checkInit() {
        if (viewName == null) {
            throw new ViewNameNotInitializedException("view name not initialized");
        }
    }
}
