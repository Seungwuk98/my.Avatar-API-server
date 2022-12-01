package avatar.apiserver.inferenceserver;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import avatar.apiserver.inferenceserver.requestbody.CombineAvatarRequestBody;
import avatar.apiserver.inferenceserver.requestbody.InferenceHeadRequestBody;
import lombok.Setter;


@Component
public class WebClientInferenceServerInterface implements InferenceServerInterface{

    private WebClient client;
    private final WebClient[] clients = new WebClient[2];

    public WebClientInferenceServerInterface() {
        clients[0] = WebClient.create("https://myavatar.co.kr/fast0");
        clients[1] = WebClient.create("https://myavatar.co.kr/fast1");
        client = clients[0];
    }

    @Override
    public InferenceResult inferenceHead(String imageUrl, String styleCode) {

        InferenceHeadRequestBody requestBody = new InferenceHeadRequestBody();
        requestBody.setPhotoUrl(imageUrl);
        requestBody.setStyleCode(styleCode);

        InferenceResult result = client.post() 
                                       .uri("/make-head")
                                       .contentType(MediaType.APPLICATION_JSON)   
                                       .bodyValue(requestBody)
                                       .accept(MediaType.APPLICATION_JSON)
                                       .retrieve()
                                       .bodyToMono(InferenceResult.class)
                                       .block();

        return result;
    }

    @Override
    public CombineAvatarResult combineAvatar(String headUrl, int bodyNum, int hairNum) {

        CombineAvatarRequestBody requestBody = new CombineAvatarRequestBody();
        requestBody.setHeadUrl(headUrl);
        requestBody.setBodyNum(bodyNum);
        requestBody.setHairNum(hairNum);

        CombineAvatarResult result = client.post()
                                           .uri("/combine-avatar")
                                           .contentType(MediaType.APPLICATION_JSON)
                                           .bodyValue(requestBody)
                                           .accept(MediaType.APPLICATION_JSON)
                                           .retrieve()
                                           .bodyToMono(CombineAvatarResult.class)
                                           .block();
        return result;
    }

    public void setClient(int num) {
        if (num < 0 || num > 2) throw new IllegalArgumentException("존재하지 않는 gpu입니다.");
        this.client = this.clients[num];
    }
    
}
