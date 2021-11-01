package com.md.tattle.Models;

public class gMessageModel {

    public String sendersName , sendersIcon , sendersMessage  , sendersId;
    public long sendersMsgTimeStamp;

    public gMessageModel() {
    }

    public gMessageModel(String sendersName, String sendersIcon, String sendersMessage, String sendersId, long sendersMsgTimeStamp) {
        this.sendersName = sendersName;
        this.sendersIcon = sendersIcon;
        this.sendersMessage = sendersMessage;
        this.sendersId = sendersId;
        this.sendersMsgTimeStamp = sendersMsgTimeStamp;
    }

    public String getSendersName() {
        return sendersName;
    }

    public void setSendersName(String sendersName) {
        this.sendersName = sendersName;
    }

    public String getSendersIcon() {
        return sendersIcon;
    }

    public void setSendersIcon(String sendersIcon) {
        this.sendersIcon = sendersIcon;
    }

    public String getSendersMessage() {
        return sendersMessage;
    }

    public void setSendersMessage(String sendersMessage) {
        this.sendersMessage = sendersMessage;
    }

    public long getSendersMsgTimeStamp() {
        return sendersMsgTimeStamp;
    }

    public void setSendersMsgTimeStamp(long sendersMsgTimeStamp) {
        this.sendersMsgTimeStamp = sendersMsgTimeStamp;
    }

    public String getSendersId() {
        return sendersId;
    }

    public void setSendersId(String sendersId) {
        this.sendersId = sendersId;
    }
}
