package sample.cluster.factorial;

import java.math.BigInteger;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.UntypedActor;

//#backend
public class FactorialBackend extends UntypedActor {
  final int N = 20;
  LoggingAdapter log = Logging.getLogger(getContext().system(), this);
  private double calculatePiFor(int start, int nrOfElements) {
    double acc = 0.0;
    for (int i = start * nrOfElements; i <= ((start + 1) * nrOfElements - 1); i++) {
      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
    }
    return acc;
  }

  @Override
  public void onReceive(Object message) {
    //log.debug("message received");
    if (message instanceof PiWork) {
      final int n = ((PiWork) message).n;
      final int nrE = ((PiWork) message).nrofEls;
      /*
      final Integer n = (Integer) message;
      Future<Double> f = future(new Callable<Double>() {
        public Double call() {
          return calculatePiFor(n, N);
        }
      }, getContext().dispatcher());

      Future<PiResult> result = f.map(
          new Mapper<Double, PiResult>() {
            public PiResult apply(double factorial) {
              log.info("{}", factorial);
              return new PiResult(n, factorial);
            }
          }, getContext().dispatcher());

      pipe(result, getContext().dispatcher()).to(getSender());*/
      getSender().tell(new PiResult(n, calculatePiFor(n, nrE)), getSelf());

    } else {
      unhandled(message);
    }
  }

  BigInteger factorial(int n) {
    BigInteger acc = BigInteger.ONE;
    for (int i = 1; i <= n; ++i) {
      acc = acc.multiply(BigInteger.valueOf(i));
    }
    return acc;
  }
}
//#backend

