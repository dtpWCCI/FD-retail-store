package com.fantasydrawer.ecommerce.backend.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

    import javax.sql.DataSource;

	@Configuration
	public class FlywayConfig {
		@Autowired
		private DataSource dataSource;
	
		@Bean
		public Flyway flyway() {
			return Flyway.configure()
				.dataSource(dataSource)
				.load();
		}
	}
