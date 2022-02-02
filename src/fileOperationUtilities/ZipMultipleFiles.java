package fileOperationUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import console.commanders.ConsoleFactory;

public class ZipMultipleFiles {
	
	public static void zip_project_cccc_results(String project){
		project = project.replace("/", "");
		try {
		String[] appendices = {"_module_detail.csv", "_module_summary.csv", "_oo_design.csv", "_procedural.csv", "_procedural_detail.csv", "_project.csv", "_structural.csv", "_structural_detail.csv"};
		List<String> srcFiles = new ArrayList<String>();
		for (String appendix : appendices)
			srcFiles.add(project+appendix);
        FileOutputStream fos = new FileOutputStream(project + "_metrics.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcFile : srcFiles) {
            File fileToZip = new File(srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            int result = ConsoleFactory.getConsole().run(new String[] { "/bin/sh", "-c", "rm " + srcFile}, null, null);
    		
            fis.close();
        }
        zipOut.close();
        fos.close();
		}catch(IOException ioe) {
			System.out.println("Exception thrown when zipping results " + ioe.getMessage());
		}
    }
}
