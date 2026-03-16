package com.example.mycocktailbar.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cocktails")
public class Cocktail {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String instruction;
    private String category;

    public Cocktail(String name, String instruction, String category) {
        this.name = name;
        this.instruction = instruction;
        this.category = category;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}