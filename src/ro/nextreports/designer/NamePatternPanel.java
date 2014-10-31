package ro.nextreports.designer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ro.nextreports.designer.util.I18NSupport;

public class NamePatternPanel extends JPanel {
    
    private JTextField nameField;


    public NamePatternPanel(String pattern) {
    	
    	nameField = new JTextField(20);
    	if (pattern != null) {
    		nameField.setText(pattern);
    	}
    	
    	setLayout(new GridBagLayout());
    	add(new JLabel(I18NSupport.getString("pattern.action")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        add(nameField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 0, 0), 0, 0));
    	
    }

    public String getPattern() {
    	String text = nameField.getText();
    	if ("".equals(text.trim())) {
    		return null;
    	}
    	return text;
    }
}
