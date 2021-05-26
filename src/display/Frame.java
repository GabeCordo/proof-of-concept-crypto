package display;

import javax.swing.*;

class Frame {

    public static void main(String agrs[]) {
        JFrame frame = new JFrame("Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);

        frame.setVisible(true);
    }
}
