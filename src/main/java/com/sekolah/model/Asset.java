package com.sekolah.model;

public class Asset {
    private int id;
    private String name;
    private String serialNumber;
    private String location;
    private String status;

    public Asset(int id, String name, String serialNumber, String location, String status) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
        this.location = location;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSerialNumber() { return serialNumber; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
}