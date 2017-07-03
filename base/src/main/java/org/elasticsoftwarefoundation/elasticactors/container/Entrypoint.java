package org.elasticsoftwarefoundation.elasticactors.container;

import io.undertow.Undertow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsoftware.elasticactors.cluster.ClusterService;
import org.elasticsoftware.elasticactors.spring.AnnotationConfigApplicationContext;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.UndertowHttpHandlerAdapter;
import org.springframework.web.reactive.DispatcherHandler;

import java.util.concurrent.CountDownLatch;

/**
 * @author Joost van de Wijgerd
 */
public class Entrypoint {
    private static final Logger logger = LogManager.getLogger(Entrypoint.class);

    public static void main(String[] args) {
        try {
            // initialize all the beans
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ContainerConfiguration.class);  // (1)

            HttpHandler handler = DispatcherHandler.toHttpHandler(context);
            
            // start the cluster
            ClusterService clusterService = context.getBean(ClusterService.class);
            try {
                clusterService.reportReady();
            } catch (Exception e) {
                throw new RuntimeException("Exception in ClusterService.reportReady()", e);
            }

            UndertowHttpHandlerAdapter adapter = new UndertowHttpHandlerAdapter(handler);
            Undertow server = Undertow.builder().addHttpListener(8080, "0.0.0.0").setHandler(adapter).build();
            server.start();

            final CountDownLatch waitLatch = new CountDownLatch(1);

            Runtime.getRuntime().addShutdownHook(new Thread(waitLatch::countDown));

            try {
                waitLatch.await();
            } catch (InterruptedException e) {
                // do nothing
            }
            // signal to the others we are going to leave the cluster
            try {
                clusterService.reportPlannedShutdown();
            } catch(Exception e) {
                logger.error("UnexpectedException on reportPlannedShutdown()", e);
            }
            server.stop();
            context.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
