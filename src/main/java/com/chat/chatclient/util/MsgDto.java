package com.chat.chatclient.util;

/**
 * Create by Guolianxing on 2018/7/7.
 */
public class MsgDto {

    private Integer msgType; // 0: 用户发送消息 1: 系统消息用户进入聊天室 2：系统消息用户离开聊天室
    private String roomName;
    private String sendUserName;
    private String sendUserPhoto;
    private String sendTime;
    private String msg;

    public MsgDto() {
    }

    public MsgDto(Integer msgType, String roomName, String sendUserName, String sendUserPhoto, String sendTime, String msg) {
        this.msgType = msgType;
        this.roomName = roomName;
        this.sendUserName = sendUserName;
        this.sendUserPhoto = sendUserPhoto;
        this.sendTime = sendTime;
        this.msg = msg;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public String getSendUserPhoto() {
        return sendUserPhoto;
    }

    public void setSendUserPhoto(String sendUserPhoto) {
        this.sendUserPhoto = sendUserPhoto;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "MsgDto{" +
                "msgType=" + msgType +
                ", roomName='" + roomName + '\'' +
                ", sendUserName='" + sendUserName + '\'' +
                ", sendUserPhoto='" + sendUserPhoto + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
