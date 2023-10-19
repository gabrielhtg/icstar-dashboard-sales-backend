package gabrielhtg.icstardashboardsalesbackend.repository;

import gabrielhtg.icstardashboardsalesbackend.entity.ExcelFile;
import gabrielhtg.icstardashboardsalesbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExcelFileRepository extends JpaRepository<ExcelFile, String> {
    List<ExcelFile> findAllByUserUploaderEmail(String email);

    List<ExcelFile> findAllByPipelineStatus(String pipelineStatus);

    List<ExcelFile> findAllByBusinessUnit (String businessUnit);

    void deleteAllByUserUploader(User userUploader);
}
