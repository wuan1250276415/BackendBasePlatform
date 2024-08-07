package pro.wuan.common.web.validator.wrapper;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
/**
 * 验证请求包装器
 *
 */
@SuppressWarnings("ALL")
public class HttpServletRequestValidatorWrapper extends HttpServletRequestWrapper {

    private String formPath;

    public HttpServletRequestValidatorWrapper(HttpServletRequest request, String uri) {
        super(request);
        this.formPath = uri;
    }

    @Override
    public String getRequestURI() {
        return this.formPath;
    }

    @Override
    public String getServletPath() {
        return this.formPath;
    }
}
