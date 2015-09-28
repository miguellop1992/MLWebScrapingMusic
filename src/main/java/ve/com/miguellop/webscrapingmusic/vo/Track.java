/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ve.com.miguellop.webscrapingmusic.vo;

import javafx.scene.media.MediaPlayer;

/**
 *
 * @author migue_000
 */
public class Track {
    private String name;
    private String url;
    private MediaPlayer player;
    
    public Track() {
    }

    
    public Track(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Track(String name, String url, MediaPlayer player) {
        this.name = name;
        this.url = url;
        this.player = player;
    }
    
    

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
    
    
    
    
}
