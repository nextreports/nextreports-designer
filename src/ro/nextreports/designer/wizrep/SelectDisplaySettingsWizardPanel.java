package ro.nextreports.designer.wizrep;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ro.nextreports.designer.property.ExtendedColorChooser;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.engine.exporter.util.DisplayData;
import ro.nextreports.engine.util.ColorUtil;

public class SelectDisplaySettingsWizardPanel extends WizardPanel {
	
	private JTextField titleField;	
	private JCheckBox shouldRise;
	private JCheckBox shadow;
	private JTextField titleColorField;
	private JTextField valueColorField;
	private JTextField previousColorField;
	private JTextField backgroundColorField;
	private Dimension txtDim = new Dimension(150, 20);
	private Dimension buttonDim = new Dimension(20, 20);
	
	public SelectDisplaySettingsWizardPanel() {
		super();
		banner.setTitle(I18NSupport.getString("wizard.panel.step", 5, 5) + I18NSupport.getString("wizard.panel.display.title"));
		banner.setSubtitle(I18NSupport.getString("wizard.panel.display.subtitle"));
		init();
	}


	@Override
	public void onDisplay() {				
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public boolean validateNext(List<String> messages) {
		return true;
	}

	@Override
	public WizardPanel getNextPanel() {		
		return null;
	}

	@Override
	public boolean canFinish() {
		return true;
	}

	@Override
	public boolean validateFinish(List<String> messages) {		
		int color;
		try {
			color = Integer.parseInt(titleColorField.getText());
		} catch (NumberFormatException ex) {
			messages.add(I18NSupport.getString("wizard.panel.display.error.color"));
			return false;
		}
		
		context.setAttribute(WizardConstants.DISPLAY_DATA, getData()); 		
		WizardUtil.openReport(context, null);    
		
		return true;
	}

	@Override
	public void onFinish() {				
	}
	
	private void init() {
		setLayout(new GridBagLayout());  
				
		JLabel titleLabel = new JLabel(I18NSupport.getString("wizard.panel.display.title"));
		titleField = new JTextField();
		titleField.setPreferredSize(txtDim);
		titleField.setMinimumSize(txtDim);				
		
		shouldRise = new JCheckBox(I18NSupport.getString("wizard.panel.display.shouldRise"));
		shadow = new JCheckBox(I18NSupport.getString("wizard.panel.display.shadow"));
		
		Component[] titleColor = createColorField(I18NSupport.getString("wizard.panel.display.title.color"), Color.BLACK);		
		titleColorField = (JTextField)titleColor[1];			
		
		Component[] valueColor = createColorField(I18NSupport.getString("wizard.panel.display.value.color"), Color.BLUE);		
		valueColorField = (JTextField)valueColor[1];	
		
		Component[] previousColor = createColorField(I18NSupport.getString("wizard.panel.display.previous.color"), Color.LIGHT_GRAY);		
		previousColorField = (JTextField)previousColor[1];		
		
		Component[] backgroundColor = createColorField(I18NSupport.getString("wizard.panel.display.background.color"), Color.WHITE);		
		backgroundColorField = (JTextField)backgroundColor[1];		
						
		JLabel imageLabel = new JLabel(ImageUtil.getImageIcon("display_main"));
		imageLabel.setPreferredSize(new Dimension(280, 170));
		
		add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(titleField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(imageLabel, new GridBagConstraints(3, 0, 1, 8, 1.0, 1.0,  GridBagConstraints.CENTER, 
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));		
		add(shouldRise, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(shadow, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(titleColor[0], new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(titleColorField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(titleColor[2], new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(valueColor[0], new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(valueColorField, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(valueColor[2], new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(previousColor[0], new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(previousColorField, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(previousColor[2], new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(backgroundColor[0], new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(backgroundColorField, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(backgroundColor[2], new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
	}
	
	public DisplayData getData() {
		DisplayData data = new DisplayData();
		data.setTitle(titleField.getText());
		data.setShouldRise(shouldRise.isSelected());
		data.setShadow(shadow.isSelected());
		data.setTitleColor(ColorUtil.getHexColor(new Color(Integer.parseInt(titleColorField.getText()))));
		data.setValueColor(ColorUtil.getHexColor(new Color(Integer.parseInt(valueColorField.getText()))));
		data.setPreviousColor(ColorUtil.getHexColor(new Color(Integer.parseInt(previousColorField.getText()))));
		data.setBackground(ColorUtil.getHexColor(new Color(Integer.parseInt(backgroundColorField.getText()))));
		return data;
	}		
	
	private Component[] createColorField(String text, Color defaultColor) {
		JLabel colorLabel = new JLabel(text);
		final JTextField colorField = new JTextField();
		colorField.setEditable(false);
		colorField.setPreferredSize(txtDim);
		colorField.setMinimumSize(txtDim);
		colorField.setText(String.valueOf(defaultColor.getRGB()));	
		colorField.setBackground(defaultColor);
		JButton colorButton = new JButton();
		colorButton.setPreferredSize(buttonDim);
		colorButton.setMinimumSize(buttonDim);
		colorButton.setMaximumSize(buttonDim);
		colorButton.setIcon(ImageUtil.getImageIcon("copy_settings"));
		colorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color color = ExtendedColorChooser.showDialog(SwingUtilities.getWindowAncestor(SelectDisplaySettingsWizardPanel.this), 
						I18NSupport.getString("color.dialog.title"), null);
				if (color != null) {
					colorField.setText(String.valueOf(color.getRGB()));	
					colorField.setBackground(color);
				}
			}			
		});		
		return new Component[] {colorLabel, colorField, colorButton};
	}

}
