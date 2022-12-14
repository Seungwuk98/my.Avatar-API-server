package avatar.apiserver.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import avatar.apiserver.controller.apiresultitem.CombineAvatarApiResult;
import avatar.apiserver.controller.apiresultitem.GetAvatarApiResult;
import avatar.apiserver.controller.apiresultitem.GetToonifyApiResult;
import avatar.apiserver.controller.apiresultitem.InferenceApiResult;
import avatar.apiserver.controller.dto.CombineAvatarDto;
import avatar.apiserver.controller.dto.GetBodyInfosDto;
import avatar.apiserver.controller.dto.GetHairInfosDto;
import avatar.apiserver.controller.errorresult.BadRequestResult;
import avatar.apiserver.controller.errorresult.NotFoundErrorResult;
import avatar.apiserver.domain.BodyInfoItem;
import avatar.apiserver.domain.DbItem;
import avatar.apiserver.domain.HairInfoItem;
import avatar.apiserver.domain.Sexuality;
import avatar.apiserver.domain.StyleCodeItem;
import avatar.apiserver.inferenceserver.CombineAvatarResult;
import avatar.apiserver.inferenceserver.InferenceResult;
import avatar.apiserver.serivce.AvatarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequiredArgsConstructor
public class RestApiController {

    private final AvatarService avatarService;
    private static final String[] FILE_EXTENSION = {"jpg", "jpeg", "png", "bmp"};


    /**
     * 
     * @param file : ????????? ?????? ?????? ???
     * @return id : Db??? id???
     * @throws IllegalStateException
     * @throws IOException
     */ 
    @PostMapping("/inference")
    public InferenceApiResult inference(@RequestParam MultipartFile file,
                                          @RequestParam String styleCode,
                                          @RequestParam String sexuality
    ) throws IllegalStateException, IOException {
        log.info("preprocess called, file = {}", file);
        InferenceApiResult result = new InferenceApiResult();
        
        if (!file.isEmpty()) {
            // ????????? ???????????? ??????
            String filename = file.getOriginalFilename();
            int idx = filename.indexOf('.');
            if (idx < 0) {
                result.setResult(1);
                return result;
            } 
            String fe = filename.substring(idx+1); // ?????? ?????????

            if (Stream.of(FILE_EXTENSION).anyMatch(fe::equals)){
                // ????????? ????????? ???????????? ??????
                return avatarService.preprocessAndMakeHead(file, styleCode, result, Sexuality.valueOf(sexuality.toUpperCase()));
            } else {
                result.setResult(3);
            }
        } else {
            result.setResult(4);
        }
        return result;
    }

    @PostMapping("/combine-avatar")
    public CombineAvatarApiResult combineAvatar(@RequestBody CombineAvatarDto combineAvatarDto) {
        CombineAvatarApiResult result = new CombineAvatarApiResult();
        CombineAvatarResult combineAvatarResult = avatarService.makeFullAvatar(combineAvatarDto.getDbId(), combineAvatarDto.getBodyNum(), combineAvatarDto.getHairNum());
        result.setObjectUrl(combineAvatarResult.getObjectUrl());
        result.setResult(combineAvatarResult.getResult());
        return result;
    }


    /**
     * 
     * @param dbId : ????????? ID
     * @return
     */
    @GetMapping("/avatar")
    public GetAvatarApiResult getAvatar(@RequestParam String dbId) {
        GetAvatarApiResult result = new GetAvatarApiResult();
        try {
            String url = avatarService.getAvatar(dbId);
            result.setResult(0);
            result.setObjectUrl(url);
        } catch (RuntimeException e) { // TODO ???????????? ?????? ????????? ?????? ?????? // 400?????? ?????? Illegal~~
            result.setResult(1);
        }
        return result;
    }

    /**
     * 
     * @param dbId : ????????? ID
     * @return
     */
    @GetMapping("/toonify")
    public GetToonifyApiResult getToonify(@RequestParam String dbId) {
        GetToonifyApiResult result = new GetToonifyApiResult();
        try {
            String url = avatarService.getToonify(dbId);
            result.setResult(0);
            result.setImageUrl(url);
        } catch (RuntimeException e) { // TODO ???????????? ?????? ????????? ?????? ?????? 
            result.setResult(1);
        } // npe ????????? null??????
        return result;
    }


    @GetMapping("/bodys") 
    public List<BodyInfoItem> GetBodyInfos(@RequestParam String sexuality) {
        return avatarService.getBodyInfos(Sexuality.valueOf(sexuality.toUpperCase()));
    }

    @GetMapping("/hairs")
    public List<HairInfoItem> GetHairInfos(@RequestParam String sexuality) {
        return avatarService.getHairInfos(Sexuality.valueOf(sexuality.toUpperCase()));
    }

    @GetMapping("/sexuality")
    public Sexuality getSexualityApi(@RequestParam String dbId) {
        return avatarService.getItem(dbId).getSexuality();
    }
    
    @GetMapping("/style")
    public List<StyleCodeItem> styles() {
        return avatarService.getStyleInfos();
    }

    @ExceptionHandler({EmptyResultDataAccessException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public NotFoundErrorResult notFoundHandling() {
        NotFoundErrorResult notFoundErrorResult = new NotFoundErrorResult();
        notFoundErrorResult.setMessage("id??? ?????? ??? ????????????.");
        return notFoundErrorResult;
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BadRequestResult badParameter() {
        BadRequestResult badRequestResult = new BadRequestResult();
        badRequestResult.setMessage("?????? ??????????????? ???????????? ????????????.");
        return badRequestResult;
    }
}
