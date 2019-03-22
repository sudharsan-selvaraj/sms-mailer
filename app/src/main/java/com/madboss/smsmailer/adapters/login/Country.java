package com.madboss.smsmailer.adapters.login;


import java.io.Serializable;

public class Country implements Serializable {

    public String code;
    public String name;
    public String ISOname;
    public int flagImageId;

    public Country(String code, String name, String ISOname, int flagImageId) {
        this.code = code;
        this.name = name;
        this.ISOname = ISOname;
        this.flagImageId = flagImageId;
    }

}
