package com.example.naginowdiary.model;

import android.graphics.Color;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class JournalItem implements Serializable {
    private String id;
    private Date date;
    private String title;
    private String content;
    private int color;

    public JournalItem() {

    }

    public JournalItem(Date date, String title, String content, int color) {
        this.date = date;
        this.title = title;
        this.content = content;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
