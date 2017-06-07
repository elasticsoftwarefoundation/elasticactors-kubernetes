package org.elasticsoftwarefoundation.elasticactors.container;

import io.undertow.Undertow;
import org.elasticsoftware.elasticactors.cluster.ClusterService;
import org.elasticsoftware.elasticactors.spring.AnnotationConfigApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.UndertowHttpHandlerAdapter;
import org.springframework.web.reactive.DispatcherHandler;

import java.util.concurrent.CountDownLatch;

/**
 * @author Joost van de Wijgerd
 */
public class Entrypoint {
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

            server.stop();
            context.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
