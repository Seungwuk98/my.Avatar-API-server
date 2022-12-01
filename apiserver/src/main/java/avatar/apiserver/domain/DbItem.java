package avatar.apiserver.domain;

import java.sql.Timestamp;

import avatar.apiserver.controller.StatusConst;
import lombok.Data;

/**
 * DbItem
 */
@Data
public class DbItem {
    private String id;
    private Sexuality sexuality;
    private String photoUrl;
    private String toonifyUrl;
    private String headUrl;
    private String objectUrl;
    private StatusConst status;
    private Timestamp photoUploadedAt;
    private Timestamp headCreatedAt;
    private Timestamp toonifiedAt;
    private Timestamp objectCreatedAt;
    
}