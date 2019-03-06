package com.example.teja2.chatroomfirebase;

/* InClass09 - Group 32
Created by
1. Bala Guna Teja Karlapudi
2. Mandar Phapale
*/
public class messages {
    String fname, lname, userId, msgId, msg, time, imgRef="";

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getImgRef() {
        return imgRef;
    }

    public void setImgRef(String imgRef) {
        this.imgRef = imgRef;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
