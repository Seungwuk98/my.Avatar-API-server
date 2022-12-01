package avatar.apiserver.repository;

import java.util.List;
import java.util.Optional;

import avatar.apiserver.controller.StatusConst;
import avatar.apiserver.domain.BodyInfoItem;
import avatar.apiserver.domain.DbItem;
import avatar.apiserver.domain.GpuWorkingItem;
import avatar.apiserver.domain.HairInfoItem;
import avatar.apiserver.domain.Sexuality;
import avatar.apiserver.domain.StyleCodeItem;
import avatar.apiserver.domain.WorkqueueOrder;

/**
 * DatabaseRepository
 */
public interface DatabaseRepository {
    DbItem save(DbItem item);
    void updateStatus(String id, StatusConst status);
    void updateToonify(String id, String url);
    void updateObject(String id, String url);
    void updateHead(String id, String url);
    void createOrder(String id, String photoUrl, String styleCode);
    Optional<WorkqueueOrder> getNextOrder();
    String getOrderStatus(String id);
    void setOrderStatusWaiting(String id);
    void setOrderStatusLoading(String id);
    void setOrderStatusComplete(String id);
    List<BodyInfoItem> findAllBodys(Sexuality sexuality);
    List<HairInfoItem> findAllHairs(Sexuality sexuality);
    List<StyleCodeItem> findAllStyles();
    DbItem findById(String id);
    List<DbItem> findByStatus(StatusConst status);
    List<GpuWorkingItem> getGPUStatus();
    void setGPUStatus(Integer num, String status);
    Optional<String> readCachingAvatar(String id, int bodyNum, int hairNum);
    void writeCachingAvatar(String id, int bodyNum, int hairNum, String objectUrl);
}