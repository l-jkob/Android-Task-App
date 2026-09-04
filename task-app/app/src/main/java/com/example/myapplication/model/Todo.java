package com.example.myapplication.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.myapplication.db.RoomConverters;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "todos")
@TypeConverters(RoomConverters.class)
public class Todo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private static final long serialVersionUID = -7481912314472891511L;
    private String name;
    private String description;
    private boolean done;
    private boolean favorite;
    private String dueDate;
    private String dueTime;
    private ArrayList<String> linkedContacts;


    public Todo() {
        this.linkedContacts = new ArrayList<>();
    }


    public long getId() { return id; }


    public String getName() { return name; }

    public String getDescription() { return description; }

    public boolean isDone() { return done; }

    public boolean isFavorite() { return favorite; }

    public String getDueDate() { return dueDate; }

    public String getDueTime() { return dueTime; }

    public ArrayList<String> getLinkedContacts() { return linkedContacts; }

    public void setId(long id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setDescription(String description) { this.description = description; }

    public void setDone(boolean done) { this.done = done; }

    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public void setDueTime(String dueTime) { this.dueTime = dueTime; }

    public void setLinkedContacts(ArrayList<String> linkedContacts) {
        this.linkedContacts = linkedContacts;
    }





    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Todo)) {
            return false;
        } else {
            return ((Todo) other).getId() == this.getId();
        }
    }

    @Override
    public String toString() {
        return "{Todo " + this.getId() + " " + this.getName() + "}";
    }

}