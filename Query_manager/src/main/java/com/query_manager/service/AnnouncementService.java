// package: com.query_manager.service
package com.query_manager.service;
import com.query_manager.entity.Announcement;
import com.query_manager.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepo;

    public Announcement saveAnnouncement(Announcement announcement) {
        return announcementRepo.save(announcement);
    }

//    public List<Announcement> getLatestAnnouncements() {
//        return announcementRepo.findTop5ByOrderByCreatedAtDesc();
//    }
    public void deleteAllAnnouncements() {
        announcementRepo.deleteAll();
    }
    public void deleteAnnouncement(Long id) {
        announcementRepo.deleteById(id);
    }


}
