package kataster.cw1_kat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MenuController implements Initializable {
    private Parent root;
    private Stage stage;
    private Scene scene;
    @FXML
    private Button loadBtn;
    @FXML
    private Button submitBtn;
    @FXML
    private Button themeBtn;
    private boolean dark;
    private File contourFile;
    private Map<String, String> invalidContours;
    private Set<String> mentionedContourNumber;

    public void chooseFile(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik zawierajacy dane o konturze");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik tekstowy (.txt)", "*.txt"));
        File file = fileChooser.showOpenDialog(this.stage);
        if(file!=null){
            contourFile = file;
            String newText = contourFile.getName();
            loadBtn.setText(newText);
            loadBtn.setPrefWidth(150+newText.length());
            submitBtn.setDisable(false);
        }
    }

    public void checkContours(ActionEvent actionEvent){
        String numbPat = "\\d+\\.\\d+|^\\d+$";
        Pattern decimalNumbersPattern = Pattern.compile(numbPat);
        String letPat = "[a-zA-Z]+";
        Pattern letterPattern = Pattern.compile(letPat);

        try(Stream<String> lines = Files.lines(this.contourFile.toPath(), Charset.forName("ISO-8859-2"))){
            this.invalidContours = new HashMap<>();
            this.mentionedContourNumber = new HashSet<>();
            lines.forEach(line -> checkContourInLine(line, decimalNumbersPattern, letterPattern));
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        try{
            loadInvalidContoursWindow();
        }
        catch(IOException e){
            System.out.println("Error while loading second window");
            System.out.println(e.getMessage());
        }
    }

    private void checkContourInLine(String line, Pattern pattern, Pattern letterPattern) {
        if (line.isEmpty()) {
            return;
        }

        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return;
        }

        line = line.strip();
        if (!line.contains("-")) {
            addInvalidContour(line, "Brak znaku '-' w numerze konturu.");
            return;
        }

        if (!line.contains("/")) {
            addInvalidContour(line, "Brak znaku '/' w identyfikatorze konturu.");
            return;
        }

        String[] split = line.split("/");
        if (split.length > 2) {
            addInvalidContour(line, "Zbyt dużo znaków '/' w identyfikatorze konturu.");
            return;
        }

        if (split[0].contains(" ")) {
            addInvalidContour(line, "Numer konturu nie powinien zawierać znaków białych.");
            return;
        }

        if(this.mentionedContourNumber.contains(split[0])){
            addInvalidContour(line, "Numer konturu został już użyty, numery powinny być unikalne.");
            return;
        }
        this.mentionedContourNumber.add(split[0]);

        matcher = letterPattern.matcher(split[0]);
        if (matcher.find()) {
            addInvalidContour(line, "Numer konturu powinien składać się tylko z liczb.");
            return;
        }

        String contourId = split[1];
        String status = getContourCorrectnessStatus(contourId);
        if (!status.isEmpty()) {
            addInvalidContour(line, status);
        }
    }

    private void addInvalidContour(String line, String reason){
        this.invalidContours.put(line, reason);
    }
    private String getContourCorrectnessStatus(String contourId){
        String[] ofuOzu = contourId.split("-");
        Kontur kontur = (ofuOzu.length>1) ? new Kontur(ofuOzu[0], ofuOzu[1]) : new Kontur(ofuOzu[0]);
        return kontur.getStatus();
    }

    public void loadInvalidContoursWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmls/invalidContoursDisplay.fxml"));
        Scene secondScene = new Scene(loader.load());
        InvalidContoursController controller = loader.getController();
        controller.setData(this.invalidContours);
        controller.populateScrollBar();
        controller.setTheme(this.dark);
        Stage stage = new Stage();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/glebaIm.jpg"))));
        stage.setScene(secondScene);
        stage.show();
    }

    public void switchTheme(ActionEvent actionEvent){
        Parent root = loadBtn.getParent();
        this.dark = !this.dark;
        if (this.dark) {
            themeBtn.setText("Light");
            root.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("styles/light.css")).toExternalForm());
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/dark.css")).toExternalForm());
        } else {
            themeBtn.setText("Dark");
            root.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("styles/dark.css")).toExternalForm());
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/light.css")).toExternalForm());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.invalidContours = new LinkedHashMap<>();
        this.mentionedContourNumber = new HashSet<>();
        submitBtn.setDisable(true);
        this.dark = false;


    }
}
