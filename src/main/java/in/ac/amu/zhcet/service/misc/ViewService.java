package in.ac.amu.zhcet.service.misc;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.utils.Utils;
import org.springframework.stereotype.Service;

@Service
public class ViewService {

    public String getClassForCourse(Course course) {
        if (course.getSemester() == null)
            return "tag-default";

        switch (course.getSemester()) {
            case 3:
                return "tag-danger";
            case 4:
                return "tag-info";
            case 5:
                return "bg-pink";
            case 6:
                return "bg-orange";
            case 7:
                return "tag-primary";
            case 8:
                return "tag-success";
            default:
                return "tag-default";
        }
    }

    public String getAvatarUrl(String url) {
        if (Utils.isEmpty(url))
            return "https://zhcet-web-amu.firebaseapp.com/static/img/account.svg";

        return url;
    }

}
