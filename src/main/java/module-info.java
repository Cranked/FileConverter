module com.raisedsoftware {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires poi.ooxml;
    requires tess4j;
    requires fr.opensagres.poi.xwpf.converter.pdf;
    opens com.raisedsoftware;
    opens com.raisedsoftware.animation;
}