package com.videogamedb.app.models;

import java.util.Date;
import java.util.Objects;

import org.springframework.data.mongodb.core.mapping.Field;

public class GamePlay {
    @Field("vgId")
    private String vgId;

    @Field("datetimeOpened")
    private Date datetimeOpened;

    @Field("timePlayed")
    private Integer timePlayed; // seconds

    public GamePlay() {
    }

    public GamePlay(String vgId, Date datetimeOpened, Integer timePlayed) {
        this.vgId = vgId;
        this.datetimeOpened = datetimeOpened;
        this.timePlayed = timePlayed;
    }

    public String getVgId() {
        return this.vgId;
    }

    public void setVgId(String vgId) {
        this.vgId = vgId;
    }

    public Date getDatetimeOpened() {
        return this.datetimeOpened;
    }

    public void setDatetimeOpened(Date datetimeOpened) {
        this.datetimeOpened = datetimeOpened;
    }

    public Integer getTimePlayed() {
        return this.timePlayed;
    }

    public void setTimePlayed(Integer timePlayed) {
        this.timePlayed = timePlayed;
    }

    public GamePlay vgId(String vgId) {
        setVgId(vgId);
        return this;
    }

    public GamePlay datetimeOpened(Date datetimeOpened) {
        setDatetimeOpened(datetimeOpened);
        return this;
    }

    public GamePlay timePlayed(Integer timePlayed) {
        setTimePlayed(timePlayed);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GamePlay)) {
            return false;
        }
        GamePlay gamePlay = (GamePlay) o;
        return Objects.equals(vgId, gamePlay.vgId) && Objects.equals(datetimeOpened, gamePlay.datetimeOpened)
                && Objects.equals(timePlayed, gamePlay.timePlayed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vgId, datetimeOpened, timePlayed);
    }

    @Override
    public String toString() {
        return "{" +
                " vgId='" + getVgId() + "'" +
                ", datetimeOpened='" + getDatetimeOpened() + "'" +
                ", timePlayed='" + getTimePlayed() + "'" +
                "}";
    }

}
