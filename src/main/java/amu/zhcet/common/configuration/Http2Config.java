package amu.zhcet.common.configuration;

import io.undertow.UndertowOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class Http2Config {

    @Bean
    UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
        UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
        factory.addBuilderCustomizers(
                builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));

        return factory;
    }

}