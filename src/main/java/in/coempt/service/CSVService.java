package in.coempt.service;

import org.springframework.web.multipart.MultipartFile;

public interface CSVService {
    public void saveCSV(MultipartFile file);

    void saveMappingCSV(MultipartFile file);
}
