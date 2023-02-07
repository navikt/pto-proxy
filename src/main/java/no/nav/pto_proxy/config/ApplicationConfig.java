package no.nav.pto_proxy.config;

import no.nav.pto_proxy.filter.PostRequestZuulFilter;
import no.nav.pto_proxy.filter.PreRequestZuulFilter;
import no.nav.pto_proxy.utils.ProxyUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@EnableZuulProxy
@Configuration
@EnableConfigurationProperties({EnvironmentProperties.class})
public class ApplicationConfig {

    private final static List<String> PROXIED_APPLICATIONS = List.of(
            "veilarbaktivitet",
            "veilarbdialog",
            "veilarbjobbsokerkompetanse",
            "veilarblest",
            "veilarboppfolging",
            "veilarbperson",
            "veilarbvedtakinfo",
            "frontendlogger"
    );

    @Bean
    public PreRequestZuulFilter preRequestZuulFilter() {
        return new PreRequestZuulFilter("/proxy", ProxyUtils.createApiGwKeyMap(PROXIED_APPLICATIONS));
    }

    @Bean
    public PostRequestZuulFilter postRequestZuulFilter() {
        return new PostRequestZuulFilter();
    }

}
