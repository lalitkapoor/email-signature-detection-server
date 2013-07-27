import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;
import org.vertx.java.core.buffer.Buffer;

import jangada.SigFilePredictor;
import org.json.simple.JSONObject;

import java.util.Map;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;


public class Server extends Verticle {

  public static String page = "<html>"
  + "<head></head>"
  + "<body><h1>Email Signature Detection</h1><form method='POST'>"
  + "<textarea name='demobox' rows=40 cols=80 placeholder='paste email in here'></textarea>"
  + "<br /><input type='submit' />"
  + "</form></body>"
  + "</html>";
  public void start() {
    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
      public void handle(final HttpServerRequest req) {
        if (req.method().compareTo("POST") != 0) {
          req.response().headers().set("Content-Type", "text/html; charset=UTF-8");
          req.response().end(page);
          return;
        }

        req.bodyHandler(new Handler<Buffer>() {
          public void handle(Buffer buffer) {
            String email = buffer.toString();
            try {
              if(email.indexOf("demobox=") == 0) email = URLDecoder.decode(email.substring(8), "UTF-8");
            } catch (UnsupportedEncodingException e) {
              req.response().headers().set("Content-Type", "application/json; charset=UTF-8");
              req.response().end("");
            }
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