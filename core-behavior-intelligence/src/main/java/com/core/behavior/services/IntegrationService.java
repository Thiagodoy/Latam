/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.dto.AirMovimentDTO;
import com.core.behavior.dto.FileIntegrationDTO;
import com.core.behavior.dto.TicketIntegrationDTO;
import com.core.behavior.exception.ApplicationException;
import com.core.behavior.io.BeanIoWriter;
import com.core.behavior.jobs.IntegrationJob;
import com.core.behavior.model.Ticket;
import com.core.behavior.properties.AnaliticsProperties;
import com.core.behavior.repository.FileRepository;
import com.core.behavior.repository.TicketRepository;
import com.core.behavior.util.StatusEnum;
import com.core.behavior.util.Stream;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TicketStatusEnum;
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
    private TicketRepository ticketRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private JavaMailSender sender;

    public void integrate(List<Ticket> tickets) throws Exception {

        Long id = tickets.stream().findFirst().get().getFileId();

        try {

            int total = tickets.size();

            this.move(tickets);
            this.callSpDataCollector();
            this.makeFileResultIntegration();

            if (total > 0) {
                this.makeFileResultDataCollector(id, total);
            }

        } catch (Exception ex) {
            Logger.getLogger(IntegrationService.class.getName()).log(Level.SEVERE, "[ integrate ]", ex);

            com.core.behavior.model.File f = this.fileRepository.findById(id).get();
            f.setStatus(StatusEnum.VALIDATION_ERROR);            
            this.fileRepository.save(f);            
            String fileName = f.getName();

            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject("[ Data Collector ] - Erro  na Integração");
            helper.setFrom("latamupload@behint.net.br");
            helper.setTo(new String[]{"deniz.sanchez@behint.net.br", "thiagodoy@hotmail.com", "marcelo.rosim@bandtec.com.br","paulo.baptista@behint.net.br","fernando.land@behint.net.br"});
            helper.setText("Não foi possivel realizar a integração do arquivo -> " + fileName + ".\n\n Favor entrar em contato com o responsável da aplicação!\nErro:\n" + ex.getMessage());

            sender.send(message);

        }
    }

    public void makeFileResultDataCollector(Long idfile, int total) {

        File tempFileCupom = null;
        File tempFileDuplicity = null;
        File fileDuplicity = null;
        File fileCupom = null;
        File zip = null;

        List<File> files = new ArrayList();
        long start = System.currentTimeMillis();

        try {
            List<Ticket> listticketDuplicity = this.ticketRepository.findByFileIdAndStatus(idfile, TicketStatusEnum.BACKOFFICE_DUPLICITY);
            List<Ticket> listticketCupom = this.ticketRepository.findByFileIdAndStatus(idfile, TicketStatusEnum.BACKOFFICE_CUPOM);

            if (listticketDuplicity.size() > 0) {
                FileIntegrationDTO fileIntegrationDTODuplicity = mountDto(listticketDuplicity);
                tempFileDuplicity = new File("Duplicity.csv");
                fileDuplicity = BeanIoWriter.writer(tempFileDuplicity, TicketLayoutEnum.FULL, fileIntegrationDTODuplicity, Stream.FULL_LAYOUT_INTEGRATION);
                files.add(fileDuplicity);
            }

            if (listticketCupom.size() > 0) {
                FileIntegrationDTO fileIntegrationDTOCupom = mountDto(listticketCupom);
                tempFileCupom = new File("Cupom.csv");
                fileCupom = BeanIoWriter.writer(tempFileCupom, TicketLayoutEnum.FULL, fileIntegrationDTOCupom, Stream.FULL_LAYOUT_INTEGRATION);
                files.add(fileCupom);
            }

            com.core.behavior.model.File file = fileRepository.findById(idfile).get();
            String nameFile = file.getName();

            if (!files.isEmpty()) {
                zip = Utils.zipFiles("Evidencia.zip", 1L, files);
            }

            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject("[ Data Collector ] - BackOffice (Cupom/Duplicidade)");
            helper.setFrom("latamupload@behint.net.br");
            helper.setTo(new String[]{"marcelo.rosim@bandtec.com.br", "deniz.sanchez@behint.net.br", "fernando.land@behint.net.br", "paulo.baptista@behint.net.br","thiagodoy@hotmail.com"});

            long qtdTotal = file.getQtdTotalLines();

            double percentualAprovado = (total / (double) qtdTotal) * 100;

            long erroValidacao = file.getRepeatedLine();
            String percentualValidação = Utils.formatDecimal((erroValidacao / (double) qtdTotal) * 100);

            long erroCupom = listticketCupom.size();
            String percentualErroCupom = Utils.formatDecimal((erroCupom / (double) qtdTotal) * 100);

            long erroDuplicidade = listticketDuplicity.size();
            String percentualErroDuplicidade = Utils.formatDecimal((erroDuplicidade / (double) qtdTotal) * 100);

            String mess = "Segue em anexos os tickets que não foram processados, por estarem com erros de cupom ou duplicidade.\n Referente ao arquivo : " + nameFile;

            mess += MessageFormat.format("\n\nResumo:\nTotal Arquivo : {0}\nTotal Aprovado : {1} ( {2} %)\nErro/Cupom : {3} ( {4} %)\nErro/Duplicidade : {5} ( {6} %)\nErro/Validação : {7} ( {8} %)",
                    qtdTotal, total, percentualAprovado, erroCupom, percentualErroCupom, erroDuplicidade, percentualErroDuplicidade, erroValidacao, percentualValidação);

            helper.setText(mess);

            if (zip != null) {
                helper.addAttachment("Evidencia.zip", zip);
            }

            sender.send(message);

        } catch (Exception e) {
            Logger.getLogger(IntegrationService.class.getName()).log(Level.SEVERE, "[ makeFileResultDataCollector ]", e);
        } finally {
            Utils.forceDeleteFile(zip);
            Utils.forceDeleteFile(fileCupom);
            Utils.forceDeleteFile(fileDuplicity);
            Utils.forceDeleteFile(tempFileCupom);
            Utils.forceDeleteFile(tempFileDuplicity);
            Logger.getLogger(IntegrationService.class.getName()).log(Level.INFO, "[ makeFileResultDataCollector ] -> Tempo" + ((System.currentTimeMillis() - start) / 1000) + " sec");
        }

    }

    private FileIntegrationDTO mountDto(List<Ticket> list) {

        FileIntegrationDTO fileIntegrationDTO = new FileIntegrationDTO();

        List<TicketIntegrationDTO> dtos = list.parallelStream()
                .map(t -> new TicketIntegrationDTO(t))
                .collect(Collectors.toList());

        fileIntegrationDTO.setIntegrationDTOs(dtos);
        return fileIntegrationDTO;

    }

    private void move(List<Ticket> tickets) throws Exception {

        long start = System.currentTimeMillis();
        Logger.getLogger(IntegrationJob.class.getName()).log(Level.INFO, " Inicializando integração");

        List<AirMovimentDTO> airMovimentDTOs = tickets
                .parallelStream()
                .map(t -> new AirMovimentDTO(t))
                .collect(Collectors.toList());

        if (airMovimentDTOs.isEmpty()) {
            return;
        }

        tickets.clear();
        List<AirMovimentDTO> collection = airMovimentDTOs;
        final Connection connection = this.getConnection();

        try {

            List<String> inserts = new ArrayList<>();
            int count = 0;
            while (!collection.isEmpty()) {
                inserts.add(mountBatchInsert(collection.remove(0), Utils.TypeField.AIR));
                count++;
                if (count == 3000) {
                    String query = "INSERT INTO `ltm_stage`.`AirMovimentDC` VALUES " + inserts.stream().collect(Collectors.joining(","));

                    Statement ps = connection.createStatement();
                    ps.clearBatch();
                    ps.addBatch(query);

                    ps.executeBatch();
                    connection.commit();
                    count = 0;
                    inserts.clear();
                }

            }

            String query = "INSERT INTO `ltm_stage`.`AirMovimentDC` VALUES " + inserts.stream().collect(Collectors.joining(","));
            Statement ps = connection.createStatement();
            ps.clearBatch();
            ps.addBatch(query);

            ps.executeBatch();
            connection.commit();

        } catch (Exception e) {
            Logger.getLogger(IntegrationService.class.getName()).log(Level.SEVERE, "[ move ]", e);
            throw new ApplicationException(0l, e.getMessage());
        } finally {
            this.closeConnection(connection);
            Logger.getLogger(IntegrationService.class.getName()).log(Level.INFO, "[ move ] -> Tempo" + ((System.currentTimeMillis() - start) / 1000) + " sec");
        }

    }

    private void callSpDataCollector() throws SQLException {

        Connection connection = null;
        long start = System.currentTimeMillis();
        try {
            connection = this.getConnection();
            CallableStatement c = connection.prepareCall("{call SP_DataCollector}");
            c.execute();
            connection.commit();
        } catch (Exception e) {
            Logger.getLogger(IntegrationService.class.getName()).log(Level.SEVERE, "[ callSpDataCollector ]", e);
        } finally {
            this.closeConnection(connection);
            Logger.getLogger(IntegrationService.class.getName()).log(Level.INFO, "[ callSpDataCollector ] -> Tempo" + ((System.currentTimeMillis() - start) / 1000) + " sec");
        }

    }

    public void makeFileResultIntegration() throws SQLException, IOException {

        File file = null;
        Connection connection = null;
        long start = System.currentTimeMillis();
        try {
            connection = this.getConnection();

            String query = "select * from `ltm_stage`.`LogErroDataCollector` where Processamento = '" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + "'";

            PreparedStatement st = connection.prepareStatement(query);

            ResultSet result = st.executeQuery();

            file = File.createTempFile("tempStatusProcessamento", ".csv");

            FileWriter writer = new FileWriter(file, false);
            writer.write("Data;Erro;Tipo\n");

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
            this.closeConnection(connection);
            Utils.forceDeleteFile(file);
            Logger.getLogger(IntegrationService.class.getName()).log(Level.INFO, "[ makeFileResultIntegration ] -> Tempo" + ((System.currentTimeMillis() - start) / 1000) + " sec");
        }

    }

    public void sendEmail(File attachment) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject("[ Data Collector ] - BackOffice");
        helper.setFrom("latamupload@behint.net.br");
        helper.setTo(new String[]{"marcelo.rosim@bandtec.com.br", "deniz.sanchez@behint.net.br", "fernando.land@behint.net.br", "paulo.baptista@behint.net.br"});
        helper.setText("Segue em anexos os erros na validação da procedure SP_DataCollector");
        helper.addAttachment("Evidencia.zip", attachment);
        sender.send(message);

    }

    public synchronized void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }

        } catch (Exception e) {
            Logger.getLogger(IntegrationService.class.getName()).log(Level.SEVERE, "[ closeConnection ]", e);
        }
    }

    public synchronized Connection getConnection() {

        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(properties.getUrl(), properties.getUser(), properties.getPassword());
            connection.setAutoCommit(false);

        } catch (Exception e) {
            Logger.getLogger(IntegrationService.class.getName()).log(Level.SEVERE, "[ openConnection ]", e);
        } finally {
            return connection;
        }
    }
}
