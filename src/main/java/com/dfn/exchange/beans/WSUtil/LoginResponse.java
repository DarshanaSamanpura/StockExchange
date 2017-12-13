package com.dfn.exchange.beans.WSUtil;

import com.google.gson.annotations.SerializedName;

/**
 * Created by manodyas on 12/6/2017.
 */
public class LoginResponse {
    @SerializedName("usrname")
    private String usrname;
    @SerializedName("resCode")
    private int resCode;
    @SerializedName("resDescription")
    private String resDescription;

    public String getUsrname() {
        return usrname;
    }

    public void setUsrname(String usrname) {
        this.usrname = usrname;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getResDescription() {
        return resDescription;
    }

    public void setResDescription(String resDescription) {
        this.resDescription = resDescription;
    }
}
