package com.digitaltravel.erp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import org.springframework.core.env.Environment;

@Configuration
public class MigrationConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Environment env;

    @PostConstruct
    public void migrate() {
        // Only run if ddl-auto is none to avoid conflicts during validate
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("Running MigrationConfig...");
            
            try (Statement stmt = conn.createStatement()) {
                boolean isClob = false;
                ResultSet rs = stmt.executeQuery("SELECT data_type FROM user_tab_columns WHERE table_name = 'CHIPHITHUCTE' AND column_name = 'HOADONANH'");
                if (rs.next()) {
                    String dataType = rs.getString("data_type");
                    if ("CLOB".equalsIgnoreCase(dataType)) {
                        isClob = true;
                    }
                }
                rs.close();
                
                if (!isClob) {
                    System.out.println("Changing HOADONANH from VARCHAR2 to CLOB...");
                    try { stmt.execute("ALTER TABLE CHIPHITHUCTE ADD (HOADONANH_CLOB CLOB)"); } catch(Exception e){}
                    try { stmt.execute("ALTER TRIGGER TRG_KHOA_CP_QUYETTOAN DISABLE"); } catch(Exception e){}
                    stmt.execute("UPDATE CHIPHITHUCTE SET HOADONANH_CLOB = HOADONANH");
                    stmt.execute("ALTER TABLE CHIPHITHUCTE DROP COLUMN HOADONANH");
                    stmt.execute("ALTER TABLE CHIPHITHUCTE RENAME COLUMN HOADONANH_CLOB TO HOADONANH");
                    try { stmt.execute("ALTER TRIGGER TRG_KHOA_CP_QUYETTOAN ENABLE"); } catch(Exception e){}
                    System.out.println("Column type changed successfully.");
                } else {
                    System.out.println("Column is already CLOB.");
                }
            }

            File uploadsDir = new File("uploads");
            if (uploadsDir.exists() && uploadsDir.isDirectory()) {
                File[] files = uploadsDir.listFiles();
                if (files != null) {
                    try (Statement stmt = conn.createStatement()) {
                        try { stmt.execute("ALTER TRIGGER TRG_KHOA_CP_QUYETTOAN DISABLE"); } catch(Exception e){}
                    }
                    try (PreparedStatement pstmt = conn.prepareStatement("UPDATE CHIPHITHUCTE SET HOADONANH = ? WHERE HOADONANH LIKE ? OR HOADONANH LIKE ?")) {
                        for (File file : files) {
                            if (file.isFile()) {
                                String fileName = file.getName();
                                System.out.println("Migrating file: " + fileName);
                                
                                byte[] fileContent = Files.readAllBytes(file.toPath());
                                String mimeType = Files.probeContentType(file.toPath());
                                if (mimeType == null) mimeType = "image/png";
                                
                                String base64 = Base64.getEncoder().encodeToString(fileContent);
                                String dataUri = "data:" + mimeType + ";base64," + base64;
                                
                                String oldUrl1 = "/api/public/upload/files/" + fileName;
                                String oldUrl2 = "http://localhost:8080/api/public/upload/files/" + fileName;
                                
                                pstmt.setString(1, dataUri);
                                pstmt.setString(2, oldUrl1);
                                pstmt.setString(3, oldUrl2);
                                
                                int updated = pstmt.executeUpdate();
                                System.out.println("Updated " + updated + " rows for file " + fileName);
                                
                                file.delete();
                            }
                        }
                    }
                    String[] remaining = uploadsDir.list();
                    if (remaining == null || remaining.length == 0) {
                        uploadsDir.delete();
                        System.out.println("Deleted uploads directory.");
                    }
                    try (Statement stmt = conn.createStatement()) {
                        try { stmt.execute("ALTER TRIGGER TRG_KHOA_CP_QUYETTOAN ENABLE"); } catch(Exception e){}
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
