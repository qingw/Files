import com.apamatesoft.file.Copy;
import com.apamatesoft.file.Delete;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) {

        JFrame w = new JFrame();
        JPanel panel = new JPanel();
        JButton cancel = new JButton("cancelar");
        JButton eliminar = new JButton("eliminar");
        JButton conservar = new JButton("conservar");
        JLabel label = new JLabel();

        panel.add(conservar);
        panel.add(eliminar);
        panel.add(cancel);
        panel.add(label);
        w.add(panel);

        // -------------------------------------------------------------------------------------------------------------

        File origin = new File("origen");
        File destiny = new File("destino");

        Copy copy = new Copy.Builder(origin.listFiles(), destiny )
//            .onFinish( () -> label.setText("Copia terminada") )
            .onAlredyExists( f -> {
                label.setText("archivo \""+f.getOriginDestiny().destiny+"\" ya existe");
                cancel.setVisible(true);
                cancel.addActionListener( e -> {
                    cancel.setVisible(false);
                    f.delete(f.getOriginDestiny());
                });
            })
            .build();



        copy.start();

//        Delete delete = new Delete(f);
//        Delete.delete(f);

//        new Delete.Builder(f)
//            .onBefore( () -> System.out.println("before") )
//            .onUpdate( u -> System.out.println(u) )
//            .onFileNotDeleted( (file, e) -> System.err.println("Archivo "+file.getAbsolutePath()+" no eliminado, codigo de error: "+e) )
//            .onError( e-> System.err.println(e.getMessage()) )
//            .onFinish( u -> System.out.println(u.getI()+" archivos eliminados") )
//            .start();

//        delete(f.listFiles());

//        new d(f.listFiles(), new File("destino")).start();




        w.setSize(800, 600);
        w.setLayout(null);

        panel.setLayout(null);
        panel.setSize(w.getSize());

        cancel.setVisible(false);
        cancel.setSize(100, 30);
        cancel.setLocation(10, 10);

        eliminar.setSize(100, 30);
        eliminar.setLocation(cancel.getWidth()+cancel.getX()+10, 10);

        conservar.setSize(100, 30);
        conservar.setLocation(eliminar.getWidth()+eliminar.getX()+10, 10);

        label.setSize(200, 30);
        label.setLocation(10, 100);

        w.setVisible(true);

    }

}