package avatar.apiserver.domain;

import lombok.Data;

@Data
public class StyleCodeItem {
    private String code;
    private String imgUrl;
    private String description;
    private boolean recommend;
}
