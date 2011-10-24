package com.thoughtworks.core.domain;


import com.thoughtworks.core.utils.CoreUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ExtendedJarFile {

    private static Logger logger = CoreUtils.getLoggerFor(ExtendedJarFile.class);
    private JarFile jarFile;

    public ExtendedJarFile(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    public void extract(String path, File destination) throws IOException {
        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
        while (jarEntryEnumeration.hasMoreElements()) {
            JarEntry jarEntry = jarEntryEnumeration.nextElement();
            if (!jarEntry.getName().startsWith(path)){
                continue;
            }

            logger.trace("Going ahead with jar entry: " + jarEntry.getName());
            extractFileOrDirectory(path, jarEntry, destination);
        }
    }

    private void extractFileOrDirectory(String path, JarEntry jarEntry, File destination) throws IOException {
        String newPath = pruneParentDirectoriesFromJarEntryName(path, jarEntry);
        File extractedFile = new File(destination.getAbsolutePath() + File.separatorChar + newPath);
        logger.trace("Extracting: " + extractedFile);

        if (jarEntry.isDirectory()) {
            extractedFile.mkdirs();
        } else {
            extractFile(jarEntry, extractedFile);
        }
    }

    private String pruneParentDirectoriesFromJarEntryName(String path, JarEntry jarEntry) {
        String prunedPath = jarEntry.getName().replaceFirst(new File(path).getParentFile().getPath(), "");
        if (prunedPath.startsWith("/")) { prunedPath = prunedPath.substring(1, prunedPath.length());}
        return prunedPath;
    }

    private void extractFile(JarEntry jarEntry, File extractedFile) throws IOException {
        InputStream inputStream = jarFile.getInputStream(jarEntry);
        FileOutputStream outputStream = new FileOutputStream(extractedFile);
        try {
            while (inputStream.available() > 0) {
                outputStream.write(inputStream.read());
            }
        } finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static ExtendedJarFile fromUrl(URL url) throws IOException {
        String pathToJarFile = url.getPath().substring(5, url.getPath().indexOf("!"));
        String decodedPath = URLDecoder.decode(pathToJarFile, "UTF-8");
        logger.trace("Creating extended jar file from: " + decodedPath);
        return new ExtendedJarFile(new JarFile(new File(decodedPath)));
    }
}
