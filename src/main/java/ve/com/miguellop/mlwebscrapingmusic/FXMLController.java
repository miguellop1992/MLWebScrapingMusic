package ve.com.miguellop.mlwebscrapingmusic;

import ve.com.miguellop.webscrapingmusic.vo.Track;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FXMLController implements Initializable {
    
    
    @FXML 
    private TextField txt;
    @FXML 
    private TableView<Track> table;
    @FXML
    private WebView web;
    @FXML
    private Text text;
    
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
        
    }    

    private void loadWeb(String text) throws IOException {
        Connection connec = Jsoup.connect(text);
        Document doc = connec.get();
        Elements link = doc.select("a[href*=.mp3]");
        String name=null,url=null,author=null;
        for (Element l : link) {
            url=l.attr("href");
            name=l.parent().select("font").first().text();
            author=l.text() ;
            MediaPlayer mp = new MediaPlayer(new Media(url));
            if(name!=null || !name.isEmpty()){
                table.getItems().add(new Track(name+" - "+author,url,mp));
            }
        }
        maxLenght=table.getItems().size()-1;
    }
    private void playTrack(int index) {
        playTrack(table.getItems().get(index));
    }
    
    private void playTrack(Track track) {
        text.setText(track.getName());
        web.getEngine().load(track.getUrl());
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
