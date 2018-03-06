package amu.zhcet.core;

import amu.zhcet.data.course.Course;
import amu.zhcet.data.user.Gender;
import com.google.common.base.Strings;
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

    public String getClassForGender(Gender gender) {
        if (gender == null) return "";
        return gender.equals(Gender.M) ? "blue-dark" : "pink-dark";
    }

    public String getAvatarUrl(String url) {
        if (Strings.isNullOrEmpty(url))
            return "https://zhcet-web-amu.firebaseapp.com/static/img/account.svg";

        return url;
    }

    public String getStatus(char status) {
        switch (status) {
            case 'G':
                return "Graduated";
            case 'N':
                return "Name Removed";
            default:
                return "Active";
        }
    }

}
