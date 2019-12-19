package com.core.behavior.services;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.core.activiti.model.UserActiviti;
import com.core.activiti.repository.UserActivitiRepository;
import com.core.activiti.repository.UserInfoRepository;
import com.core.behavior.aws.client.ClientAws;
import com.core.behavior.dto.FileStatusProcessDTO;
import com.core.behavior.dto.LogStatusSinteticoDTO;
import com.core.behavior.dto.FileLinesApprovedDTO;
import com.core.behavior.exception.ApplicationException;
import com.core.behavior.jobs.ProcessFileJob;
import com.core.behavior.model.Agency;
import com.core.behavior.model.File;
import com.core.behavior.model.Notificacao;
import com.core.behavior.io.BeanIoReader;
import com.core.behavior.jobs.ProcessFileJob1;
import com.core.behavior.repository.FileProcessStatusRepository;

import com.core.behavior.repository.FileRepository;
import com.core.behavior.repository.LogRepository;
import com.core.behavior.sftp.ClientSftp;
import com.core.behavior.specifications.FileSpecification;
import com.core.behavior.util.LayoutEmailEnum;
import com.core.behavior.util.MessageCode;

import com.core.behavior.util.StatusEnum;
import com.core.behavior.util.Stream;
import com.core.behavior.util.ThreadPoolFileValidation;
import com.core.behavior.util.Utils;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserActivitiRepository userActivitiRepository;

    @Autowired
    private FileProcessStatusRepository fileProcessStatusRepository;

    @Autowired
    private FileProcessStatusService fileProcessStatusService;

    @Autowired
    private GroupMemberSevice groupMemberSevice;

    @Autowired
    private LogService logService;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private AgencyService agencyService;    

    @Autowired
    private ClientAws clientAws;

    @Autowired
    private ClientSftp clientSftp;

    @Autowired
    private BeanIoReader beanIoReader;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private ThreadPoolFileValidation threadPoolFileValidation;
    
    @Autowired
    private ApplicationContext context;

   

    public File findById(Long id) {
        return fileRepository.findById(id).get();
    }   
    
    public java.io.File downloadFile(String fileName, Long companyId, boolean  original) throws IOException {

        Agency agency = agencyService.findById(companyId);
        String folder = original ? agency.getS3Path().split("\\\\")[1] + "/ORIGINAL" : agency.getS3Path().split("\\\\")[1];

        return clientAws.downloadFile(fileName, folder);
    }

    public void persistFile(MultipartFile fileInput, String userId, Long id, boolean uploadAws, boolean uploadFtp, boolean processFile) throws Exception {

        java.io.File file = Utils.convertToFile(fileInput);

        if (Utils.isEmpty(file)) {
            FileUtils.forceDelete(file);
            throw new ApplicationException(MessageCode.FILE_EMPTY);
        }

        Agency agency = agencyService.findById(id);
        String folder = agency.getS3Path().split("\\\\")[1];
        Long layout = agency.getLayoutFile();

        if (uploadAws || uploadFtp) {
            String[] s = new String[1];
            s[0] = "COLLECTOR_UPLOADED";
            Page<File> result = this.list(file.getName(), null, new Long[]{id}, null, PageRequest.of(0, 10, Sort.by("createdDate").ascending()), s, null, null);

            if (!result.getContent().isEmpty()) {
                File fileTemp = result.getContent().get(0);
                this.deleteFileCascade(fileTemp);
            }

            this.persist(userId, id, file, StatusEnum.COLLECTOR_UPLOADED, 0, 0, 0L);
            this.uploadFile(uploadAws, uploadFtp, folder, file);

            this.criaNotificacaoDeUpload(agency);

            FileUtils.forceDelete(file);

        } else if (processFile) {
            String[] s = new String[5];
            s[0] = "VALIDATION_UPLOADED";
            s[1] = "VALIDATION_PROCESSING";
            s[2] = "VALIDATION_PARSE";
            s[3] = "VALIDATION_ERROR";
            s[4] = "VALIDATION_SUCCESS";
            
            
            final String pathOriginal = folder + "/ORIGINAL";
            this.uploadFile(true, false, pathOriginal, file);

            Page<File> result = this.list(file.getName(), null, new Long[]{id}, null, PageRequest.of(0, 100, Sort.by("createdDate").descending()), s, null, null);
            long versao = 1L;
            if (!result.getContent().isEmpty()) {
                File f = result.getContent().get(0);
                versao = f.getVersion().longValue() + 1;
            }

            if (!Optional.ofNullable(agency.getLayoutFile()).isPresent()) {
                throw new ApplicationException(MessageCode.FILE_LAYOUT_NOT_DEFINED);
            }

            Stream layoutHeader = agency.getLayoutFile().equals(1L) ? Stream.HEADER_LAYOUT_SHORT : Stream.HEADER_LAYOUT_FULL;

            //Valida o header do arquivo
            beanIoReader.headerIsValid(file, layoutHeader);

            com.core.behavior.model.File f = this.persist(userId, id, file, StatusEnum.VALIDATION_UPLOADED, 1, versao,1L);
            this.processFile(userId, id, file, layout, f.getId());
        }

    }

    private void criaNotificacaoDeUpload(Agency agency) {

        List<String> ids = userInfoRepository
                .findByKeyAndValue("agencia", String.valueOf(agency.getId()))
                .stream()
                .map(u -> u.getUserId())
                .collect(Collectors.toList());

        //Remove usuarios suporte behavior
        List<String> emails = groupMemberSevice.findById(ids)
                .stream().filter(g -> !g.getGroupId().equals("suporte behavior"))
                .map(gg -> gg.getUserId())
                .collect(Collectors.toList());

        //Notifica os usuários que estão na mesma agência
        emails.forEach(email -> {
            Optional<UserActiviti> u = userActivitiRepository.findById(email);
            if (u.isPresent()) {

                Map<String, String> parameter = new HashMap<String, String>();
                parameter.put(":agencia", Utils.replaceAccentToEntityHtml(agency.getName()));
                parameter.put(":email", u.get().getEmail());
                Notificacao notificacao = new Notificacao();
                notificacao.setLayout(LayoutEmailEnum.NOTIFICACAO_UPLOAD);
                notificacao.setParameters(Utils.mapToString(parameter));
                notificacaoService.save(notificacao);

            }
        });

    }

    private void uploadFile(boolean uploadAws, boolean uploadFtp, String folder, java.io.File file) throws IOException {

        if (uploadAws) {
            try {
                clientAws.uploadFile(file, folder);
            } catch (AmazonS3Exception e) {
                FileUtils.forceDelete(file);
                throw new ApplicationException(MessageCode.SERVER_ERROR_AWS);
            }
        }

        if (uploadFtp) {
            try {
                clientSftp.uploadFile(file, folder);
            } catch (Exception e) {
                FileUtils.forceDelete(file);
                throw new ApplicationException(MessageCode.SERVER_ERROR_SFTP);
            }
        }

    }

    private void processFile(String userId, Long id, java.io.File file, Long layout, Long fileId) throws SchedulerException {        
         
        ProcessFileJob processFileJob = context.getBean(ProcessFileJob.class);
        processFileJob.setParameter(ProcessFileJob.DATA_USER_ID, userId);
        processFileJob.setParameter(ProcessFileJob.DATA_COMPANY, id);
        processFileJob.setParameter(ProcessFileJob.DATA_FILE, file);
        processFileJob.setParameter(ProcessFileJob.DATA_FILE_ID, fileId);
        processFileJob.setParameter(ProcessFileJob.DATA_LAYOUT_FILE, layout);

        threadPoolFileValidation.getExecutor().submit(processFileJob);

    }

    public void deleteFileCascade(File file) {
        //ticketRepository.deleteByFileId(file.getId());
        logRepository.deleteByFileId(file.getId());
        fileProcessStatusRepository.deleteByFileId(file.getId());
        fileRepository.delete(file);
    }

    private com.core.behavior.model.File persist(String userId, Long id, java.io.File file, StatusEnum status, long stage, long versao, long original) throws IOException {
        com.core.behavior.model.File f = new com.core.behavior.model.File();
        f.setCompany(id);
        f.setName(file.getName());
        f.setUserId(userId);
        f.setStatus(status);
        f.setStage(stage);
        f.setCreatedDate(LocalDateTime.now());
        f.setVersion(versao);
        f.setOriginal(original);

        try {
            f = this.saveFile(f);
            return f;
        } catch (DataIntegrityViolationException e) {
            FileUtils.forceDelete(file);
            throw new ApplicationException(MessageCode.FILE_NAME_REPETED);
        }
    }

    @Transactional
    public com.core.behavior.model.File saveFile(com.core.behavior.model.File f) {
        return fileRepository.save(f);
    }

    public List<com.core.behavior.model.File> listFilesOfPending() {
        return fileRepository.findByStatus(StatusEnum.COLLECTOR_UPLOADED);
    }

    @Transactional
    public void update(com.core.behavior.model.File file) {
        this.fileRepository.save(file);
    }

    public List<LogStatusSinteticoDTO> generateLogStatusSintetico(Long file, String fieldName) {
        return this.logService.listLogSintetico(file, fieldName);
    }

    public StringBuilder generateFileErrors(Long idFile, boolean isCsv) {

        StringBuilder buffer = new StringBuilder();
        String header = Utils.layoutMin.stream().collect(Collectors.joining(";"));
        buffer.append("ERRO;" + header + "\n");
        
        return buffer;
    }

    @Transactional
    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }

    @Transactional
    public void setStatus(Long fileId, StatusEnum status) {
        File file = fileRepository.findById(fileId).get();
        file.setStatus(status);
        fileRepository.save(file);
    }

    @Transactional
    public void setStage(Long fileId, long s) {
        File file = fileRepository.findById(fileId).get();
        file.setStage(s);
        fileRepository.save(file);
    }

    @Transactional
    public void setExecutionTime(Long fileId, Long time) {
        File file = fileRepository.findById(fileId).get();
        file.setExecutionTime(time);
        fileRepository.save(file);
    }

    @Transactional
    public void setParseTime(Long fileId, Long time) {
        File file = fileRepository.findById(fileId).get();
        file.setParseTime(time);
        fileRepository.save(file);
    }

    @Transactional
    public void setPersistTime(Long fileId, Long time) {
        File file = fileRepository.findById(fileId).get();
        file.setPersistTime(time);
        fileRepository.save(file);
    }

    @Transactional
    public void setValidationTime(Long fileId, Long time) {
        File file = fileRepository.findById(fileId).get();
        file.setValidationTime(time);
        fileRepository.save(file);
    }

    public Page<File> list(String fileName, String userId, Long[] company, LocalDateTime createdAt, Pageable page, String[] status, Long start, Long end) {

        List<Specification<File>> predicates = new ArrayList<>();

        if (Optional.ofNullable(fileName).isPresent()) {
            predicates.add(FileSpecification.fileName(fileName));
        }

        if (company != null) {
            predicates.add(FileSpecification.company(Arrays.asList(company)));
        }

        if (status != null) {
            predicates.add(FileSpecification.status(Arrays.asList(status)));
        }

        if (start != null && end != null) {
            LocalDateTime lstart = LocalDateTime.ofInstant(Instant.ofEpochMilli(start), ZoneId.systemDefault());
            LocalDateTime lend = LocalDateTime.ofInstant(Instant.ofEpochMilli(end), ZoneId.systemDefault());
            predicates.add(FileSpecification.dateBetweem(lstart, lend));
        }

        Specification<File> specification = predicates.stream().reduce((a, b) -> a.and(b)).orElse(null);
        Page<File> fileResponse = fileRepository.findAll(specification, page);

        fileResponse.getContent().parallelStream().forEach(ff -> {
            ff.setStatusProcess(fileProcessStatusService.getStatusFile(ff.getId()));
        });

        return fileResponse;
    }

    public List<FileStatusProcessDTO> statusFilesProcess(Long id, Long start, Long end) {
        LocalDateTime lstart = LocalDateTime.ofInstant(Instant.ofEpochMilli(start), ZoneId.systemDefault());
        LocalDateTime lend = LocalDateTime.ofInstant(Instant.ofEpochMilli(end), ZoneId.systemDefault());
        return this.fileRepository.statusProcesss(id, lstart, lend);
    }

    public boolean hasFileToday(Long idAgency) {

        LocalDateTime init = LocalDate.now().atTime(LocalTime.MIN);
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return !fileRepository.findByCompanyAndCreatedDateBetween(idAgency, init, end).isEmpty();
    }

    public Optional<FileLinesApprovedDTO> fileInfo(Long id) {        
        return fileRepository.moveToAnalitics(id);
    }

}
