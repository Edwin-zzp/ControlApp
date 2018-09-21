package com.example.zhang.controlapp.models;

public class MovieModel {
    private Long mId;
    private String mName;

    public MovieModel() {
        super();
        this.mId = (long)-1;
    }

    public MovieModel(String name,String host, Integer port) {
        super();
        this.mName = name;
    }

    public MovieModel(Long id, String name, String host, Integer port) {
        super();
        this.mId = id;
        this.mName = name;
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

}
