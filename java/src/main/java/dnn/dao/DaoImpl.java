package dnn.dao;

import dnn.common.dto.BaseDto;
import dnn.common.utils.GenericsUtils;
import dnn.entity.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huanghuanlai on 16/9/3.
 */
public class DaoImpl<Entity extends BaseEntity, Dto extends BaseDto> implements IDao<Entity, Dto> {

    @Autowired
    protected MongoTemplate mongoTemplate;

    protected Class<Entity> clazz;

    public DaoImpl() {
        clazz = GenericsUtils.getSuperClassGenricType(this.getClass());
    }

    @Override
    public List<Entity> findAll() {
        return mongoTemplate.findAll(clazz);
    }

    @Override
    public List<Entity> findByPage(Dto dto) {
        Query query = new Query();
        query.skip(dto.getSkip());
        query.limit(dto.getLimit());
        long count = count();
        return mongoTemplate.find(query, clazz);
    }

    @Override
    public Long count() {
        return mongoTemplate.count(new Query(), clazz);
    }

    @Override
    public Entity findById(String id) {
        return mongoTemplate.findById(id, clazz);
    }

    @Override
    public void save(Entity entity) {
        mongoTemplate.save(entity);
    }

    @Override
    public void delete(String id) {
        mongoTemplate.remove(new Query(Criteria.where("id").is(id)), clazz);
    }

    @Override
    public void delete(Entity entity) {
        mongoTemplate.remove(entity);
    }

    @Override
    public void update(Entity entity) {
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(entity.getId())),
                Update.update(clazz.getSimpleName(), entity), clazz);
    }

    @Override
    public List<Entity> findByCondition(Map<String, Object> conditions) {
        Query query = new Query();
        if (null != conditions && conditions.size() > 0) {
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
            }
        }
        return mongoTemplate.find(query, clazz);
    }

    @Override
    public List<Entity> findByFuzzy(Map<String, Object> conditions) {
        Query query = new Query();
        if (null != conditions && conditions.size() > 0) {
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                query.addCriteria(Criteria.where(entry.getKey()).regex(entry.getValue().toString()));
            }
        }
        return mongoTemplate.find(query, clazz);
    }
}
