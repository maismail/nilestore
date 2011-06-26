package eg.nileu.cis.nilestore.utils.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class ConfigFile {

	private final String EQUAL = "=";
	private final String SECTIONSTART = "[";
	private final String SECTIONEND = "]";

	private Map<String, Map<String, String>> elements = new TreeMap<String, Map<String, String>>();
	private final File file;

	public ConfigFile(String filepath) throws IOException {
		this.file = new File(filepath);
		if (file.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			fill(br);
		}
	}

	public ConfigFile(File file) throws IOException {
		this.file = file;
		if (file.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			fill(br);
		}
	}

	private void fill(BufferedReader reader) throws IOException {
		String line;
		String currSection = "";
		line = reader.readLine();
		while (line != null) {
			if (!line.equals("") && !line.startsWith("#")) {
				if (line.startsWith(SECTIONSTART)) {
					currSection = line.trim().replace(SECTIONSTART, "")
							.replace(SECTIONEND, "");
					elements.put(currSection, new TreeMap<String, String>());
					line = reader.readLine();
					continue;
				}

				StringTokenizer tokenizer = new StringTokenizer(line.trim(),
						EQUAL);
				String key = tokenizer.nextToken();
				String value = tokenizer.nextToken();

				elements.get(currSection).put(key, value);
			}
			line = reader.readLine();
		}
	}

	public Integer getInteger(String section, String key)
			throws ConfigFileException {
		String value = get(section, key);
		if (value != null) {
			try {
				return Integer.valueOf(value);
			} catch (NumberFormatException e) {
				throw new ConfigFileException(
						String.format(
								"Error at ConfigFile.getInteger[section=%s,key=%s] : integer expected but we got (%s)",
								section, key, value));
			}
		}
		throw new ConfigFileException(
				String.format(
						"Error at ConfigFile.getInteger[section=%s,key=%s] : integer expected but we got (null)",
						section, key));
	}

	public Boolean getBoolean(String section, String key)
			throws ConfigFileException {
		String value = get(section, key);
		if (value != null) {
			if (isBoolean(value)) {
				return Boolean.valueOf(value);
			} else {
				throw new ConfigFileException(
						String.format(
								"Error at ConfigFile.getBoolean[section=%s,key=%s] : boolean expected but we got (%s)",
								section, key, value));
			}
		}
		throw new ConfigFileException(
				String.format(
						"Error at ConfigFile.getBoolean[section=%s,key=%s] : boolean expected but we got (null)",
						section, key));
	}

	public String getString(String section, String key)
			throws ConfigFileException {
		String value = get(section, key);
		if (value != null)
			return value;
		throw new ConfigFileException(
				String.format(
						"Error at ConfigFile.getString[section=%s,key=%s] : string expected but we got (null)",
						section, key));
	}

	public void setString(String section, String key, String val) {
		set(section, key, val);
	}

	public void setInteger(String section, String key, Integer val) {
		set(section, key, String.valueOf(val));
	}

	public void setBoolean(String section, String key, Boolean val) {
		set(section, key, String.valueOf(val));
	}

	public boolean hasValue(String section, String key) {
		if (get(section, key) != null) {
			return true;
		}
		return false;
	}

	public void save() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for (String section : elements.keySet()) {

			writer.write("[" + section + "]");
			writer.newLine();

			Map<String, String> section_elements = elements.get(section);
			for (String key : section_elements.keySet()) {
				writer.write(key + "=" + section_elements.get(key));
				writer.newLine();
			}
			writer.newLine();
		}
		writer.close();
	}

	private String get(String section, String key) {
		if (elements.containsKey(section)) {
			Map<String, String> section_elements = elements.get(section);
			if (section_elements.containsKey(key)) {
				return section_elements.get(key);
			}
		}
		return null;
	}

	private void set(String section, String key, String val) {
		if (!elements.containsKey(section))
			elements.put(section, new TreeMap<String, String>());

		elements.get(section).put(key, val);
	}

	private boolean isBoolean(String s) {
		s = s.toLowerCase();
		return s.equals("true") || s.equals("false");
	}
}
