package com.zapp.library.merchant.network.response;

public class AvailableBankAppsResponse {
    /**
     * The bank name
     */
    private String bankName;
    /**
     * The logo image url
     */
    private String smallLogo;
    /**
     * The large logo image url
     */
    private String largeLogo;

    /**
     * Get bank name
     * @return bank name
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * Set bank name
     * @param bankName
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    /**
     * Get small logo url
     * @return string url of small logo
     */
    public String getSmallLogo() {
        return smallLogo;
    }

    /**
     * Set small logo url
     * @param smallLogo
     */
    public void setSmallLogo(String smallLogo) {
        this.smallLogo = smallLogo;
    }

    /**
     * Get large logo url
     * @return string url of large logo
     */
    public String getLargeLogo() {
        return largeLogo;
    }

    /**
     * Set large logo url
     * @param largeLogo
     */
    public void setLargeLogo(String largeLogo) {
        this.largeLogo = largeLogo;
    }
}
