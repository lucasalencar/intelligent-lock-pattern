package org.unioeste.ilp.models;

import java.util.List;
import org.unioeste.ilp.models.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table experiences.
 */
public class Experience {

    private Long id;
    private Boolean done;
    private Long user_id;
    private Long pattern_id;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ExperienceDao myDao;

    private User user;
    private Long user__resolvedKey;

    private Pattern pattern;
    private Long pattern__resolvedKey;

    private List<Attempt> attempts;

    public Experience() {
    }

    public Experience(Long id) {
        this.id = id;
    }

    public Experience(Long id, Boolean done, Long user_id, Long pattern_id) {
        this.id = id;
        this.done = done;
        this.user_id = user_id;
        this.pattern_id = pattern_id;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getExperienceDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getPattern_id() {
        return pattern_id;
    }

    public void setPattern_id(Long pattern_id) {
        this.pattern_id = pattern_id;
    }

    /** To-one relationship, resolved on first access. */
    public User getUser() {
        if (user__resolvedKey == null || !user__resolvedKey.equals(user_id)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            user = targetDao.load(user_id);
            user__resolvedKey = user_id;
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        user_id = user == null ? null : user.getId();
        user__resolvedKey = user_id;
    }

    /** To-one relationship, resolved on first access. */
    public Pattern getPattern() {
        if (pattern__resolvedKey == null || !pattern__resolvedKey.equals(pattern_id)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PatternDao targetDao = daoSession.getPatternDao();
            pattern = targetDao.load(pattern_id);
            pattern__resolvedKey = pattern_id;
        }
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
        pattern_id = pattern == null ? null : pattern.getId();
        pattern__resolvedKey = pattern_id;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public synchronized List<Attempt> getAttempts() {
        if (attempts == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AttemptDao targetDao = daoSession.getAttemptDao();
            attempts = targetDao._queryExperience_Attempts(id);
        }
        return attempts;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetAttempts() {
        attempts = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
