package ro.nextreports.designer.wizpublish;

import java.io.File;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;
import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.XStreamFactory;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.util.NextChartUtil;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.server.api.client.ChartMetaData;
import ro.nextreports.server.api.client.FileMetaData;
import ro.nextreports.server.api.client.ReportMetaData;
import ro.nextreports.server.api.client.WebServiceClient;

/**
 * Helper for download & save of report
 * 
 * @author daniel.avieritei
 *
 */
public class DownloadHelper {

	public static void downloadReport(String reportPath, WebServiceClient client, UIActivator activator)
			throws Exception {
		ReportMetaData reportMetaData = client.getReport(reportPath);
		XStream xstream = XStreamFactory.createXStream();
		Report report = (Report) xstream.fromXML(new String(reportMetaData.getMainFile().getFileContent(), "UTF-8"));

		byte status = ReportUtil.isValidReportVersion(report);
		if (ReportUtil.REPORT_INVALID_OLDER == status) {
			Show.error(I18NSupport.getString("report.version.invalid.older"));
			return;
		} else if (ReportUtil.REPORT_INVALID_NEWER == status) {
			Show.error(I18NSupport.getString("report.version.invalid.newer", ReleaseInfoAdapter.getVersionNumber()));
			return;
		}

		// save the report
		String existingPath = Globals.getCurrentReportAbsolutePath();

		// check if the path exists, if not create the
		// report automatically
		// extract path and report name

		String repPath = reportMetaData.getPath();
		repPath = repPath.replaceFirst("/reports", "");
		String repName = null;
		try {
			repName = repPath.substring(repPath.lastIndexOf("/") + 1);
			repPath = repPath.substring(0, repPath.lastIndexOf("/"));
		} catch (Exception e) {
			repName = repPath.substring(repPath.lastIndexOf("\\"));
			repPath = repPath.substring(0, repPath.lastIndexOf("/"));
		}

		File reportsGlobalFolderFile = new File(FileReportPersistence.getReportsRelativePath());
		if (!reportsGlobalFolderFile.exists()) {
			reportsGlobalFolderFile.mkdirs();
		}

		repPath = FileReportPersistence.getReportsRelativePath() + File.separator + repPath;
		File reportPathFile = new File(repPath);

		String name = null;
		// if the path exists ask for a new name for the
		// report
		if (!reportPathFile.exists()) {
			reportPathFile.mkdirs();
		}

		File reportFullPathFile = new File(reportPathFile.getAbsolutePath() + File.separator + repName);

		if (!reportFullPathFile.exists()) {
			Globals.setCurrentReportAbsolutePath(reportPathFile.getAbsolutePath() + File.separator + repName
					+ FormSaver.REPORT_EXTENSION_SEPARATOR + FormSaver.REPORT_EXTENSION);
			report.setName(repName + FormSaver.REPORT_EXTENSION_SEPARATOR + FormSaver.REPORT_EXTENSION);
			name = FormSaver.getInstance().save(I18NSupport.getString("save.report"), false, report);
		} else {
			report.setName(repName);
			name = FormSaver.getInstance().save(I18NSupport.getString("save.report"), true, report);
		}

		if (name != null) {
			String path = Globals.getCurrentReportAbsolutePath();

			// this is buggy , adding report directly to
			// folder on automatic, rather have refresh
			// datasouce all together

			// Globals.getMainFrame().getQueryBuilderPanel().addReport(name,
			// path);

			Globals.getReportUndoManager().discardAllEdits();

			// save images
			List<FileMetaData> list = reportMetaData.getImages();
			if (list != null) {
				String prefix = path.substring(0, path.lastIndexOf(File.separator));
				for (FileMetaData image : list) {
					FileUtil.createFile(prefix + File.separator + image.getFileName(), image.getFileContent());
				}
			}

			// save template
			FileMetaData fmd = reportMetaData.getTemplate();
			if (fmd != null) {
				String prefix = path.substring(0, path.lastIndexOf(File.separator));
				FileUtil.createFile(prefix + File.separator + fmd.getFileName(), fmd.getFileContent());
			}
		}

		Globals.setCurrentReportAbsolutePath(existingPath);
		//
		if (activator != null) {
			activator.updateProgress();
		}
	}

	public static void downloadChart(String chartPath, WebServiceClient client, UIActivator activator)
			throws Exception {
		ChartMetaData chartMetaData = client.getChart(chartPath);
		XStream xstream = XStreamFactory.createXStream();
		Chart chart = (Chart) xstream.fromXML(new String(chartMetaData.getMainFile().getFileContent(), "UTF-8"));

		byte status = NextChartUtil.isValidChartVersion(chart);
		if (NextChartUtil.CHART_INVALID_NEWER == status) {
			Show.error(I18NSupport.getString("chart.version.invalid.newer", ReleaseInfoAdapter.getVersionNumber()));
			return;
		}

		// save the chart
		String existingPath = Globals.getCurrentChartAbsolutePath();

		String chartAbsolutePath = chartMetaData.getPath();
		chartAbsolutePath = chartAbsolutePath.replaceFirst("/charts", "");
		String chartName = null;
		try {
			chartName = chartAbsolutePath.substring(chartAbsolutePath.lastIndexOf("/") + 1);
			chartAbsolutePath = chartAbsolutePath.substring(0, chartAbsolutePath.lastIndexOf("/"));
		} catch (Exception e) {
			chartName = chartAbsolutePath.substring(chartAbsolutePath.lastIndexOf("\\"));
			chartAbsolutePath = chartAbsolutePath.substring(0, chartAbsolutePath.lastIndexOf("/"));
		}

		File chartsGlobalFolderPathFile = new File(FileReportPersistence.getChartsRelativePath());
		if (!chartsGlobalFolderPathFile.exists()) {
			chartsGlobalFolderPathFile.mkdirs();
		}

		chartAbsolutePath = FileReportPersistence.getChartsRelativePath() + File.separator + chartAbsolutePath;
		File chartFolderPath = new File(chartAbsolutePath);

		String name = null;
		// if the path exists ask for a new name for the
		// report
		if (!chartFolderPath.exists()) {
			chartFolderPath.mkdirs();
		}

		File chartFullPathFile = new File(chartFolderPath.getAbsolutePath() + File.separator + chartName
				+ (chartName.endsWith(ChartUtil.CHART_EXTENSION) ? ""
						: ChartUtil.CHART_EXTENSION_SEPARATOR + ChartUtil.CHART_EXTENSION));
		// report name usually contains .report in it, charts it does not

		if (!chartFullPathFile.exists()) {
			Globals.setCurrentChartAbsolutePath(chartFolderPath.getAbsolutePath() + File.separator + chartName
					+ ChartUtil.CHART_EXTENSION_SEPARATOR + ChartUtil.CHART_EXTENSION);
			chart.setName(chartName + ChartUtil.CHART_EXTENSION_SEPARATOR + ChartUtil.CHART_EXTENSION);
			name = ChartUtil.saveChart(I18NSupport.getString("save.chart"), false, chart);
		} else {
			chart.setName(chartName);
			name = ChartUtil.saveChart(I18NSupport.getString("save.chart"), true, chart);
		}

		if (name != null) {
			String path = Globals.getCurrentChartAbsolutePath();
			Globals.getMainFrame().getQueryBuilderPanel().addChart(name, path);
			Globals.getReportUndoManager().discardAllEdits();
		}
		Globals.setCurrentChartAbsolutePath(existingPath);

		if (activator != null) {
			activator.updateProgress();
		}
	}
}
