package com.dfn.exchange.beans.WSUtil;

import com.google.gson.annotations.SerializedName;

/**
 * Created by manodyas on 12/6/2017.
 */
public class Message {
    @SerializedName("HED")
    private Header header;
    @SerializedName("DAT")
    private Object data;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
