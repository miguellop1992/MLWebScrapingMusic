package ve.com.miguellop.mlwebscrapingmusic;

import ve.com.miguellop.webscrapingmusic.vo.Track;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FXMLController implements Initializable {
    
    
    @FXML
    private MediaView media;
    
    @FXML 
    private TextField txt;
    
    @FXML 
    private TableView<Track> table;
    
    private int maxLenght;
    
    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        loadWeb(txt.getText());
    }
    
    @FXML
    private void handleBackAction(ActionEvent event) throws IOException {
        back();
    }
    
    @FXML
    private void handlePlayAction(ActionEvent event) throws IOException {
        if(media.getMediaPlayer()!=null){
            media.getMediaPlayer().play();
        }else{
            playTrack(0);
        }
    }
    
    @FXML
    private void handlePauseAction(ActionEvent event) throws IOException {
        if(media.getMediaPlayer()!=null){
            media.getMediaPlayer().pause();
        }
    }
    
    @FXML
    private void handleNextAction(ActionEvent event) throws IOException {
        next();
    }
    
    @FXML
    private void handleTableClicked(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2) {
            Track track = table.getSelectionModel().getSelectedItem();
            if (track != null) {
                playTrack(track);
            }
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txt.setText("http://www.esenciagaitera.com/gaitas_mp3.htm");
        media.setFitHeight(200);
        media.setFitHeight(200);
    }    

    private void loadWeb(String text) throws IOException {
        Connection connec = Jsoup.connect(text);
        Document doc = connec.get();
        Elements link = doc.select("a[href*=.mp3]");
        String name="",url=null;
        for (Element l : link) {
            url=l.attr("href");
            name=url.substring(url.lastIndexOf("/")+1).toUpperCase();
            MediaPlayer mp = new MediaPlayer(new Media(url));
            mp.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    next();
                }
            });
            table.getItems().add(new Track(name,url,mp));
        }
        maxLenght=table.getItems().size()-1;
    }
    private void playTrack(int index) {
        playTrack(table.getItems().get(index));
    }
    
    private void playTrack(Track track) {
        if(media.getMediaPlayer()!=null){
            media.getMediaPlayer().stop();
            media.setMediaPlayer(null);
        }
        media.setMediaPlayer(track.getPlayer());
        media.getMediaPlayer().setAutoPlay(true);
        media.getMediaPlayer().play();
        System.out.println("play " + track.getName());
    }
    
    private void back(){
        int index=table.getSelectionModel().getSelectedIndex()-1;
        if(index<=0){
            index=maxLenght;
        }
        playTrack(index);
        table.getSelectionModel().select(index);
    }
    
    private void next(){
        int index=table.getSelectionModel().getSelectedIndex()+1;
        if(index>maxLenght){
            index=0;
        }
        playTrack(index);
        table.getSelectionModel().select(index);
    }
    
    
}
