package com.raisedsoftware;

import com.raisedsoftware.animation.Animation;
import com.raisedsoftware.model.ImageViewModel;
import com.raisedsoftware.model.Shadow;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class Controller {
    ImageViewModel imageViewModel = new ImageViewModel();
    Animation animation = new Animation();
    Shadow shadow = new Shadow();
    @FXML
    public VBox preViewVbox;

    @FXML
    ComboBox selectedTypeComboBox;

    @FXML
    ImageView sourceImageView;


    @FXML
    ScrollPane rightScrollPane;

    @FXML
    VBox rightMenuSubVbox;

    @FXML
    private void handleDragOver(DragEvent dragEvent) {
        System.out.println("Dosya geldi");
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    private void handleDrop(DragEvent dragEvent) {
        try {
            List<File> files = dragEvent.getDragboard().getFiles();
            String extension = getFileExtension(files.get(0)).toLowerCase();
            VBox previewVBox = loadFilesToPreview(files, rightMenuSubVbox);
            rightScrollPane.setContent(previewVBox);
            switch (extension) {
                case "jpeg":
                case "jpg":
                case "png":
                    Image image = new Image(new FileInputStream(files.get(0)));
                    Rectangle rectangle = shadow.createShadowedBox(sourceImageView.getFitWidth(), sourceImageView.getFitHeight(), sourceImageView.getFitWidth(), sourceImageView.getFitWidth(), 3, 50, 30);
                    sourceImageView.setClip(rectangle);
                    sourceImageView.setImage(image);
                    break;
                case "pdf":

                    break;
                case "docx":

                    break;

                case "xlsx":
                    break;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

    private VBox loadFilesToPreview(List<File> files, VBox vBox) {
        vBox.setSpacing(10);
        for (File file : files) {
            try {
                switch (getFileExtension(file)) {
                    case "jpeg":
                    case "jpg":
                    case "png":
                        HBox hBox = new HBox();
                        hBox.setPrefSize(240, 200);
                        ImageView imgView = new ImageView();
                        imgView.setOnMouseClicked(mouseEvent -> {
                            try {
                                Image image = new Image(new FileInputStream(file));
                                setFilePreview(sourceImageView, image);
                                imageViewModel.setImage(image);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        });
                        imgView.prefHeight(150);
                        imgView.prefWidth(240);
                        imgView.setFitWidth(230);
                        imgView.setFitHeight(150);
                        imgView.setImage(new Image(new FileInputStream(file.getAbsolutePath())));
                        hBox.fillHeightProperty().set(true);
                        hBox.getChildren().add(imgView);
                        vBox.getChildren().add(hBox);
                        break;
                    case "pdf":
                        setFilePreview(sourceImageView, new Image(new FileInputStream(file)));
                        file.canRead();
                        break;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return vBox;
    }

    @FXML
    public void hoverAnimationPulse(MouseEvent event) {
        Node node = (Node) event.getSource();
        animation.hoverAnimationPulse(node);
    }

    @FXML
    private void exitAnimationPulse(MouseEvent event) {
        Node node = (Node) event.getSource();
        animation.exitAnimationPulse(node);
    }

    @FXML
    private void exitMouse(MouseEvent event) {
        Node node = (Node) event.getSource();
        System.exit(0);
    }

    private void setFilePreview(ImageView imageView, Image image) {
        imageView.setImage(image);
    }

    public void init() {
        if (selectedTypeComboBox.getItems().size() <= 0) {
            selectedTypeComboBox.getItems().addAll("Pdf Belgesi (.pdf)", "Word Belgesi (.docx)", "Excel Dosyası (.xlsx)", "Metin Belgesi (.txt)", "Resim (.png)");
            selectedTypeComboBox.getSelectionModel().select(0);
        }

    }
}
