module com.raisedsoftware {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.fontbox;
    requires org.apache.pdfbox;
    requires com.twelvemonkeys.common.image;
    requires com.twelvemonkeys.common.io;
    requires com.twelvemonkeys.imageio.core;
    requires java.desktop;
    opens com.raisedsoftware;
    opens com.raisedsoftware.animation;
}