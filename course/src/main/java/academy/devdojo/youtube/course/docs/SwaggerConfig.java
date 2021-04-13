package academy.devdojo.youtube.course.docs;

import org.springframework.context.annotation.Configuration;

import academy.devdojo.youtube.core.docs.BaseSwaggerConfig;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {

	public SwaggerConfig() {
		super("academy.devdojo.youtube.course.endpoint.controller");
	}

}
