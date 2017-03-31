package com.ch.web.action.frame;

import com.ch.frame.Globals;
import com.ch.web.base.ParentAction;
import com.ch.web.context.Menu;
import com.ch.web.context.ModelAndView2;
import com.ch.web.context.WebConfig;
import com.ch.web.context.WebContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class FrameAction extends ParentAction {
	@RequestMapping("main")
	public ModelAndView main(){
		String home = WebConfig.get().getHomeView();	
		//TODO:
		WebContext.get().setSession(Globals.ORGID, 1L);
		WebContext.get().setSession(Globals.USERID, 1L);
		if(!StringUtils.isBlank(home)){
			return new ModelAndView(home);
		}
		Menu m = WebConfig.get().getFirstView();
		if(m != null){
			return new ModelAndView2()
			.redirectTo("/menu/change?id=" + m.getId());
		}
		return new ModelAndView("notfound");
	}
    @RequestMapping("notfound")
    public ModelAndView2 notfound() {
        return new ModelAndView2(ModelAndView2.NOTFOUND_VIEW_NAME);
    }
}
