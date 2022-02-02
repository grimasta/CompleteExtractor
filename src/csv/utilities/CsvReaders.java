package csv.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import csvBeans.AccessesBean;
import csvBeans.CallsBean;
import csvBeans.CsvBean;
import csvBeans.IncludesBean;
import csvBeans.SetsBean;

public class CsvReaders {
	private List<AccessesBean> theAccessesData;
	private List<SetsBean> theSetsData;
	private List<IncludesBean> theIncludesData;
	private List<CallsBean> theCallsData;
	
	public void loadData() {

			theAccessesData = 
					beanBuilderExample(Paths.get("relation_data/elisa_accesses.csv"), AccessesBean.class);
			theSetsData = 
					beanBuilderExample(Paths.get("relation_data/elisa_accesses.csv"), SetsBean.class);
			theIncludesData = 
					beanBuilderExample(Paths.get("relation_data/elisa_accesses.csv"), IncludesBean.class);
			theCallsData = 
					beanBuilderExample(Paths.get("relation_data/elisa_accesses.csv"), CallsBean.class);
			System.out.println("explicit relation data succsefully loaded");
	}

	public <T extends CsvBean> List<T> beanBuilderExample(Path path, Class<T> t) {
		try {
			Reader inputReader = new InputStreamReader(new FileInputStream(new File("relation_data/elisa_accesses.csv")));
			BeanListProcessor<T> rowProcessor = new BeanListProcessor<T>(t);
			CsvParserSettings settings = new CsvParserSettings();
			settings.setHeaderExtractionEnabled(true);
			settings.setProcessor(rowProcessor);
			CsvParser parser = new CsvParser(settings);
			parser.parse(inputReader);
			return rowProcessor.getBeans();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ArrayList<>();
		}
	}
	
	public List<AccessesBean> getTheAccessesData() {
		return theAccessesData;
	}

	public List<SetsBean> getTheSetsData() {
		return theSetsData;
	}

	public List<IncludesBean> getTheIncludesData() {
		return theIncludesData;
	}

	public List<CallsBean> getTheCallsData() {
		return theCallsData;
	}

}
