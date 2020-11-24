/*
 * Created on 05.02.2004
 */
package org.lisasp.swing.filechooser.filefilter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;

import javax.swing.filechooser.FileFilter;

/**
 * @author Dennis
 */
public class SimpleFileFilter extends FileFilter implements FilenameFilter {

    public static SimpleFileFilter[] createFilterArray(String[] names, String[] suffixes) {
        if (suffixes == null) {
            throw new IllegalArgumentException("suffixes must not be null.");
        }
        if (names == null) {
            throw new IllegalArgumentException("names must not be null.");
        }
        if (suffixes.length != names.length) {
            throw new IllegalArgumentException(
                    "suffixes and names must have equal length (" + suffixes.length + "!=" + names.length + ")");
        }
        SimpleFileFilter[] sff = new SimpleFileFilter[names.length];
        for (int x = 0; x < sff.length; x++) {
            sff[x] = new SimpleFileFilter(names[x], suffixes[x]);
        }
        return sff;
    }

    private String description = null;
    private String[] suffixes = new String[0];

    public SimpleFileFilter(String name, String... suffix) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null.");
        }
        description = name;
        if (suffix != null) {
            LinkedList<String> ll = new LinkedList<>();
            for (String aSuffix : suffix) {
                if (aSuffix != null) {
                    String s = aSuffix;
                    if (!s.startsWith(".")) {
                        s = "." + s;
                    }
                    ll.addLast(s);
                }
            }
            suffixes = ll.toArray(new String[ll.size()]);
        }
    }

    public String getSuffix() {
        if (suffixes.length == 0) {
            return "";
        }
        return suffixes[0];
    }

    public String[] getSuffixes() {
        return suffixes;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String filename = file.getName().toLowerCase();
        for (String suffixe : suffixes) {
            if (filename.endsWith(suffixe)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return description;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(description);
        if (suffixes.length > 0) {
            sb.append(" (");
            sb.append(suffixes[0]);
            for (int x = 1; x < suffixes.length; x++) {
                sb.append(", *");
                sb.append(suffixes[x]);
            }
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public boolean accept(File dir, String filename) {
        for (String suffixe : suffixes) {
            if (filename.endsWith(suffixe)) {
                return true;
            }
        }
        return false;
    }
}