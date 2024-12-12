package edu.pw.ii.pap.z29.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.*;


public class GameFrame extends JFrame{
    Vector<Vector<JTextField>> allLetterFields;
    GUI gui;
    int focusedLine;
    boolean isUpdating;
    int length;
    JButton enterButton;
    InputMap inputs;
    ActionMap actions;
    final static int MAX_IT = 3;
    public GameFrame(GUI gui){
        super("Game");
        this.gui = gui;
        allLetterFields = new Vector<Vector<JTextField>>();
        focusedLine = 0;
        isUpdating = false;
        length = gui.getMainController().getGameController().getWordLength();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(GUI.BLACK);
        getContentPane().setLayout(new GridBagLayout());
        addGuiParts();
        createFocusManager();
        pack();
        setVisible(true);
        allLetterFields.get(0).get(0).requestFocusInWindow();
    }
    private void setFocus(int line, boolean offOn){
        for(var field : allLetterFields.get(line)){
            field.setFocusable(offOn);
            field.setRequestFocusEnabled(offOn);
        }
        allLetterFields.get(line).get(0).requestFocusInWindow();
    }
    private class EnterButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            String a = "";
            for (int i = 0; i < length; ++i){
                a = a.concat(allLetterFields.get(focusedLine).get(i).getText());
            }
            ArrayList<Integer> vals = new ArrayList<Integer>(gui.getMainController().getGameController().check(a));
            if (vals.size() == 0){
                JOptionPane.showMessageDialog(GameFrame.this, "Word doesn't exist");
                return;
            } else if (vals.size() == 1){
                JOptionPane.showMessageDialog(GameFrame.this, "Incorrect Length");
                return;
            }
            SwingUtilities.invokeLater(() -> {
            int i = 0;
            for (var val : vals){
                JTextField letterField = allLetterFields.get(focusedLine).get(i);
                //letterField.setEnabled(false);
                if(val == 0){
                    letterField.setBackground(GUI.GREEN);
                } else if (val == 1){
                    letterField.setBackground(GUI.YELLOW);
                } else{
                    letterField.setBackground(GUI.BLACK);
                }
                ++i;
            }
            if(vals.stream().distinct().limit(2).count() <= 1 && vals.get(0) == 0){
                JOptionPane.showMessageDialog(GameFrame.this, "Congratulations!!");
                return;
            }
            setFocus(focusedLine, false);
            ++focusedLine;
            if (focusedLine < MAX_IT){
                setFocus(focusedLine, true);
            } else{
                JOptionPane.showMessageDialog(GameFrame.this, "You lose!!");

            }
            });

        }
    }
    private class LetterFieldDocumentListener implements DocumentListener
    {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    int a;
                    JTextField letterField = (JTextField) e.getDocument().getProperty("SOURCE");
                    String text = letterField.getText();
                    if (text.length() >= 1) {
                        letterField.setText(text.substring(text.length()-1).toUpperCase());
                    } else if (text.isEmpty()){
                        return;
                    }
                    char l = text.charAt(0);
                    if (!(l>='a' && l <= 'z') && !(l>='A' && l<='Z') ){
                        letterField.setText("");
                        return;
                    }
                    if((a = allLetterFields.get(focusedLine).indexOf(letterField) + 1) < allLetterFields.get(focusedLine).size() && !text.equals(letterField.getText())){
                        allLetterFields.get(focusedLine).get(a).requestFocusInWindow();

                    }
                });
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                //pass
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                //pass
            }
    }
    private JPanel createCentralPanel(){
        var centralPanel = new JPanel();
        centralPanel.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));
        centralPanel.setBackground(GUI.MAIN_COLOR);
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.PAGE_AXIS));
        return centralPanel;
    }
    private JTextField createLetterField(){
        var letterField = new JTextField();
        letterField.setHorizontalAlignment(JTextField.CENTER);
        letterField.setColumns(2);
        letterField.setRequestFocusEnabled(false);
        letterField.getDocument().putProperty("SOURCE", letterField);
        letterField.getDocument().addDocumentListener(new LetterFieldDocumentListener());
        return letterField;
    }
    private JLabel createDefinitionLabel(){
        var definitionLabel = new JLabel();
        definitionLabel.setFont(new Font("Dialog", Font.BOLD, 10));
        definitionLabel.setBackground(Color.WHITE);
        definitionLabel.setForeground(GUI.SECONDARY_COLOR);
        definitionLabel.setHorizontalAlignment(JLabel.CENTER);
        definitionLabel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2, true));
        definitionLabel.setPreferredSize(new Dimension(200, 50));
        return definitionLabel;
    }
    private JButton createDefinitionButton(JLabel definitionLabel){
        var showButton = new JButton("Show Definition");
        showButton.setFont(new Font("Dialog", Font.BOLD, 10));
        showButton.setBackground(GUI.SECONDARY_COLOR);
        showButton.setForeground(GUI.MAIN_COLOR);
        showButton.setHorizontalAlignment(JButton.CENTER);
        showButton.addActionListener((ActionEvent e)->{
            definitionLabel.setText(gui.getMainController().getGameController().getDefinition());

        });
        return showButton;
    }
    private void createEnterWordButton(){
        enterButton = new JButton("Enter");
        enterButton.setFont(new Font("Dialog", Font.BOLD, 25));
        enterButton.setBackground(GUI.SECONDARY_COLOR);
        enterButton.setForeground(GUI.MAIN_COLOR);
        enterButton.addActionListener(new EnterButtonListener());
    }
    private void createFocusManager(){
        var forwardKeys = getFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        var newForwardKeys = new HashSet<AWTKeyStroke>(forwardKeys);
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
            newForwardKeys);

        var backwardKeys = getFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        var newBackwardKeys = new HashSet<AWTKeyStroke>(backwardKeys);
        newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
            newBackwardKeys);
        Action enterWordAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterButton.doClick();
            }
        };
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = this.getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterWord");
        actionMap.put("enterWord", enterWordAction);
        
    }
    private void addGuiParts() {
        var centralPanel = createCentralPanel();
        add(centralPanel);

        JLabel titleLabel = GUI.createTitleLabel(40);
        centralPanel.add(titleLabel);

        JLabel definitionLabel = createDefinitionLabel();
        JButton showButton = createDefinitionButton(definitionLabel);

        centralPanel.add(showButton);
        centralPanel.add(definitionLabel);

        for (int j = 0; j < MAX_IT; ++j){
            var letterFieldsPanel = new JPanel();
            letterFieldsPanel.setBackground(GUI.MAIN_COLOR);
            var letterFieldsVector = new Vector<JTextField>();
            for(int i = 0; i < length; ++i){
                var letterField = createLetterField();
                letterFieldsVector.addElement(letterField);
                letterFieldsPanel.add(letterField);
            }
            allLetterFields.addElement(letterFieldsVector);
            setFocus(j, false);
            centralPanel.add(letterFieldsPanel);
        }
        createEnterWordButton();
        centralPanel.add(enterButton);
        setFocus(0, true);
    }

}
