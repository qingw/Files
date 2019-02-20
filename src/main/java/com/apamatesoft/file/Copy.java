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

    public static final byte ACTION_DELETE = 1;
    public static final byte ACTION_KEEP = 2;
    public static final byte ACTION_NO_COPY = 3;

    //<editor-fold desc="FUNCTIONS">
    private BeforeFunction beforeFunction;
    private UpdateFunction updateFunction;
    private AlredyExistsFunction alredyExistsFunction;
    private ErrorFunction errorFunction;
    private FinishFunction finishFunction;
    //</editor-fold>

    //<editor-fold desc="ATTRIBUTES">
    private List<File> files;
    private File destiny;
    private List<OriginDestiny> wait;
    private File fileOrigin, fileDestiny;
    private int i;
    private byte action;
    //</editor-fold>

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

    private Copy(Builder b) {
        beforeFunction = b.beforeFunction;
        updateFunction = b.updateFunction;
        alredyExistsFunction = b.alredyExistsFunction;
        errorFunction = b.errorFunction;
        finishFunction = b.finishFunction;
        files = b.files;
        destiny = b.destiny;
    }
    //</editor-fold>

    @Override
    public void run() {
        try {
            if (beforeFunction!=null) beforeFunction.action();
            // NO DEBE FUNCIONAR ASI.
            copy(files, destiny);
            if (wait.size()>0 && alredyExistsFunction!=null) alredyExistsFunction.action(this);
        } catch (Exception e) {
            if (errorFunction!=null) errorFunction.action(e);
        } finally {
            if (finishFunction!=null) finishFunction.action();
        }
    }

    public void start() {
        wait =  new ArrayList<>();
        i = -1;
        new Thread( this ).start();
    }

    //<editor-fold desc="ACTIOS">
    public void delete(OriginDestiny a) {

        Copy copy = new Builder(a.origin, a.destiny.getParentFile())
            .onUpdate( () -> System.out.println(">>: copy update") )
            .onFinish( () -> {
                if (wait.size()>0) wait.remove(0);
                if (wait.size()>0 && alredyExistsFunction!=null) alredyExistsFunction.action(this);
            })
            .build();

        Delete delete = new Delete.Builder(a.destiny)
            .onUpdate( u -> System.out.println(">>: delete: "+u) )
            .onFinish( d -> {
                System.out.println(">>: delete finish");
                copy.start();
            } )
            .build();

        delete.start();

    }

    public void keep() {
    }

    public void cancel() {
    }
    //</editor-fold>

    //<editor-fold desc="GETTERS">
    public File getFile() {
        return fileOrigin;
    }

    public int getI() {
        return i;
    }

    public OriginDestiny getOriginDestiny() {
        return wait.get(0);
    }
    //</editor-fold>

    public void copy(File[] files, File destiny) throws IOException {
        copy(Arrays.asList(files), destiny);
    }

    public void copy(File file, File destiny) throws IOException {
        List<File> files = new ArrayList<>();
        files.add(file);
        copy(files, destiny);
    }

    private void copy(OriginDestiny a) {
        try {
            copy(a.origin, a.destiny);
        } catch (Exception e) {
            if (errorFunction!=null) errorFunction.action(e);
        }
    }

    public void copy(List<File> files, File destiny) throws IOException {

        Path from, to;

        for (File a: files) {
            i++;
            fileOrigin = a;
            if (updateFunction!=null) updateFunction.action();
            File b = new File(destiny, a.getName());
            fileDestiny = b;
            System.out.println(">>: "+i+" origin: "+fileOrigin+", destiny: "+fileDestiny);
            if (b.exists()) {
                System.out.println(">>:   archivo \""+b+"\" ya existe");
                wait.add( new OriginDestiny(fileOrigin, fileDestiny) );
                System.out.println(">>:   LISTA DE ESPERA");
                for (OriginDestiny z: wait) {
                    System.out.println(">>:    - "+z);
                }
                continue;
            }
            from = Paths.get(a.toURI());
            to = Paths.get(b.toURI());
            Files.copy(from, to, REPLACE_EXISTING);
            if (a.isDirectory() && a.listFiles().length>0) copy(Arrays.asList(a.listFiles()), b);
        }

    }

    //<editor-fold desc="ADD FUNCTIONS">
    public void onBefore(BeforeFunction f) {
        beforeFunction = f;
    }

    public void onUpdate(UpdateFunction f) {
        updateFunction = f;
    }

    public void onAlredyExists(AlredyExistsFunction f) {
        alredyExistsFunction = f;
    }

    public void onError(ErrorFunction f) {
        errorFunction = f;
    }

    public void onFinish(FinishFunction f) {
        finishFunction = f;
    }
    //</editor-fold>

    //<editor-fold desc="FUNCTIONAL INTERFACES">
    @FunctionalInterface
    public interface BeforeFunction {
        void action();
    }

    @FunctionalInterface
    public interface UpdateFunction {
        void action();
    }

    @FunctionalInterface
    public interface AlredyExistsFunction {
        void action(Copy a);
    }

    @FunctionalInterface
    public interface ErrorFunction {
        void action(Exception e);
    }

    @FunctionalInterface
    public interface FinishFunction {
        void action();
    }
    //</editor-fold>

    public class OriginDestiny {

        public File origin;
        public File destiny;

        public OriginDestiny(File origin, File destiny) {
            this.origin = origin;
            this.destiny = destiny;
        }

        @Override
        public String toString() {
            return "OriginDestiny{" +
                "origin=" + origin +
                ", destiny=" + destiny +
                '}';
        }
    }

    public static class Builder {

        //<editor-fold desc="FUNCTIONS">
        private BeforeFunction beforeFunction;
        private UpdateFunction updateFunction;
        private AlredyExistsFunction alredyExistsFunction;
        private ErrorFunction errorFunction;
        private FinishFunction finishFunction;
        //</editor-fold>

        //<editor-fold desc="ATTRIBUTES">
        private List<File> files;
        private File destiny;
        //</editor-fold>

        //<editor-fold desc="CONSTRUCTOR">
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

        //<editor-fold desc="ADD FUNCTIONS">
        public Builder onBefore(BeforeFunction f) {
            beforeFunction = f;
            return this;
        }

        public Builder onUpdate(UpdateFunction f) {
            updateFunction = f;
            return this;
        }

        public Builder onAlredyExists(AlredyExistsFunction f) {
            alredyExistsFunction = f;
            return this;
        }

        public Builder onError(ErrorFunction f) {
            errorFunction = f;
            return this;
        }

        public Builder onFinish(FinishFunction f) {
            finishFunction = f;
            return this;
        }
        //</editor-fold>

        public void start() {
            new Copy(this).start();
        }

        public Copy build() {
            return new Copy(this);
        }

    }

}