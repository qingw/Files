package com.apamatesoft.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Delete implements Runnable {

    //<editor-fold desc="FUNCTIONS">
    private BeforeFunction beforeFunction;
    private UpdateFunction updateFunction;
    private FileNotDeletedFunction fileNotDeletedFunction;
    private ErrorFunction errorFunction;
    private FinishFunction finishFunction;
    //</editor-fold>

    //<editor-fold desc="ATTRIBUTES">
    private List<File> files;
    private int i;
    private File file;
    //</editor-fold>

    //<editor-fold desc="CONSTRUCTORS">
    public Delete(File[] files) {
        this.files = Arrays.asList(files);
    }

    public Delete(List<File> files) {
        this.files = files;
    }

    public Delete(File file) {
        File[] files = {file};
        this.files = Arrays.asList(files);
    }

    private Delete(Builder b) {
        files = b.files;
        beforeFunction = b.beforeFunction;
        updateFunction = b.updateFunction;
        fileNotDeletedFunction = b.fileNotDeletedFunction;
        errorFunction = b.errorFunction;
        finishFunction = b.finishFunction;
    }
    //</editor-fold>

    @Override
    public void run() {
        try {
            if (beforeFunction!=null) beforeFunction.action();
            delete(files);
        } catch (Exception e) {
            if (errorFunction!=null) errorFunction.action(e);
        } finally {
            if (finishFunction!=null) finishFunction.action(this);
        }
    }

    public void delete() {
        action(files);
    }

    private void action(List<File> files) {
        for (File a: files) {
            ++i;
            file = a;
            if (updateFunction!=null) updateFunction.action(this);
            if (a.isDirectory() && a.listFiles().length>0) delete(Arrays.asList( a.listFiles()) );
            if (!a.delete() && fileNotDeletedFunction!=null) fileNotDeletedFunction.action(a, Files.getError(a));
        }
    }

    //<editor-fold desc="GETTERS">
    public int getI() {
        return i;
    }

    public File getFile() {
        return file;
    }
    //</editor-fold>

    public void start() {
        i = -1;
        new Thread(this).start();
    }

    @Override
    public String toString() {
        return "Delete: { i: "+i+", file: "+file+"}";
    }

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

    //<editor-fold desc="ADD FUNCTIONS">
    public void onBefore(BeforeFunction f) {
        beforeFunction = f;
    }

    public void onError(ErrorFunction f) {
        errorFunction = f;
    }

    public void onFileNotDeleted(FileNotDeletedFunction f) {
        fileNotDeletedFunction = f;
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
    public interface BeforeFunction {
        void action();
    }

    @FunctionalInterface
    public interface UpdateFunction {
        void action(Delete d);
    }

    @FunctionalInterface
    public interface ErrorFunction {
        void action(Exception e);
    }

    @FunctionalInterface
    public interface FileNotDeletedFunction {
        void action(File f, byte errorCode);
    }

    @FunctionalInterface
    public interface FinishFunction {
        void action(Delete d);
    }
    //</editor-fold>

    public static class Builder {

        //<editor-fold desc="FUNCTIONS">
        private ErrorFunction errorFunction;
        private FileNotDeletedFunction fileNotDeletedFunction;
        private FinishFunction finishFunction;
        private UpdateFunction updateFunction;
        private BeforeFunction beforeFunction;
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
        public Builder onBefore(BeforeFunction f) {
            beforeFunction = f;
            return this;
        }

        public Builder onError(ErrorFunction f) {
            errorFunction = f;
            return this;
        }

        public Builder onFileNotDeleted(FileNotDeletedFunction f) {
            fileNotDeletedFunction = f;
            return this;
        }

        public Builder onFinish(FinishFunction f) {
            finishFunction = f;
            return this;
        }

        public Builder onUpdate(UpdateFunction f) {
            updateFunction = f;
            return this;
        }
        //</editor-fold>

        public Delete build() {
            return new Delete(this);
        }

        public void start() {
            new Delete(this).start();
        }

    }

}