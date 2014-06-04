package ro.nextreports.designer.i18n.action;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.NextReportsUtil;

public class KeySelectionPanel extends JPanel {
        
    private JComboBox keysCombo;
    private JTextField valueField;
    private boolean showValueField;
    private JCheckBox allCheck;

    private Dimension dim = new Dimension(200, 22);

    public KeySelectionPanel(boolean showValueField) {
        this.showValueField = showValueField;        
       
        keysCombo = new JComboBox();
        keysCombo.setEditable(true);
        AutoCompleteDecorator.decorate(keysCombo);
        keysCombo.setMinimumSize(dim);
        keysCombo.setPreferredSize(dim);
        if (showValueField) {
        	keysCombo.setEnabled(false);
        }
        
        List<String> keys = NextReportsUtil.getReportKeys();
        for (String key : keys) {
        	keysCombo.addItem(key);
        }        
        
        valueField = new JTextField();
        valueField.setMinimumSize(dim);
        valueField.setPreferredSize(dim);
        
        allCheck = new JCheckBox(I18NSupport.getString("languages.keys.selection.key.all"));

        setLayout(new GridBagLayout());
        
        add(new JLabel(I18NSupport.getString("languages.keys.selection.key")),
        		new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(keysCombo,
        		new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
        
        if (showValueField) {
        	add(new JLabel(I18NSupport.getString("languages.keys.selection.value")),
            		new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
            add(valueField,
            		new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        } else {
        	add(allCheck,
            		new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        }
    }
    
    public String getKey() {
    	return (String)keysCombo.getSelectedItem();
    }
    
    public List<String> getKeys() {
    	List<String> result = new ArrayList<String>();
    	if (allCheck.isSelected()) {
    		for (int i=0, size=keysCombo.getItemCount(); i<size; i++) {
    			result.add((String)keysCombo.getItemAt(i));
    		}
    	} else {
    		result.add(getKey());
    	}
    	return result;
    }
    
    public String getValue() {
    	return valueField.getText();
    }
            
    public boolean showValueField() {
    	return showValueField;
    }
    
    public void setKey(String key) {
    	keysCombo.setSelectedItem(key);
    }
    
    public void setValue(String value) {
    	valueField.setText(value);
    }
    
    public boolean isAll() {
    	return allCheck.isSelected();
    }

    
}
