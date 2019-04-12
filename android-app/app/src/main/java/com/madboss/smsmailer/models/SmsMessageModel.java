package com.madboss.smsmailer.models;

import android.telephony.SmsMessage;

public class SmsMessageModel {

    public String fromNumber;
    public String message;
    public String serviceCenterAddress;

    public SmsMessageModel(SmsMessage messageObject) {
         fromNumber = messageObject.getDisplayOriginatingAddress();
         message = messageObject.getDisplayMessageBody();
         serviceCenterAddress = messageObject.getServiceCenterAddress();
    }

}
