package ro.nextreports.designer.util.file;

public class ExcelFilter extends ExtensionsFilter {

	////////////////////
	// constants

    /**
     * The extensions that this filter will search for.
     */       
    public static final String[] EXCEL_EXTENSIONS = new String[] {".XLS", ".XLSX", ".XLSM"};

	////////////////////
	// constructors
    public ExcelFilter() {
        super(EXCEL_EXTENSIONS);
    }

}
