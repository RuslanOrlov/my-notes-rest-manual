package notes.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Type;
//import notes.models.Note;

@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {
	
//	@Override
//	public void configureRepositoryRestConfiguration(
//			RepositoryRestConfiguration config, CorsRegistry cors) {
//		config.exposeIdsFor(Note.class);
//	}
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public void configureRepositoryRestConfiguration(
			RepositoryRestConfiguration config, CorsRegistry cors) {
		Class[] classes = entityManager.getMetamodel()
			.getEntities().stream().map(Type::getJavaType).toArray(Class[]::new);
		config.exposeIdsFor(classes);
	}
	
}
