package client.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.ByteString;

public class ClientActor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ByteString.class, byteString -> {
                    String message = byteString.decodeString("UTF-8");
                    System.out.println(message);
                })
                .match(String.class, string -> {
                    getContext().actorSelection("akka.tcp://server@127.0.0.1:2552/user/manager")
                            .tell(ByteString.fromString(string), getSelf());
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }
}
