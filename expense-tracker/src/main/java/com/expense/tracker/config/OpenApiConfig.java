package com.expense.tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI expenseTrackerOpenApi() {
		String schemeName = "bearerAuth";

		return new OpenAPI()
				.info(new Info().title("Expense Tracker API").version("v1")
						.description("APIs for Auth, Categories, Expenses, Reporting"))
				.addSecurityItem(new SecurityRequirement().addList(schemeName))
				.components(new Components().addSecuritySchemes(schemeName, new SecurityScheme().name(schemeName)
						.type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
	}
}
