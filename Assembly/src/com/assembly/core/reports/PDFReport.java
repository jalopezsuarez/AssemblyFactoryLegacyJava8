package com.assembly.core.reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.pdfview.PDFViewer;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

import com.assembly.core.commons.FileHelper;
import com.assembly.core.trace.Trace;

public final class PDFReport
{

    public static final String ReferenceTemplates = "/res/templates/";

    // =======================================================
    private static final PDFReport INSTANCE = new PDFReport();

    public static PDFReport instance()
    {
        return INSTANCE;
    }

    // =======================================================
    private PDFReport()
    {
    }

    public void annex()
    {
        try
        {
            // 1) Load ODT file and set Velocity template engine and cache it to the registry
            InputStream in = new FileInputStream(new File("ODTHelloWordWithVelocity.odt"));
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

            // 2) Create Java model context
            IContext context = report.createContext();
            context.put("name", "world");

            // 3) Set PDF as format converter
            Options options = Options.getTo(ConverterTypeTo.PDF);

            // 3) Generate report by merging Java model with the ODT and convert it to PDF
            OutputStream out = new FileOutputStream(new File("ODTHelloWordWithVelocity_Out.odt"));
            report.convert(context, options, out);
        }
        catch (Exception ex)
        {

        }
    }

    public void date()
    {
        try
        {
            String currentDirectory = FileHelper.currentDirectory();
            currentDirectory += ReferenceTemplates + "prueba.pdf";

            File file = new File(currentDirectory);
            // ResourceFile.instance().read(xxx);

            PDFViewer pdfv = new PDFViewer(true);
            pdfv.openFile(file);
            pdfv.setEnabling();
            pdfv.pack();
            pdfv.setVisible(true);

        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
    }

    public void save()
    {

    }

    public void dispose()
    {

    }
}
