package ro.nextreports.designer.wizrep;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import ro.nextreports.designer.ReportLayoutUtil;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.util.ObjectCloner;

public class SelectTwoColumnsWizardPanel extends WizardPanel {

	private JComboBox firstCombo;
	private JComboBox secondCombo;
    private List<String> allColumns = new ArrayList<String>();

    public SelectTwoColumnsWizardPanel() {
        super();
        banner.setTitle(I18NSupport.getString("wizard.panel.step",4,5) + I18NSupport.getString("wizard.panel.selcolumns.title"));
        banner.setSubtitle(I18NSupport.getString("wizard.panel.selcolumns.subtitle"));
        init();
    }

    /**
     * Called when the panel is set.
     */
    @SuppressWarnings("unchecked")
	public void onDisplay() {
        String sql = ((Query) context.getAttribute(WizardConstants.QUERY)).getText();
        try {
            allColumns = ReportLayoutUtil.getAllColumnNamesForSql(null, sql);           
        } catch (Exception e) {
            e.printStackTrace();  
        }        
        for (String s : allColumns) {
        	firstCombo.addItem(s);
        }
        
        List<String> secondAllColumns = ObjectCloner.silenceDeepCopy(allColumns);
        secondCombo.addItem("-");
        for (String s : secondAllColumns) {
        	secondCombo.addItem(s);
        }
        
        List<String> all = (List<String>)context.getAttribute(WizardConstants.REPORT_COLUMNS);
        if (all != null) {
        	firstCombo.setSelectedItem( all.get(0) );
        	if (all.size() > 1) {
        		secondCombo.setEnabled(true);
        		secondCombo.setSelectedItem( all.get(1));
        	} else {
        		secondCombo.setEnabled(false);
        	}
        }
    }

    /**
     * Is there be a next panel?
     *
     * @return true if there is a panel to move to next
     */
    public boolean hasNext() {
        return true;
    }

    /**
     * Called to validate the panel before moving to next panel.
     *
     * @param messages a List of messages to be displayed.
     * @return true if the panel is valid,
     */
    public boolean validateNext(List<String> messages) {     
    	List<String> columns = new ArrayList<String>();
    	String column = getFirstReportColumn();
    	columns.add(column);
    	if (column.contains(" ")) {
    		messages.add(I18NSupport.getString("wizard.panel.selcolumns.error"));
    		return false;
    	}
    	String secondColumn = getSecondReportColumn();
    	if (!"-".equals(secondColumn)) {
    		columns.add(secondColumn);
    	}	
    	context.setAttribute(WizardConstants.REPORT_COLUMNS, columns);
        return true;
    }

    /**
     * Get the next panel to go to.
     */
	public WizardPanel getNextPanel() {
		String entity = (String) context.getAttribute(WizardConstants.ENTITY);
		Integer reportType = (Integer) context.getAttribute(WizardConstants.REPORT_TYPE);
		if (WizardConstants.ENTITY_REPORT.equals(entity)) {
			if (reportType.equals(ResultExporter.DISPLAY_TYPE)) {
				return new SelectDisplaySettingsWizardPanel();
			} 
		}
		return new SelectTemplateWizardPanel();
	}

    /**
     * Can this panel finish the wizard?
     *
     * @return true if this panel can finish the wizard.
     */
    public boolean canFinish() {
        return false;
    }

    /**
     * Called to validate the panel before finishing the wizard. Should return
     * false if canFinish returns false.
     *
     * @param messages a List of messages to be displayed.
     * @return true if it is valid for this wizard to finish.
     */
    public boolean validateFinish(List<String> messages) {
       return false;
    }

    /**
     * Handle finishing the wizard.
     */
    public void onFinish() {
    }

    private void init() {
        setLayout(new GridBagLayout());        

        firstCombo = new JComboBox();    
        secondCombo = new JComboBox();
        add(firstCombo, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(secondCombo, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(new JLabel(""), new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        add(new JLabel(""), new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
    }
    
    public String getFirstReportColumn() {
        return (String)firstCombo.getSelectedItem();
    }  
    
    public String getSecondReportColumn() {
        return (String)secondCombo.getSelectedItem();
    }

}
