import javax.swing.*;
import java.awt.*;

public class View  extends JFrame {

    private ClickPlayFunction clickPlay;
    private ClickRemplazarFunction clickRemplazar;
    private ClickMantenerFunction clickMantener;
    private ClickIgnorarFunction clickIgnorar;

    private JButton btnRemplazar, btnMantener, btnIgnorar, btnPlay;
    private Dimension d;
    public static final int M = 10;

    public View() {
        init();
    }

    private View(Builder b) {
        clickPlay = b.clickPlay;
        clickRemplazar = b.clickRemplazar;
        clickMantener = b.clickMantener;
        clickIgnorar = b.clickIgnorar;
        init();
    }

    private void init() {
        d = new Dimension(100, 30);

        setSize(800, 600);
        setLocation( (Toolkit.getDefaultToolkit().getScreenSize().width-getWidth())/2, (Toolkit.getDefaultToolkit().getScreenSize().height-getHeight())/2 );
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        btnPlay = new JButton("PLAY");
        btnRemplazar = new JButton("REMPLAZAR");
        btnMantener = new JButton("MANTENER");
        btnIgnorar = new JButton("IGNORAR");

        add(btnRemplazar);
        add(btnMantener);
        add(btnIgnorar);
        add(btnPlay);

        btnPlay.setSize(d);
        btnPlay.setLocation(M, M);
        btnPlay.addActionListener( e -> {
            if (clickPlay!=null) clickPlay.action();
        });

        btnRemplazar.setSize(d);
        btnRemplazar.setLocation(M, dy(btnPlay)+M);
        btnRemplazar.setVisible(false);
        btnRemplazar.addActionListener( e -> {
            if (clickRemplazar!=null) clickRemplazar.action();
        });

        btnIgnorar.setVisible(false);
        btnIgnorar.setSize(d);
        btnIgnorar.setLocation(dx(btnRemplazar)+M, btnRemplazar.getY());
        btnIgnorar.addActionListener( e -> {
            if (clickIgnorar!=null) clickIgnorar.action();
        });

        btnMantener.setVisible(false);
        btnMantener.setSize(d);
        btnMantener.setLocation(dx(btnIgnorar)+M, btnRemplazar.getY());
        btnMantener.addActionListener( e -> {
            if (clickMantener!=null) clickMantener.action();
        });

        setVisible(true);
    }

    public static int dx(Container c) {
        return c.getX()+c.getWidth();
    }

    public static int dy(Container c) {
        return c.getY()+c.getHeight();
    }

    public void onClickPlay(ClickPlayFunction f) {
        clickPlay = f;
    }

    public void onClickRemplazar(ClickRemplazarFunction f) {
        clickRemplazar = f;
    }

    public void onClickIgnorar(ClickIgnorarFunction f) {
        clickIgnorar = f;
    }

    public void onClickMantener(ClickMantenerFunction f) {
        clickMantener = f;
    }

    public JButton getBtnRemplazar() {
        return btnRemplazar;
    }

    public JButton getBtnMantener() {
        return btnMantener;
    }

    public JButton getBtnIgnorar() {
        return btnIgnorar;
    }

    public void setVisible(JButton btn) {
        btnMantener.setVisible(false);
        btnIgnorar.setVisible(false);
        btnRemplazar.setVisible(false);
        btn.setVisible(true);
    }

    @FunctionalInterface
    public interface ClickPlayFunction {
        void action();
    }

    @FunctionalInterface
    public interface ClickRemplazarFunction {
        void action();
    }

    @FunctionalInterface
    public interface ClickMantenerFunction {
        void action();
    }

    @FunctionalInterface
    public interface ClickIgnorarFunction {
        void action();
    }

    public static class Builder {

        private ClickPlayFunction clickPlay;
        private ClickRemplazarFunction clickRemplazar;
        private ClickMantenerFunction clickMantener;
        private ClickIgnorarFunction clickIgnorar;

        public Builder onClickPlay(ClickPlayFunction f) {
            clickPlay = f;
            return this;
        }

        public Builder onClickRemplazar(ClickRemplazarFunction f) {
            clickRemplazar = f;
            return this;
        }

        public Builder onClickIgnorar(ClickIgnorarFunction f) {
            clickIgnorar = f;
            return this;
        }

        public Builder onClickMantener(ClickMantenerFunction f) {
            clickMantener = f;
            return this;
        }

        public View build() {
            return new View(this);
        }

    }


}
