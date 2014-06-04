/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.designer.querybuilder;

import ro.nextreports.engine.querybuilder.IdNameRenderer;
import ro.nextreports.engine.util.comparator.IdNameComparator;
import ro.nextreports.engine.util.ParameterUtil;
import ro.nextreports.engine.util.StringUtil;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.exception.ConnectionException;
import ro.nextreports.designer.dbviewer.common.InvalidSqlException;
import ro.nextreports.designer.dbviewer.common.NextSqlException;
import ro.nextreports.designer.i18n.action.I18nManager;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.JDateTimePicker;
import ro.nextreports.designer.util.ListAddPanel;
import ro.nextreports.designer.util.ListSelectionPanel;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;

import ro.nextreports.engine.queryexec.IdName;
import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.queryexec.QueryExecutor;
import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.EngineProperties;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;
import java.io.Serializable;
import java.sql.Connection;

/**
 * @author Decebal Suiu
 */
public class RuntimeParametersPanel extends JPanel {

    private List<QueryParameter> paramList;
    private List<JComponent> components;
    private List<JCheckBox> checks;

    private Dimension scrDim = new Dimension(450, 400);
    private Dimension listDim = new Dimension(200, 60);
    private Dimension scrListDim = new Dimension(130, 120);

    private static Map<String, Object> parametersValues;
    private static Map<String, Boolean> parametersIgnore;        

    private static final Log LOG = LogFactory.getLog(RuntimeParametersPanel.class);
    private Connection con;

    private boolean error = false;

    public RuntimeParametersPanel(Map<String, QueryParameter> params) {
        this(params, null);
    }

    public RuntimeParametersPanel(Map<String, QueryParameter> params, DataSource runDS) {
        super();
        con = Globals.getConnection();
        if (runDS != null) {
            try {
                con = Globals.createTempConnection(runDS);
            } catch (ConnectionException e) {
                e.printStackTrace();
                Show.error(e);
                return;
            }
        }
               
        paramList = new ArrayList<QueryParameter>(params.values());        
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        components = new ArrayList<JComponent>();
        checks = new ArrayList<JCheckBox>();

        int size = paramList.size();        
        boolean shouldExpand = false;
        boolean needScroll = false;
        for (int i = 0; i < size; i++) {
        	final int pos = i;
            final QueryParameter param = paramList.get(i);            
            if (param.isHidden()) {
            	components.add(null);
            	checks.add(null);              	
            	initHiddenParameterValues(param);   
            	continue;
            }
            String source = param.getSource();
            String defaultSource = param.getDefaultSource();

            if ((defaultSource != null) && !defaultSource.trim().equals("")) {
                try {
                    param.setDefaultSourceValues(Globals.getDBViewer().getDefaultSourceValues(con, param));
                } catch (NextSqlException e) {
                    Show.error(e);
                }
            }

            final JComponent component;
            int anchor = GridBagConstraints.WEST;
            double y = 0.0;
            int expand = GridBagConstraints.HORIZONTAL;
            if ((source != null) && !source.equals("")) {
                List<IdName> values = new ArrayList<IdName>();
                try {
                    if (param.isManualSource()) {
                        if (!param.isDependent()) {
                            values = Globals.getDBViewer().getValues(con, source, true, param.getOrderBy());
                        }
                    } else {
                        int index = source.indexOf(".");
                        int index2 = source.lastIndexOf(".");
                        String tableName = source.substring(0, index);
                        String columnName;
                        String shownColumnName = null;
                        if (index == index2) {
                            columnName = source.substring(index + 1);
                        } else {
                            columnName = source.substring(index + 1, index2);
                            shownColumnName = source.substring(index2 + 1);
                        }                        
                        values = Globals.getDBViewer().getColumnValues(con, param.getSchema(), tableName, columnName, shownColumnName, param.getOrderBy());
                    }
                } catch (NextSqlException e) {
                    error = true;
                    Show.error(e);
                } catch (InvalidSqlException e) {
                    String m = I18NSupport.getString("source.dialog.valid");
                    Show.info(m + " : \"select <exp1> , <exp2> from ...\"");
                }                               
                if (param.getSelection().equals(ParameterEditPanel.SINGLE_SELECTION)) {
                    component = new JComboBox();
                    final JComboBox combo = (JComboBox) component;
                    combo.setRenderer(new IdNameRenderer());
                    combo.addItem("-- " + I18NSupport.getString("parameter.value.select") + " --");
                    for (int j = 0, len = values.size(); j < len; j++) {
                        combo.addItem(values.get(j));
                    }                    

                    combo.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                IdName in = null;
                                if (combo.getSelectedIndex() > 0) {
                                    in = (IdName) combo.getSelectedItem();
                                }
                                parameterSelection(pos, in);
                            }
                        }
                    });
                    AutoCompleteDecorator.decorate(combo);
                    needScroll = false;
                } else {                	
                    anchor = GridBagConstraints.NORTHWEST;
                    y = 1.0;
                    expand = GridBagConstraints.BOTH;

                    DefaultListModel model = new DefaultListModel();
                    for (int j = 0, len = values.size(); j < len; j++) {
                        model.addElement(values.get(j));
                    }
                    List srcList = Arrays.asList(model.toArray());                    
                    component = new ListSelectionPanel(srcList, new ArrayList(), "", "", true, false) {
                        protected void onAdd() {
                            selection();
                        }

                        protected void onRemove() {
                            selection();
                        }

                        // needed for saved parameters on rerun
                        protected void onSetRight() {
                            selection();
                        }

                        private void selection() {
                            if (ParameterManager.getInstance().getChildDependentParameters(param).size() > 0) {
                                Object[] values = getDestinationElements().toArray();
                                if (values.length == 0) {
                                	values = new Object[]{ParameterUtil.NULL};                                	
                                }
                                parameterSelection(pos, values);
                            }
                        }
                    };
                    ((ListSelectionPanel) component).setListSize(scrListDim);
                    ((ListSelectionPanel) component).setRenderer(new IdNameRenderer(), new IdNameComparator(param.getOrderBy()));

                    shouldExpand = true;
                }

            } else {            	
                if (param.getSelection().equals(QueryParameter.MULTIPLE_SELECTION)) {
                    anchor = GridBagConstraints.NORTHWEST;
                    y = 1.0;
                    expand = GridBagConstraints.BOTH;                    ;
                    component = new ListAddPanel(param) {
                    	protected void onAdd() {
                            selection();
                        }

                        protected void onRemove() {
                            selection();
                        }
                        
                        private void selection() {
                            if (ParameterManager.getInstance().getChildDependentParameters(param).size() > 0) {
                                Object[] values = getElements().toArray();
                                parameterSelection(pos, values);
                            }
                        }
                    };                    
                } else {
                    needScroll = false;
                    if (param.getValueClassName().equals("java.util.Date")) {
                    	 component = new JXDatePicker();                    	                     	                    	 
                    	 ((JXDatePicker)component).addPropertyChangeListener(new PropertyChangeListener() {
                    	     public void propertyChange(PropertyChangeEvent e) {
                    	         if ("date".equals(e.getPropertyName())) {
                    	        	 parameterSelection(pos, ((JXDatePicker)component).getDate());
                    	         }
                    	     }
                    	 });
                         // hack to fix bug with big popup button
                         JButton popupButton = (JButton) component.getComponent(1);
                         //popupButton.setMargin(new Insets(2, 2, 2, 2));
                         popupButton.setMinimumSize(new Dimension(20, (int) getPreferredSize().getHeight()));
                         popupButton.setPreferredSize(new Dimension(20, (int) getPreferredSize().getHeight()));
                         popupButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));                    	
                    } else if(param.getValueClassName().equals("java.sql.Timestamp") ||
                            param.getValueClassName().equals("java.sql.Time")) {
                        component = new JDateTimePicker() {
                        	protected void onChange() {
                        		parameterSelection(pos,getDate());
                        	}
                        };
                        // hack to fix bug with big popup button
                        JButton popupButton = (JButton) (((JDateTimePicker) component).getDatePicker()).getComponent(1);
                        //popupButton.setMargin(new Insets(2, 2, 2, 2));
                        popupButton.setMinimumSize(new Dimension(20, (int) getPreferredSize().getHeight()));
                        popupButton.setPreferredSize(new Dimension(20, (int) getPreferredSize().getHeight()));
                        popupButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    } else if (param.getValueClassName().equals("java.lang.Boolean")) {
                        component = new JCheckBox();
                        ((JCheckBox)component).addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent actionEvent) {
                                AbstractButton abstractButton = (AbstractButton)actionEvent.getSource();
                                boolean selected = abstractButton.getModel().isSelected();
                                parameterSelection(pos,selected);
                              }
                        });
                    } else {
                        component = new JTextField(25);
                        ((JTextField)component).getDocument().addDocumentListener(new DocumentListener() {
                        	
                        	@Override
                            public void changedUpdate(DocumentEvent e) {
                                updateFromTextField(e);
                            }

                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                updateFromTextField(e);
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                updateFromTextField(e);
                            }

                            private void updateFromTextField(DocumentEvent e) {
                                java.awt.EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                    	Object value = null;								
        								try {
        									if ("".equals(((JTextField)component).getText().trim())) {
        										value = null;
        									} else {	
        										value = ParameterUtil.getParameterValueFromString(param.getValueClassName(),((JTextField)component).getText());			                    											
        									}
        									parameterSelection(pos, value);
        								} catch (Exception e) {
        									e.printStackTrace();
        									LOG.error(e.getMessage(), e);
        								}			
                                    }
                                });
                            }
                        });
                    }
                }
            }
            components.add(component);
            final JCheckBox cb = new JCheckBox(I18NSupport.getString("run.parameter.ignore"));
            checks.add(cb);


            final JLabel label = new JLabel(getRuntimeParameterName(param));
            panel.add(label, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, anchor,
                    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            final JComponent addComponent;
            if (needScroll) {
                JScrollPane scr = new JScrollPane(component, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scr.setPreferredSize(listDim);
                addComponent = scr;
            } else {
                addComponent = component;
            }
            panel.add(addComponent, new GridBagConstraints(1, i, 1, 1, 1.0, y, GridBagConstraints.WEST,
                    expand, new Insets(5, 0, 5, 5), 0, 0));
            int checkAnchor = GridBagConstraints.WEST;
            if ((addComponent instanceof JScrollPane) || (addComponent instanceof ListSelectionPanel)) {
                checkAnchor = GridBagConstraints.NORTHWEST;
            }

            if (Globals.getParametersIgnore()) {
                panel.add(cb, new GridBagConstraints(2, i, 1, 1, 0.0, 0.0, checkAnchor,
                        GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
            }

            cb.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (cb.isSelected()) {
                        if (addComponent instanceof JScrollPane) {
                            component.setEnabled(false);
                        }
                        label.setEnabled(false);
                        addComponent.setEnabled(false);
                        param.setIgnore(true);
                    } else {
                        if (addComponent instanceof JScrollPane) {
                            component.setEnabled(true);
                        }
                        label.setEnabled(true);
                        addComponent.setEnabled(true);
                        param.setIgnore(false);
                    }
                }
            });
        }
        
        // populate hidden dependent parameters (this will be done if a parameter depends only on a single hidden parameter)
        // if a parameter depends on a hidden parameter and other parameters, it cannot be populated here
        for (int i = 0; i < size; i++) {
            final QueryParameter param = paramList.get(i);
            if (param.isHidden()) {
            	populateDependentParameters(param, false);
            }
        }    

        if (!shouldExpand) {
            panel.add(new JLabel(), new GridBagConstraints(0, size, 3, 1, 1.0, 1.0, GridBagConstraints.WEST,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }

        JScrollPane scrPanel = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrPanel.setPreferredSize(scrDim);
        scrPanel.setMinimumSize(scrDim);

        add(scrPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        setParameterValues(parametersValues);
        setParameterIgnore(parametersIgnore);
    }

    private String getRuntimeParameterName(QueryParameter param) {
        String s = param.getRuntimeName();
        if ((s == null) || s.trim().equals("")) {
            s = param.getName();
        } else {                    	
            s = StringUtil.getI18nString(s, I18nManager.getInstance().getCurrentLanguage());                    
        }
        if (param.isMandatory()) {
            s = "* " + s;
        }
        return s;
    }


    public Map<String, Object> getParametersValues() throws RuntimeParameterException {
        parametersValues = new HashMap<String, Object>();
        parametersIgnore = new HashMap<String, Boolean>();
        boolean exception = false;
        String exceptionText = "";

        for (int i = 0; i < paramList.size(); i++) {

            QueryParameter qp = paramList.get(i);
            String paramName = qp.getName();
            if (qp.isHidden()) {            	
            	initHiddenParameterValues(qp);    	
                continue;
            }            
            Object value = getParameterValue(qp);
            
            parametersValues.put(paramName, value);           
            parametersIgnore.put(paramName, checks.get(i).isSelected());
        }

        if (exception) {
            throw new RuntimeParameterException(exceptionText);
        }
        return parametersValues;
    }

    public Object getParameterValue(QueryParameter qp) throws RuntimeParameterException {

        String paramName = qp.getName();
        String runtimeParamName = qp.getRuntimeName();
        if ((runtimeParamName == null) || runtimeParamName.trim().equals("")) {
            runtimeParamName = paramName;
        }
        JComponent component = getComponent(qp);
        String type = qp.getValueClassName();

        Object value = null;

        if (!qp.isIgnore()) {
            if (component instanceof JTextField) {
                value = ((JTextField) component).getText();
                if (value.equals("")) {
                    if (qp.isMandatory()) {
                        throw new RuntimeParameterException(I18NSupport.getString("runtime.parameters.notentered", runtimeParamName));
                    } else {
                        value = null;
                    }
                }
            } else if (component instanceof JComboBox) {
                JComboBox combo = (JComboBox) component;
                if (combo.getSelectedIndex() == 0) {
                    if (qp.isMandatory()) {
                        throw new RuntimeParameterException(I18NSupport.getString("runtime.parameters.notselected", runtimeParamName));
                    } else {
                        value = null;
                    }
                } else {
                    value = combo.getSelectedItem();
                }
            } else if (component instanceof ListSelectionPanel) {
                value = ((ListSelectionPanel) component).getDestinationElements().toArray();
                if (((Object[]) value).length == 0) {
                    if (qp.isMandatory()) {
                        throw new RuntimeParameterException(I18NSupport.getString("runtime.parameters.notselected", runtimeParamName));
                    } else {
                        value = new Object[]{ParameterUtil.NULL};
                    }
                }
            } else if (component instanceof ListAddPanel) {
                value = ((ListAddPanel) component).getElements().toArray();
                if (((Object[]) value).length == 0) {
                    if (qp.isMandatory()) {
                        throw new RuntimeParameterException(I18NSupport.getString("runtime.parameters.notselected", runtimeParamName));
                    } else {
                        value = new Object[]{ParameterUtil.NULL};
                    }
                }
            } else if (component instanceof JDateTimePicker) {
                value = ((JDateTimePicker) component).getDate();
                if (value == null) {
                    if (qp.isMandatory()) {
                        throw new RuntimeParameterException(I18NSupport.getString("runtime.parameters.notentered", runtimeParamName));
                    }
                }
            } else if (component instanceof JXDatePicker) {
                value = ((JXDatePicker) component).getDate();
                if (value == null) {
                    if (qp.isMandatory()) {
                        throw new RuntimeParameterException(I18NSupport.getString("runtime.parameters.notentered", runtimeParamName));
                    }
                }
            } else if (component instanceof JCheckBox) {
                value = ((JCheckBox) component).isSelected();
            }

            if (value != null) {               
                if (value.getClass().getName().equals("java.lang.String")) {                        
                    try {
                    	value = ParameterUtil.getParameterValueFromString(qp.getValueClassName(),(String)value );
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
						// the exception is thrown outside the for statement so that
		                // all parameter values are saved
		                throw new RuntimeParameterException("Invalid parameter value " + value +
		                            " for parameter " + runtimeParamName + " of type " + type + " .");
				    }                        
                }               
            }
        }
        return value;
    }
     
    private void setParameterIgnore(Map<String, Boolean> paramIgnore) {

        if (paramIgnore == null) {
            return;
        }

        for (int i = 0; i < paramList.size(); i++) {
            QueryParameter qp = paramList.get(i);
            if (qp.isHidden()) {
                continue;
            }
            String paramName = qp.getName();
            JCheckBox cb = checks.get(i);

            Boolean value = paramIgnore.get(paramName);
            if (value == null) {
                cb.setSelected(false);
                continue;
            }

            if (value) {
                cb.setSelected(true);
            } else {
                cb.setSelected(false);
            }
        }

    }

    @SuppressWarnings("unchecked")
	private void setParameterValues(Map<String, Object> paramValues) {
        if (paramValues == null) {
            return;
        }
        for (int i = 0; i < paramList.size(); i++) {
            QueryParameter qp = paramList.get(i);            
            if (qp.isHidden()) {
            	// component is null
            	populateDependentParameters(qp, false);
                continue;
            }
            ArrayList<Serializable> defaultValues = qp.getDefaultValues();
            if ((qp.getDefaultSourceValues() != null) && (qp.getDefaultSourceValues().size() > 0)) {
                defaultValues = qp.getDefaultSourceValues();
            }
            String paramName = qp.getName();
            JComponent component = components.get(i);

            Object value = paramValues.get(paramName);
            if ((value == null) && ((defaultValues == null) || (defaultValues.size() == 0))) {
                continue;
            }
            if (component instanceof JTextField) {
                if (value != null) {
                    ((JTextField) component).setText(value.toString());
                } else if ((defaultValues != null) && (defaultValues.size() > 0)) {
                    ((JTextField) component).setText(defaultValues.get(0).toString());
                }
            } else if (component instanceof JComboBox) {
                if (value != null) {
                    ((JComboBox) component).setSelectedItem(value);
                } else if ((defaultValues != null) && (defaultValues.size() > 0)) {
                    Serializable id = defaultValues.get(0);
                    if (id instanceof IdName) {
                        id = ((IdName) id).getId();
                    }
                    IdName in = findIdName(((JComboBox) component).getModel(), id);
                    ((JComboBox) component).setSelectedItem(in);
                }

            } else if (component instanceof ListSelectionPanel) {
                Object[] selected = new Object[0];

                ListSelectionPanel lsp = (ListSelectionPanel) component;
                List srcList = lsp.getSourceElements();
                if (value != null) {
                    selected = (Object[]) value;
                } else if ((defaultValues != null) && (defaultValues.size() > 0)) {
                    selected = new Object[defaultValues.size()];
                    for (int k = 0, len = selected.length; k < len; k++) {
                        Serializable id = defaultValues.get(k);
                        if (id instanceof IdName) {
                            id = ((IdName) id).getId();
                        }
                        IdName in = findIdName(srcList, id);
                        selected[k] = in;
                    }                    
                }
                List dstList = Arrays.asList(selected);
                srcList.removeAll(dstList);
                if ((dstList.size() == 1) && ParameterUtil.NULL.equals(dstList.get(0))) {
                    dstList = new ArrayList();
                }
                lsp.setLists(srcList, dstList);
            } else if (component instanceof ListAddPanel) {
                Object[] selected = new Object[0];

                ListAddPanel lsp = (ListAddPanel) component;
                if (value != null) {
                    selected = (Object[]) value;
                } else if ((defaultValues != null) && (defaultValues.size() > 0)) {
                    selected = new Object[defaultValues.size()];
                    for (int k = 0, len = selected.length; k < len; k++) {
                        Serializable id = defaultValues.get(k);
                        if (id instanceof IdName) {
                            id = ((IdName) id).getId();
                        }
                        selected[k] = id;
                    }
                }
                List dstList = Arrays.asList(selected);
                if ((dstList.size() == 1) && ParameterUtil.NULL.equals(dstList.get(0))) {
                    dstList = new ArrayList();
                }
                lsp.setElements(dstList);
            } else if (component instanceof JDateTimePicker) {
                if (value != null) {
                    ((JDateTimePicker) component).setDate((Date) value);
                } else if ((defaultValues != null) && (defaultValues.size() > 0)) {
                    ((JDateTimePicker) component).setDate((Date) defaultValues.get(0));
                }
            } else if (component instanceof JXDatePicker) {
                if (value != null) {
                    ((JXDatePicker) component).setDate((Date) value);
                } else if ((defaultValues != null) && (defaultValues.size() > 0)) {
                    ((JXDatePicker) component).setDate((Date) defaultValues.get(0));
                }
            } else if (component instanceof JCheckBox) {
                if (value != null) {
                    ((JCheckBox) component).setSelected((Boolean) value);
                } else if ((defaultValues != null) && (defaultValues.size() > 0)) {
                    ((JCheckBox) component).setSelected((Boolean) defaultValues.get(0));
                }
            }

            populateDependentParameters(qp, true);
        }

    }

    private IdName findIdName(List list, Serializable id) {
        for (int i = 0, size = list.size(); i < size; i++) {
            IdName in = (IdName) list.get(i);
            if (in.getId() == null) {
            	LOG.error("A value from select used in default values is null! and it is ignored");
            } else {
            	if (in.getId().equals(id)) {
            		return in;
            	}
            }
        }
        return null;
    }

    private IdName findIdName(ComboBoxModel model, Serializable id) {
        // first element in combo model is a --select--        
        for (int i = 1, size = model.getSize(); i < size; i++) {
            IdName in = (IdName) model.getElementAt(i);    
            if (in.getId() == null) {
            	LOG.error("A value from select used in default values is null! and it is ignored");
            } else {
            	if (in.getId().equals(id)) {
            		return in;
            	}
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void initParameterValue(JComponent component, Object value, String paramName,
                                    List<Serializable> defaultValues) {    	
        if (value == null) {
            return;
        }        
        if (component instanceof JTextField) {
            ((JTextField) component).setText(value.toString());
        } else if (component instanceof JComboBox) {
            JComboBox combo = ((JComboBox) component);
            List<IdName> values = (List<IdName>) value;
            combo.removeAllItems();
            combo.addItem("-- " + I18NSupport.getString("parameter.value.select") + " --");
            for (int j = 0, len = values.size(); j < len; j++) {
                combo.addItem(values.get(j));
            }
            Object old = parametersValues.get(paramName);
            if (old != null) {
                combo.setSelectedItem(old);
            } else if ((defaultValues != null) && (defaultValues.size() > 0)) {
                Serializable id = defaultValues.get(0);
                if (id instanceof IdName) {
                    id = ((IdName) id).getId();
                }
                combo.setSelectedItem(findIdName(combo.getModel(), id));
            }
        } else if (component instanceof ListSelectionPanel) {        	
            ListSelectionPanel lsp = (ListSelectionPanel) component;
            DefaultListModel model = new DefaultListModel();
            if (value != null) {
                List<IdName> values = (List<IdName>) value;                
                for (int j = 0, len = values.size(); j < len; j++) {
                    model.addElement(values.get(j));
                }
            }
            ArrayList srcList = new ArrayList(Arrays.asList(model.toArray()));

           
            Object old = parametersValues.get(paramName);
            Object[] selected = new Object[0];
			if (old != null) {
				selected = (Object[]) old;				
			} else if ((defaultValues != null) && (defaultValues.size() > 0)) {
				selected = new Object[defaultValues.size()];
				for (int k = 0, len = selected.length; k < len; k++) {
					Serializable id = defaultValues.get(k);
					if (id instanceof IdName) {
						id = ((IdName) id).getId();
					}
					IdName in = findIdName(srcList, id);
					selected[k] = in;
				}
			}	
            List dstList = Arrays.asList(selected);
            
            if (!srcList.containsAll(dstList)) {
                dstList = new ArrayList();                
                parametersValues.put(paramName, null);
            } else {
                srcList.removeAll(dstList);
            }
            if ((dstList.size() == 1) && ParameterUtil.NULL.equals(dstList.get(0))) {
                dstList = new ArrayList();
            }                        	
            lsp.setLists(srcList, dstList);            	

        } else if (component instanceof ListAddPanel) {
            ListAddPanel lap = (ListAddPanel)component;
            DefaultListModel model = new DefaultListModel();
            if (value != null) {
                List<Object> values = (List<Object>) value;
                for (int j = 0, len = values.size(); j < len; j++) {
                    model.addElement(values.get(j));
                }
            }
            ArrayList srcList = new ArrayList(Arrays.asList(model.toArray()));

            Object old = parametersValues.get(paramName);
            Object[] selected = new Object[0];
            if (old != null) {
                selected = (Object[]) old;
            } else if ((defaultValues != null) && (defaultValues.size() > 0)) {
                selected = new Object[defaultValues.size()];
                for (int k = 0, len = selected.length; k < len; k++) {
                    Serializable id = defaultValues.get(k);
                    if (id instanceof IdName) {
                        id = ((IdName) id).getId();
                    }
                    selected[k] = id;
                }
            }
            List dstList = Arrays.asList(selected);
            if (!srcList.containsAll(dstList)) {
                dstList = new ArrayList();
                parametersValues.put(paramName, null);
            } else {
                srcList.removeAll(dstList);
            }
            if ((dstList.size() == 1) && ParameterUtil.NULL.equals(dstList.get(0))) {
                dstList = new ArrayList();
            }

            lap.setElements(dstList);
        } else if (component instanceof JDateTimePicker) {
            ((JDateTimePicker) component).setDate((Date) value);
        } else if (component instanceof JXDatePicker) {
            ((JXDatePicker) component).setDate((Date) value);
        } else if (component instanceof JCheckBox) {
            ((JCheckBox) component).setSelected((Boolean) value);
        }        
    }

    // called when a parameter is added, removed or modified
    public static void resetParametersValues() {
        parametersValues = new HashMap<String, Object>();
        parametersIgnore = new HashMap<String, Boolean>();
    }

    private void parameterSelection(final int pos, final Object obj) {
    	    	    	
        QueryParameter param = paramList.get(pos);
        parametersValues.put(param.getName(), obj);        
        final Map<String, QueryParameter> dependents = ParameterManager.getInstance().getChildDependentParameters(param);
        if (dependents.size() > 0) {

            Thread executorThread = new Thread(new Runnable() {

                public void run() {
                    UIActivator activator = new UIActivator((JDialog) SwingUtilities.getWindowAncestor(RuntimeParametersPanel.this),
                            I18NSupport.getString("run.load.dependent.parameters"));
                    activator.start();

                    try {
                        for (final QueryParameter qp : dependents.values()) {
                            Map<String, QueryParameter> map = ParameterManager.getInstance().getParentDependentParameters(qp);
                            Map<String, Object> vals = new HashMap<String, Object>();
                            boolean selected = true;
                            for (QueryParameter p : map.values()) {
                                Object v = parametersValues.get(p.getName());
                                if (((obj instanceof Object[]) && (((Object[]) obj).length == 0)) || (v == null)) {
                                    selected = false;
                                }
                                vals.put(p.getName(), v);
                            }

                            final List<IdName> values = new ArrayList<IdName>();
                            // all parent parameters selected
                            if (selected) {
                                Query query = new Query(qp.getSource());
                                // no count and no check for other parameters completition
                                QueryExecutor executor = new QueryExecutor(query, map, vals, con, false, false, false);
                                executor.setTimeout(Globals.getQueryTimeout());
                                executor.setMaxRows(0);
                                QueryResult qr = executor.execute();
                                //int count = qr.getRowCount();
                                int columnCount = qr.getColumnCount();

                                // two columns in manual select source!!!
                                //for (int i = 0; i < count; i++) {
                                while (qr.hasNext()) {
                                    IdName in = new IdName();
                                    in.setId((Serializable) qr.nextValue(0));
                                    if (columnCount == 1) {
                                        in.setName((Serializable) qr.nextValue(0));
                                    } else {
                                        in.setName((Serializable) qr.nextValue(1));
                                    }
                                    values.add(in);
                                }
                                Collections.sort(values, new IdNameComparator(qp.getOrderBy()));
                                qr.close();
                            }
                            SwingUtilities.invokeAndWait(new Runnable() {
                                public void run() {
                                    ArrayList<Serializable> defaultValues = qp.getDefaultValues();
                                    if ((qp.getDefaultSourceValues() != null) && (qp.getDefaultSourceValues().size() > 1)) {
                                        defaultValues = qp.getDefaultSourceValues();
                                    }
                                    initParameterValue(getComponent(qp), values, qp.getName(), defaultValues);                                                                       
                                }
                            });

                        }


                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                        ex.printStackTrace();
                    } finally {
                        if (activator != null) {
                            activator.stop();
                        }
                    }

                }
            }, "NEXT : " + getClass().getSimpleName());
            executorThread.setPriority(EngineProperties.getRunPriority());
            executorThread.start();           
        }


    }

    private JComponent getComponent(QueryParameter qp) {
        for (int i = 0, size = paramList.size(); i < size; i++) {
            QueryParameter q = paramList.get(i);
            if (q.equals(qp)) {
                return components.get(i);
            }
        }
        return null;
    }

    public boolean isError() {
        return error;
    }

    // some parameters may depend on other parameters
    // when we select the values for a parameter we must take all children dependent parameters
    // and set their values too
    private void populateDependentParameters(QueryParameter parameter, boolean fromDefaults) {
        Map<String, QueryParameter> childParams = ParameterUtil.getChildDependentParameters(paramList, parameter);

        // update model parameter values for every child parameter
        for (QueryParameter childParam : childParams.values()) {
            if (!paramList.contains(childParam)) {
                continue;
            }

            JComponent childComponent = getComponent(childParam);

            List<IdName> values = new ArrayList<IdName>();

            // a parameter may depend on more than one parameter (has more parents)
            // we must see if all the parents have selected values 
            boolean allParentsHaveValues = true;
            Map<String, Serializable> allParameterValues = new HashMap<String, Serializable>();
            Map<String, QueryParameter> allParentParams = ParameterUtil.getParentDependentParameters(paramList, childParam);            
            for (QueryParameter parentParam : allParentParams.values()) {                	
                if (allParameterValues.get(parentParam.getName()) == null) {
                    allParentsHaveValues = false;
                    break;
                }
            }                        

            if ((childParam.getSource() != null) && (childParam.getSource().trim().length() > 0)  && allParentsHaveValues) {
                try {                    
                    for (String name : allParentParams.keySet()) {
                        QueryParameter parent = allParentParams.get(name);
                        if (fromDefaults) {
                        	// in designer we populate hidden dependent parameters with default values so that we have
                        	// all children parameters with some values
                        	if (parent.isHidden()) {                        		
                        		ParameterUtil.initDefaultSParameterValues(con, parent, allParameterValues);                        		
                        	}
                        } else {
                        	allParameterValues.put(name, (Serializable) getParameterValue(parent));
                        }
                    }

                    values = ParameterUtil.getParameterValues(con, childParam,
                            ParameterUtil.toMap(paramList), allParameterValues);
                    ArrayList<Serializable> defaultValues = childParam.getDefaultValues();
                    if ((childParam.getDefaultSourceValues() != null) && (childParam.getDefaultSourceValues().size() > 1)) {
                        defaultValues = childParam.getDefaultSourceValues();
                    }
                    initParameterValue(childComponent, values, childParam.getName(), defaultValues);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } 

        }
    }
    
    private void initHiddenParameterValues(QueryParameter qp) {    	
    	if ((qp.getDefaultValues() != null) && (qp.getDefaultValues().size() > 0)) {            		
    		if (qp.getDefaultValues().size() == 1) {    			
    			parametersValues.put(qp.getName(),qp.getDefaultValues().get(0));
			} else {						
				parametersValues.put(qp.getName(), qp.getDefaultValues().toArray());
			}
    	} else if ((qp.getDefaultSource() != null) && !qp.getDefaultSource().trim().equals("")) {
    		try {
				ParameterUtil.initDefaultParameterValues(con, qp, parametersValues);
			} catch (QueryException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
			}
    	}
    }
}
