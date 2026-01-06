package org.akash.sofvalidator.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfSofParserService {

    public List<String> extractInputFileNames(File pdfFile) throws IOException {
        List<String> fileNames = new ArrayList<>();
        PDDocument document = PDDocument.load(pdfFile);
        PDFTextStripper stripper=new PDFTextStripper();
        String text=stripper.getText(document);
        document.close();
        for (String line:text.split("\n")){
            if(line.endsWith(".dat")|| line.endsWith(".txt")){
                fileNames.add(line.trim());
            }
        }
        return fileNames;
    }
}
