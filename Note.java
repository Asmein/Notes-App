package com.example.notesapp;

import java.util.Date;

public class Note {
    private String title;
    private String content;
    private Date createdAt;
    private Date lastModified;
    private int backgroundColor;
    // Add more fields like tags later

    public  Note(String title, String content, int backgroundColor) {
        this.createdAt = new Date();
        this.lastModified = new Date();
        this.title = title;
        this.content = content;
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        this.lastModified = new Date();
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.lastModified = new Date();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.lastModified = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
