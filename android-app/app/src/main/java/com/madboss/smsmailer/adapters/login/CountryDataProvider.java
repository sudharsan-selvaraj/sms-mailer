package com.madboss.smsmailer.adapters.login;

import java.util.List;

public class CountryDataProvider {

    private static List<Country> countryList;

    public static void setCountryList(List<Country> countryList) {
        CountryDataProvider.countryList = countryList;
    }

    public static List<Country> getCountryList() {
        return countryList;
    }

    public static Country getCountyInfo(String countryISO) {
        countryISO = countryISO.toUpperCase();
        for (Country c : countryList) {
            if (c.ISOname.equals(countryISO)) {
                return c;
            }
        }
        return null;
    }
}
