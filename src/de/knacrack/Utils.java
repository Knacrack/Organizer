package de.knacrack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Utils {

    public static final File[] getFilesOfDirectory(String pPath) {
        return new File(pPath).listFiles();
    }



    private static final SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH-mm");
    }



    private static BasicFileAttributes getFileAttribute(File pFile) {
        try {
            return Files.readAttributes(pFile.toPath(), BasicFileAttributes.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    private static final String getPattern() {
        return Pattern.quote("\\d{4}-\\d{2}-\\d{2} \\d{2}-\\d{2}-\\d{2}");
    }



    private static final String getFormattedCreationTime(File pFile) {
        return getDateFormat().format(new Date(getFileAttribute(pFile).creationTime().toMillis()));
    }



    private static final boolean FileNameMatchPattern(File pFile) {

        if (pFile.getName().contains(".")) {
            return pFile.getName().split(".")[0].matches(getPattern());
        }

        return pFile.getName().matches(getPattern());
    }



    public static void createFolders(File[] pFiles) {

        for (File file : pFiles) {
            File f = new File(Main.getPath(), getFormattedCreationTime(file));
            if (!(f.isDirectory() && f.mkdir())) {
                f.mkdirs();
            }
        }
    }



    private static void createOrganisationalFolders(File[] pFiles) {
        File file;

        for (File inputFile : pFiles) {

            file = new File(Main.getPath(), getFormattedCreationTime(inputFile));

            if (file.mkdir()) {
                continue;
            }

            if (FileNameMatchPattern(file) && !file.isDirectory()) {
                file.mkdirs();
            }
            else if (!FileNameMatchPattern(file) && file.isDirectory()) {
                file.mkdirs();
            }
        }
    }



    private static List<File> getOrganisationalFolders(String pPath) {
        List<File> orgFiles = new ArrayList<File>();

        for (File file : getFilesOfDirectory(pPath)) {
            if (file.isDirectory() && FileNameMatchPattern(file)) {
                orgFiles.add(file);
            }
        }

        return orgFiles;
    }



    private static void moveFilesIntoOrganisationalFolders(List<File> pFolders, File[] pFiles) {

        for (File file : pFiles) {
            if (!pFolders.contains(file)) {
                pFolders.indexOf(file);
                try {
                    Files.move(Paths.get(file.toURI()), Paths.get(pFolders.get(pFolders.indexOf(file)).toURI()));
                }
                catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
