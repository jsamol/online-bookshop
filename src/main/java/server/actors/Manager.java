package server.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.ByteString;
import server.utilities.LocalMessage;

import java.util.HashMap;
import java.util.Map;

public class Manager extends AbstractActor {
    private final Map<String, String> roles;
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static Props props(Map<String, String> roles) {
        return Props.create(Manager.class, () -> new Manager(roles));
    }

    private Manager(Map<String, String> roles) {
        this.roles = new HashMap<>();
        this.roles.putAll(roles);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ByteString.class, byteString -> {
                    String message = byteString.decodeString("UTF-8");
                    log.info("Received message \"" + message + "\" from " + getSender());
                    String[] messageAsArray = message.split(" ", 2);
                    if (roles.containsKey(messageAsArray[0])) {
                        context().child(roles.get(messageAsArray[0]))
                                .get().tell(new LocalMessage(messageAsArray[1], getSender()), getSelf());
                    }
                    else {
                        log.info("Received unknown message.");
                    }
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }

    @Override
    public void preStart() throws Exception {
        context().actorOf(Props.create(OrderActor.class), roles.get("order"));
        context().actorOf(Props.create(SearchActor.class), roles.get("find"));
        context().actorOf(Props.create(StreamActor.class), roles.get("read"));
    }
}
