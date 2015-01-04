package sample.cluster.factorial;

import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class FactorialBackendMain {

  public static void main(String[] args) {
    // Override the configuration of the port when specified as program argument
    final String port =  "25515";
    final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
      withFallback(ConfigFactory.load("factorial"));

    final ActorSystem system = ActorSystem.create("ClusterSystem", config);

    Cluster.get(system).registerOnMemberUp(new Runnable() {
      @Override
      public void run() {

        system.actorOf(Props.create(FactorialBackend.class), "factorialBackend");

        system.actorOf(Props.create(MetricsListener.class), "metricsListener");
      }
    });
  }

}
