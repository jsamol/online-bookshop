package server.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import server.utilities.LocalMessage;

public class StreamActor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(LocalMessage.class, localMessage -> {

                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }
}
