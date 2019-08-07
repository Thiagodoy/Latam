package com.core.behavior.services;

import com.core.behavior.dto.FileStatusDTO;
import com.core.behavior.model.Agency;
import com.core.behavior.model.File;
import com.core.behavior.model.FileProcessStatus;
import com.core.behavior.model.Ticket;
import com.core.behavior.repository.AgencyRepository;
import com.core.behavior.repository.FileProcessStatusRepository;
import com.core.behavior.repository.FileRepository;
import com.core.behavior.util.Utils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class FileProcessStatusService {

    @Autowired
    private FileProcessStatusRepository fileProcessStatusRepository;

    @Autowired
    private FileRepository fileRepository;
    
    @Autowired
    private AgencyRepository agencyRepository;

    public List<FileProcessStatus> getStatusFile(Long fileId) {
        return fileProcessStatusRepository.findByFileId(fileId);
    }

    @Transactional
    public void generateProcessStatus(Long fileId) {

        File file = fileRepository.findById(fileId).get();
        long layoutFile =  agencyRepository.findById(file.getCompany()).get().getLayoutFile();

        Map<String, FileProcessStatus> mapStatusMock = Arrays
                .asList(Ticket.class.getDeclaredFields())
                .stream() //FIXME: alterar isLayout quando for adotar o layout completo
                .filter((f) -> !f.getName().equalsIgnoreCase("created_at") && f.isAnnotationPresent(Column.class) && Utils.isLayoutMin(f.getName(), layoutFile))
                .map((ff) -> {
                    return new FileProcessStatus(fileId, ff.getName(), 0l, file.getQtdTotalLines(), 0d, 100d);
                })
                .collect(Collectors.toMap(FileProcessStatus::getFieldName, ss -> ss));

        //        custom
       // mapStatusMock.put("layout", new FileProcessStatus(fileId, "layout", 0l, file.getQtdTotalLines(), 0d, 100d));
        //mapStatusMock.put("generic", new FileProcessStatus(fileId, "generic", 0l, file.getQtdTotalLines(), 0d, 100d));

        List<FileStatusDTO> listStatus = this.fileProcessStatusRepository.getProcessStatus(fileId);

        if (listStatus.size() > 0) {
            listStatus.forEach(t -> {
                if (mapStatusMock.containsKey(t.getFieldName())) {

                    if (t.getQtdErrors() > 0) {
                        mapStatusMock.remove(t.getFieldName());
                        mapStatusMock.put(t.getFieldName(), new FileProcessStatus(fileId, t.getFieldName(), t.getQtdErrors(), file.getQtdTotalLines(), t.getPercentualError(), t.getPercentualHit()));
                    }

                }
            });
        }

        if (mapStatusMock.containsKey("layout") && mapStatusMock.get("layout").getQtdErrors() > 0) {
            fileProcessStatusRepository.save(mapStatusMock.get("layout"));
        } else {

            mapStatusMock.forEach((key, fileStatus) -> {
                fileProcessStatusRepository.save(fileStatus);
            });

        }

    }

}
