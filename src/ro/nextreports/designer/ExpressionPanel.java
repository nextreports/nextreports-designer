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
package ro.nextreports.designer;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXList;

import ro.nextreports.designer.querybuilder.ParameterManager;
import ro.nextreports.designer.ui.sqleditor.Editor;
import ro.nextreports.designer.util.I18NSupport;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.text.Collator;

import ro.nextreports.engine.util.NameType;
import ro.nextreports.engine.exporter.util.variable.VariableFactory;
import ro.nextreports.engine.exporter.util.variable.Variable;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.queryexec.QueryParameter;

/**
 * User: mihai.panaitescu
 * Date: 30-Apr-2010
 * Time: 13:54:34
 */
public class ExpressionPanel extends JXPanel {

    //@todo i18n
    public static String STRING_TYPE = "String";
    public static String DATE_TYPE = "Date";
    public static String NUMERIC_TYPE = "Numeric";
    public static String BOOLEAN_TYPE = "Boolean";

    private Dimension scrDim = new Dimension(180, 106);
    private Dimension smallScrDim = new Dimension(80, 100);
    private Dimension editorDim = new Dimension(540, 300);

    private JXList columnList;
    private DefaultListModel columnModel = new DefaultListModel();
    private JScrollPane scrColumn;
    private JXList functionList;
    private DefaultListModel functionModel = new DefaultListModel();
    private JScrollPane scrFunction;
    private JXList variableList;
    private DefaultListModel variableModel = new DefaultListModel();
    private JScrollPane scrVariable;
    private JXList parameterList;
    private DefaultListModel parameterModel = new DefaultListModel();
    private JScrollPane scrParameter;
    private JXList operatorList;
    private DefaultListModel operatorModel = new DefaultListModel();
    private JScrollPane scrOperator;
    private Editor editor;
    private JScrollPane scrEditor;
    private JTextField nameText;

    public ExpressionPanel(boolean isStatic, boolean isFooter, String bandName) {
        this(isStatic, isFooter, bandName, true);
    }  
    
    public ExpressionPanel(boolean isStatic, boolean isHeaderOrFooter, String bandName, boolean showName) {

        if (!isStatic) {
            addColumns();
        }
        if (isHeaderOrFooter) {
        	addFunctions(bandName);
        }
        addVariables();
        addParameters();
        addOperators();

        initUI(isStatic, isHeaderOrFooter, showName);
    }

    private void initUI(boolean isStatic, boolean isHeaderOrFooter, boolean showName) {
        setLayout(new GridBagLayout());
        if (!isStatic) {
            columnList = new JXList(columnModel);
            columnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            columnList.setCellRenderer(new ColumnCellRenderer());
            columnList.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int position = editor.getEditorPanel().getEditorPane().getCaretPosition();
                        String old = editor.getText();
                        int index = columnList.locationToIndex(e.getPoint());
                        NameType nt = (NameType) columnModel.getElementAt(index);
                        String col = nt.getName().replaceAll("\\s", ResultExporter.SPACE_REPLACEMENT);
                        editor.setText(newExpression(old, position, col, "C"));
                    }
                }
            });
            scrColumn = new JScrollPane(columnList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrColumn.setPreferredSize(scrDim);
            scrColumn.setMinimumSize(scrDim);
            scrColumn.setBorder(new TitledBorder(I18NSupport.getString("expression.columns")));
        }
        
        if (isHeaderOrFooter && (functionModel.size() > 0)) {
            functionList = new JXList(functionModel);
            functionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);       
            functionList.setCellRenderer(new FunctionCellRenderer());
            functionList.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int position = editor.getEditorPanel().getEditorPane().getCaretPosition();
                        String old = editor.getText();
                        int index = functionList.locationToIndex(e.getPoint());
                        String f = (String) functionModel.getElementAt(index);                        
                        editor.setText(newExpression(old, position, f, "F"));
                    }
                }
            });
            scrFunction = new JScrollPane(functionList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrFunction.setPreferredSize(scrDim);
            scrFunction.setMinimumSize(scrDim);
            scrFunction.setBorder(new TitledBorder(I18NSupport.getString("expression.functions")));
        }

        variableList = new JXList(variableModel);
        variableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        variableList.setCellRenderer(new VariableCellRenderer());
        variableList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int position = editor.getEditorPanel().getEditorPane().getCaretPosition();
                    String old = editor.getText();
                    int index = variableList.locationToIndex(e.getPoint());
                    Variable variable = (Variable) variableModel.getElementAt(index);                   
                    editor.setText(newExpression(old, position, variable.getName(), "V"));
                }
            }
        });
        scrVariable = new JScrollPane(variableList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrVariable.setPreferredSize(scrDim);
        scrVariable.setMinimumSize(scrDim);
        scrVariable.setBorder(new TitledBorder(I18NSupport.getString("expression.variables")));

        parameterList = new JXList(parameterModel);
        parameterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        parameterList.setCellRenderer(new ParameterCellRenderer());
        parameterList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int position = editor.getEditorPanel().getEditorPane().getCaretPosition();
                    String old = editor.getText();
                    int index = parameterList.locationToIndex(e.getPoint());
                    QueryParameter parameter = (QueryParameter) parameterModel.getElementAt(index);                    
                    editor.setText(newExpression(old, position, parameter.getName(), "P"));
                }
            }
        });
        scrParameter = new JScrollPane(parameterList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrParameter.setPreferredSize(scrDim);
        scrParameter.setMinimumSize(scrDim);
        scrParameter.setBorder(new TitledBorder(I18NSupport.getString("expression.parameters")));

        operatorList = new JXList(operatorModel);
        operatorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        operatorList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int position = editor.getEditorPanel().getEditorPane().getCaretPosition();
                    String old = editor.getText();
                    int index = operatorList.locationToIndex(e.getPoint());
                    String operator = (String) operatorModel.getElementAt(index);
                    editor.setText(newExpressionOperator(old, position, operator));
                }
            }
        });
        scrOperator = new JScrollPane(operatorList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrOperator.setPreferredSize(smallScrDim);
        scrOperator.setMinimumSize(smallScrDim);
        scrOperator.setBorder(new TitledBorder(I18NSupport.getString("expression.operators")));

        editor = new Editor();
        scrEditor = new JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrEditor.setPreferredSize(editorDim);
        scrEditor.setMinimumSize(editorDim);
        scrEditor.setBorder(new TitledBorder(I18NSupport.getString("expression.editor")));

        nameText = new JTextField();
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        namePanel.add(new JLabel(I18NSupport.getString("expression.name")));
        namePanel.add(Box.createHorizontalStrut(5));
        namePanel.add(nameText);

        if (showName) {
            add(namePanel, new GridBagConstraints(0, 0, 1, 1, 0.1, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        }

        int index = 1;
        if (!isStatic) {
            add(scrColumn, new GridBagConstraints(0, 1, 1, 1, 0.1, 1.0, GridBagConstraints.WEST,
                    GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
            index++;
        } 
        if (isHeaderOrFooter && (functionModel.size() > 0)) {
        	add(scrFunction, new GridBagConstraints(0, index, 1, 1, 0.1, 1.0, GridBagConstraints.WEST,
                    GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
            index++;
        }
        add(scrVariable, new GridBagConstraints(0, index, 1, 1, 0.1, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
        int gridHeight = index;
        if (!parameterModel.isEmpty()) {
        	gridHeight = index+1;
        	add(scrParameter, new GridBagConstraints(0, gridHeight, 1, 1, 0.1, 1.0, GridBagConstraints.WEST,
                    GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
        }  
        
        add(scrOperator, new GridBagConstraints(1, 1, 1, gridHeight, 0.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.VERTICAL, new Insets(5, 0, 5, 5), 0, 0));
        add(scrEditor, new GridBagConstraints(2, 1, 1, gridHeight, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
    }

    class ColumnCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (value != null) {
                NameType column = (NameType) value;
                String type = NUMERIC_TYPE;
                if ("java.lang.String".equals(column.getType())) {
                    type = STRING_TYPE;
                } else if ("java.util.Date".equals(column.getType())) {
                    type = DATE_TYPE;
                } else if ("java.lang.Boolean".equals(column.getType())) {
                    type = BOOLEAN_TYPE;
                }
                comp.setText("<html>$C_" + column.getName() + " <font color=\"#cccccc\">(" + type + ")</font></html>");
//                    value = column.getName();
//                    list.setToolTipText(value.toString());
            }
            return comp;
        }
    }

    class VariableCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (value != null) {
                Variable variable = (Variable) value;
                String type = STRING_TYPE;
                if (variable.getName().equals(Variable.DATE_VARIABLE)) {
                    type = DATE_TYPE;
                } else if (variable.getName().equals(Variable.ROW_VARIABLE)) {
                    type = NUMERIC_TYPE;
                }  else if (variable.getName().equals(Variable.GROUP_ROW_VARIABLE)) {
                    type = NUMERIC_TYPE;
                } else if (variable.getName().equals(Variable.PRODUCT_VARIABLE)) {
                    type = STRING_TYPE;
                } else if (variable.getName().equals(Variable.USER_VARIABLE)) {
                    type = STRING_TYPE;
                }
                comp.setText("<html>$V_" + variable.getName() + " <font color=\"#cccccc\">(" + type + ")</font></html>");
            }
            return comp;
        }
    }

    class ParameterCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (value != null) {
                QueryParameter parameter = (QueryParameter) value;
                String type = NUMERIC_TYPE;
                if ("java.lang.String".equals(parameter.getValueClassName())) {
                    type = STRING_TYPE;
                } else if ("java.lang.Boolean".equals(parameter.getValueClassName())) {
                    type = BOOLEAN_TYPE;
                } else if ("java.util.Date".equals(parameter.getValueClassName())) {
                    type = DATE_TYPE;
                }
                comp.setText("<html>$P_" + parameter.getName() + " <font color=\"#cccccc\">(" + type + ")</font></html>");
            }
            return comp;
        }
    }
    
    class FunctionCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (value != null) {
                String func = (String) value;
                String type = NUMERIC_TYPE;                
                comp.setText("<html>$F_" + func + " <font color=\"#cccccc\">(" + type + ")</font></html>");
            }
            return comp;
        }
    }

    private void addColumns() {
        String sql = Globals.getMainFrame().getQueryBuilderPanel().getUserSql();
        List<NameType> columns = null;
        try {
            columns = ReportLayoutUtil.getAllColumnsForReport(sql);
            Collections.sort(columns, new Comparator<NameType>() {
                public int compare(NameType o1, NameType o2) {
                    return Collator.getInstance().compare(o1.getName(), o2.getName());
                }
            });
            for (NameType nt : columns) {
                columnModel.addElement(nt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void addFunctions(String bandName) {
        Set<String> functions = LayoutHelper.getReportLayout().getFunctions(bandName);
        for (String f : functions) {
        	functionModel.addElement(f);
        }
    }

    private void addVariables() {
        List<Variable> variables = VariableFactory.getVariables();
        Collections.sort(variables, new Comparator<Variable>() {
            public int compare(Variable o1, Variable o2) {
                return Collator.getInstance().compare(o1.getName(), o2.getName());
            }
        });
        for (Variable v : variables) {
            variableModel.addElement(v);
        }
    }

    private void addParameters() {
        List<QueryParameter> parameters = ParameterManager.getInstance().getParameters();
        Collections.sort(parameters, new Comparator<QueryParameter>() {
            public int compare(QueryParameter o1, QueryParameter o2) {
                return Collator.getInstance().compare(o1.getName(), o2.getName());
            }
        });
        for (QueryParameter param : parameters) {
            parameterModel.addElement(param);
        }
    }

    private void addOperators() {
        operatorModel.addElement("+");
        operatorModel.addElement("-");
        operatorModel.addElement("*");
        operatorModel.addElement("/ (div)");
        operatorModel.addElement("% (mod)");
        operatorModel.addElement("&& (and)");
        operatorModel.addElement("|| (or)");
        operatorModel.addElement("! (not)");
        operatorModel.addElement("== (eq)");
        operatorModel.addElement("!= (ne)");
        operatorModel.addElement("< (lt)");
        operatorModel.addElement("<= (le)");
        operatorModel.addElement("> (gt)");
        operatorModel.addElement(">= (ge)");
        operatorModel.addElement("&");
        operatorModel.addElement("|");
        operatorModel.addElement("^");
        operatorModel.addElement("~");
        operatorModel.addElement("if..else..");
    }

    private String newExpression(String oldExpression, int position, String element, String index) {
        String prefix = oldExpression.substring(0, position);
        if (!prefix.endsWith(" ")) {
            prefix = prefix + " ";
        }
        String sufix = oldExpression.substring(position);
        if (!sufix.startsWith(" ")) {
            sufix = " " + sufix;
        }
        return prefix + "$" + index + "_" + element + sufix;
    }

    private String newExpressionOperator(String oldExpression, int position, String operator) {
         String prefix = oldExpression.substring(0, position);
        if (!prefix.endsWith(" ")) {
            prefix = prefix + " ";
        }
        String sufix = oldExpression.substring(position);
        if (!sufix.startsWith(" ")) {
            sufix = " " + sufix;
        }
        int index = operator.indexOf("(");
        if (index > 0) {
            operator = operator.substring(0, index-1);
        }
        if (operator.startsWith("if")) {
            operator = "if ( ) { ; } else { ; }";
        }
        return prefix + operator + sufix;
    }

    public String getExpression(){
        return editor.getText();
    }

    public void setExpression(String expression) {
        editor.setText(expression);
    }

    public String getExpressionName() {
        return nameText.getText();
    }

    public void setExpressionName(String expressionName) {
        nameText.setText(expressionName);
        if (!expressionName.equals("")) {
            nameText.setEditable(false);
        }
    }

}
