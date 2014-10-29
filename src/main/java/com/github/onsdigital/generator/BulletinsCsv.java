package com.github.onsdigital.generator;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.json.Serialiser;

public class BulletinsCsv {

	static Set<Folder> folders;

	public static void buildFolders() throws IOException {
		Serialiser.getBuilder().setPrettyPrinting();
		Reader reader = ResourceUtils.getReader("/bulletins.csv");

		String title = null;
		String date = null;
		Folder titleFolder = null;
		Folder dateFolder = null;
		int titleCounter = 0;
		int dateCounter = 0;

		try (CSVReader csvReader = new CSVReader(reader)) {

			String[] headers = csvReader.readNext();
			int nodeIndex = ArrayUtils.indexOf(headers, "TaxonomyNode");
			int titleIndex = ArrayUtils.indexOf(headers, "Title");

			folders = new HashSet<>();
			String[] row;
			while ((row = csvReader.readNext()) != null) {

				if (StringUtils.isNotBlank(row[nodeIndex])) {
					title = row[nodeIndex];
					titleFolder = new Folder();
					titleFolder.name = title;
					titleFolder.index = titleCounter++;
					dateCounter = 0;
					folders.add(titleFolder);
					date = null;
				}

				if (StringUtils.isNotBlank(row[titleIndex])) {
					date = row[titleIndex];
					dateFolder = new Folder();
					dateFolder.name = date;
					dateFolder.parent = titleFolder;
					dateFolder.index = dateCounter++;
					titleFolder.children.add(dateFolder);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			buildFolders();

			for (Folder folder : folders) {
				System.out.println("folderName: " + folder.name);
				for (Folder childFolder : folder.children) {
					System.out.println("date: " + childFolder.name);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}