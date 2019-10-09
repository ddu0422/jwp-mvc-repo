package nextstep.mvc.tobe.viewresolver;

import nextstep.mvc.tobe.view.JsonView;
import nextstep.mvc.tobe.view.ModelAndView;
import nextstep.mvc.tobe.view.View;

public class JsonViewResolver implements ViewResolver {

    @Override
    public boolean supports(ModelAndView mav) {
        return !mav.isViewNameExist();
    }

    @Override
    public View resolve() {
        return new JsonView();
    }
}
