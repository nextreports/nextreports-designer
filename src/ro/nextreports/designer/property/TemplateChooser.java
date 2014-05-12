package ro.nextreports.designer.property;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.file.ExcelFilter;

public class TemplateChooser {
	
	public static String showDialog(Component parent, String title, String initialImage) {
		
		JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(I18NSupport.getString("property.template.name"));
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new ExcelFilter());
        
        int returnVal = fc.showOpenDialog(Globals.getMainFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File f = fc.getSelectedFile();
            if (f != null) {
            	 try {
                     FileUtil.copyToDir(f, new File(Globals.getCurrentReportAbsolutePath()).getParentFile(), true);                
                 } catch (IOException e) {
                     e.printStackTrace();  
                 }
            	return f.getName();
            }            
        } 
        return null;        		       
	}

}
