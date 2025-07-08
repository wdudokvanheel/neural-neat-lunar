package nl.wdudokvanheel.neat.lunar.neural;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class GenomeSerializationPanel extends JPanel {
    private TextArea textArea;
    private JButton copy = new JButton("Copy champion genome");
    public static int WIDTH = 800;
    public static int HEIGHT = 400;

    public GenomeSerializationPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, HEIGHT));

        copy.addActionListener(c -> {
            setClipboard(textArea.getText());
        });
        copy.setFocusable(false);

        this.setLayout(new BorderLayout());
        textArea = new TextArea();
        textArea.setFocusable(false);
        add(textArea,  BorderLayout.CENTER);
        add(copy, BorderLayout.SOUTH);
    }

    public String getText(){
        return textArea.getText();
    }

    public void setText(String text){
        textArea.setText(text);
    }

    public void setClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

}
