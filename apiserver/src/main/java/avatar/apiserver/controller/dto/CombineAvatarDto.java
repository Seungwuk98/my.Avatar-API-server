package avatar.apiserver.controller.dto;

import lombok.Data;

@Data
public class CombineAvatarDto {
    private String dbId;
    private int bodyNum;
    private int hairNum;
}
