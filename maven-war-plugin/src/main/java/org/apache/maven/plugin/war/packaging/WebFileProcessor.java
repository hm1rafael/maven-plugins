package org.apache.maven.plugin.war.packaging;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.shared.utils.StringUtils;

class WebFileProcessor {

	private String version;
	private Pattern pattern;
	
	private static String[] CSS_JAVASCRIPT_EXTENSIONS = new String[] { "css","js" };
	
	public WebFileProcessor(WarPackagingContext context) {
		this.pattern = Pattern.compile("(\\w*).js");
		this.version = context.getProject().getVersion();
	}
	
	public String getDestinationName(String targetPrefix, String fileToCopyName) {
		if ( targetPrefix == null )
		{
			if (isJavascriptOrCss(fileToCopyName)) {
				return fileToCopyName + "-" + this.version;
			}
		    return fileToCopyName;
		}
		return targetPrefix + fileToCopyName;
	}

	public File createSourceFile(File sourceBaseDir, String fileToCopyName) {
		File sourceFile = new File(sourceBaseDir, fileToCopyName);
		if (isJavascriptOrCss(fileToCopyName)) {
			return sourceFile;
		}
		return processScriptTags(fileToCopyName, sourceFile);
	}

	private File processScriptTags(String fileToCopyName, File sourceFile) {
		try {
			String probablyHtmlFileText = FileUtils.readFileToString(sourceFile);
			Matcher matcher = pattern.matcher(probablyHtmlFileText);
			while(matcher.find()) {
				probablyHtmlFileText = matcher.replaceAll("$1-" + version);
			}
			File file = new File("temp",fileToCopyName);
			FileUtils.writeStringToFile(file, probablyHtmlFileText);
			return file;
		} catch (IOException e) {
			throw new IllegalStateException("Nao foi possivel reescrever o arquivo");
		}
	}
	
	private boolean isJavascriptOrCss(String fileName) {
		return FilenameUtils.isExtension(fileName, CSS_JAVASCRIPT_EXTENSIONS);
	}
	
	
	
}


