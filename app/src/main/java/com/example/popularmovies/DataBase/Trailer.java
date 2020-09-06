package com.example.popularmovies.DataBase;

public class Trailer {
    private String id;
    private String youtubeId;
    private String title;

    public Trailer(String id, String youtubeId, String title) {
        this.id = id;
        this.youtubeId = youtubeId;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
