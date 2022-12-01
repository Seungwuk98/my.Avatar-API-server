package avatar.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Sexuality {
    MALE("male"),
    FEMALE("female");

    private String value;
    Sexuality(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    @JsonCreator
    public static Sexuality fromJson(String sexuality) {
        return valueOf(sexuality.toUpperCase());
    }
}
