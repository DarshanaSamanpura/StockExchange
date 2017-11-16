package com.dfn.exchange;


import quickfix.SessionID;
import quickfix.fix42.Message;

/**
 * Created by darshanas on 11/2/2017.
 */
public class OutMessageFix {

    private final Message fixMessage;
    private final SessionID sessionID;

    public OutMessageFix(Message fixMessage, SessionID sessionID) {
        this.fixMessage = fixMessage;
        this.sessionID = sessionID;
    }

    public Message getFixMessage() {
        return fixMessage;
    }

    public SessionID getSessionID() {
        return sessionID;
    }
}
