package com.raisedsoftware;

import com.raisedsoftware.animation.Animation;
import com.raisedsoftware.model.ImageViewModel;
import com.raisedsoftware.model.Shadow;
import com.raisedsoftware.util.KeyCreater;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
    Button convertButton;

    @FXML
    ImageView sourceImageView;

    @FXML
    AnchorPane mainAnchorPane;

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
                case "jfif":
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
            System.err.println(e);
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
                String type = getFileExtension(file);
                switch (type) {
                    case "jpeg":
                    case "png":
                    case "jfif":
                    case "jpg":
                        vBox.getChildren().add(createHboxFromImage(vBox, file));
                        break;
                    case "pdf":
                        convertPdftoImage(file, vBox);
                        break;
                    case "docx":
                        File pdfFile = convertDocxtoPdf(file);
                        convertPdftoImage(pdfFile, vBox);
                        break;
                }
            } catch (Exception e) {
                System.err.println(e);
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
        hBox.setPrefSize(280, 200);
        ImageView imgView = new ImageView();
        ImageView close = new ImageView();
        imgView.setId(KeyCreater.randomNumber());
        close.setFitWidth(20);
        close.setFitHeight(20);
        close.setPickOnBounds(true);
        imgView.setOnMouseClicked(mouseEvent -> {
            try {
                Image image = convertImage(file);
                setFilePreview(sourceImageView, image);
                model.setImage(image);
                model.setId(imgView.getId());
            } catch (Exception e) {
                System.out.println(e);
            }
        });
        close.setOnMouseClicked(event -> {
            if (imgView.getId().equals(model.getId())) {
                sourceImageView.setImage(convertToFxImage(createImage("empty", sourceImageView.getFitWidth(), sourceImageView.getFitHeight(), "Dosyayı sürükle veya Seç", "png")));
                model.setImage(null);
                model.setId(null);
            }
            vBox.getChildren().remove(hBox);
            vBox.requestLayout();

        });
        imgView.prefHeight(180);
        imgView.prefWidth(270);
        imgView.setFitWidth(270);
        imgView.setFitHeight(180);
        imgView.setImage(convertImage(file));

        close.setImage(new Image(getClass().getResourceAsStream("/icons/icon-close.png")));
        hBox.fillHeightProperty().set(true);
        hBox.getChildren().add(imgView);
        hBox.getChildren().add(close);

        return hBox;
    }

    public void convertPdftoImage(File file, VBox vBox) throws IOException {
        PDDocument pdDocument = PDDocument.load(file);
        PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
        for (int i = 0; i < pdDocument.getNumberOfPages(); i++) {
            var bufferedImage = pdfRenderer.renderImage(i);
            vBox.getChildren().add(createHboxFromImage(vBox, convertToFxImage(bufferedImage)));
        }
        pdDocument.close();
    }

    public String convertImagetoText(File file, String language) {
        String result = "";
        try {

            Tesseract tesseract = new Tesseract();
//            tesseract.setLanguage(language);
            Path dataDirectory = Paths.get(ClassLoader.getSystemResource("data").toURI());
            tesseract.setDatapath(dataDirectory.toString());
            tesseract.setOcrEngineMode(1);
            BufferedImage image = ImageIO.read(new FileInputStream(file.getAbsolutePath()));
            result = tesseract.doOCR(image);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println(e);
        }
        return result;
    }

    public Image convertImage(File file) throws FileNotFoundException {
        return new Image(new FileInputStream(file.getAbsolutePath()));
    }

    public File convertDocxtoPdf(File file) throws IOException {
        XWPFDocument document = new XWPFDocument(new FileInputStream(file.getAbsolutePath()));
        PdfOptions pdfOptions = PdfOptions.create();
        File pdfFile = new File(file.getName().substring(0, file.getName().lastIndexOf(".")) + ".pdf");
        OutputStream outputStream = new FileOutputStream(pdfFile);
        PdfConverter converter = (PdfConverter) PdfConverter.getInstance();
        converter.convert(document, outputStream, pdfOptions);
        document.close();
        outputStream.close();
        return pdfFile;
    }

    public HBox createHboxFromImage(VBox vBox, Image image) {
        HBox hBox = new HBox();
        hBox.setPrefSize(280, 200);
        ImageView imgView = new ImageView();
        ImageView close = new ImageView();
        imgView.setId(KeyCreater.randomNumber());

        close.setPickOnBounds(true);
        close.setFitWidth(20);
        close.setFitHeight(20);
        imgView.setOnMouseClicked(mouseEvent -> {
            setFilePreview(sourceImageView, image);
            model.setImage(image);
            model.setId(imgView.getId());

        });
        close.setOnMouseClicked(event -> {
            if (imgView.getId().equals(model.getId())) {
                sourceImageView.setImage(convertToFxImage(createImage("empty", sourceImageView.getFitWidth(), sourceImageView.getFitHeight(), "Dosyayı sürükle veya Seç", "png")));
                model.setImage(null);
                model.setId(null);
            }
            vBox.getChildren().remove(hBox);
            vBox.requestLayout();

        });
        close.setImage(new Image(getClass().getResourceAsStream("/icons/icon-close.png")));
        imgView.prefHeight(180);
        imgView.prefWidth(270);
        imgView.setFitWidth(270);
        imgView.setFitHeight(180);
        imgView.setImage(image);
        hBox.fillHeightProperty().set(true);
        hBox.getChildren().add(imgView);
        hBox.getChildren().add(close);

        return hBox;
    }

    public void init() {
        try {
            if (selectedTypeComboBox.getItems().size() <= 0) {
                selectedTypeComboBox.getItems().addAll("Pdf Belgesi (.pdf)", "Word Belgesi (.docx)", "Metin Belgesi (.txt)", "Resim (.png)");
                selectedTypeComboBox.getSelectionModel().select(0);
            }
            sourceImageView.setImage(convertToFxImage(createImage("empty", sourceImageView.getFitWidth(), sourceImageView.getFitHeight(), "Dosyayı sürükle veya Seç", "png")));
        } catch (Exception e) {
            System.err.println(e);
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

    public BufferedImage createImage(String fileName, double width, double height, String drawString, String formatName) {
        try {
            File file = new File(fileName + "." + formatName);
            BufferedImage bufferedImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
            // Create a graphics which can be used to draw into the buffered image
            Graphics2D g2d = bufferedImage.createGraphics();

            g2d.fillRect(0, 0, (int) width, (int) height);


            g2d.setColor(Color.lightGray);
            g2d.drawString(drawString, (int) width / 2 - 100, (int) height / 2 - 50);

            // Disposes of this graphics context and releases any system resources that it is using.
            g2d.dispose();
            ImageIO.write(bufferedImage, formatName, file);
            return bufferedImage;

        } catch (Exception e) {
            return null;
        }
    }

    @FXML
    public void showFileChooser() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
                , new FileChooser.ExtensionFilter("Word Files", "*.docx")
                , new FileChooser.ExtensionFilter("Pdf Files", "*.pdf")
                , new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpeg", "*.jpg", "*.jfif")
        );
        fileChooser.setTitle("Select file");
        Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null) {
            VBox previewVBox = loadFilesToPreview(files, rightMenuSubVbox);
            rightScrollPane.setContent(previewVBox);
        }
    }
}