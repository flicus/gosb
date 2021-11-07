package org.schors.gos;

import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class BattleApplication {

  public static void main(String[] args) throws TelegramApiException {
    nu.pattern.OpenCV.loadLocally();
//    var context = new AnnotationConfigApplicationContext(BattleApplication.class);
//
//    var api = context.getBean(TelegramBotsApi.class);

    // deploy MainVerticle via verticle identifier name

//    TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
//    api.registerBot(new RecognitionTestBot());
    ConfigurableApplicationContext context = SpringApplication.run(BattleApplication.class);
    var vertx = context.getBean(Vertx.class);
    var factory = context.getBean(VerticleFactory.class);
    vertx
      .deployVerticle(factory.prefix() + ":" + MainVerticle.class.getName())
      .onFailure(Throwable::printStackTrace)
      .onComplete(System.out::println);

  }
}
