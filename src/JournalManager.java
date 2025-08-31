import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;
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
        f.setLocationRelativeTo(null);

        ta = new JTextArea();
        ta.setBounds(30,50,530,400);
        // Wrap the entry text area in a scroll pane
        JScrollPane taScrollPane = new JScrollPane(ta);
        taScrollPane.setBounds(30,50,530,400);

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
        // Add the scroll pane instead of the bare text area
        f.add(taScrollPane);
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
class EntryList extends MouseAdapter implements ActionListener {
    private JList<String> list;
    private JTextArea ta;
    private JFrame f;
    private JFrame viewEntryFrame;
    private JButton viewEntryBackButton;
    private JButton back;
    private DefaultListModel<String> entryList;
    private JScrollPane scrollPane;
    private File textFile;
    private Scanner reader;
    private Pattern datePatternRegex;
    private Matcher datePatternMatcher;
    EntryList() {
        entryList = new DefaultListModel<>();
        try {
            String line;
            textFile = new File("journal.txt");
            reader = new Scanner(textFile);
            datePatternRegex = Pattern.compile("^\\(\\d{2}/\\d{2}/\\d{4}\\)");
            while(reader.hasNextLine()) {
                line = reader.nextLine();
                datePatternMatcher = datePatternRegex.matcher(line);
                if (datePatternMatcher.find()) {
                    String date = line.substring(1,11);
                    entryList.addElement(date);
                }
            }
            if(entryList.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No entries found");
            }
        } catch (FileNotFoundException fnfe) {
               JOptionPane.showMessageDialog(null, "Error: " + fnfe.getMessage());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        list = new JList<>(entryList);
        list.setBounds(100,50,200,200);
        list.addMouseListener(this);

        scrollPane = new JScrollPane(list);
        scrollPane.setBounds(100,50,200,200);
        back = new JButton("Back");

        back.setBounds(100,300,100,30);
        back.addActionListener(this);

        f = new JFrame("Journal Entries");
        f.setSize(400,400);
        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);

        f.add(scrollPane);
        f.add(back);
        f.setVisible(true);

        viewEntryFrame = new JFrame("View Entry");
        viewEntryFrame.setSize(600,600);
        viewEntryFrame.setLayout(null);
        viewEntryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewEntryFrame.setVisible(false);
        viewEntryFrame.setLocationRelativeTo(null);

        ta = new JTextArea();
        ta.setBounds(30,50,530,400);
        ta.setEditable(false);
        viewEntryBackButton = new JButton("Back");
        viewEntryBackButton.setBounds(100,500,100,30);

        JScrollPane viewScrollPane = new JScrollPane(ta);
        viewScrollPane.setBounds(30,50,530,400);
        viewEntryFrame.add(viewScrollPane);
        viewEntryFrame.add(viewEntryBackButton);
    }
    public void close() {
        f.dispose();
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == back) {
            close();
        }
    }
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int index = list.locationToIndex(e.getPoint());
            String selectedDate = list.getModel().getElementAt(index);
            StringBuilder entryText = new StringBuilder();
            try (Scanner scanner = new Scanner(new File("journal.txt"))) {
                boolean inEntry = false;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("(" + selectedDate + ")")) {
                        inEntry = true;
                        entryText.append(line.substring(12)).append("\n");
                        continue;
                    }
                    if (line.startsWith("-----/-----/-----/-----/-----/-----/-----")) {
                        if (inEntry) break;
                    }
                    if (inEntry) {
                        entryText.append(line).append("\n");
                    }
                }
            } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error reading entry: " + ex.getMessage());
            }
            ta.setText(entryText.toString());

            viewEntryFrame.setVisible(true);

            for (ActionListener al : viewEntryBackButton.getActionListeners()) {
                viewEntryBackButton.removeActionListener(al);
            }
            viewEntryBackButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    viewEntryFrame.dispose();
                }
            });
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
        f.setLocationRelativeTo(null);

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
