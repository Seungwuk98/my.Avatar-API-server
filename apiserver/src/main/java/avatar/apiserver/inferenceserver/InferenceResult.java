package avatar.apiserver.inferenceserver;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class InferenceResult {
    private String toonifyUrl;
    private String headUrl;
    private int result;
}
