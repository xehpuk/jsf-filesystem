package de.xehpuk.filesystem;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

@ManagedBean
public class FileLister {
	private final Path rootDir = (Path) ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getAttribute("root");

	public List<Path> listDirectories(final String root) throws IOException {
		return list(root, new DirectoryStream.Filter<Path>() {
			@Override
			public boolean accept(final Path entry) throws IOException {
				return Files.isDirectory(entry);
			}
		});
	}

	public List<Path> listFiles(final String root) throws IOException {
		return list(root, new DirectoryStream.Filter<Path>() {
			@Override
			public boolean accept(final Path entry) throws IOException {
				return Files.isRegularFile(entry);
			}
		});
	}
	
	public long size(final Path path) throws IOException {
		return Files.size(path);
	}

	private List<Path> list(final String root, final Filter<Path> filter) throws IOException {
		try {
			final Path dir = rootDir.resolve(root).normalize();
			if (!isValid(dir) || !Files.isDirectory(dir)) {
				return Collections.emptyList();
			}
			try (final DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
				final List<Path> paths = new ArrayList<>();
				for (final Path path : stream) {
					if (isValid(path)) {
						paths.add(path);
					}
				}
				return paths;
			}
		} catch (final InvalidPathException e) {
			return Collections.emptyList();
		}
	}

	public boolean isValid(final Path root) throws IOException {
		return root != null && Files.exists(root) && !Files.isHidden(root) && root.startsWith(rootDir);
	}

	public boolean isValid(final String root) throws IOException {
		try {
			final Path dir = rootDir.resolve(root).normalize();
			return isValid(dir);
		} catch (final InvalidPathException e) {
			return false;
		}
	}
	
	public String getRoot() {
		return rootDir.getFileName().toString();
	}
}