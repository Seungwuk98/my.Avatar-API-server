package avatar.apiserver.preprocess;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebClientPreprocesser implements Preprocesser {

    private final WebClient client;

    WebClientPreprocesser() {
        client = WebClient.create("https://myavatar.co.kr/fast0");
    }

    @Override
    public PreprocessResult process(String photoUrl) {
        PreprocessResult result = new PreprocessResult();
        
        WebClientPreprocessorBody webClientPreprocessorBody = new WebClientPreprocessorBody();
        webClientPreprocessorBody.setPhotoUrl(photoUrl);

        try {
            WebClientPreprocessResult webClientPreprocessResult = client
                  .post()
                  .uri("/align_face")
                  .contentType(MediaType.APPLICATION_JSON)
                  .bodyValue(webClientPreprocessorBody)
                  .accept(MediaType.APPLICATION_JSON)
                  .retrieve()
                  .bodyToMono(WebClientPreprocessResult.class)
                  .block();
            result.setResult(0);
            result.setUrl(webClientPreprocessResult.getPhotoUrl());
        } catch (Exception e) {
            result.setResult(1);
        }
        return result;
    }
    
}
