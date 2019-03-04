package com.apamatesoft.file;

abstract public class Task {

    //<editor-fold defaultstate="collapsed" desc="FUNCTIONS">
    protected BeforeFunction beforeFunction;
    protected UpdateFunction updateFunction;
    protected SuccessFunction successFunction;
    protected CallbackFunction callbackFunction;
    protected ErrorFunction errorFunction;
    protected AfterFunction afterFunction;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ATRIBUTES">
    protected int i;
    protected int time;
    protected long startTime;
    //</editor-fold>

    protected Task() {}

    protected Task(Builder b) {
        beforeFunction = b.beforeFunction;
        updateFunction = b.updateFunction;
        successFunction = b.successFunction;
        callbackFunction = b.callbackFunction;
        errorFunction = b.errorFunction;
        afterFunction = b.afterFunction;
    }

    public abstract void start();

    protected void setTime() {
        time = (int) (System.currentTimeMillis()-startTime);
    }

    protected void update() {
        i++;
        setTime();
    }

    //<editor-fold defaultstate="collapsed" desc="GETTERS">
    public int getI() {
        return i;
    }

    public int getTime() {
        return time;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ADD FUNCTIONS">
    /**
     * Función que se ejecuta siempre antes de comenzar la tarea
     * @param beforeFunction
     */
    public void onBefore(BeforeFunction beforeFunction) {
        this.beforeFunction = beforeFunction;
    }

    /**
     * Función que se ejecuta en cada iteración de la tarea
     * @param updateFunction
     */
    public void onUpdate(UpdateFunction updateFunction) {
        this.updateFunction = updateFunction;
    }

    /**
     * Función que se ejecuta al culminar la tarea exitosamente
     * @param successFunction
     */
    public void onSuccess(SuccessFunction successFunction) {
        this.successFunction = successFunction;
    }

    /**
     * Función que se ejecuta al culminar la tarea exitosamente, para posteriormente ejecutar la siguiente tarea en cola
     * @param callbackFunction
     */
    public void onCallback(CallbackFunction callbackFunction) {
        this.callbackFunction = callbackFunction;
    }

    /**
     * Función que se ejecuta en caso de producirse algún error
     * @param errorFunction
     */
    public void onError(ErrorFunction errorFunction) {
        this.errorFunction = errorFunction;
    }

    /**
     * Funcón que se ejecuta siempre despues de terminar la tarea
     * @param afterFunction
     */
    public void onAfter(AfterFunction afterFunction) {
        this.afterFunction = afterFunction;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="FUNCTIONAL INTERFACES">
    @FunctionalInterface
    public interface BeforeFunction {
        void action();
    }

    @FunctionalInterface
    public interface UpdateFunction {
        void action(Task task);
    }

    @FunctionalInterface
    public interface SuccessFunction {
        void action();
    }

    @FunctionalInterface
    public interface CallbackFunction {
        Task action();
    }

    @FunctionalInterface
    public interface ErrorFunction {
        void action(Exception e);
    }

    @FunctionalInterface
    public interface AfterFunction {
        void action();
    }
    //</editor-fold>

    abstract public static class Builder {

        //<editor-fold defaultstate="collapsed" desc="FUNCTIONS">
        protected BeforeFunction beforeFunction;
        protected UpdateFunction updateFunction;
        protected SuccessFunction successFunction;
        protected CallbackFunction callbackFunction;
        protected ErrorFunction errorFunction;
        protected AfterFunction afterFunction;
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="ADD FUNCTIONS">
        /**
         * Función que se ejecuta siempre antes de comenzar la tarea
         * @param beforeFunction
         * @return Builder
         */
        public Builder onBefore(BeforeFunction beforeFunction) {
            this.beforeFunction = beforeFunction;
            return this;
        }

        /**
         * Función que se ejecuta en cada iteración de la tarea
         * @param updateFunction
         * @return Builder
         */
        public Builder onUpdate(UpdateFunction updateFunction) {
            this.updateFunction = updateFunction;
            return this;
        }

        /**
         * Función que se ejecuta al culminar la tarea exitosamente
         * @param successFunction
         * @return Builder
         */
        public Builder onSuccess(SuccessFunction successFunction) {
            this.successFunction = successFunction;
            return this;
        }

        /**
         * Función que se ejecuta al culminar la tarea exitosamente, para posteriormente ejecutar la siguiente tarea en
         * cola
         * @param callbackFunction
         * @return Builder
         */
        public Builder onCallback(CallbackFunction callbackFunction) {
            this.callbackFunction = callbackFunction;
            return this;
        }

        /**
         * Función que se ejecuta en caso de producirse algún error
         * @param errorFunction
         * @return Builder
         */
        public Builder onError(ErrorFunction errorFunction) {
            this.errorFunction = errorFunction;
            return this;
        }

        /**
         * Funcón que se ejecuta siempre despues de terminar la tarea
         * @param afterFunction
         * @return Builder
         */
        public Builder onAfter(AfterFunction afterFunction) {
            this.afterFunction = afterFunction;
            return this;
        }
        //</editor-fold>

        abstract public void start();

        abstract public Object build();

    }

}