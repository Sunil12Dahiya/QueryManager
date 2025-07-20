// package: com.query_manager.controller
package com.query_manager.controller;

import com.query_manager.entity.Announcement;
import com.query_manager.repository.AnnouncementRepository;
import com.query_manager.service.AnnouncementService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/announcement")
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementRepository announcementRepository;

    // 📝 Show form to create a new announcement
    @GetMapping("/form")
    public String viewAnnouncementForm(Model model) {
        model.addAttribute("announcement", new Announcement());
        return "admin-announcement-form"; // This should exist: templates/admin-announcement-form.html
    }

    // ✅ Save a new announcement
    @PostMapping("/save")
    public String saveAnnouncement(@ModelAttribute Announcement announcement,
                                   RedirectAttributes redirectAttributes) {
        announcementService.saveAnnouncement(announcement);
        redirectAttributes.addFlashAttribute("message", "Announcement posted successfully!");
        return "redirect:/admin/announcement";
    }

    // 🗑 Delete an announcement
    @PostMapping("/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        announcementService.deleteAnnouncement(id);
        redirectAttributes.addFlashAttribute("message", "Announcement deleted successfully!");
        return "redirect:/admin/announcement";
    }

    // 📋 Show all announcements
    @GetMapping("")
    public String showAnnouncementPage(Model model) {
        List<Announcement> announcements = announcementRepository.findAll(); // or findAllByOrderByCreatedAtDesc()
        model.addAttribute("announcements", announcements);
        return "admin-announcement"; // This should exist: templates/admin-announcement.html
    }
}
