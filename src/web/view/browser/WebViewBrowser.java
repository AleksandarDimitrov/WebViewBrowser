package web.view.browser;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WebViewBrowser extends Application {
	// The default URL.
	public static final String DEFAULT_URL = "http://google.com";
	// Size on the window
	public static final double width = 900;
	public static final double height = 600;
	public static final String icon = "/resource/webViewBrowser.png";

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Web View");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(icon)));
		Group root = new Group();
		Scene scene = new Scene(root, width, height, Color.web("#666970"));
		
		WebView webView = new WebView();
		WebEngine webEngine = webView.getEngine();

		final TextField urlField = new TextField(DEFAULT_URL);
		webEngine.locationProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				urlField.setText(newValue);
			}
		});

		// Action definition for the Button Go.
		EventHandler<ActionEvent> goAction = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				webEngine.load(
						urlField.getText().startsWith("http://") ? urlField.getText() : "http://" + urlField.getText());
			}
		};
		Worker<Void> worker = webEngine.getLoadWorker();
		worker.stateProperty().addListener((ov, oldState, newState) -> {
			switch (newState) {
			case SUCCEEDED:
				urlField.setText(webEngine.getLocation());
				break;
			case FAILED: // assume urlField is a keyword
				webEngine.load("https://www.google.bg/search?q=" + urlField.getText().substring(7));
				break;
			default:
			}
		});

		urlField.setOnAction(goAction);

		Button goButton = new Button("Go");
		goButton.setDefaultButton(true);
		goButton.setOnAction(goAction);

		HBox hBox = new HBox();
		hBox.setPrefWidth(width);
		hBox.getChildren().setAll(goButton, urlField);
		HBox.setHgrow(urlField, Priority.ALWAYS);

		final VBox vBox = new VBox();
		vBox.getChildren().setAll(hBox, webView);
		VBox.setVgrow(webView, Priority.ALWAYS);

		webEngine.load(DEFAULT_URL);
		root.getChildren().setAll(vBox);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
