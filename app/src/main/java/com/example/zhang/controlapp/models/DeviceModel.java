package com.example.zhang.controlapp.models;

import java.io.Serializable;

public class DeviceModel implements Serializable {
    private Long mId;
    private String mName;
    private String mHost;
    private Integer mPort;


    public DeviceModel() {
        super();
        this.mId = (long)-1;
    }

    public DeviceModel(String name,String host, Integer port) {
        super();
        this.mName = name;
        this.mHost = host;
        this.mPort = port;

    }

    public DeviceModel(Long id, String name, String host, Integer port) {
        super();
        this.mId = id;
        this.mName = name;

        this.mHost = host;

        this.mPort = port;

    }

    public long getId() {
        return mId;
    }

    public void setId(Long id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getHost() {
        return mHost;
    }

    public void setHost(String host) {
        this.mHost = host;
    }


    public Integer getPort() {
        return mPort;
    }
    public void setPort(Integer port) {
        this.mPort = port;
    }


}


