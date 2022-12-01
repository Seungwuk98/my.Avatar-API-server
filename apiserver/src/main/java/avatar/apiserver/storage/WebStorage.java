package avatar.apiserver.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebStorage implements Storage {

    private final WebClient client;
    
    @Value("${storage.API_KEY}")
    private String API_KEY;

    WebStorage() {
        client = WebClient.create("https://myavatar.co.kr/storage");
    }

    @Override
    public String save(File file) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(file));
        builder.part("apiKey", API_KEY);
        String url = client.post()
              .uri("/save")
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .body(BodyInserters.fromMultipartData(builder.build()))
              .retrieve()
              .bodyToMono(WebStorageSaveResult.class)
              .block()
              .getUrl();

        return url;
    }

    @Override
    public String save(MultipartFile mfile) {
        File file;
        try {
            file = new File(mfile.getOriginalFilename());
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(mfile.getBytes());
            fos.close();
        } catch (IOException e) {
            // TODO exception 만들기
            throw new RuntimeException(e);
        }
        return save(file);
    }

    @Override
    public File getFile(String url) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
