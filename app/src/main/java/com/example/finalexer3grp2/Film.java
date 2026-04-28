package com.example.finalexer3grp2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "films_table")
public class Film {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String year;
    private String director;
    private String genres;
    private String description;
    private String posterUri;
    private long createdAt;
    private long updatedAt;

    @Ignore
    public Film(int id, String title, String year, String director, String genres, String description, String posterUri) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.description = description;
        this.posterUri = posterUri;
    }

    // Default constructor for Room
    public Film() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getGenres() { return genres; }
    public void setGenres(String genres) { this.genres = genres; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPosterUri() { return posterUri; }
    public void setPosterUri(String posterUri) { this.posterUri = posterUri; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}

