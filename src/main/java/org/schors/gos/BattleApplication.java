package org.schors.gos;

import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class BattleApplication {

    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(BattleApplication.class);
        var vertx = context.getBean(Vertx.class);
        var factory = context.getBean(VerticleFactory.class);

        // deploy MainVerticle via verticle identifier name
        vertx
            .deployVerticle(factory.prefix() + ":" + MainVerticle.class.getName())
            .onFailure(Throwable::printStackTrace)
            .onComplete(System.out::println);
    }
}
