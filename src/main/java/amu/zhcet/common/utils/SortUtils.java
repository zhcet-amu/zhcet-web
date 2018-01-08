package amu.zhcet.common.utils;

import amu.zhcet.data.attendance.AttendanceUpload;
import amu.zhcet.data.course.registration.CourseRegistration;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

import java.util.List;

public class SortUtils {

    // Prevent instantiation of Util class
    private SortUtils() {}

    public static void sortAttendanceUpload(List<AttendanceUpload> attendanceUploads) {
        attendanceUploads.sort((att1, att2) ->
                ComparisonChain.start()
                        .compare(att1.getSection(), att2.getSection(), Ordering.natural().nullsFirst())
                        .compare(att1.getFaculty_no().substring(5), att2.getFaculty_no().substring(5))
                        .result()
        );
    }

    public static void sortCourseAttendance(List<CourseRegistration> courseRegistrations) {
        courseRegistrations.sort((att1, att2) ->
                ComparisonChain.start()
                        .compare(att1.getStudent().getSection(), att2.getStudent().getSection(), Ordering.natural().nullsFirst())
                        .compare(att1.getStudent().getFacultyNumber().substring(5), att2.getStudent().getFacultyNumber().substring(5))
                        .result()
        );
    }

}
