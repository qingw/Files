import com.apamatesoft.file.Copy2;

import java.io.File;

public class Test {

    private static View view;
    private static File a, b;
    private static Copy2 copy;

    public static void main(String[] args) {

        a = new File("origen");
        b = new File("destino");

        copy = (Copy2) new Copy2.Builder(a.listFiles(), b)
            .onUpdate( e -> System.out.println("UPDATE I: "+e.getI()))
            .onBefore( () -> System.out.println("BEFORE") )
            .onSuccess( Test::onSuccess )
            .onAfter( () -> System.out.println("AFTER") )
            .build();

        view = new View.Builder()
            .onClickPlay( Test::play )
            .onClickRemplazar( Test::remplazar )
            .onClickIgnorar( Test::ignorar )
            .onClickMantener( Test::mantener )
            .build();

    }

    private static void play() {
        copy.start();
    }

    private static void remplazar() {

    }

    private static void ignorar() {

    }

    private static void mantener() {
        view.getBtnMantener().setVisible(false);
        copy.start();
    }

    private static void onSuccess() {

        System.out.println(">>: onSuccess");
        System.out.println(">>: queue: "+copy.getQueue().size() );

        if (copy.getQueue().size()>0) {
            view.setVisible(view.getBtnMantener());
            System.out.println(">>: OriginDestiny: "+copy.getQueue().get(0));
            copy.keepAction(copy.getQueue().get(0).origin, copy.getQueue().get(0).destiny.getParentFile());
        }

    }

}