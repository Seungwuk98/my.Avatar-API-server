package avatar.apiserver.controller.apiresultitem;

import lombok.Data;

@Data
public class InferenceApiResult {
    private String dbId;
    private String headUrl;
    private String toonifyUrl;
    private int result;
}
