package org.akash.sofvalidator.service;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RarExtractorService {

    public List<String> extractFileNames(File rarFile) throws RarException, IOException {
        List<String> fileNames = new ArrayList<>();
        Archive archive = new Archive(rarFile);
        FileHeader header;
        while ((header=archive.nextFileHeader())!=null){
            if(!header.isDirectory()){
                fileNames.add(header.getFileNameString().trim());
            }
        }
        archive.close();
        return fileNames;
    }
}
