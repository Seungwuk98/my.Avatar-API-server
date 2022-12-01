package avatar.apiserver.serivce;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputFilter.Status;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import avatar.apiserver.controller.StatusConst;
import avatar.apiserver.controller.apiresultitem.InferenceApiResult;
import avatar.apiserver.domain.BodyInfoItem;
import avatar.apiserver.domain.DbItem;
import avatar.apiserver.domain.HairInfoItem;
import avatar.apiserver.domain.Sexuality;
import avatar.apiserver.domain.StyleCodeItem;
import avatar.apiserver.inferenceserver.CombineAvatarResult;
import avatar.apiserver.inferenceserver.InferenceServerInterface;
import avatar.apiserver.preprocess.PreprocessResult;
import avatar.apiserver.preprocess.Preprocesser;
import avatar.apiserver.repository.DatabaseRepository;
import avatar.apiserver.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {
    
    private final DatabaseRepository repository;
    private final Preprocesser preprocesser;
    private final InferenceServerInterface inferenceServerInterface;
    private final Storage storage;
    
    public DbItem saveItem(DbItem item) {
        DbItem savedItem = repository.save(item);
        return savedItem;
    }

    public DbItem getItem(String dbId) {
        return repository.findById(dbId);
    }

    public InferenceApiResult preprocessAndMakeHead(MultipartFile file, String styleCode, InferenceApiResult result, Sexuality sexuality) throws IllegalStateException, IOException {
        // db 아이디 생성 및 시간 지정
        String dbId = getId();
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        
        // file 풀 네임 설정
        String filename = dbId + '_' + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(now);
        log.info("filename = {}", filename);

        // Storage에 사진 파일 저장
        String photoUrl = storage.save(file);

        // 전처리
        PreprocessResult preprocessResult = preprocesser.process(photoUrl);

        // 전처리 실패 시 
        if (preprocessResult.getResult() != 0) {
            result.setResult(preprocessResult.getResult());
            return result;
        }

        // 전처리 성공 -> db row 생성
        DbItem item = new DbItem();
        item.setPhotoUrl(preprocessResult.getUrl());
        item.setPhotoUploadedAt(now);
        item.setId(dbId);
        item.setSexuality(sexuality);
        repository.save(item);

        // 대기열 생성
        repository.createOrder(dbId, photoUrl, styleCode);
        while (true) {
            String orderStatus = repository.getOrderStatus(dbId);
            if (orderStatus.equals("complete")) break;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        DbItem resulItem = getItem(dbId);

        result.setDbId(dbId);
        result.setHeadUrl(resulItem.getHeadUrl());
        result.setToonifyUrl(resulItem.getToonifyUrl());

        return result;

    }
    

    
    public CombineAvatarResult makeFullAvatar(String dbId, int bodyNum, int hairNum) {
        Optional<String> readCachingAvatar = repository.readCachingAvatar(dbId, bodyNum, hairNum);
        CombineAvatarResult result = new CombineAvatarResult();
        if (readCachingAvatar.isEmpty()) {
            DbItem item = getItem(dbId);
            String headUrl = item.getHeadUrl();
            result = inferenceServerInterface.combineAvatar(headUrl, bodyNum, hairNum);
            repository.writeCachingAvatar(dbId, bodyNum, hairNum, result.getObjectUrl());
            afterCombineAvatar(dbId, result.getObjectUrl());
        } else {
            afterCombineAvatar(dbId, readCachingAvatar.get());
            result.setObjectUrl(readCachingAvatar.get());
            result.setResult(0);
        }
        return result;
    }

    public String getAvatar(String dbId) {
        DbItem item = getItem(dbId);
        return item.getObjectUrl();
    }
    
    public String getToonify(String dbId) {
        DbItem item = getItem(dbId);
        return item.getToonifyUrl();
    }

    public List<BodyInfoItem> getBodyInfos(Sexuality sexuality) {
        return repository.findAllBodys(sexuality);
    }

    public List<HairInfoItem> getHairInfos(Sexuality sexuality) {
        return repository.findAllHairs(sexuality);
    }

    public List<StyleCodeItem> getStyleInfos() {
        return repository.findAllStyles();
    }

    private String getId() {
        while (true) {
            String uuid = UUID.randomUUID().toString().substring(0, 13);
            log.info("id length : {}", uuid);
            try {
                repository.findById(uuid);
            } catch (EmptyResultDataAccessException e) {
                return uuid;
            }
            log.info("중복 id 존재, 재 지정");
        }
    }


    private void afterCombineAvatar(String dbId, String objectUrl) {
        repository.updateStatus(dbId, StatusConst.AFTER_COMBINE);
        repository.updateObject(dbId, objectUrl);
    }
}
