package com.apamatesoft.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Delete2 extends Task implements Runnable {

    //<editor-fold desc="FUNCTIONS">
    private UpdateFunction update;
    private FileNotDeletedFunction fileNotDeleted;
    //</editor-fold>

    private List<File> files;
    private File file;

    //<editor-fold desc="CONSTRUCTORS">
    public Delete2(File[] files) {
        this.files = Arrays.asList(files);
    }

    public Delete2(List<File> files) {
        this.files = files;
    }

    public Delete2(File file) {
        File[] files = {file};
        this.files = Arrays.asList(files);
    }

    private Delete2(Builder b) {
        super(b);
        update = b.update;
    }
    //</editor-fold>

    @Override
    public void run() {

        try {
            if (beforeFunction !=null) beforeFunction.action();
            delete(files);
            if (successFunction !=null) successFunction.action();
            if (callbackFunction !=null) callbackFunction.action().start();
        } catch (Exception e) {
            if (errorFunction!=null) errorFunction.action(e);
        } finally {
            if (afterFunction !=null) afterFunction.action();
        }

    }

    @Override
    public void start() {
        i = -1;
        startTime = System.currentTimeMillis();
        new Thread(this).start();
    }

    public void delete() {
        action(files);
    }

    private void action(List<File> files) {
        for (File a: files) {
            ++i;
            setTime();
            file = a;
            if (update!=null) update.action(this);
            if (a.isDirectory() && a.listFiles().length>0) delete(Arrays.asList( a.listFiles()) );
            if (!a.delete() && fileNotDeleted!=null) fileNotDeleted.action(a, Files.getError(a));
        }
    }

    //<editor-fold desc="ADD FUNCTIONS">
    public void onUpdate(UpdateFunction update) {
        this.update = update;
    }

    public void onFileNotDeleted(FileNotDeletedFunction fileNotDeleted) {
        this.fileNotDeleted = fileNotDeleted;
    }
    //</editor-fold>

    //<editor-fold desc="STATICS">
    public static void delete(List<File> files) {
        for (File a: files) {
            if (a.isDirectory()) delete(Arrays.asList( a.listFiles()) );
            a.delete();
        }
    }

    public static void delete(File[] files) {
        delete(Arrays.asList(files));
    }

    public static void delete(File file) {
        List<File> files = new ArrayList<>();
        files.add(file);
        delete(files);
    }
    //</editor-fold>

    //<editor-fold desc="FUNTIONAL INTERFACES">
    @FunctionalInterface
    public interface UpdateFunction {
        void action(Delete2 delete);
    }

    @FunctionalInterface
    public interface FileNotDeletedFunction {
        void action(File f, byte errorCode);
    }
    //</editor-fold>

    public static class Builder extends Task.Builder {

        //<editor-fold desc="FUNCTIONS">
        private UpdateFunction update;
        private FileNotDeletedFunction fileNotDeleted;
        //</editor-fold>

        private List<File> files;

        //<editor-fold desc="CONSTRUCTORS">
        public Builder(File[] files) {
            this.files = Arrays.asList(files);
        }

        public Builder(List<File> files) {
            this.files = files;
        }

        public Builder(File file) {
            File[] files = {file};
            this.files = Arrays.asList(files);
        }
        //</editor-fold>

        //<editor-fold desc="ADD FUNCTIONS">
        public Builder onFileNotDeleted(FileNotDeletedFunction fileNotDeleted) {
            this.fileNotDeleted = fileNotDeleted;
            return this;
        }

        public Builder onUpdate(UpdateFunction update) {
            this.update = update;
            return this;
        }
        //</editor-fold>

        public Delete2 build() {
            return new Delete2(this);
        }

        @Override
        public void start() {
            new Delete2(this).start();
        }

    }

}