package com.fmi.mp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryContainer {
    @JsonProperty("name")
    private String name;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("unicodeFlag")
    private String unicodeFlag;
    @JsonProperty("flag")
    private String flag;
    @JsonProperty("dialCode")
    private String dialCode;

    public CountryContainer() {
    }

    public CountryContainer(String name, String currency, String unicodeFlag, String flag, String dialCode) {
        this.name = name;
        this.currency = currency;
        this.unicodeFlag = unicodeFlag;
        this.flag = flag;
        this.dialCode = dialCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnicodeFlag() {
        return unicodeFlag;
    }

    public void setUnicodeFlag(String unicodeFlag) {
        this.unicodeFlag = unicodeFlag;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }
}
