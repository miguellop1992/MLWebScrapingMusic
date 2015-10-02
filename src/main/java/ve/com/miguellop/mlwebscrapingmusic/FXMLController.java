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
import javafx.util.Duration;
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
    
    @FXML
    private Slider timeSlider;
    
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label displayTime;
    @FXML
    private Text text;
    @FXML
    private ProgressIndicator progress;
    
    private int maxLenght;
    private Duration duration;
    
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
        timeSlider.valueProperty().addListener((Observable observable) -> {
            if(timeSlider.isValueChanging()){
                media.getMediaPlayer().seek(duration.multiply(timeSlider.getValue()/100d));
            }
        });
        volumeSlider.setValueChanging(true);
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(media.getMediaPlayer()!=null)
                media.getMediaPlayer().setVolume(newValue.doubleValue());
            }
        });
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
            mp.setOnEndOfMedia(() -> {next();});
            mp.currentTimeProperty().addListener(o->{updateValue();});
            mp.setOnReady(()->{ 
                duration=mp.getMedia().getDuration(); 
                updateValue();
            });
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
        if(media.getMediaPlayer()!=null){
            media.getMediaPlayer().stop();
            media.setMediaPlayer(null);
        }
        text.setText(track.getName());
        media.setMediaPlayer(track.getPlayer());
        media.getMediaPlayer().setAutoPlay(true);
        media.getMediaPlayer().play();
        media.getMediaPlayer().bufferProgressTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                System.out.println("newValue = " + newValue.toSeconds());
                progress.setProgress(duration.toSeconds()/(newValue.toSeconds()*100));
            }
        }
        );
        volumeSlider.setValue((int) Math.round(media.getMediaPlayer().getVolume() * 100));

        media.getMediaPlayer().play();
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
    
    
    protected void updateValue(){
        if(displayTime!=null && timeSlider != null && volumeSlider != null && media.getMediaPlayer()!=null){
            Platform.runLater(() -> {
                Duration currentTime = media.getMediaPlayer().getCurrentTime();
                displayTime.setText(formatTime(currentTime,duration));
                timeSlider.setDisable(duration.isUnknown());
                if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
                    timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis()*100d);
                }
                
            });
        }
    }
    
    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }
    
}
