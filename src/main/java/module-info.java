module kataster.cw1_kat {
    requires javafx.controls;
    requires javafx.fxml;


    opens kataster.cw1_kat to javafx.fxml;
    exports kataster.cw1_kat;
}