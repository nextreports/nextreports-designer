package ro.nextreports.designer.action.report.layout.export;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.ReportRunner;
import ro.nextreports.engine.exporter.ExporterBean;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.exporter.XlsxExporter;

public class ExportToExcelXAction extends ExportAction {

    public ExportToExcelXAction(Report report) {
		super(report);
        exportType = ReportRunner.EXCEL_XLSX_FORMAT;
        putValue(NAME, I18NSupport.getString("export.excelx.short.desc"));
		putValue(SMALL_ICON, ImageUtil.getImageIcon("excel_xlsx"));
		putValue(MNEMONIC_KEY, new Integer('E'));
		putValue(SHORT_DESCRIPTION, I18NSupport.getString("export.excelx.short.desc"));
		putValue(LONG_DESCRIPTION, I18NSupport.getString("export.excelx.long.desc"));
	}	
	
	@Override
	protected String getFileExtension() {
		if (hasMacro()) {
			return "xlsm";
		} else {
			return "xlsx";
		}
	}

	@Override
	protected ResultExporter getResultExporter(ExporterBean bean) {
		ResultExporter exporter = new XlsxExporter(bean);
		exporter.setImageChartPath(Globals.USER_DATA_DIR + "/reports");
		return exporter;
	}
	
	@Override
	protected boolean hasMacro() {
		ReportLayout layout = LayoutHelper.getReportLayout();
        if (report != null) {
            layout = report.getLayout();
        }   
		String templateName = layout.getTemplateName();
		return (templateName != null) && templateName.endsWith(".xlsm");
    }
	
}
