package com.github.onsdigital.generator.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.onsdigital.generator.TimeseriesData;
import com.github.onsdigital.json.timeseries.TimeSeries;
import com.github.onsdigital.json.timeseries.TimeSeriesValue;

public class DataCSV {

	static ExecutorService executorService = Executors.newCachedThreadPool();

	public static void parse() throws IOException {
		Collection<Path> files = getFiles();

		for (Path file : files) {
			read(file);
		}
	}

	private static void read(Path file) throws IOException {
		try (CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(Files.newInputStream(file), Charset.forName("CP1252"))))) {

			String name = FilenameUtils.getBaseName(file.getFileName().toString());
			Set<TimeSeries> dataset = new HashSet<TimeSeries>();
			Data.addDataset(name, dataset);

			// Check all the CDIDs in the header row:
			int duplicates = 0;
			String[] header = csvReader.readNext();
			for (int i = 1; i < header.length; i++) {
				TimeSeries timeseries = Data.timeseries(header[i]);
				if (timeseries == null) {
					timeseries = new TimeSeries();
					timeseries.setCdid(header[i]);
					timeseries.data = new ArrayList<>();
					Data.addTimeseries(timeseries);
				} else {
					// Don't process this timeseries - it's a duplicate.
					duplicates++;
					header[i] = null;
				}
				dataset.add(timeseries);
			}

			// Now read the data - each row *may* contain one additional value
			// for each timeseries:
			String[] row;
			rows: while ((row = csvReader.readNext()) != null) {

				// There is a blank line between the data and the
				// additional information below, so stop reading there:
				if (row.length == 0 || StringUtils.isBlank(row[0])) {
					break rows;
				}

				// Add data to timeseries:
				String date = row[0];
				for (int i = 1; i < row.length; i++) {
					if (StringUtils.isNotBlank(row[i])) {
						String cdid = header[i];
						if (cdid == null) {
							// This one was marked as a duplicate
							continue;
						}
						String value = row[i];
						TimeSeriesValue timeSeriesValue = new TimeSeriesValue();
						timeSeriesValue.date = date;
						timeSeriesValue.value = value;
					}
				}
			}

			System.out.println(name + " contains " + dataset.size() + " timeseries (" + duplicates + " duplicates)");
		}
	}

	private static Collection<Path> getFiles() throws IOException {
		Set<Path> result = new HashSet<>();

		try {
			URL resource = TimeseriesData.class.getResource("/data");
			Path folder = Paths.get(resource.toURI());

			try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.csv")) {

				// Iterate the paths in this directory:
				for (Path item : stream) {
					if (!StringUtils.equals(item.getFileName().toString(), "Data for non-CDID hero series.xlsx")) {
						result.add(item);
					}
				}

			}

		} catch (URISyntaxException e) {
			throw new IOException(e);
		}

		return result;
	}
}
