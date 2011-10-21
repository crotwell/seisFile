package edu.sc.seis.seisFile.stationxml;

import java.util.List;


public abstract class Equipment {
    
    
    
    public String getEquipType() {
        return equipType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public String getVendor() {
        return vendor;
    }
    
    public String getModel() {
        return model;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public String getInstallationDate() {
        return installationDate;
    }
    
    public String getRemovalDate() {
        return removalDate;
    }
    
    public List<String> getCalibrationDate() {
        return calibrationDate;
    }
    
    protected String equipType, description, manufacturer, vendor, model, serialNumber, installationDate, removalDate;
    protected List<String> calibrationDate;
}
