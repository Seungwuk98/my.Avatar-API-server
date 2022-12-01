package avatar.apiserver.inferenceserver.requestbody;

import lombok.Data;

@Data
public class InferenceHeadRequestBody {
    private String photoUrl;
    private String styleCode;
}
