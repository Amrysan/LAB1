package com.example.bsuirschedule.model;

import java.util.List;

public class Schedule {
    private String studentGroup;
    private String date;
    private List<String> subjects;

    public Schedule() {}

    public Schedule(String studentGroup, String date, List<String> subjects) {
        this.studentGroup = studentGroup;
        this.date = date;
        this.subjects = subjects;
    }

    public String getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(String studentGroup) {
        this.studentGroup = studentGroup;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }
}
