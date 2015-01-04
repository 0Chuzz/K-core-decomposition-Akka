package sample.cluster.factorial;

import java.util.concurrent.TimeUnit;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;

//#frontend
public class FactorialFrontend extends UntypedActor {
  final int upToN;
  final boolean repeat;
  private double totpi = 0;
  private int counter = 0;

  LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  ActorRef backend = getContext().actorOf(FromConfig.getInstance().props(),
      "factorialBackendRouter");

  public FactorialFrontend(int upToN, boolean repeat) {
    this.upToN = upToN;
    this.repeat = repeat;
  }

  @Override
  public void preStart() {
    log.info("Initializing...");
    sendJobs();
    getContext().setReceiveTimeout(Duration.create(10, TimeUnit.SECONDS));
  }

  @Override
  public void onReceive(Object message) {
    if (message instanceof PiResult) {
      PiResult result = (PiResult) message;
      log.debug("Result received: {}! = {}", result.n, result.pi);
      totpi += result.pi;
      counter++;
      if (counter == upToN){
        log.info("PI = {}", totpi);
        counter = 0;
        totpi = 0;
        sendJobs();
      }
      /*
      if (result.n == upToN) {
        if (repeat) {
          log.info("Top result received, restarting jobs...");
          sendJobs();
        }else
          getContext().stop(getSelf());
      }*/


    } else if (message instanceof ReceiveTimeout) {
      log.info("Timeout");
      sendJobs();

    } else {
      unhandled(message);
    }
  }

  void sendJobs() {
    log.info("Starting batch of factorials up to [{}]", upToN);
    for (int n = 1; n <= upToN; n++) {
      backend.tell(n, getSelf());
    }
  }

}

//#frontend

