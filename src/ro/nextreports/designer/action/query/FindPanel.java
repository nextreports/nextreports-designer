package ro.nextreports.designer.action.query;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ro.nextreports.designer.util.I18NSupport;

public class FindPanel extends JPanel {

	private JTextField oldText;
	private JTextField newText;
	private JCheckBox ck;

	public FindPanel() {

		setLayout(new GridBagLayout());
		
		oldText = new JTextField(20);
		newText = new JTextField(20);
		ck = new JCheckBox(I18NSupport.getString("sqleditor.findReplaceDialog.caseSensitive"));		

		add(new JLabel(I18NSupport.getString("validate.replace.old")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 0), 0, 0));
		add(oldText, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		add(new JLabel(I18NSupport.getString("validate.replace.new")), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 0), 0, 0));
		add(newText, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		add(ck, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

	}
	
	public String getOldText() {
		return oldText.getText();
	}
	
	public String getNewText() {
		return newText.getText();
	}
	
	public boolean isCaseSensitive() {
		return ck.isSelected();
	}

}
