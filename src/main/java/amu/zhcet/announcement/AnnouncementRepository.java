package amu.zhcet.announcement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AnnouncementRepository extends PagingAndSortingRepository<Announcement, Long> {

    Page<Announcement> findBySenderUserId(String userId, Pageable Page);

}
