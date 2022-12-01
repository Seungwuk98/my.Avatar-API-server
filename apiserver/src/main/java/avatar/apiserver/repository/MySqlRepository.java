package avatar.apiserver.repository;

import java.lang.StackWalker.Option;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import avatar.apiserver.controller.StatusConst;
import avatar.apiserver.domain.BodyInfoItem;
import avatar.apiserver.domain.DbItem;
import avatar.apiserver.domain.GpuWorkingItem;
import avatar.apiserver.domain.HairInfoItem;
import avatar.apiserver.domain.Sexuality;
import avatar.apiserver.domain.StyleCodeItem;
import avatar.apiserver.domain.WorkqueueOrder;
import avatar.apiserver.repository.sql.SqlManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
 * MySqlRepository
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MySqlRepository implements DatabaseRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    @Transactional
    public DbItem save(DbItem item) {
        String sql = SqlManager.savePhotoUrl;
        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(item);
        param.registerSqlType("sexuality", Types.VARCHAR);
        log.info("param = {}", param);
        jdbcTemplate.update(sql, param);

        return item;
    }
    
    @Override
    public void updateStatus(String id, StatusConst status) {
        String sql = SqlManager.updateStatus;
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        param.put("status", status.ordinal());
        jdbcTemplate.update(sql, param);
    }
    
    @Override
    public void updateToonify(String id, String url) {
        String sql = SqlManager.updateToonify;
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        param.put("toonifyUrl", url);
        param.put("toonifiedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        jdbcTemplate.update(sql, param);
    }
    
    @Override
    public void updateObject(String id, String url) {
        String sql = SqlManager.updateObject;
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        param.put("objectUrl", url);
        param.put("objectCreatedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        jdbcTemplate.update(sql, param);
    }

    @Override
    public void updateHead(String id, String url) {
        String sql = SqlManager.updateHead;
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        param.put("headUrl", url);
        param.put("headCreatedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        jdbcTemplate.update(sql, param);
    }

    @Override
    public DbItem findById(String id) {
        String sql = SqlManager.findById;
        Map<String, String> param = Map.of("id", id);
        return jdbcTemplate.queryForObject(sql, param, dbItemRowMapper());
    }

    @Override
    public List<DbItem> findByStatus(StatusConst status) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<BodyInfoItem> findAllBodys(Sexuality sexuality) {
        Map<String, String> param = Map.of("sexuality", sexuality.getValue());
        return jdbcTemplate.query(SqlManager.findBodys, param, bodyInfoRowItemMapper());
    }

    @Override
    public List<HairInfoItem> findAllHairs(Sexuality sexuality) {
        Map<String, String> param = Map.of("sexuality", sexuality.getValue());
        return jdbcTemplate.query(SqlManager.findHairs, param, hairInfoRowMapper());
    }

    @Override
    public List<StyleCodeItem> findAllStyles() {
        return jdbcTemplate.query(SqlManager.findStyles, styleCodeItemRowMapper());
    }


    private RowMapper<DbItem> dbItemRowMapper() {
        return ((rs, rowNum) -> {
            DbItem item = new DbItem();
            item.setId(rs.getString("id"));
            item.setSexuality(Sexuality.valueOf(rs.getString("sexuality").toUpperCase()));
            item.setPhotoUrl(rs.getString("photo_url"));
            item.setHeadUrl(rs.getString("head_url"));
            item.setObjectUrl(rs.getString("object_url"));
            item.setToonifyUrl(rs.getString("toonify_url"));
            item.setHeadCreatedAt(rs.getTimestamp("head_created_at"));
            item.setPhotoUploadedAt(rs.getTimestamp("photo_uploaded_at"));
            item.setToonifiedAt(rs.getTimestamp("toonified_at"));
            item.setObjectCreatedAt(rs.getTimestamp("object_created_at"));
            item.setStatus(StatusConst.values()[rs.getInt("status")]);
            return item;
        });
    }

    private RowMapper<BodyInfoItem> bodyInfoRowItemMapper() {
        return ((rs, rowNum) -> {
            BodyInfoItem item = new BodyInfoItem();
            item.setNum(rs.getInt("num"));
            item.setFbxUrl(rs.getString("fbx_url"));
            item.setImgUrl(rs.getString("img_url"));
            return item;
        });
    }

    private RowMapper<HairInfoItem> hairInfoRowMapper() {
        return ((rs, rowNum) -> {
            HairInfoItem item = new HairInfoItem();
            item.setNum(rs.getInt("num"));
            item.setFbxUrl(rs.getString("fbx_url"));
            item.setImgUrl(rs.getString("img_url"));
            return item;
        });
    }

    private RowMapper<StyleCodeItem> styleCodeItemRowMapper() {
        return ((rs, rowNum) -> {
            StyleCodeItem item = new StyleCodeItem();
            item.setCode(rs.getString("code"));
            item.setDescription(rs.getString("description"));
            item.setImgUrl(rs.getString("img_url"));
            item.setRecommend(rs.getBoolean("recommend"));
            return item;
        });
    }

    @Override
    public Optional<WorkqueueOrder> getNextOrder() {
        List<WorkqueueOrder> query = jdbcTemplate.query(SqlManager.getNextOrder, (rs, rowNumber) -> {
            WorkqueueOrder order = new WorkqueueOrder();
            order.setId(rs.getString("id"));
            order.setPhotoUrl(rs.getString("photo_url"));
            order.setStyleCode(rs.getString("style_code"));
            return order;
        });
        if (query.size() == 0) return Optional.empty();
        return Optional.of(query.get(0));
    }

    @Override
    public List<GpuWorkingItem> getGPUStatus() {
        List<GpuWorkingItem> result = jdbcTemplate.query(SqlManager.checkGPUStatus, (rs, rowNumber) -> {
            GpuWorkingItem item = new GpuWorkingItem();
            item.setNum(rs.getInt("num"));
            item.setStatus(rs.getString("status"));
            return item;
        });
        return result;
    }

    @Override
    @Transactional
    public void setGPUStatus(Integer num, String status) {
        Map<String, Object> param = Map.of("status", status, "num", num);
        jdbcTemplate.update(SqlManager.updateGPUStatus, param);
    }

    @Override
    @Transactional
    public void createOrder(String id, String photoUrl, String styleCode) {
        Map<String, String> param = Map.of("id", id, "status", "waiting", "photo_url", photoUrl, "style_code", styleCode);        
        jdbcTemplate.update(SqlManager.createOrder, param);
    }

    @Override
    public String getOrderStatus(String id) {
        Map<String, String> param = Map.of("id", id);
        String queryForObject = jdbcTemplate.queryForObject(SqlManager.getOrderStatus, param, (rs, rowNum) -> {
            return rs.getString("status");
        });
        return queryForObject;
    }
    
    @Override
    @Transactional
    public void setOrderStatusWaiting(String id) {
        Map<String, Object> param = Map.of("id", id, "time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        jdbcTemplate.update(SqlManager.setOrderStatusWaiting, param);
    }
    @Override
    @Transactional
    public void setOrderStatusLoading(String id) {
        Map<String, Object> param = Map.of("id", id, "time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        jdbcTemplate.update(SqlManager.setOrderStatusLoading, param);
    }

    @Override
    public void setOrderStatusComplete(String id) {
        Map<String, Object> param = Map.of("id", id, "time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        jdbcTemplate.update(SqlManager.setOrderStatusComplete, param);
    }

    @Override
    public Optional<String> readCachingAvatar(String id, int bodyNum, int hairNum) {
        Map<String, Object> param = Map.of("id", id, "body_num", (Integer)bodyNum, "hair_num", (Integer)hairNum);
        List<String> result = jdbcTemplate.query(SqlManager.readCache, param, (rs, rowNum) -> {
            return rs.getString("object_url");
        });
        if (result.size() == 0) return Optional.empty();
        else return Optional.of(result.get(0));
    }

    @Override
    @Transactional
    public void writeCachingAvatar(String id, int bodyNum, int hairNum, String objectUrl) {
        Map<String, Object> param = Map.of("id", id, "body_num", (Integer)bodyNum, "hair_num", (Integer)hairNum, "object_url", objectUrl);
        jdbcTemplate.update(SqlManager.writeCache, param);
    }

    
}