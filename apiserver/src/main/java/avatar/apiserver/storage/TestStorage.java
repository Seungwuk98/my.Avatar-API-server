package avatar.apiserver.storage;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestStorage implements Storage{

    @Value("${storage.file-dir}")
    private String FILE_DIR;

    @Override
    public String save(File file) {
        log.info("file = {}, filename = {}, Storage에 저장 완료", file, file.getName());
        String fulldir = FILE_DIR + "/" + file.getName();
        try {
            File dest = new File(fulldir);
            FileUtils.copyFile(file, dest);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO 적절한 예외 찾기
        }
        return "http://localhost:8080/file/" + file.getName();
    }

    @Override
    public File getFile(String url) {
        return new File(url);
    }

    @Override
    public String save(MultipartFile mfile) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
