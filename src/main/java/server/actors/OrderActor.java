package server.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class OrderActor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, string -> {

                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }
}
