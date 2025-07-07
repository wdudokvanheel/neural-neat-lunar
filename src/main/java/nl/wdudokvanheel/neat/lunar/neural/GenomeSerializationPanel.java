package nl.wdudokvanheel.neat.lunar.neural;

import javax.swing.*;
import java.awt.*;

public class GenomeSerializationPanel extends JPanel {
    private TextArea textArea;
    public static int WIDTH = 800;
    public static int HEIGHT = 400;

    public GenomeSerializationPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, HEIGHT));

        this.setLayout(new BorderLayout());
        textArea = new TextArea();
        textArea.setFocusable(false);
        add(textArea,  BorderLayout.CENTER);
    }

    public String getText(){
        return textArea.getText();
    }

    public void setText(String text){
        textArea.setText(text);
    }
}
