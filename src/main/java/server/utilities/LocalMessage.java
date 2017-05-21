package server.utilities;

import akka.actor.ActorRef;

public class LocalMessage {
    private String message;
    private ActorRef actorRef;

    public LocalMessage(String message, ActorRef actorRef) {
        this.message = message;
        this.actorRef = actorRef;
    }

    public String getMessage() {
        return message;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }
}
