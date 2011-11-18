package com.temenos.interaction.example.country;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlRootElement
public class Country {

    @XmlElement(name = "countryCode")
    protected String countryCode;
    @XmlElement(name = "currencyCode")
    protected String currencyCode;
    @XmlElement(name = "countryName")
    protected String countryName;
    @XmlElement(name = "shortName")
    protected String shortName;
    @XmlElement(name = "presentationCode")
    protected String presentationCode;
    @XmlElement(name = "centralBankCode")
    protected String centralBankCode;
    @XmlElement(name = "geographicalBlock")
    protected String geographicalBlock;
    @XmlElement(name = "businessCentre")
    protected String businessCentre;
    @XmlElement(name = "cgIndexDate")
    protected String cgIndexDate;
    @XmlElement(name = "cgIndex")
    protected String cgIndex;
    @XmlElement(name = "tracerDays")
    protected String tracerDays;
    @XmlElement(name = "highRisk")
    protected String highRisk;

    @XmlAttribute
    protected String id;


// Getters

    public String getCountryCode(){
        return countryCode;
    }
		
    public String getCurrencyCode(){
        return currencyCode;
    }
		
    public String getCountryName(){
        return countryName;
    }
		
    public String getShortName(){
        return shortName;
    }
		
    public String getPresentationCode(){
        return presentationCode;
    }
		
    public String getCentralBankCode(){
        return centralBankCode;
    }
		
    public String getGeographicalBlock(){
        return geographicalBlock;
    }
		
    public String getBusinessCentre(){
        return businessCentre;
    }
		
    public String getCgIndexDate(){
        return cgIndexDate;
    }
		
    public String getCgIndex(){
        return cgIndex;
    }
		
    public String getTracerDays(){
        return tracerDays;
    }
		
    public String getHighRisk(){
        return highRisk;
    }
		
    public String getId() {
        return id;
    }
		

//Setters

    public void setCountryCode(String newCountryCode){
        this.countryCode = newCountryCode;
    }
		
    public void setCurrencyCode(String newCurrencyCode){
        this.currencyCode = newCurrencyCode;
    }
		
    public void setCountryName(String newCountryName){
        this.countryName = newCountryName;
    }
		
    public void setShortName(String newShortName){
        this.shortName = newShortName;
    }
		
    public void setPresentationCode(String newPresentationCode){
        this.presentationCode = newPresentationCode;
    }
		
    public void setCentralBankCode(String newCentralBankCode){
        this.centralBankCode = newCentralBankCode;
    }
		
    public void setGeographicalBlock(String newGeographicalBlock){
        this.geographicalBlock = newGeographicalBlock;
    }
		
    public void setBusinessCentre(String newBusinessCentre){
        this.businessCentre = newBusinessCentre;
    }
		
    public void setCgIndexDate(String newCgIndexDate){
        this.cgIndexDate = newCgIndexDate;
    }
		
    public void setCgIndex(String newCgIndex){
        this.cgIndex = newCgIndex;
    }
		
    public void setTracerDays(String newTracerDays){
        this.tracerDays = newTracerDays;
    }
		
    public void setHighRisk(String newHighRisk){
        this.highRisk = newHighRisk;
    }
		
    public void setId(String value) {
        this.id = value;
    }
}
