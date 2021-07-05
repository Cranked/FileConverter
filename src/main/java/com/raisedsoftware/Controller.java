package com.raisedsoftware;

import com.raisedsoftware.animation.Animation;
import com.raisedsoftware.model.ImageViewModel;
import com.raisedsoftware.model.Shadow;
import com.raisedsoftware.util.KeyCreater;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.crypto.KeyGenerator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    ImageViewModel model = new ImageViewModel();
    Animation animation = new Animation();
    Shadow shadow = new Shadow();
    ArrayList<Image> images = new ArrayList<>();
    @FXML
    VBox preViewVbox;

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
                    model.setImage(image);
                    sourceImageView.setImage(image);
                    break;
                case "pdf":
                    PDDocument pdDocument = PDDocument.load(files.get(0));
                    PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
                    Image pdfImage = convertToFxImage(pdfRenderer.renderImage(0));
                    sourceImageView.setImage(pdfImage);
                    pdDocument.close();
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
                        vBox.getChildren().add(createHboxFromImage(vBox, file));
                        break;
                    case "pdf":
                        PDDocument pdDocument = PDDocument.load(file);
                        PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
                        for (int i = 0; i < pdDocument.getNumberOfPages(); i++) {
                            var bufferedImage = pdfRenderer.renderImage(i);
                            vBox.getChildren().add(createHboxFromImage(vBox, convertToFxImage(bufferedImage)));
                        }
                        pdDocument.close();
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
        System.exit(0);
    }

    public void setFilePreview(ImageView imageView, Image image) {
        imageView.setImage(image);
    }

    public HBox createHboxFromImage(VBox vBox, File file) throws FileNotFoundException {
        HBox hBox = new HBox();
        hBox.setPrefSize(240, 200);
        ImageView imgView = new ImageView();
        ImageView close = new ImageView();
        imgView.setId(KeyCreater.randomNumber());
        close.setFitWidth(20);
        close.setFitHeight(20);
        close.setPickOnBounds(true);
        imgView.setOnMouseClicked(mouseEvent -> {
            try {
                Image image = new Image(new FileInputStream(file.getAbsolutePath()));
                setFilePreview(sourceImageView, image);
                model.setImage(image);
                model.setId(imgView.getId());
            } catch (Exception e) {
                System.out.println(e);
            }
        });
        close.setOnMouseClicked(event -> {
            if (imgView.getId() == model.getId()) {
                sourceImageView.setImage(null);
                model.setImage(null);
            }
            vBox.getChildren().remove(hBox);
            vBox.requestLayout();

        });
        imgView.prefHeight(150);
        imgView.prefWidth(220);
        imgView.setFitWidth(210);
        imgView.setFitHeight(150);
        imgView.setImage(new Image(new FileInputStream(file.getAbsolutePath())));

        close.setImage(new Image(getClass().getResourceAsStream("/icons/icon-close.png")));
        hBox.fillHeightProperty().set(true);
        hBox.getChildren().add(imgView);
        hBox.getChildren().add(close);

        return hBox;
    }

    public HBox createHboxFromImage(VBox vBox, Image image) {
        HBox hBox = new HBox();
        hBox.setPrefSize(240, 200);
        ImageView imgView = new ImageView();
        ImageView close = new ImageView();
        close.setPickOnBounds(true);
        close.setFitWidth(20);
        close.setFitHeight(20);
        imgView.setOnMouseClicked(mouseEvent -> {
            setFilePreview(sourceImageView, image);
            model.setImage(image);
        });
        close.setOnMouseClicked(event -> {
            vBox.getChildren().remove(hBox);
            vBox.requestLayout();

        });
        close.setImage(new Image(getClass().getResourceAsStream("/icons/icon-close.png")));
        imgView.prefHeight(150);
        imgView.prefWidth(240);
        imgView.setFitWidth(230);
        imgView.setFitHeight(150);
        imgView.setImage(image);
        hBox.fillHeightProperty().set(true);
        hBox.getChildren().add(imgView);
        hBox.getChildren().add(close);

        return hBox;
    }

    public void init() {
        if (selectedTypeComboBox.getItems().size() <= 0) {
            selectedTypeComboBox.getItems().addAll("Pdf Belgesi (.pdf)", "Word Belgesi (.docx)", "Metin Belgesi (.txt)", "Resim (.png)");
            selectedTypeComboBox.getSelectionModel().select(0);
        }
    }

    private static Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }
        return new ImageView(wr).getImage();
    }
}