/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.dto.AirMovimentDTO;
import com.core.behavior.model.Ticket;
import com.core.behavior.properties.AnaliticsProperties;
import com.core.behavior.util.Utils;
import static com.core.behavior.util.Utils.mountBatchInsert;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class IntegrationService {

    @Autowired
    private AnaliticsProperties properties;

    @Autowired
    private JavaMailSender sender;

    private Connection connection;

    public void integrate(List<Ticket> tickets) throws Exception {

        
        try {

            this.move(tickets);
            this.callSpDataCollector();
            this.makeFile();

        } catch (Exception ex) {
            Logger.getLogger(IntegrationService.class.getName()).log(Level.SEVERE, "[ integrate ]", ex);            
        } finally {
            this.closeConnection();        
        }
    }

    private void move(List<Ticket> tickets) {

        List<AirMovimentDTO> airMovimentDTOs = tickets
                .parallelStream()
                .map(t -> new AirMovimentDTO(t))
                .collect(Collectors.toList());

        if (airMovimentDTOs.isEmpty()) {
            return;
        }

        this.openConnection();

        try {
            List<String> inserts = new ArrayList<>();
            int count = 0;
            for (AirMovimentDTO t : airMovimentDTOs) {
                inserts.add(mountBatchInsert(t, Utils.TypeField.AIR));
                count++;
                if (count == 3000) {
                    String query = "INSERT INTO `ltm_stage`.`AirMovimentDC` VALUES " + inserts.stream().collect(Collectors.joining(","));

                    Statement ps = this.connection.createStatement();
                    ps.clearBatch();
                    ps.addBatch(query);

                    ps.executeBatch();
                    this.connection.commit();
                    count = 0;
                    inserts.clear();
                }

            }

            String query = "INSERT INTO `ltm_stage`.`AirMovimentDC` VALUES " + inserts.stream().collect(Collectors.joining(","));
            Statement ps = this.connection.createStatement();
            ps.clearBatch();
            ps.addBatch(query);

            ps.executeBatch();
            this.connection.commit();
        } catch (Exception e) {
            Logger.getLogger(IntegrationService.class.getName()).log(Level.SEVERE, "[ move ]", e);
        } finally {
            this.closeConnection();
        }
    }

    private void callSpDataCollector() throws SQLException {

        try {
            this.openConnection();
            CallableStatement c = this.connection.prepareCall("{call SP_DataCollector}");
            c.execute();
            this.connection.commit();
        } catch (Exception e) {
            Logger.getLogger(IntegrationService.class.getName()).log(Level.SEVERE, "[ callSpDataCollector ]", e);
        } finally {
            this.closeConnection();
        }

    }

    public void makeFile() throws SQLException, IOException {

           File file = null;
        
        try {
            this.openConnection();

            String query = "select * from `ltm_stage`.`LogErroDataCollector` where Processamento = '" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + "'";

            PreparedStatement st = this.connection.prepareStatement(query);

            ResultSet result = st.executeQuery();

            file = File.createTempFile("tempStatusProcessamento", ".csv");

            FileWriter writer = new FileWriter(file, false);
            writer.write("Data;Erro;Tipo\n");

            SimpleDateFormat format = new SimpleDateFormat("");

            while (result.next()) {
                String line = MessageFormat.format("{0};{1};{2}\n", Utils.formatDateSqlToString(result.getDate("Processamento")), result.getString("msg_erro"), result.getInt("type_err"));
                writer.write(line);
            }

            writer.flush();
            writer.close();

            file = Utils.zipFiles(file.getName(), 1L, Arrays.asList(file));

            this.sendEmail(file);

        } catch (Exception e) {
            Logger.getLogger(IntegrationService.class.getName()).log(Level.SEVERE, "[ makeFile ]", e);
        } finally {
            this.closeConnection();
            if (file != null) {
                FileUtils.forceDelete(file);
            }
        }

    }

    public void sendEmail(File attachment) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject("[ Data Collector ] - BackOffice");
        helper.setFrom("latamupload@behint.net.br");
        helper.setTo(new String[]{"marcelo.rosim@bandtec.com.br", "deniz.sanchez@behint.net.br"});
        helper.setText("Segue em anexos os erros na validação da procedure SP_DataCollector");
        helper.addAttachment("Evidencia.zip", attachment);
        sender.send(message);

    }

    public void closeConnection() {
        try {
            if (!this.connection.isClosed()) {
                this.connection.close();
            }

        } catch (Exception e) {
            Logger.getLogger(IntegrationService.class.getName()).log(Level.SEVERE, "[ closeConnection ]", e);
        }
    }

    public void openConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(properties.getUrl(), properties.getUser(), properties.getPassword());
            connection.setAutoCommit(false);

        } catch (Exception e) {
            Logger.getLogger(IntegrationService.class.getName()).log(Level.SEVERE, "[ openConnection ]", e);
        }
    }
}
