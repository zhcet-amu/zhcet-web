package amu.zhcet.common.configuration;

import io.undertow.UndertowOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class Http2Config {

    @Bean
    UndertowServletWebServerFactory undertowServletWebServerFactory() {
        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
        factory.addBuilderCustomizers(
                builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));

        return factory;
    }

}