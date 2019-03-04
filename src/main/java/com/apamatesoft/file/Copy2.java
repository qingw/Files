/**
 * ¿Que pasa si el destino no es una carpeta?
 */
package com.apamatesoft.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Copy2 extends Task implements Runnable{

    //<editor-fold defaultstate="collapsed" desc="FUNCTIONS">
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ATTRIBUTES">
    private List<File> files; // lista de archivos a ser copiados
    private File destiny; // Carpeta de destino
    private List<OriginDestiny> queue = new ArrayList<>();
    private boolean keep;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CONSTRUCTOR">
    public Copy2(File[] files, File destiny) {
        this.files = Arrays.asList(files);
        this.destiny = destiny;
    }

    public Copy2(List<File> files, File destiny) {
        this.files = files;
        this.destiny = destiny;
    }

    public Copy2(File file, File destiny) {
        List<File> files = new ArrayList<>();
        files.add(file);
        this.files = files;
        this.destiny = destiny;
    }

    public Copy2(Builder b) {
        super(b);
        updateFunction = b.updateFunction;
        files = b.files;
        destiny = b.destiny;
    }
    //</editor-fold>

    @Override
    public void run() {

        try {
            if (beforeFunction !=null) beforeFunction.action();
            copy(files, destiny);
            if (successFunction!=null) successFunction.action();
        } catch (Exception e) {
            if (errorFunction!=null) errorFunction.action(e);
        } finally {
            if (afterFunction !=null) afterFunction.action();
        }

    }

    @Override
    public void start() {
        new Thread(this).start();
    }

    public void copy(List<File> files, File destiny) throws IOException {
        Path from, to;
        for (File a: files) {
            update();
            if (updateFunction!=null) updateFunction.action(this);
            File b = new File(destiny, a.getName());
            System.out.println(">>: a: "+a);
            System.out.println(">>: b: "+b);
            if (b.exists()) {
                if (keep) {
                    System.out.println(">>: mantener");
                    keep = false;
                    b = new File( destiny, getNewName(a, b.getParentFile()) );
                    System.out.println(">>: new Name: "+b);
                } else {
                    System.out.println(">>: en cola");
                    queue.add( new OriginDestiny(a, b) );
                    continue;
                }
            }
            from = Paths.get(a.toURI());
            to = Paths.get(b.toURI());
            Files.copy(from, to, REPLACE_EXISTING);
            if (a.isDirectory() && a.listFiles().length>0) copy(Arrays.asList(a.listFiles()), b);
        }
    }

    public void keepAction(File a, File b) {
        keep = true;
        queue.remove(0);
        files = new ArrayList<>();
        files.add(a);
        destiny = b;
    }

    public List<OriginDestiny> getQueue() {
        return queue;
    }

    //<editor-fold defaultstate="collapsed" desc="STATICS">
    public static String getNewName(File file) {
        return getNewName(file.getName());
    }

    public static String getNewName(String name) {
        String[] s = name.split(Pattern.quote("."));
        String o = "";
        if (!s[0].equals("")) {
            for (int i=0; i<s.length; i++) o += s[i]+ (i==0 ? "(copy)" : "") +".";
        } else {
            for (int i=0; i<s.length; i++) o += s[i]+ (i==1 ? "(copy)" : "") +".";
        }
        return o.substring(0, o.length()-1);
    }

    public static String getNewName(File origin, File destiny) {
        String[] s = origin.getName().split(Pattern.quote("."));
        int n = 1;
        File f;
        String o;
        do {
            o = "";
            for (int i=0; i<s.length; i++) o += s[i]+(i==(s[0].equals("") ? 1 : 0) ? "("+n+")" : "") +".";
            o = o.substring(0, o.length()-1);
            f = new File(destiny, o);
            n++;
        } while (f.exists());
        return o;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ADD FUNCTIONS">
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="FUNCTIONAL INTERFACES">
    //</editor-fold>

    public static class Builder extends Task.Builder {

        //<editor-fold defaultstate="collapsed" desc="FUNCTIONS">
        //</editor-fold>

        //<editor-fold desc="ATTRIBUTES">
        private List<File> files;
        private File destiny;
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="CONSTRUCTOR">
        public Builder(File[] files, File destiny) {
            this.files = Arrays.asList(files);
            this.destiny = destiny;
        }

        public Builder(List<File> files, File destiny) {
            this.files = files;
            this.destiny = destiny;
        }

        public Builder(File file, File destiny) {
            List<File> files = new ArrayList<>();
            files.add(file);
            this.files = files;
            this.destiny = destiny;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="ADD FUNCTIONS">
        //</editor-fold>

        public void start() {
            new Copy2(this).start();
        }

        public Copy2 build() {
            return new Copy2(this);
        }

    }

    public class OriginDestiny {

        public File origin;
        public File destiny;

        public OriginDestiny(File origin, File destiny) {
            this.origin = origin;
            this.destiny = destiny;
        }

        @Override
        public String toString() {
            return "OriginDestiny{origin: "+origin+", destiny: "+destiny+'}';
        }

    }

}