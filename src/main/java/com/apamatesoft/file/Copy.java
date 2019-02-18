package com.apamatesoft.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Copy implements Runnable {

    private ErrorFunction errorFunction;
    private AlredyExistsFunction alredyExistsFunction;
    private FinishFunction finishFunction;
    private UpdateFunction updateFunction;

    private List<File> files;
    private File destiny;
    private boolean wait = false;

    //<editor-fold desc="CONSTRUCTOR">
    public Copy(File[] files, File destiny) {
        this.files = Arrays.asList(files);
        this.destiny = destiny;
    }

    public Copy(List<File> files, File destiny) {
        this.files = files;
        this.destiny = destiny;
    }

    public Copy(File file, File destiny) {
        List<File> files = new ArrayList<>();
        files.add(file);
        this.files = files;
        this.destiny = destiny;
    }
    //</editor-fold>

    @Override
    public void run() {
        try {
            copy(files, destiny);
        } catch (Exception e) {
            if (errorFunction!=null) errorFunction.action(e);
        } finally {
            if (finishFunction!=null) finishFunction.action();
        }
    }

    public void start() {
        new Thread( this ).start();
    }

    public void restart() {
        wait = false;
    }

    public void copy(List<File> files, File destiny) throws IOException, InterruptedException {
        System.out.println();
        System.out.println(">>: root: "+files.get(0).getParentFile()+" - - - - - - - - - -");

        Path from, to;

        for (File a: files) {
            if (updateFunction!=null) updateFunction.action();
            System.out.println();

            File b = new File(destiny, a.getName());
            if (b.exists() && alredyExistsFunction!=null) {
                alredyExistsFunction.action(b);
                wait = true;
                while (wait) Thread.sleep(100);
            }

            System.out.println(">>: a: "+a);
            System.out.println(">>: b: "+b);

            from = Paths.get(a.toURI());
            to = Paths.get(b.toURI());
            Files.copy(from, to, REPLACE_EXISTING);

            if (a.isDirectory() && a.listFiles().length>0) copy(Arrays.asList(a.listFiles()), b);

        }

    }

    //<editor-fold desc="ADD FUNCTIONS">
    public void onError(ErrorFunction f) {
        errorFunction = f;
    }

    public void onAlredyExists(AlredyExistsFunction f) {
        alredyExistsFunction = f;
    }

    public void onFinish(FinishFunction f) {
        finishFunction = f;
    }

    public void onUpdate(UpdateFunction f) {
        updateFunction = f;
    }
    //</editor-fold>

    //<editor-fold desc="FUNCTIONAL INTERFACES">
    @FunctionalInterface
    public interface ErrorFunction {
        void action(Exception e);
    }

    @FunctionalInterface
    public interface AlredyExistsFunction {
        void action(File f);
    }

    @FunctionalInterface
    public interface FinishFunction {
        void action();
    }

    @FunctionalInterface
    public interface UpdateFunction {
        void action();
    }
    //</editor-fold>

}