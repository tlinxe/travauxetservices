package fr.travauxetservices.model;

import java.io.*;
import java.util.Properties;

/**
 * Created by Phobos on 23/02/15.
 */
public class Configuration {
    static private String PROPERTIES_DIR = "TES";
    static private String PROPERTIES_FILE = "server.properties";

    private String mailSmtpHost;
    private String mailSmtpAddress;
    private int mailSmtpPort;

    private String jdbcPlatform;
    private String jdbcDriver;
    private String jdbcUrl;
    private String jdbcUser;
    private String jdbcPassword;
    private String loggingLevel;
//    private String ddlGeneration;
//    private String ddlGenerationOutputMode;

    private boolean saved;

    public Configuration() {
        saved = false;
        load();
    }

    public boolean isSaved() {
        return saved;
    }

    public String getMailSmtpHost() {
        return mailSmtpHost;
    }

    public void setMailSmtpHost(String s) {
        this.mailSmtpHost = s;
    }

    public String getMailSmtpAddress() {
        return mailSmtpAddress;
    }

    public void setMailSmtpAddress(String s) {
        this.mailSmtpAddress = s;
    }

    public int getMailSmtpPort() {
        return mailSmtpPort;
    }

    public void setMailSmtpPort(int n) {
        this.mailSmtpPort = n;
    }

    public String getJdbcPlatform() {
        return jdbcPlatform;
    }

    public void setJdbcPlatform(String s) {
        this.jdbcPlatform = s;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String s) {
        this.jdbcDriver = s;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String s) {
        this.jdbcUrl = s;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public void setJdbcUser(String s) {
        this.jdbcUser = s;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public void setJdbcPassword(String s) {
        this.jdbcPassword = s;
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(String s) {
        this.loggingLevel = s;
    }

//    public String getDDLGeneration() {
//        return ddlGeneration;
//    }
//
//    public void setDDLGeneration(String s) {
//        this.ddlGeneration = s;
//    }
//
//    public String getDDLGenerationOutputMode() {
//        return ddlGenerationOutputMode;
//    }
//
//    public void setDDLGenerationOutputMode(String s) {
//        this.ddlGenerationOutputMode = s;
//    }

    public void load() {
        Properties props = new Properties();
        InputStream is = null;

        // First try loading from the current directory
        try {
            File dir = new File(getHomePath());
            File file = new File(dir, PROPERTIES_FILE);
            is = new FileInputStream(file);
        } catch (Exception e) {
            is = null;
        }

        try {
            if (is == null) {
                // Try loading from classpath
                is = getClass().getResourceAsStream(PROPERTIES_FILE);
            }

            // Try loading properties from the file (if found)
            props.load(is);
            saved = true;
        } catch (Exception e) {
            saved = false;
        }

        mailSmtpHost = props.getProperty("mail.smtp.host", "smtp.numericable.fr");
        mailSmtpAddress = props.getProperty("mail.smtp.address", "thierry.linxe@numericable.fr");
        mailSmtpPort = new Integer(props.getProperty("mail.smtp.port", "25"));

        jdbcPlatform = props.getProperty("eclipselink.jdbc.platform", "org.eclipse.persistence.platform.database.H2Platform");
        jdbcDriver = props.getProperty("eclipselink.jdbc.driver", "org.h2.Driver");
        jdbcUrl = props.getProperty("eclipselink.jdbc.url", "jdbc:h2:C:/Sgbd/H2/travauxetservices;AUTO_SERVER=TRUE");
        jdbcUser = props.getProperty("eclipselink.jdbc.user", "sa");
        jdbcPassword = props.getProperty("eclipselink.jdbc.password", "sa");
        loggingLevel = props.getProperty("eclipselink.logging.level", "FINE");
//        ddlGeneration = props.getProperty("eclipselink.ddl-generation", "drop-and-create-tables");
//        ddlGenerationOutputMode = props.getProperty("eclipselink.ddl-generation.output-mode", "database");
    }

    public Properties getProperties() {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", mailSmtpHost);
        props.setProperty("mail.smtp.address", mailSmtpAddress);
        props.setProperty("mail.smtp.port", "" + mailSmtpPort);
        props.setProperty("eclipselink.jdbc.platform", jdbcPlatform);
        props.setProperty("eclipselink.jdbc.driver", jdbcDriver);
        props.setProperty("eclipselink.jdbc.url", jdbcUrl);
        props.setProperty("eclipselink.jdbc.user", jdbcUser);
        props.setProperty("eclipselink.jdbc.password", jdbcPassword);
        props.setProperty("eclipselink.logging.level", loggingLevel);
//        props.setProperty("eclipselink.ddl-generatione", ddlGeneration);
//        props.setProperty("eclipselink.ddl-generation.output-mode", ddlGenerationOutputMode);
        return props;
    }

    public void save() {
        try {
            Properties props = getProperties();
            File dir = new File(getHomePath());
            File file = new File(dir, PROPERTIES_FILE);
            OutputStream out = new FileOutputStream(file);
            props.store(out, "This is an optional header comment string");
            saved = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Configuration getConfiguration() {
        return new Configuration();
    }

    public static String getHomePath() {
        return tryHomePath(System.getenv("LOCALAPPDATA") + File.separatorChar + PROPERTIES_DIR);
    }

    private static String tryHomePath(String path) {
        if (path == null || path.length() == 0) return null;

        File dir = new File(path);

        // if Windows, try to clean up the path
        // if Unix, do nothing (Unix separators are the default, and we probably don't want to canonicalize symlinks
        if ('\\' == File.separatorChar) {
            try {
                dir = dir.getCanonicalFile();
            } catch (IOException e) {
                System.err.printf("[OpenAGE] Cannot canonicalize path \"%s\".%n", path);
            }
        }

        String absPath = dir.getAbsolutePath();

        if (dir.exists() && !dir.isDirectory()) {
            throw new IllegalStateException("Not a directory: " + absPath);
        } else if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new IllegalStateException("Cannot create directory: " + absPath);
        }

        if (absPath.endsWith(File.separator)) {
            absPath = absPath.substring(0, absPath.length() - 1);
        }

        return absPath;
    }

    public static void main(String[] args) {
        String path = tryHomePath(System.getenv("LOCALAPPDATA"));
        System.out.println("path " + path);
    }
}
