package avatar.apiserver.controller.dto;

import avatar.apiserver.domain.Sexuality;
import lombok.Data;

@Data
public class GetHairInfosDto {
    private Sexuality sexuality;
}
