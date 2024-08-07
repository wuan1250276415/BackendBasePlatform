package pro.wuan.common.db.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.service.IBaseService;


public class BaseController<Service extends IBaseService,Dao extends IBaseMapper,T extends IDModel> {

    @Autowired
    protected Service service;

    protected HttpServletRequest request;

    protected HttpServletResponse response;

    protected HttpSession session;

    protected ModelAndView modelAndView;

    /**
     * @description 绑定默认参数
     * @param request
     * @param response
     */
    @ModelAttribute
    public void initDefaultParam(HttpServletRequest request,HttpServletResponse response,ModelAndView modelAndView) {
        this.request = request;
        this.response = response;
        this.session = request.getSession();
        this.modelAndView = modelAndView;
    }

}
