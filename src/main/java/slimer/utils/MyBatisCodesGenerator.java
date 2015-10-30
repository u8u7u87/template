package slimer.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.config.Configuration;

public class MyBatisCodesGenerator {

	public static void main(String[] args) {
		try {
			List<String> warnings = new ArrayList();
			boolean overwrite = true;
			File configFile = new File(MyBatisCodesGenerator.class.getResource("MyBatisCodesConfig.xml").getPath());
			ConfigurationParser cp = new ConfigurationParser(warnings);
			Configuration config = cp.parseConfiguration(configFile);
			DefaultShellCallback callback = new DefaultShellCallback(overwrite);
			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
			myBatisGenerator.generate(null);
			System.err.println("代码生成完成.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
