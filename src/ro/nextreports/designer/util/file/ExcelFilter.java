package ro.nextreports.designer.util.file;

public class ExcelFilter extends ExtensionFilter {

	////////////////////
	// constants

    /**
     * The extension that this filter will search for.
     */
    private static final String EXCEL_EXTENSION = ".XLS";

	////////////////////
	// constructors

    public ExcelFilter() {
        super(EXCEL_EXTENSION);
    }

}
