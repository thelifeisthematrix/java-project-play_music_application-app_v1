package application;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;


public class Controller implements Initializable {
	
	@FXML
	private Label label;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private ComboBox<String> comboBox;
	@FXML
	private Slider slider;
	@FXML
	private AnchorPane scene;
	
	private File file;
	private File[] files;
	private ArrayList<File> songs;
	private Media media;
	private MediaPlayer mediaPlayer;
	private int currSong;
	private int[] speed = {25, 50, 75, 100, 125, 150, 175, 200};
	private Timer timer;
	private TimerTask timerTask;
	private boolean running;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.songs = new ArrayList<File>();
		this.file = new File("./src/application/music");
		this.files = this.file.listFiles();
		if(this.files != null) {
			for(File file : files) {
				this.songs.add(file);
				System.out.println(file);
			}
		}
		this.media = new Media(this.songs.get(this.currSong).toURI().toString());
		this.mediaPlayer = new MediaPlayer(this.media);
		this.label.setText(this.songs.get(this.currSong).getName());
		
		for(int i = 0; i < this.speed.length; i++) {
			this.comboBox.getItems().add(this.speed[i] + "%");
		}
		this.comboBox.setOnAction(this::setSpeed);
		
		this.slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				Controller.this.mediaPlayer.setVolume(Controller.this.slider.getValue()*0.01);
			}
		});
	}
	
	public void play() {
		this.mediaPlayer.play();
		this.setSpeed(null);
		this.mediaPlayer.setVolume(this.slider.getValue()*0.01);
		this.beginTimer();
	}
	
	public void pause() {
		this.mediaPlayer.pause();
		this.cancelTimer();
	}
	
	public void reset() {
		this.mediaPlayer.seek(Duration.seconds(0));
		this.progressBar.setProgress(0);
	}
	
	public void previous() {
		this.currSong = this.currSong > 0 ? this.currSong - 1 : this.songs.size() - 1;
		this.mediaPlayer.stop();
		if(this.running) {
			this.cancelTimer();
		}
		this.media = new Media(this.songs.get(this.currSong).toURI().toString());
		this.mediaPlayer = new MediaPlayer(this.media);
		this.label.setText(this.songs.get(this.currSong).getName());
		this.play();
	}
	
	public void next() {
		this.currSong = this.currSong < this.songs.size() - 1 ? this.currSong + 1 : 0;
		this.mediaPlayer.stop();
		if(this.running) {
			this.cancelTimer();
		}
		this.media = new Media(this.songs.get(this.currSong).toURI().toString());
		this.mediaPlayer = new MediaPlayer(this.media);
		this.label.setText(this.songs.get(this.currSong).getName());
		this.play();
	}
	
	public void setSpeed(ActionEvent e) {
		if(this.comboBox.getValue() == null) {
			this.mediaPlayer.setRate(1);
		}else {
			this.mediaPlayer.setRate(Integer.parseInt(this.comboBox.getValue().substring(0, this.comboBox.getValue().length() - 1))*0.01);
		}
	}
	
	public void beginTimer() {
		this.timer = new Timer();
		this.timerTask = new TimerTask() {
			@Override
			public void run() {
				Controller.this.running = true;
				double current = Controller.this.mediaPlayer.getCurrentTime().toSeconds();
				double end = Controller.this.media.getDuration().toSeconds();
				Controller.this.progressBar.setProgress(current/end);
				if(current/end == 1) {
					Controller.this.cancelTimer();
				}
			}
		};
		this.timer.scheduleAtFixedRate(timerTask, 50, 1000);
	}
	
	public void cancelTimer() {
		this.running = false;
		this.timer.cancel();
	}

}
