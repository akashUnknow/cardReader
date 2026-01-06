package org.akash.sofvalidator.service;

import java.util.List;

public class FileMatchService {
    public String compare(List<String> sofFiles, List<String> rarFiles) {
        StringBuilder report = new StringBuilder();
        report.append("✅ Matched Files:\n");
        sofFiles.stream()
                .filter(rarFiles::contains)
                .forEach(f->report.append(f).append("\n"));
        report.append("\n❌ Missing Files:\n");
        sofFiles.stream()
                .filter(f -> !rarFiles.contains(f))
                .forEach(f -> report.append(f).append("\n"));

        return report.toString();
    }

}
