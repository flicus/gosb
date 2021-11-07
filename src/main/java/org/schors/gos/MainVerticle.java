package org.schors.gos;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MainVerticle extends AbstractVerticle {

  final ApiHandler apiHandler;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.createHttpServer()
      .requestHandler(routes(apiHandler))
      .listen(9090, "127.0.0.1")
      .onComplete(httpServerAsyncResult -> startPromise.complete());
  }

  private Router routes(ApiHandler apiHandler) {
    Router router = Router.router(vertx);
    router.route().handler(CorsHandler.create()
      .addOrigin("http://127.0.0.1:4200")
      .addOrigin("http://localhost:4200")
      .addOrigin("http://baikal:8080")
      .addOrigin("http://baikal:9090")
      .allowedMethod(HttpMethod.GET)
      .allowedMethod(HttpMethod.DELETE)
      .allowedMethod(HttpMethod.POST)
      .allowedMethod(HttpMethod.PUT));

    router.route().handler(StaticHandler.create("./web"));

    router.get("/api/player").produces("application/json").handler(apiHandler::getPlayers);
    router.get("/api/player/:id").produces("application/json").handler(apiHandler::getPlayer);
    router.post("/api/player").produces("application/json")
      .handler(BodyHandler.create())
      .handler(apiHandler::createPlayer);
    router.put("/api/player/:id").produces("application/json")
      .handler(BodyHandler.create())
      .handler(apiHandler::updatePlayer);
    router.delete("/api/player/:id").produces("application/json").handler(apiHandler::deletePlayer);
    router.get("/api/week").produces("application/json").handler(apiHandler::getCurrentWeek);
    router.post("/api/week").produces("application/json")
      .handler(BodyHandler.create())
      .handler(apiHandler::addPlayerLayout);
    router.delete("/api/week/:id").produces("application/json").handler(apiHandler::deleteWeek);
    router.put("/api/week/:id").produces("application/json")
      .handler(BodyHandler.create())
      .handler(apiHandler::updateWeek);
    return router;
  }
}
