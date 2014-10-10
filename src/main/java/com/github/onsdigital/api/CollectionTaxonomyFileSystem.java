package com.github.onsdigital.api;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.bean.CollectionSearchResult;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.index.ScanFileSystem;

@Endpoint
public class CollectionTaxonomyFileSystem {

	@GET
	public Object get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
		return search(extractQuery(request), extractPage(request), request.getParameter("type"));
	}

	private Object search(String query, int page, String type) throws Exception {
		List<File> files = new ArrayList<File>();
		String taxonomyRoot = Configuration.getTaxonomyPath() + query;
		final Path rootDir = Paths.get(taxonomyRoot);
		files = ScanFileSystem.getFiles(files, rootDir);
		return new CollectionSearchResult(files);
	}

	private int extractPage(HttpServletRequest request) {
		String page = request.getParameter("page");
		if (StringUtils.isNotEmpty(page) && StringUtils.isNumeric(page)) {
			int pageNumber = Integer.parseInt(page);
			return pageNumber < 1 ? 1 : pageNumber;
		}
		return 1;
	}

	private String extractQuery(HttpServletRequest request) {
		String query = request.getParameter("q");

		if (StringUtils.isEmpty(query)) {
			throw new RuntimeException("No search query provided");
		}

		return query;
	}
}