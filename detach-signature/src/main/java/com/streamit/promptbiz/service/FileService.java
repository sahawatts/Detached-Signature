package com.streamit.promptbiz.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class FileService {

    /**
     * List all files in current directory, not including sub-directory, and filtered the file name with the specific file type given in parameter. Please noted that, this function not included files in sub-directory in the directory given in parameter. To included all, must assigned max depth from 1 to Integer.MAX_VALUE.
     * @param dir of the directory
     * @param fileType String in file extension format e.g. ".txt", ".zip", ".xml"
     * @return List of File with specific file-type in directory
     * @throws IOException
     */
    public List<File> getAllFileWithTypeInDirectory(File dir, String fileType) throws IOException {
        return Files.find(
                Paths.get(dir.getAbsolutePath()),
                1,
                (filePath, fileAttr) -> fileAttr.isRegularFile())
                .filter(p -> p.toString().toLowerCase().endsWith(fileType.toLowerCase()))
                .map(p -> p.toFile())
                .collect(Collectors.toList());
    }

    /**
     * List all files in current directory, not including sub-directory, and filtered the file name with the specific file type given in parameter. Please noted that, this function not included files in sub-directory in the directory given in parameter. To included all, must assigned max depth from 1 to Integer.MAX_VALUE.
     * @param dir of the directory.
     * @param fileType String in file extension format e.g. ".txt", ".zip", ".xml".
     * @return List of File with specific file-type in directory.
     * @throws IOException
     */

    /**
     * List all files in current directory, not including sub-directory, and filtered the file name which contains all the words given in parameters. Please noted that, this function not included files in sub-directory in the directory given in parameter. To included all, must assigned max depth from 1 to Integer.MAX_VALUE.
     * @param dir of the directory.
     * @param containWord substrings or words to be contained in file name.
     * @return List of File which contains all the words given in parameters.
     * @throws IOException
     */
    public List<File> getAllNameFilteredFileInDirectory(File dir, String... containWord) throws IOException {
        try {
            return Files.find(
                    Paths.get(dir.getAbsolutePath()),
                    1,
                    (filePath, fileAttr) -> fileAttr.isRegularFile())
                    .filter(p -> {
                        for (String word : containWord) {
                            if (!p.toString().toLowerCase().contains(word.toLowerCase())) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .map(p -> p.toFile())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }
}
