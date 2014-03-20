package ro.nextreports.designer.action.help;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

public class SurveyAction extends AbstractAction {

    public SurveyAction() {
        putValue(Action.NAME, I18NSupport.getString("menu.survey"));      
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("survey"));
    }

    public void actionPerformed(ActionEvent e) {        
    	FileUtil.openUrl("http://www.next-reports.com/survey1", SurveyAction.class);              
    }
}
