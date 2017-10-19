package in.ac.amu.zhcet.data.model.envers;


import in.ac.amu.zhcet.service.user.Auditor;
import org.hibernate.envers.RevisionListener;

public class UserRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        UserRevisionEntity userRevisionEntity = (UserRevisionEntity) revisionEntity;
        userRevisionEntity.setUsername(Auditor.getLoggedInUsername());
    }

}
