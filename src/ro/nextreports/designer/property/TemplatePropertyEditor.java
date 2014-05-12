package ro.nextreports.designer.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import ro.nextreports.designer.util.I18NSupport;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.ComponentFactory;
import com.l2fprod.common.swing.PercentLayout;
import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

public class TemplatePropertyEditor extends AbstractPropertyEditor {

	private DefaultCellRenderer label;

	private JButton button;

	private String template;

	public TemplatePropertyEditor() {
		JPanel paddingEditor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0));
		editor = paddingEditor;
		paddingEditor.add("*", label = new DefaultCellRenderer());
		label.setOpaque(false);
		paddingEditor.add(button = ComponentFactory.Helper.getFactory().createMiniButton());
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				selectTemplate();
			}

		});
		paddingEditor.add(button = ComponentFactory.Helper.getFactory().createMiniButton());
		button.setText("X");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				selectNull();
			}

		});
		paddingEditor.setOpaque(false);
	}

	public Object getValue() {
		return template;
	}

	public void setValue(Object value) {
		template = (String) value;
		label.setValue(value);
	}

	protected void selectTemplate() {
		String selectedImage = TemplateChooser.showDialog(editor, I18NSupport.getString("property.template.name"), template);
		if (selectedImage != null) {
			String oldImage = template;
			String newImage = selectedImage;
			label.setValue(newImage);
			template = newImage;
			firePropertyChange(oldImage, newImage);
		}
	}

	protected void selectNull() {
		Object oldImage = template;
		label.setValue(null);
		template = null;
		firePropertyChange(oldImage, null);
	}

}
