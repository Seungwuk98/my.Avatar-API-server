package avatar.apiserver.storage;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public interface Storage {
    String save(File file);
    String save(MultipartFile mfile);
    File getFile(String url);
}
