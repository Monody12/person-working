package com.example.netdisk.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author monody
 * @date 2021/12/31 9:26 下午
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfig {
    // 文档地址 http://localhost:6500/swagger-ui.html   默认的
    // 文档地址     使用美化后的 http://localhost:6500/doc.html

    // 扫描多个包
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(Predicates.or(RequestHandlerSelectors.basePackage("com.example.netdisk.controller"),
                        RequestHandlerSelectors.basePackage("com.example.netdisk.onlinedoc.controller")) )
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 汇总api文档相关信息
     *
     * @return ApiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Person-Working 网盘服务 接口api")
                .contact(new Contact("monody", null, "qq614908309@gmail.com"))
                .description("Person-Working 网盘服务 接口api文档")
                .version("0.1.0")
//                .termsOfServiceUrl("https://test.com")
                .build();
    }
}
