package org.elasticsoftwarefoundation.elasticactors.container;

import org.elasticsoftware.elasticactors.ServiceActor;
import org.elasticsoftware.elasticactors.configuration.BackplaneConfiguration;
import org.elasticsoftware.elasticactors.configuration.ClusteringConfiguration;
import org.elasticsoftware.elasticactors.configuration.MessagingConfiguration;
import org.elasticsoftware.elasticactors.configuration.NodeConfiguration;
import org.elasticsoftware.elasticactors.spring.ActorAnnotationBeanNameGenerator;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * @author Joost van de Wijgerd
 */
@Configuration
@EnableSpringConfigured
@EnableWebFlux
@EnableMBeanExport
@PropertySource(value = "classpath:system.properties")
@ComponentScan(nameGenerator = ActorAnnotationBeanNameGenerator.class,
        includeFilters = {@ComponentScan.Filter(value = {ServiceActor.class}, type = FilterType.ANNOTATION)})
@Import(value = {ClusteringConfiguration.class, NodeConfiguration.class, MessagingConfiguration.class, BackplaneConfiguration.class})
public class ContainerConfiguration {
}
