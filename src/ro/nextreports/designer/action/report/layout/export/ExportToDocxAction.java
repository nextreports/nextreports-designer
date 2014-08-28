package ro.nextreports.designer.action.report.layout.export;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportRunner;
import ro.nextreports.engine.exporter.DocxExporter;
import ro.nextreports.engine.exporter.ExporterBean;
import ro.nextreports.engine.exporter.ResultExporter;

public class ExportToDocxAction extends ExportAction {

    public ExportToDocxAction(Report report) {
		super(report, false); // orientation is taken from layout
        exportType = ReportRunner.DOCX_FORMAT;  
        putValue(NAME, I18NSupport.getString("export.docx.short.desc"));
		putValue(SMALL_ICON, ImageUtil.getImageIcon("word"));
		putValue(MNEMONIC_KEY, new Integer('D'));
		putValue(SHORT_DESCRIPTION, I18NSupport.getString("export.docx.short.desc"));
		putValue(LONG_DESCRIPTION, I18NSupport.getString("export.docx.long.desc"));
	}	
	
	@Override
	protected String getFileExtension() {		
		return "docx";
	}

	@Override
	protected ResultExporter getResultExporter(ExporterBean bean) {
		ResultExporter exporter = new DocxExporter(bean);
		exporter.setImageChartPath(Globals.USER_DATA_DIR + "/reports");
		return exporter;
	}
}
