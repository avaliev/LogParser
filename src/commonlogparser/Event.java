/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonlogparser;

import java.util.Date;


public class Event {
    
    private int id;

    public static final int SUSPICION_ADDRESS = 1;

    public static final int OVERLOAD = 2;

    private int eventType;

    private Date dateTime;

    private String ipAddress;

    private String descr;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        if (eventType == SUSPICION_ADDRESS || eventType == OVERLOAD) {
            this.eventType = eventType;
        } else {
            throw new IllegalArgumentException("Incorrert EventType Code!");
        }
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

}
