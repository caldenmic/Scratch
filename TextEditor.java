import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.*;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import javax.swing.text.*;
import java.awt.event.WindowEvent;
import javax.swing.JFileChooser;
import javax.swing.undo.UndoManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;
import javax.swing.text.StyledEditorKit.FontSizeAction;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

public class TextEditor {
    //To render the frame
    private JFrame frame;
    //To provide a layout to enter text and images
    private JTextPane pane;
    //To disable text wrapping we initialize a JPanel
    private JPanel noWrapPanel;

    //To create menu buttons
    private JMenuBar menuBar;
    private JMenu menu;

    //To store the file being saved
    private File f;

    //Default title of the document
    private String DOC_TITLE = "Text Document 1";

    //UndoManager declaration
    UndoManager undo;

    //SimpleAttributeSet declaration
    private SimpleAttributeSet attributes;

    //Font size combo box
    JComboBox<String> fontSizeCombo;
    private static final String [] FONT_SIZES  = {"Font Size", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30"};

    static boolean saveFlag = true;

    public static void main(String args[]) {

        //To run the Swing application (Template code)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TextEditor().displayTextEditor();
            }
        });
    }

    public void save(String file_name) {
        try {
            //To write the contents of the pane to the saved file
            FileWriter writer = new FileWriter(f, false);
            BufferedWriter bw = new BufferedWriter(writer);
            frame.setTitle(file_name);
            bw.write(pane.getText());
            bw.flush();
            bw.close();
            saveFlag = true;
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }
    }

    public void saveFunction() {
        JFileChooser file = new JFileChooser();

        /* 
        If the file is created for the first time the file
        should be saved using the save file dialog box 
        */
        if(frame.getTitle() == DOC_TITLE) {
            int option = file.showSaveDialog(null);
            if(option == JFileChooser.APPROVE_OPTION) {
                f = new File(file.getSelectedFile().getAbsolutePath());
                File selected_file = file.getSelectedFile();
                String file_name = selected_file.getName();

                save(file_name);
                saveFlag = true;
            }
        }
        //To save after we have already saved it
        else {
            save(f.getName());
        }
    }

    public void openFunction() {
        JFileChooser fileChooser = new JFileChooser();
                
        //To display file explorer to open a file
        int temp = fileChooser.showOpenDialog(null);

        if(temp == JFileChooser.APPROVE_OPTION) {
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());

            try {
                String s1 = "";
                String s2 = "";
                FileReader reader = new FileReader(file);
                BufferedReader br = new BufferedReader(reader);
                s2 = br.readLine();

                //Appending text data into the string
                while((s1=br.readLine()) != null) {
                    s2 += "\n" + s1;
                }

                //To set the title to the name of the file being opened
                frame.setTitle(file.getName());
                pane.setText(s2);
                saveFlag = true;
            }
            catch(Exception e) {
                JOptionPane.showMessageDialog(frame, e.getMessage());
            }
        }
    }

    public void newFunction() {
        pane.setText("");
        frame.setTitle("Text Document 1");
        saveFlag = false;
    }

    public void boldFunction() {
        StyledDocument doc = (StyledDocument) pane.getDocument();
        int selectionEnd = pane.getSelectionEnd();
        int selectionStart = pane.getSelectionStart();
        if (selectionStart == selectionEnd) {
            return;
        }
        Element element = doc.getCharacterElement(selectionStart);
        AttributeSet as = element.getAttributes();

        MutableAttributeSet asNew = new SimpleAttributeSet(as.copyAttributes());
        StyleConstants.setBold(asNew, !StyleConstants.isBold(as));
        doc.setCharacterAttributes(selectionStart, pane.getSelectedText().length(), asNew, true);
    }

    public void italicFunction() {
        StyledDocument doc = (StyledDocument) pane.getDocument();
        int selectionEnd = pane.getSelectionEnd();
        int selectionStart = pane.getSelectionStart();
        if (selectionStart == selectionEnd) {
            return;
        }
        Element element = doc.getCharacterElement(selectionStart);
        AttributeSet as = element.getAttributes();

        MutableAttributeSet asNew = new SimpleAttributeSet(as.copyAttributes());
        StyleConstants.setItalic(asNew, !StyleConstants.isItalic(as));
        doc.setCharacterAttributes(selectionStart, pane.getSelectedText().length(), asNew, true);
    }

    public void underlineFunction() {
        StyledDocument doc = (StyledDocument) pane.getDocument();
        int selectionEnd = pane.getSelectionEnd();
        int selectionStart = pane.getSelectionStart();
        if (selectionStart == selectionEnd) {
            return;
        }
        Element element = doc.getCharacterElement(selectionStart);
        AttributeSet as = element.getAttributes();

        MutableAttributeSet asNew = new SimpleAttributeSet(as.copyAttributes());
        StyleConstants.setUnderline(asNew, !StyleConstants.isUnderline(as));
        doc.setCharacterAttributes(selectionStart, pane.getSelectedText().length(), asNew, true);
    }

    public void rightAlignFunction() {
        attributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_RIGHT);
        pane.setParagraphAttributes(attributes, true);
    }

    public void leftAlignFunction() {
        attributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_LEFT);
        pane.setParagraphAttributes(attributes, true);
    }

    public void centerAlignFunction() {
        attributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_CENTER);
        pane.setParagraphAttributes(attributes, true);
    }

    public void justifyFunction() {
        attributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_JUSTIFIED);
        pane.setParagraphAttributes(attributes, true);
    }

    public void colorFunction() {
        Color newColor = JColorChooser.showDialog(frame, "Choose a color", Color.BLACK);

        if(newColor == null) {
            pane.requestFocusInWindow();
            return;
        }
        int start = pane.getSelectionStart();
        int end = pane.getSelectionEnd();
        int lengthSelected = end - start;
        StyledDocument style = pane.getStyledDocument();

        AttributeSet oldSet = style.getCharacterElement(end-1).getAttributes();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet s = sc.addAttribute(oldSet, StyleConstants.Foreground, newColor);
        style.setCharacterAttributes(start, lengthSelected, s, true);
    }

    private int showWarningMessage() {
        String[] buttonLabels = {"Yes", "No", "Cancel"};
        String defaultOption = buttonLabels[0];
        Icon icon = null;
         
        return JOptionPane.showOptionDialog(null, "There's still something unsaved.\n" + "Do you want to save before exiting?", "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, icon, buttonLabels, defaultOption);    
    }

    public void windowCloseFunction() {
        if(saveFlag) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        else {
            int action = showWarningMessage();

            switch(action) {
                case JOptionPane.YES_OPTION:
                saveFunction();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                break;
                case JOptionPane.NO_OPTION:
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                break;
                case JOptionPane.CANCEL_OPTION:
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                break;
            }
        }
    }

    public void displayTextEditor() {
        //Creating a JFrame instance
        frame = new JFrame(DOC_TITLE);
        //Creating a instance of JTextPane
        pane = new JTextPane();
        //noWrapPanel is used to remove text wrapping
        noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(pane);

        //Creating an instance of UndoManager and adding it to the pane
        UndoManager undo = new UndoManager();
        pane.getDocument().addUndoableEditListener(undo);

        //Menu Bar initialization
        menuBar = new JMenuBar();
        
        /* 
        Creating a instance of JScroll Pane
        It is used to accomodate text if it overflows the given area by 
        providing scroll bars
        */ 
        JScrollPane scrollPane = new JScrollPane(noWrapPanel);
        //To set horizontal and vertical scroll bars only when the text overflows
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        //Add window listener
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                windowCloseFunction();
            }
        });

        //Adding Menu entries
        menu = new JMenu("File");
        menuBar.add(menu);

        //To create a new document on click of the New option in dropdown of File in the menu bar
        JMenuItem item_new = new JMenuItem("New");
        item_new.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
                newFunction();
            }
        });
        //Adding New option under the File dropdown
        menu.add(item_new);

        //To save a file 
        JMenuItem item_save = new JMenuItem("Save");
        item_save.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
                saveFunction();
            }
        });
        menu.add(item_save);

        //To Save As a file
        JMenuItem item_saveAs = new JMenuItem("Save As");
        item_saveAs.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
                JFileChooser file = new JFileChooser();

                /* 
                To use the Dialog box of save to navigate to the desired
                directory and save the file 
                */
                int option = file.showSaveDialog(null);
                if(option == JFileChooser.APPROVE_OPTION) {
                    f = new File(file.getSelectedFile().getAbsolutePath());
                    File selected_file = file.getSelectedFile();
                    String file_name = selected_file.getName();

                    save(file_name);
                }
            }
        });
        menu.add(item_saveAs);

        //To open a file
        JMenuItem item_open = new JMenuItem("Open");
        item_open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                openFunction();
            }
        });
        menu.add(item_open);

        //To exit from the text editor
        JMenuItem item_exit = new JMenuItem("Exit");
        item_exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });
        menu.add(item_exit);

        //To print the file
        JMenuItem item_print = new  JMenuItem("Print");
        item_print.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    boolean done = pane.print();
                    if(done) {
                        System.out.println("Printing is done");       
                    }
                    else {
                        System.out.println("Error");
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
        menu.add(item_print);

        //Edit in menu bar
        JMenu menu_edit = new JMenu("Edit");
        menuBar.add(menu_edit);

        //For cut menu item
        JMenuItem item_cut = new JMenuItem("Cut");
        item_cut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int start = pane.getSelectionStart();
                int end = pane.getSelectionEnd();
                
                //Store the selected text
                String cut_text = pane.getSelectedText();

                //StringBuilders are normally more effecient to work with
                //rather than string because they are mutable
                StringBuilder strBuilder = new StringBuilder(pane.getText());
                strBuilder.replace(start, end, "");

                pane.setText(strBuilder.toString());
                
                //Get System's clipboard
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(cut_text);
                
                //Add the selected text to the clipboard
                cb.setContents(stringSelection, null);
            }
        });
        menu_edit.add(item_cut);

        //For copy menu item
        JMenuItem item_copy = new JMenuItem("Copy");
        item_copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                pane.copy();
            }
        });
        menu_edit.add(item_copy);

        //For paste menu item
        JMenuItem item_paste = new JMenuItem("Paste");
        item_paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                pane.paste();
            }
        });
        menu_edit.add(item_paste);

        //For undo menu item
        JMenuItem item_undo = new JMenuItem("Undo");
        item_undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                undo.undo();
            }
        });
        menu_edit.add(item_undo);

        //Ctrl+Z key binding for undo
        pane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                undo.undo();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        //For redo menu item
        JMenuItem item_redo = new JMenuItem("Redo");
        item_redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                undo.redo();
            }
        });
        menu_edit.add(item_redo);

        //Ctrl+Y key binding for redo
        pane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                undo.redo();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        //CTRL+S key binding to save a file
        pane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                saveFunction();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        //CTRL+O key binding to open a file
        pane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                openFunction();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        //CTRL+N key binding to open a new file
        pane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                newFunction();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        //Format in menu bar
        JMenu menu_format = new JMenu("Format");
        menuBar.add(menu_format);

        //Bold menu item
        JMenuItem item_bold = new JMenuItem("Bold");
        item_bold.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                boldFunction();
            }
        });
        menu_format.add(item_bold);

        //Italic menu item
        JMenuItem item_italic = new JMenuItem("Italic");
        item_italic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                italicFunction();
            }
        });
        menu_format.add(item_italic);

        //Underline menu item
        JMenuItem item_underline = new JMenuItem("Underline");
        item_underline.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                underlineFunction();
            }
        });
        menu_format.add(item_underline);

        //Alignment menu inside format
        JMenu item_alignment = new JMenu("Alignment");
        
        //Right Alignment
        JMenuItem item_right = new JMenuItem("Right");
        item_right.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                rightAlignFunction();
            }
        });
        item_alignment.add(item_right);
        menu_format.add(item_alignment);

        //Left Alignment
        JMenuItem item_left = new JMenuItem("Left");
        item_left.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                leftAlignFunction();
            }
        });
        item_alignment.add(item_left);
        menu_format.add(item_alignment);

        //Center Alignment
        JMenuItem item_center = new JMenuItem("Center");
        item_center.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                centerAlignFunction();
            }
        });
        item_alignment.add(item_center);
        menu_format.add(item_alignment);

        //Justify Alignment
        JMenuItem item_justify = new JMenuItem("Justify");
        item_justify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                justifyFunction();
            }
        });
        item_alignment.add(item_justify);
        menu_format.add(item_alignment);

        //CTRL+B key binding to make the text bold
        pane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                boldFunction();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        //CTRL+I key binding to make the text italic
        pane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                italicFunction();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        //CTRL+U key binding to make the text underlined
        pane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                underlineFunction();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        //CTRL+R key binding to make the text right aligned
        pane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                rightAlignFunction();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        //CTRL+L key binding to make the text left aligned
        pane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                leftAlignFunction();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        //CTRL+E key binding to make the text center aligned
        pane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                centerAlignFunction();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        //CTRL+E key binding to make the text center aligned
        pane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                justifyFunction();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        //Menu Item for Text Color from the Color dialog box in java swing
        JMenuItem item_color = new JMenuItem("Color");
        item_color.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                colorFunction();
            }
        });
        menu_format.add(item_color);

        //For handling keyboard events
        pane.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                saveFlag = false;
            }
        });

        fontSizeCombo = new JComboBox<String>(FONT_SIZES);
        fontSizeCombo.setEditable(false);
        fontSizeCombo.addItemListener(new FontSizeItemListener());
        menuBar.add(fontSizeCombo);

        //Pictures menu
        JMenu menu_pic = new JMenu("Picture");
        menuBar.add(menu_pic);

        //Insert picture
        JMenuItem item_insert = new JMenuItem("Insert");
        item_insert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter picture = new FileNameExtensionFilter("JPEG files (*.png)", "png");
                fc.setFileFilter(picture);
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

                if (fc.showDialog(frame, "Insert")!=JFileChooser.APPROVE_OPTION)  return;
                String filename = fc.getSelectedFile().getAbsolutePath();

                if (filename==null) return;

                try {
                    Image img = ImageIO.read(new File(filename));
                    ImageIcon pictureImage = new ImageIcon(img);
                    pane.insertIcon(pictureImage);
                } 
                catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "Could not find file: " + filename);
                }
            }
        });
        menu_pic.add(item_insert);

        pane.setFocusable(true);
        pane.requestFocusInWindow();
        
        //Setting frame properties
        frame.setSize(500, 500);
        frame.setLocation(500, 100);
        frame.setResizable(true);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        //Adding the JScrollPane to the frame
        frame.add(scrollPane);
        frame.setJMenuBar(menuBar);
    }

    private class FontSizeItemListener implements ItemListener {
        public void itemStateChanged(ItemEvent ie) {
            if((ie.getStateChange() != ItemEvent.SELECTED) || (fontSizeCombo.getSelectedIndex() == 0)) {
                return;
            }
            String fontSizeInString = (String) ie.getItem();
            int fontSize = 0;
            try {
                fontSize = Integer.parseInt(fontSizeInString);
            }
            catch(NumberFormatException e) {
                return;
            }
            fontSizeCombo.setAction(new FontSizeAction(fontSizeInString, fontSize));
            fontSizeCombo.setSelectedIndex(0);
            pane.requestFocusInWindow();
        }
    }
}