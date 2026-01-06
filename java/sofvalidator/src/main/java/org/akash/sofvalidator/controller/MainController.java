package org.akash.sofvalidator.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.akash.sofvalidator.service.FileMatchService;
import org.akash.sofvalidator.service.PdfSofParserService;
import org.akash.sofvalidator.service.RarExtractorService;

import java.io.File;
import java.util.List;

public class MainController {
    public TextArea resultArea;

    private File sofPdfFile;
    private File rarFile;

    public void uploadSofPdf(ActionEvent actionEvent) {
        FileChooser chooser=new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files","*.pdf")
        );
        sofPdfFile = chooser.showOpenDialog(new Stage());
    }

    public void uploadRar(ActionEvent actionEvent) {
        FileChooser chooser=new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("RAR Files","*.rar")
        );
        rarFile = chooser.showOpenDialog(new Stage());
    }

    public void validateFiles(ActionEvent actionEvent) {
        if(sofPdfFile==null || rarFile==null){
            resultArea.setText("❌ Please upload both SOF PDF and RAR file.");
            return;
        }
        try {
            PdfSofParserService pdfService = new PdfSofParserService();
            RarExtractorService rarService = new RarExtractorService();
            FileMatchService matchService = new FileMatchService();
            List<String> sofFileNames=pdfService.extractInputFileNames(sofPdfFile);
            List<String> rarFileNames = rarService.extractFileNames(rarFile);
            String report = matchService.compare(sofFileNames, rarFileNames);
            resultArea.setText(report);
        }catch (Exception e){
            resultArea.setText("error: "+e.getMessage());
        }
    }
}
