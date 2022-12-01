package avatar.apiserver.inferenceserver.requestbody;

import lombok.Data;

@Data
public class CombineAvatarRequestBody {
    private String headUrl;
    private int bodyNum;
    private int hairNum;
}
