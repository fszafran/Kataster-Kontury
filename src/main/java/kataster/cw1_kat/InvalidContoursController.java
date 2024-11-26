package kataster.cw1_kat;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class InvalidContoursController implements Initializable {
    private Parent root;
    private Stage stage;
    private Scene scene;
    @FXML
    ScrollPane scrollPane;
    @FXML
    Label errorLabel;
    private Map<String,String> invalidContours;

    public void setData(Map<String,String> invalidContours){
        this.invalidContours = invalidContours;
    }

    public void setTheme(boolean dark){
        root = scrollPane.getParent();

        if(dark){
            root.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("styles/light.css")).toExternalForm());
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/dark.css")).toExternalForm());
        }
        else{
            root.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("styles/dark.css")).toExternalForm());
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/light.css")).toExternalForm());
        }
    }

    public void populateScrollBar() throws IOException {
        if(this.invalidContours.isEmpty()){
            Label label = new Label("Wszystkie kontury poprawne.");
            scrollPane.setContent(label);
            errorLabel.setText(String.valueOf(this.invalidContours.size()));
        }
        else{
            VBox vBox = new VBox();
            vBox.setId("scrollVbox");
            vBox.setFillWidth(true);
            this.invalidContours.forEach((key, value) -> {
                Label label = new Label(key + ": " + value);
                vBox.getChildren().add(label);
            });
            scrollPane.setContent(vBox);
            errorLabel.setText(String.valueOf(this.invalidContours.size()));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.invalidContours = new LinkedHashMap<>();
        errorLabel.setText("");
    }
}
