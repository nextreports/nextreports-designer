package ro.nextreports.designer.i18n.action;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledSeparator;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.Globals;
import ro.nextreports.engine.i18n.I18nLanguage;
import ro.nextreports.engine.i18n.I18nString;

public class ManageI18nPanel extends JPanel {

	private JXList keysList;
	private SortedListModel<String> keysModel;
	private JButton keyAddButton;
	private JButton keyEditButton;
	private JButton keyRemoveButton;

	private JXList languagesList;
	private DefaultListModel languagesModel;
	private JButton languageAddButton;
	private JButton languageEditButton;
	private JButton languageRemoveButton;

	private JXTable table;
	private KeysTableModel model;
	private JButton langKeyEditButton;

	public ManageI18nPanel() {
		super();

		Dimension buttonDim = new Dimension(20, 20);
		keysList = new JXList();
		keysList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {
					modifyKey();
				}
			}
		});

		keysModel = new SortedListModel<String>();
		JScrollPane scroll = new JScrollPane();
		scroll.setPreferredSize(new Dimension(200, 110));
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.getViewport().add(keysList, null);
		keysList.setModel(keysModel);

		keyAddButton = new JButton(ImageUtil.getImageIcon("add"));
		keyAddButton.setToolTipText(I18NSupport.getString("languages.keys.add"));
		keyAddButton.setPreferredSize(buttonDim);
		keyAddButton.setMinimumSize(buttonDim);
		keyAddButton.setMaximumSize(buttonDim);
		keyAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addKey();
			}
		});

		keyEditButton = new JButton(ImageUtil.getImageIcon("edit"));
		keyEditButton.setToolTipText(I18NSupport.getString("languages.keys.edit"));
		keyEditButton.setPreferredSize(buttonDim);
		keyEditButton.setMinimumSize(buttonDim);
		keyEditButton.setMaximumSize(buttonDim);
		keyEditButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modifyKey();
			}
		});

		keyRemoveButton = new JButton(ImageUtil.getImageIcon("clear"));
		keyRemoveButton.setToolTipText(I18NSupport.getString("languages.keys.remove"));
		keyRemoveButton.setPreferredSize(buttonDim);
		keyRemoveButton.setMinimumSize(buttonDim);
		keyRemoveButton.setMaximumSize(buttonDim);
		keyRemoveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeKeys();
			}
		});

		languagesList = new JXList();
		languagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		languagesList.setCellRenderer(new LanguageCellRenderer());
		languagesList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int index = languagesList.getSelectedIndex();
				if (index >= 0) {
					I18nLanguage language = (I18nLanguage) languagesModel.getElementAt(index);
					model.clear();
					for (I18nString s : language.getI18nStrings()) {
						model.addObject(s);
					}
				}
			}
		});
		languagesModel = new DefaultListModel();
		JScrollPane scroll2 = new JScrollPane();
		scroll2.setPreferredSize(new Dimension(200, 40));
		scroll2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll2.getViewport().add(languagesList, null);
		languagesList.setModel(languagesModel);
		languageAddButton = new JButton(ImageUtil.getImageIcon("add"));
		languageAddButton.setToolTipText(I18NSupport.getString("languages.add"));
		languageAddButton.setPreferredSize(buttonDim);
		languageAddButton.setMinimumSize(buttonDim);
		languageAddButton.setMaximumSize(buttonDim);
		languageAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addLanguage();
			}
		});

		languageEditButton = new JButton(ImageUtil.getImageIcon("edit"));
		languageEditButton.setToolTipText(I18NSupport.getString("languages.edit"));
		languageEditButton.setPreferredSize(buttonDim);
		languageEditButton.setMinimumSize(buttonDim);
		languageEditButton.setMaximumSize(buttonDim);
		languageEditButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modifyLanguage();
			}
		});

		languageRemoveButton = new JButton(ImageUtil.getImageIcon("clear"));
		languageRemoveButton.setToolTipText(I18NSupport.getString("languages.remove"));
		languageRemoveButton.setPreferredSize(buttonDim);
		languageRemoveButton.setMinimumSize(buttonDim);
		languageRemoveButton.setMaximumSize(buttonDim);
		languageRemoveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeLanguages();
			}
		});

		model = new KeysTableModel();
		table = new JXTable(model);
		table.setSortable(false);
		table.setRolloverEnabled(true);
		table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null, Color.RED));
		table.setPreferredScrollableViewportSize(new Dimension(200, 200));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {
					modifyLanguageKey();
				}
			}
		});

		langKeyEditButton = new JButton(ImageUtil.getImageIcon("edit"));
		langKeyEditButton.setToolTipText(I18NSupport.getString("languages.keys.edit"));
		langKeyEditButton.setPreferredSize(buttonDim);
		langKeyEditButton.setMinimumSize(buttonDim);
		langKeyEditButton.setMaximumSize(buttonDim);
		langKeyEditButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modifyLanguageKey();
			}
		});

		this.setLayout(new GridBagLayout());

		JPanel keysPanel = new JPanel();
		keysPanel.setLayout(new GridBagLayout());
		keysPanel.add(new JXTitledSeparator(I18NSupport.getString("languages.keys")), new GridBagConstraints(0, 0, 2, 1, 1.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
		keysPanel.add(scroll, new GridBagConstraints(0, 1, 1, 3, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(5, 0, 0, 5), 0, 0));

		keysPanel.add(keyAddButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(5, 2, 5, 5), 0, 0));
		keysPanel.add(keyEditButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 2, 5, 5), 0, 0));
		keysPanel.add(keyRemoveButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 2, 5, 5), 0, 0));

		JPanel languagesPanel = new JPanel();
		languagesPanel.setLayout(new GridBagLayout());
		languagesPanel.add(new JXTitledSeparator(I18NSupport.getString("languages")), new GridBagConstraints(0, 0, 2, 1, 1.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
		languagesPanel.add(scroll2, new GridBagConstraints(0, 1, 1, 3, 1.0, 1.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(5, 0, 0, 5), 0, 0));

		languagesPanel.add(languageAddButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(5, 2, 5, 5), 0, 0));
		languagesPanel.add(languageEditButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 2, 5, 5), 0, 0));
		languagesPanel.add(languageRemoveButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 2, 5, 5), 0, 0));

		JPanel kPanel = new JPanel();
		kPanel.setLayout(new GridBagLayout());
		JScrollPane scroll3 = new JScrollPane(table);
		scroll3.setPreferredSize(new Dimension(200, 150));
		kPanel.add(scroll3, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(5, 0, 0, 5), 0, 0));
		kPanel.add(langKeyEditButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(5, 2, 5, 5), 0, 0));

		add(keysPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
				0, 0, 0, 0), 0, 0));
		add(languagesPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(5, 0, 0, 0), 0, 0));
		add(kPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
				0, 0, 0), 0, 0));

	}

	private void addKey() {
		KeySelectionPanel panel = new KeySelectionPanel(false);
		KeySelectionDialog dialog = new KeySelectionDialog(panel, false);

		dialog.pack();
		Show.centrateComponent(Globals.getMainFrame(), dialog);
		dialog.setVisible(true);
		if (dialog.okPressed()) {
			List<String> keys = panel.getKeys();
			for (String key : keys) {
				if (!keysModel.contains(key)) {
					addKey(key);
				} else {
					// Show.info(I18NSupport.getString("languages.keys.selection.key.exists"));
				}
			}
			I18nManager.getInstance().addKeys(keys);
		}
	}

	private void modifyKey() {
		if ((keysList.getSelectedValue() == null) || (keysList.getSelectedValues().length > 1)) {
			Show.info(I18NSupport.getString("languages.keys.selection.key.invalid"));
			return;
		}
		KeySelectionPanel panel = new KeySelectionPanel(false);
		panel.setKey((String) keysList.getSelectedValue());
		KeySelectionDialog dialog = new KeySelectionDialog(panel, true);

		dialog.pack();
		Show.centrateComponent(Globals.getMainFrame(), dialog);
		dialog.setVisible(true);
		if (dialog.okPressed()) {
			int index = keysList.getSelectedIndex();
			String key = panel.getKey();
			editKey(index, key);
			I18nManager.getInstance().setKeys(keysModel.toList());
		}
	}

	private void removeKeys() {
		int[] indices = keysList.getSelectedIndices();
		for (int i = indices.length - 1; i >= 0; i--) {
			deleteKey(indices[i]);
		}
		I18nManager.getInstance().setKeys(keysModel.toList());
	}

	private void addLanguage() {
		LanguageSelectionPanel panel = new LanguageSelectionPanel();
		LanguageSelectionDialog dialog = new LanguageSelectionDialog(getLanguages(), panel, false);
		dialog.pack();
		Show.centrateComponent(Globals.getMainFrame(), dialog);
		dialog.setVisible(true);
		if (dialog.okPressed()) {
			I18nLanguage language = panel.getLanguage();
			List<I18nString> strings = new ArrayList<I18nString>();
			for (String key : keysModel.toList()) {
				strings.add(new I18nString(key, ""));
			}
			language.setI18nStrings(strings);
			if (!languagesModel.contains(language)) {
				languagesModel.addElement(language);
				I18nManager.getInstance().addLanguage(language);
			} else {
				Show.info(I18NSupport.getString("languages.keys.selection.key.exists"));
			}
		}
	}

	private void modifyLanguage() {
		if (languagesList.getSelectedValue() == null) {
			Show.info(I18NSupport.getString("languages.invalid"));
			return;
		}
		// LanguageSelectionPanel panel = new LanguageSelectionPanel();
		// panel.setLanguage((I18nLanguage)languagesList.getSelectedValue());
		// LanguageSelectionDialog dialog = new
		// LanguageSelectionDialog(getLanguages(), panel, true);
		//
		// dialog.pack();
		// Show.centrateComponent(Globals.getMainFrame(), dialog);
		// dialog.setVisible(true);
		// if (dialog.okPressed()) {
		// int index = languagesList.getSelectedIndex();
		// I18nLanguage oldLanguage =
		// (I18nLanguage)languagesList.getElementAt(index);
		// I18nLanguage language = panel.getLanguage();
		// language.setI18nStrings(oldLanguage.getI18nStrings());
		// languagesModel.set(index,language);
		// }
		int index = languagesList.getSelectedIndex();
		for (int i = 0, size = languagesModel.size(); i < size; i++) {
			I18nLanguage language = (I18nLanguage) languagesModel.get(i);
			if (i == index) {
				language.setDefault(true);
				I18nManager.getInstance().setCurrentLanguage(language);
			} else {
				language.setDefault(false);
			}
			languagesModel.set(i, language);
		}
	}

	private void removeLanguages() {
		int[] indices = languagesList.getSelectedIndices();
		System.out.println("-----  " +indices.length  + "  " + indices[0]);
		for (int i = indices.length - 1; i >= 0; i--) {
			System.out.println("  remove index = " + indices[i]);
			languagesModel.removeElementAt(indices[i]);
		}
	}

	private void modifyLanguageKey() {
		KeySelectionPanel panel = new KeySelectionPanel(true);
		int[] selectedRows = table.getSelectedRows();
		if (selectedRows.length == 0) {
			Show.info(I18NSupport.getString("languages.keys.selection.key.invalid"));
			return;
		}
		int stringIndex = selectedRows[0];

		I18nString oldString = (I18nString) model.getObjectForRow(stringIndex);
		panel.setKey(oldString.getKey());
		panel.setValue(oldString.getValue());
		KeySelectionDialog dialog = new KeySelectionDialog(panel, true);

		dialog.pack();
		Show.centrateComponent(Globals.getMainFrame(), dialog);
		dialog.setVisible(true);
		if (dialog.okPressed()) {
			int languageIndex = languagesList.getSelectedIndex();
			String value = panel.getValue();
			I18nString newString = new I18nString(oldString.getKey(), value);
			I18nManager.getInstance().getLanguages().get(languageIndex).getI18nStrings().set(stringIndex, newString);
			model.updateObject(stringIndex, newString);
		}
	}

	public List<String> getKeys() {
		List<String> result = keysModel.toList();
		// Collections.sort(result, new Comparator<String>() {
		//
		// @Override
		// public int compare(String o1, String o2) {
		// return Collator.getInstance().compare(o1, o2);
		// }
		// });
		return result;
	}

	public List<I18nLanguage> getLanguages() {
		List<I18nLanguage> result = new ArrayList<I18nLanguage>();
		for (Object obj : languagesModel.toArray()) {
			result.add((I18nLanguage) obj);
		}
		return result;
	}

	public void setKeys(List<String> keys) {
		I18nManager.getInstance().setKeys(keys);
		keysModel.clear();
		for (String s : keys) {
			keysModel.add(s);
		}

	}

	public void setLanguages(List<I18nLanguage> languages) {
		I18nManager.getInstance().setLanguages(languages);
		languagesModel.clear();
		for (I18nLanguage language : languages) {			
			languagesModel.addElement(language);
		}
		languagesList.setSelectedIndex(0);
	}

	class LanguageCellRenderer extends JLabel implements ListCellRenderer {

		public LanguageCellRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			I18nLanguage language = (I18nLanguage) value;
			StringBuilder sb = new StringBuilder(language.getName());
			if (language.isDefault()) {
				sb.append(" (").append(I18NSupport.getString("languages.default")).append(")");
			}
			setText(sb.toString());

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			return this;
		}
	}

	public class KeysTableModel extends AbstractTableModel {

		private final String[] columnNames = { I18NSupport.getString("languages.keys.selection.key"),
				I18NSupport.getString("languages.keys.selection.value") };

		private SortedSet elements = new TreeSet();

		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			// this method is called in the constructor so we must test for null
			if (elements == null) {
				return 0;
			}

			return elements.size();
		}

		@SuppressWarnings("unchecked")
		public void addObject(Object object) {
			elements.add(object);
			fireTableDataChanged();
		}

		public boolean containsObject(Object object) {
			return (elements.contains(object));
		}

		@SuppressWarnings("unchecked")
		public void addObjects(List objects) {
			elements.addAll(objects);
			fireTableDataChanged();
		}

		public void deleteObject(int rowIndex) {
			elements.remove(getObjectForRow(rowIndex));
			fireTableDataChanged();
		}

		@SuppressWarnings("unchecked")
		public void deleteObjects(List objects) {
			elements.removeAll(objects);
			fireTableDataChanged();
		}

		public void clear() {
			elements.clear();
			fireTableDataChanged();
		}

		public Object getObjectForRow(int rowIndex) {						
			return	elements.toArray()[rowIndex];			
		}
		
		public Object set(int index, Object element) {
			Object oldElement = getObjectForRow(index);
			elements.remove(oldElement);
			addObject(element);
			return oldElement;
		}

		@SuppressWarnings("unchecked")
		public void updateObject(int row, Object object) {
			row = table.convertRowIndexToModel(row);
			set(row, object);
			fireTableDataChanged();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			I18nString row = (I18nString)getObjectForRow(rowIndex);
			switch (columnIndex) {
			case 0:
				return row.getKey();
			case 1:
				return row.getValue();
			default:
				return null;
			}
		}

	}

	public void addKey(String key) {
		keysModel.add(key);
		for (I18nLanguage language : I18nManager.getInstance().getLanguages()) {
			boolean found = false;
			for (I18nString s : language.getI18nStrings()) {
				if (s.getKey().equals(key)) {
					found = true;
					break;
				}
			}
			if (!found) {
				I18nString s = new I18nString(key, "");
				language.getI18nStrings().add(s);
				if (!model.containsObject(s)) {
					model.addObject(s);
				}
			}
		}
	}

	public void editKey(int index, String key) {
		String oldKey = (String) keysModel.set(index, key);
		for (I18nLanguage language : I18nManager.getInstance().getLanguages()) {
			List<I18nString> strings = language.getI18nStrings();
			for (int i = 0, size = strings.size(); i < size; i++) {
				I18nString s = (I18nString) strings.get(i);
				if (s.getKey().equals(oldKey)) {
					String value = s.getValue();
					s = new I18nString(key, value);
					strings.set(i, s);
					model.updateObject(index, s);
				}
			}
		}
	}

	public void deleteKey(int index) {
		String key = (String) keysModel.getElementAt(index);
		keysModel.removeElementAt(index);
		for (I18nLanguage language : I18nManager.getInstance().getLanguages()) {
			for (Iterator it = language.getI18nStrings().iterator(); it.hasNext();) {
				I18nString s = (I18nString) it.next();
				if (s.getKey().equals(key)) {
					it.remove();
					if (model.getRowCount() > index) {
						model.deleteObject(index);
					}
					break;
				}
			}
		}
	}

	private class SortedListModel<T> extends AbstractListModel {
		private SortedSet<T> model;

		public SortedListModel() {
			model = new TreeSet<T>();
		}

		public int getSize() {
			return model.size();
		}

		@SuppressWarnings("unchecked")
		public T getElementAt(int index) {
			return (T) model.toArray()[index];
		}

		public void add(T element) {
			if (model.add(element)) {
				fireContentsChanged(this, 0, getSize());
			}
		}

		public void addAll(T[] elements) {
			Collection<T> c = Arrays.asList(elements);
			model.addAll(c);
			fireContentsChanged(this, 0, getSize());
		}

		public void clear() {
			model.clear();
			fireContentsChanged(this, 0, getSize());
		}

		public boolean contains(T element) {
			return model.contains(element);
		}

		public T firstElement() {
			return model.first();
		}

		public Iterator<T> iterator() {
			return model.iterator();
		}

		public T lastElement() {
			return model.last();
		}

		public boolean removeElement(T element) {
			boolean removed = model.remove(element);
			if (removed) {
				fireContentsChanged(this, 0, getSize());
			}
			return removed;
		}

		public boolean removeElementAt(int index) {
			T element = getElementAt(index);
			boolean removed = model.remove(element);
			if (removed) {
				fireContentsChanged(this, 0, getSize());
			}
			return removed;
		}

		public T set(int index, T element) {
			T oldElement = getElementAt(index);
			removeElement(oldElement);
			add(element);
			return oldElement;
		}

		public List<T> toList() {
			return new LinkedList<T>(model);
		}
	}

}
