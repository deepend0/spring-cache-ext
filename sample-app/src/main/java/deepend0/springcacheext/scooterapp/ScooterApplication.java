package deepend0.springcacheext.scooterapp;

import deepend0.springcacheext.flatcacheable.FlatCacheableContextConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableCaching
@EnableAspectJAutoProxy
@Import(FlatCacheableContextConfig.class)
public class ScooterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScooterApplication.class, args);
	}

}
