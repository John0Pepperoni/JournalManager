import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
class AddJournalEntry implements ActionListener {
    private JFrame f;
    private JButton submitButton;
    private JButton cancelButton;
    private JLabel l;
    private JTextArea ta;
    private LocalDate ld;
    private String date;
    AddJournalEntry() {
        ld = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        date = dtf.format(ld);

        f = new JFrame("Journal Enterer");
        f.setSize(600,600);
        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ta = new JTextArea();
        ta.setBounds(30,50,530,400);

        l = new JLabel("Add Entry: ");
        l.setBounds(100,20,150,30);


        submitButton = new JButton("Submit");
        submitButton.setBounds(100,500,100,30);
        submitButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(200,500,100,30);
        cancelButton.addActionListener(this);

        f.add(l);
        f.add(submitButton);f.add(cancelButton);
        f.add(ta);
        f.setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cancelButton) {
            f.dispose();
        }
        if(e.getSource() == submitButton) {
            if(ta.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter an entry");
                return;
            }
            String input ="(" +date+ ") " +ta.getText();
            try(FileWriter fw = new FileWriter("journal.txt", true)) {
                fw.write(input);
                fw.write("\n");
                fw.write("-----/-----/-----/-----/-----/-----/-----");
                fw.write("\n");
                JOptionPane.showMessageDialog(null, "Entry saved for date: " +date);
                f.dispose();
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(null, "Error: " + ioe.getMessage());
            }
        }
    }
}
class EntryList implements ActionListener {
    private JList<String> list;
    private JTextArea ta;
    private JFrame f;
    private JButton back;
    private DefaultListModel<String> entryList;
    private File textFile;
    private Scanner reader;
    EntryList() {
        entryList = new DefaultListModel<>();
        try {
            String line;
            textFile = new File("journal.txt");
            reader = new Scanner(textFile);
            while(reader.hasNextLine()) {
                line = reader.nextLine();
                if (line.startsWith("(")) {
                    String date = line.substring(1,11);
                    entryList.addElement(date);
                }
            }
            if(reader == null) {
                JOptionPane.showMessageDialog(null, "No entries found");
            }
        } catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null, "Error: " + fnfe.getMessage());
        } finally {
            reader.close();
        }
        list = new JList<>(entryList);
        list.setBounds(100,50,200,200);

        back = new JButton("Back");
        back.setBounds(100,300,100,30);
        back.addActionListener(this);

        f = new JFrame("Journal Entries");
        f.setSize(400,400);
        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        f.add(list);f.add(back);
    }
    public void close() {
        f.dispose();
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == back) {
            close();
        }
    }
}
public class JournalManager implements ActionListener {
    private JFrame f;
    private JLabel mainTitle;
    private JLabel subTitle;
    private JButton addEnt;
    private JButton viewEnt;
    private JButton exit;
    JournalManager() {
        f = new JFrame("Journal Manager");
        f.setSize(400,400);
        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainTitle = new JLabel("Journal Manager");
        mainTitle.setBounds(90,20,500,37);
        mainTitle.setFont(new Font("Arial", Font.BOLD, 25));

        subTitle = new JLabel("The current date is: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        subTitle.setBounds(100,50,500,30);
        subTitle.setForeground(Color.BLUE);

        viewEnt = new JButton("View Entries");
        viewEnt.setBounds(133,250,120,30);
        viewEnt.addActionListener(this);

        exit = new JButton("Exit");
        exit.setBounds(253,250,120,30);
        exit.addActionListener(this);

        addEnt = new JButton("Add Entry");
        addEnt.setBounds(13,250,120,30);
        addEnt.addActionListener(this);

        f.add(addEnt);f.add(viewEnt);f.add(exit);f.add(mainTitle);f.add(subTitle);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        new JournalManager();
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == addEnt) {
            new AddJournalEntry();
        } else if(e.getSource() == viewEnt) {
            new EntryList();
        } else if (e.getSource() == exit) {
            System.exit(0);
        }
    }
}
