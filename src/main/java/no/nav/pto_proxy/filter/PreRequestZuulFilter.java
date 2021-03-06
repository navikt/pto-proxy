package no.nav.pto_proxy.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import no.nav.common.log.MDCConstants;
import no.nav.pto_proxy.utils.UrlUtils;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static no.nav.common.log.LogFilter.PREFERRED_NAV_CALL_ID_HEADER_NAME;
import static no.nav.common.utils.StringUtils.of;

@Slf4j
public class PreRequestZuulFilter extends ZuulFilter {

    public final static String API_GW_KEY_HEADER = "x-nav-apiKey";

    private final String proxyContextPath;

    // Map where each key corresponds to an application and the value is the API-GW key for proxying requests to the app
    private final Map<String, String> apiGwKeyMap;

    public PreRequestZuulFilter(String proxyContextPath, Map<String, String> apiGwKeyMap) {
        this.proxyContextPath = proxyContextPath;
        this.apiGwKeyMap = apiGwKeyMap;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        String pathWithoutPrefix = UrlUtils.stripStartPath(proxyContextPath, request.getRequestURI());
        String appName = UrlUtils.getFirstSegment(pathWithoutPrefix);
        String apiGwKey = apiGwKeyMap.get(appName);

        if (apiGwKey == null) {
            log.error("Unable to find API-GW key for {}", request.getRequestURL());
            throw new ZuulException("Proxy Mapping Not Found", 404, "Unable to find API-GW proxy mapping");
        }

        ctx.addZuulRequestHeader(API_GW_KEY_HEADER, apiGwKey);

        of(MDC.get(MDCConstants.MDC_CALL_ID))
                .ifPresent(callId -> ctx.addZuulRequestHeader(PREFERRED_NAV_CALL_ID_HEADER_NAME, callId));

        log.info("Proxying request method={} url={}", request.getMethod(), request.getRequestURL());

        return null;
    }

}
