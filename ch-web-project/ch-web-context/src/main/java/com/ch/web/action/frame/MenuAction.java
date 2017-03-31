package com.ch.web.action.frame;

import com.ch.web.base.ParentAction;
import com.ch.web.context.Menu;
import com.ch.web.context.ModelAndView2;
import com.ch.web.context.WebConfig;
import com.ch.web.context.WebContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 菜单处理
 *
 * @author Administrator
 */
@Controller
@RequestMapping("/menu")
public class MenuAction extends ParentAction {
    /**
     * 顶级菜单跳转
     *
     * @param id
     * @return
     */
    @RequestMapping("/change")
    public ModelAndView change(String id) {
        Menu m = WebConfig.get().getMenu(id);
        if (m == null)
            return new ModelAndView2("notfound").setError("未找到菜单配置#" + id);
        WebContext.get().removeSession("_topmenu");
        WebContext.get().removeSession("_secmenu");
        WebContext.get().removeSession("_threemenu");
        if (WebConfig.get().getMenuLevel() > 2) {
            if (m.getParent() == null) {
                WebContext.get().setSession("_topmenu", m.getId());
            } else if (m.getParent() != null) {
                if (m.getParent().getParent() != null) {
                    WebContext.get().setSession("_threemenu", m.getId());
                    WebContext.get().setSession("_secmenu", m.getParent().getId());
                    WebContext.get().setSession("_topmenu", m.getParent().getParent().getId());
                } else {
                    WebContext.get().setSession("_secmenu", m.getId());
                    WebContext.get().setSession("_topmenu", m.getParent().getId());
                }
            }
        } else {
            if (m.getParent() == null) {
                WebContext.get().setSession("_secmenu", m.getId());
            } else {
                WebContext.get().setSession("_secmenu", m.getParent().getId());
                WebContext.get().setSession("_threemenu", m.getId());
            }
        }
        WebContext.get().setSession("_selmenu", m.getId());
        if (StringUtils.isBlank(m.getView()))
            return new ModelAndView2(ModelAndView2.NOTFOUND_VIEW_NAME)
        			.setError("未找到菜单的视图")
            		.addObject("_noext", true);
        return new ModelAndView2(m.getView()).addObject("_noext", m.isNoext());
    }

}
