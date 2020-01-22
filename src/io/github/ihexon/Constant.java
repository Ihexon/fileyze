package io.github.ihexon;

import com.sun.imageio.plugins.common.I18N;
import io.github.ihexon.utils.FileCopier;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Locale;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constant {
	public static final String PROGRAM_NAME = "WatchMe";
	public static final String DEV_TEAM = "ZZH AND YYF's BUG TEAM";
	private static final String DEV_VERSION = "Dev Build";
	public static final String PROGRAM_VERSION = DEV_VERSION;
	public static String PROGRAM_TITLE = PROGRAM_NAME + " " + PROGRAM_VERSION;

	public static final String FILE_CONFIG_NAME = "config.xml";
	public String FILE_CONFIG = FILE_CONFIG_NAME;

	public static final String FOLDER_SESSION_DEFAULT = "session";
	public  static String FOLDER_LOGS = "logs";
	public String FOLDER_SESSION = FOLDER_SESSION_DEFAULT;

	// You can write a file contain a dir list which WatchMe need to minor or exclude,
	// the list file store in LIST_CUSTOM_DIR
	public String LIST_DIR = "List";
	public String LIST_CUSTOM_DIR = LIST_DIR;

	public static final String WATCHER_USER_LOG = "watcher.user.log";

	// the real initialize configure file in ${SRC}/xml/config
	// the ${SRC}/resources/config.xml is a fallback configure file, and DO NOT USE in normal
	private static final String PATH_BUNDLED_CONFIG_XML =
			"/io/github/ihexon/resources/" + FILE_CONFIG_NAME;

	private static Constant instance = null;
	// ConfigHome is the dir which WatchMe to  read/write runtime configure files
	// But now we did not need to read/write any configure,so set this field to null
	private static String ConfigHome = null;

	public static I18N messages = null;


	/**
	 * Flag that indicates whether or not the "dev mode" is enabled.
	 *
	 * @see #isDevMode()
	 */
	private static boolean devMode = true;


	public static Constant getInstance() {
		if (instance == null) {
			// Changed to use the method createInstance().
			createInstance();
		}
		return instance;
	}



	private static synchronized void createInstance() {
		if (instance == null) {
			instance = new Constant();
		}
	}

	public Constant() {
		// Initialize the configure dir and log dir
		initializeFilesAndDirectories();
	}

	private static Manifest getManifest() {
		String className = Constant.class.getSimpleName() + ".class";
		String classPath = Constant.class.getResource(className).toString();
		if (!classPath.startsWith("jar")) {
			// Class not from JAR
			return null;
		}
		String manifestPath =
				classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
		try {
			return new Manifest(new URL(manifestPath).openStream());
		} catch (Exception e) {
			// Ignore
			return null;
		}
	}

	private static String getVersionFromManifest() {
		Manifest manifest = getManifest();
		if (manifest != null) {
			Attributes attr = manifest.getMainAttributes();
			return attr.getValue("Implementation-Version");
		} else {
			return DEV_VERSION;
		}
	}


	private static String ConfigHomeStd = null;

	private static final Pattern patternLinux = Pattern.compile("linux", Pattern.CASE_INSENSITIVE);
	public static boolean isLinux() {
		String os_name = System.getProperty("os.name");
		Matcher matcher = patternLinux.matcher(os_name);
		return matcher.find();
	}

	private static final Pattern patternMacOsX = Pattern.compile("mac", Pattern.CASE_INSENSITIVE);
	public static boolean isMacOsX() {
		String os_name = System.getProperty("os.name");
		Matcher matcher = patternMacOsX.matcher(os_name);
		return matcher.find();
	}

	private static final Pattern patternWindows = Pattern.compile("window", Pattern.CASE_INSENSITIVE);
	public static boolean isWindows() {
		String os_name = System.getProperty("os.name");
		Matcher matcher = patternWindows.matcher(os_name);
		return matcher.find();
	}

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public static boolean isDevMode() {
		return devMode || isDevBuild();
	}

	/**
	 * Tells whether or not WatchMe is running from a dev build.
	 *
	 * @return {@code true} if it's a dev build, {@code false} otherwise.
	 * @see #isDevMode()
	 */
	public static boolean isDevBuild() {
		return isDevBuild(PROGRAM_VERSION);
	}
	public static boolean isDevBuild(String version) {
		return DEV_VERSION.equals(version);
	}

	public static String getDefaultHomeDirectory(boolean incDevOption) {
		if (ConfigHomeStd == null) {
			ConfigHomeStd = System.getProperty("user.home");
			if (ConfigHomeStd == null) {
				ConfigHomeStd = ".";
			}
			if (isLinux()) {
				// Linux: Hidden    configure directory in the user's home directory
				ConfigHomeStd += FILE_SEPARATOR + "." + PROGRAM_NAME;
			} else if (isMacOsX()) {
				// Mac Os X: Support for writing the configuration into the users Library
				// This pieces of code is not tested, I do not have MacBook, hope it works maybe :)
				ConfigHomeStd +=
						FILE_SEPARATOR
								+ "Library"
								+ FILE_SEPARATOR
								+ "Application Support" // ? ? ?
								+ FILE_SEPARATOR
								+ PROGRAM_NAME;
			} else {
				// Windows: Zap directory in the user's home directory
				ConfigHomeStd += FILE_SEPARATOR + PROGRAM_NAME;
			}
		}

		if (incDevOption) {
			if (isDevMode()) {
				// Default to a different home dir to prevent messing up full releases
				return ConfigHomeStd + "_D";
			}
		}
		return ConfigHomeStd;
	}

	public static String getConfigHome() {
		if (ConfigHome == null || ConfigHome.length() == 0) ConfigHome = "No configure dir";
		return ConfigHome;
	}

	/**
	 * Returns the absolute path for the given {@code directory}.
	 *
	 * <p>NOTE: <strong>the path is terminated with a separator.</p>
	 *
	 * @param directory the directory whose path will be made absolute
	 * @return the absolute path for the given {@code directory}, terminated with a separator
	 */
	private static String getAbsolutePath(String directory) {
		String realPath = Paths.get(directory).toAbsolutePath().toString();
		String separator = FileSystems.getDefault().getSeparator();
		if (!realPath.endsWith(separator)) {
			realPath += separator;
		}
		return realPath;
	}



	public void initializeFilesAndDirectories() {
		FileCopier copier = new FileCopier();
		File f = null;
		PROGRAM_TITLE = PROGRAM_NAME + " " + PROGRAM_VERSION;
		if (ConfigHome == null) {
			ConfigHome = getDefaultHomeDirectory(true);
		}
		ConfigHome = getAbsolutePath(ConfigHome);
		f = new File(ConfigHome);
		FILE_CONFIG = ConfigHome + FILE_CONFIG;

		FOLDER_SESSION = ConfigHome + FOLDER_SESSION;
		LIST_CUSTOM_DIR = ConfigHome + LIST_DIR;

		FOLDER_LOGS = ConfigHome + FOLDER_LOGS;

		try {
			System.setProperty(WATCHER_USER_LOG, ConfigHome);
			if (!f.isDirectory()) {
				if (f.exists()) {
					System.err.println("The home path is not a directory: " + ConfigHome);
					System.exit(1);
				}
				if (!f.mkdir()) {
					System.err.println("Unable to create home directory: " + ConfigHome);
					System.err.println("Is the path correct and there's write permission?");
					System.exit(1);
				}
			} else if (!f.canWrite()) {
				System.err.println("The home path is not writable: " + ConfigHome);
				System.exit(1);
			} else {
				Path installDir = Paths.get(getWatchMeInstall()).toRealPath();
				if (installDir.equals(Paths.get(ConfigHome).toRealPath())) {
					System.err.println(
							"The install dir should not be used as home dir: " + installDir);
					System.exit(1);
				}
			}

			f = new File(FILE_CONFIG);
			if (!f.isFile()) {
				this.copyDefaultConfigs(f, false);
			}

			f = new File(FOLDER_SESSION);
			if (!f.isDirectory()) {
				System.out.println("Creating directory " + FOLDER_SESSION);
				if (!f.mkdir()) {
					// ZAP: report failure to create directory
					System.out.println("Failed to create directory " + f.getAbsolutePath());
				}
			}

			f = new File(FOLDER_LOGS);
			if (!f.isDirectory()) {
				System.out.println("Creating directory " + FOLDER_LOGS);
				if (!f.mkdir()) {
					// ZAP: report failure to create directory
					System.out.println("Failed to create directory " + f.getAbsolutePath());
				}
			}

		}catch (Exception e){
			System.err.println("Unable to initialize home directory! " + e.getMessage());
			e.printStackTrace(System.err);
			System.exit(1);
		}

		String lang;
		Locale locale = Locale.ENGLISH;
		Locale.setDefault(locale);


	}

	public void copyDefaultConfigs(File f, boolean forceReset)
			throws IOException, ConfigurationException {
		FileCopier copier = new FileCopier();
		File oldf;
		if (isDevMode()) {
			// If dev build , the configure store in ~/.WatchMe_D
			oldf = new File(getDefaultHomeDirectory(true) + FILE_SEPARATOR + FILE_CONFIG_NAME);
		} else {
			// If release build , the configure store in ~/.WatchMe
			oldf = new File(getDefaultHomeDirectory(false) + FILE_SEPARATOR + FILE_CONFIG_NAME);
		}

		if (!forceReset
				&& oldf.exists()
				&& Paths.get(ConfigHome).equals(Paths.get(getDefaultHomeDirectory(true)))) {
			// Dont copy old configs if forcedReset or they've specified a non std directory
			System.out.println("Copying defaults from " + oldf.getAbsolutePath() + " to " + FILE_CONFIG);
			copier.copy(oldf, f);
			if (isDevMode()) {
			}
		} else {
			System.out.println("Copying default configuration to " + FILE_CONFIG);
			copyDefaultConfigFile();
		}
	}




	private void copyDefaultConfigFile() throws IOException {
		copyFileToHome(Paths.get(FILE_CONFIG), "xml/" + FILE_CONFIG_NAME, PATH_BUNDLED_CONFIG_XML);
	}

	private static String WatchMeInstall = null;


	public static String getWatchMeInstall() {
		if (WatchMeInstall == null) {
			String path = ".";
			Path localDir = Paths.get(path);
			try {
				Path sourceLocation =
							Paths.get(
									watchme.class
											.getProtectionDomain()
											.getCodeSource()
											.getLocation()
											.toURI());
					if (!Files.isDirectory(sourceLocation)) {
						sourceLocation = sourceLocation.getParent();
					}
					path = sourceLocation.toString();
			} catch (URISyntaxException e) {
					System.err.println(
							"Failed to determine the WatchMe installation dir: " + e.getMessage());
					path = localDir.toAbsolutePath().toString();
			}
			WatchMeInstall = getAbsolutePath(path);
			System.out.println("Defaulting WatchMe install dir to " + path);
		}
		return WatchMeInstall;
	}

	private static void copyFileToHome(
			Path targetFile, String sourceFilePath, String fallbackResource) throws IOException {
		Path defaultConfig = Paths.get(
				getWatchMeInstall(),
				sourceFilePath);
		if (Files.exists(defaultConfig)) {
			Files.copy(defaultConfig, targetFile, StandardCopyOption.REPLACE_EXISTING);
		} else {
			try (InputStream is = Constant.class.getResourceAsStream(fallbackResource)) {
				if (is == null) {
					throw new IOException("Bundled resource not found: " + fallbackResource);
				}
				Files.copy(is, targetFile, StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}
}