import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JournalManager {
    JFrame f;
    JButton b;
    JLabel l;
    JTextArea ta;
    LocalDate ld;
    JournalManager() {
        ld = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date = dtf.format(ld);
        f = new JFrame("Journal Enterer");
        f.setSize(800,600);
        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ta = new JTextArea();
        ta.setBounds(100,100,600,300);

        l = new JLabel("Journal Manager: ");
        l.setBounds(100,50,150,30);


        b = new JButton("Submit");
        b.setBounds(350,450,100,30);
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input ="(" +date+ ") " +ta.getText();
                try(FileWriter fw = new FileWriter("journal.txt", true)) {
                    fw.write(input);
                    JOptionPane.showMessageDialog(null, "Entry saved for date: " +date);
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(null, "Error: " + ioe.getMessage());
                }
            }
        });

        f.add(l);
        f.add(b);
        f.add(ta);
        f.setVisible(true);
    }
    public static void main(String[] args) {
        new JournalManager();
    }
}
