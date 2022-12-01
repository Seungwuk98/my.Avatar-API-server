package avatar.apiserver.repository;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import avatar.apiserver.domain.BodyInfoItem;
import avatar.apiserver.domain.DbItem;
import avatar.apiserver.domain.Sexuality;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
public class RepositoryTest {

    @Autowired
    private DatabaseRepository repository;

    @Autowired
    private NamedParameterJdbcTemplate template;

    @Test
    void save() {
        // given
        DbItem item = new DbItem();
        String id = "itemA";
        item.setId(id);
        item.setPhotoUrl("bvvasd");
        
        // when
        repository.save(item);

        // then
        // DbItem findItem = repository.findById(id);
        // log.info("equals = {}", item.equals(findItem));
        // log.info("item = {}", item);
        // log.info("findItem = {}", findItem);

        // Assertions.assertThat(item).isEqualTo(findItem);
    }

    
    @Test
    void getBodys() {
        List<BodyInfoItem> items = repository.findAllBodys(Sexuality.MALE);
        System.out.println(items);
    }


}
