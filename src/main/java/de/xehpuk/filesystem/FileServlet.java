package de.xehpuk.filesystem;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The File servlet for serving from absolute path.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2007/07/fileservlet.html
 */
public class FileServlet extends HttpServlet {
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		// Get requested file by path info.
		String requestedFile = request.getPathInfo();

		// Check if file is actually supplied to the request URI.
		if (requestedFile == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		// Cut off beginning slash.
		requestedFile = requestedFile.substring(1);

		// Decode the file name (might contain spaces and on) and prepare file object.
		final Path file;
		try {
			file = ((Path) request.getServletContext().getAttribute("root")).resolve(URLDecoder.decode(requestedFile, "UTF-8"));
		} catch (final InvalidPathException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		// Check if file actually exists in filesystem.
		if (Files.notExists(file) || !Files.isRegularFile(file)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// Get content type by filename.
		String contentType = getServletContext().getMimeType(file.getFileName().toString());

		// If content type is unknown, then set the default value.
		// For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
		// To add new content types, add new mime-mapping entry in web.xml.
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		// Init servlet response.
		response.reset();
		response.setContentType(contentType);
		response.setHeader("Content-Length", String.valueOf(Files.size(file)));
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");

		log(new Date() + " >>> " + request.getRemoteHost() + '\t' + file);

		Files.copy(file, response.getOutputStream());

		log(new Date() + " <<< " + request.getRemoteHost() + '\t' + file);
	}
}