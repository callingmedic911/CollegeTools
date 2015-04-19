package com.adityaworks.collegetools;

/**
 * Created by callingmedic911 on 12/4/15.
 * Copyright Aditya Pandey <aditya@autospace.co>. All right reserved.
 */
public class Lecture {
    public String name;
    public String acro;
    public String faculty;
    public String startTime;
    public String endTime;

    public Lecture(String name, String acro, String faculty, String startTime, String endTime) {
        this.name = name;
        this.acro = acro;
        this.faculty = faculty;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
