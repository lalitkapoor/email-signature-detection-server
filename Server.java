import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;
import org.vertx.java.core.buffer.Buffer;

import jangada.SigFilePredictor;
import org.json.simple.JSONObject;

import java.util.Map;

public class Server extends Verticle {

  public void start() {
    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
      public void handle(final HttpServerRequest req) {
        req.bodyHandler(new Handler<Buffer>() {
          public void handle(Buffer buffer) {
            String email = buffer.toString();
            SigFilePredictor sigpred = new SigFilePredictor();
            JSONObject json = new JSONObject();
            String message = sigpred.getMsgWithoutSignatureLines(email);
            String signature = sigpred.getSignatureLines(email);
            json.put("message", message);
            json.put("signature", signature);
            req.response().headers().set("Content-Type", "application/json; charset=UTF-8");
            req.response().end(json.toJSONString());
          }
        });
      }
    }).listen(9092);
  }
}