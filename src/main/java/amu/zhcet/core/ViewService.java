package amu.zhcet.core;

import amu.zhcet.data.course.Course;
import amu.zhcet.data.user.Gender;
import com.google.common.base.Strings;
import org.springframework.stereotype.Service;

@Service
public class ViewService {

    public String getClassForCourse(Course course) {
        if (course.getSemester() == null)
            return "badge-secondary";

        switch (course.getSemester()) {
            case 3:
                return "badge-danger";
            case 4:
                return "badge-info";
            case 5:
                return "bg-pink";
            case 6:
                return "bg-orange";
            case 7:
                return "badge-primary";
            case 8:
                return "badge-success";
            default:
                return "badge-secondary";
        }
    }

    public String getClassForGender(Gender gender) {
        if (gender == null) return "";
        return gender.equals(Gender.M) ? "blue-dark" : "pink-dark";
    }

    public String getAvatarUrl(String url) {
        if (Strings.isNullOrEmpty(url))
            return "/img/account.svg";

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
