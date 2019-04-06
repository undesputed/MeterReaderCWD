package com.example.meterreadercwd;

public class Zone_Helper {
    private String zone_number;
    private String zone_name;
    private String zone_id;

    public Zone_Helper(String zone_number, String zone_name, String zone_id) {
        this.zone_number = zone_number;
        this.zone_name = zone_name;
        this.zone_id = zone_id;
    }

    public String getZone_number() {
        return zone_number;
    }

    public void setZone_number(String zone_number) {
        this.zone_number = zone_number;
    }

    public String getZone_name() {
        return zone_name;
    }

    public void setZone_name(String zone_name) {
        this.zone_name = zone_name;
    }

    public String getZone_id() { return zone_id; }
}
