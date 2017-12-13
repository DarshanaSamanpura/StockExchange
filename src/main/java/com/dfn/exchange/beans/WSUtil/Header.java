package com.dfn.exchange.beans.WSUtil;

import com.google.gson.annotations.SerializedName;

/**
 * Created by manodyas on 12/6/2017.
 */
public class Header {
    @SerializedName("msgGrp")
    private int messageGroup;
    @SerializedName("chnlId")
    private int channelId;
    @SerializedName("clVer")
    private String clientVersion;
    @SerializedName("usrId")
    private String userId;
    @SerializedName("sesnId")
    private String sessionId;

    public int getMessageGroup() {
        return messageGroup;
    }

    public void setMessageGroup(int messageGroup) {
        this.messageGroup = messageGroup;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
