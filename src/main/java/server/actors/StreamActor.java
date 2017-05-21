package server.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class StreamActor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, string -> {
                    String[] stringAsArray = string.split("/@@@");
                    getSender().tell(stringAsArray[1] + "/@@@" + stringAsArray[0], getSelf());
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }
}
