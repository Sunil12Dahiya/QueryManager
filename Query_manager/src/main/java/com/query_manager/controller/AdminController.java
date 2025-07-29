package com.query_manager.controller;

import com.query_manager.entity.StudentQuery;
import com.query_manager.service.StudentQueryiService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private StudentQueryiService queryService;

    // ✅ Main Admin View with filter & sort
    @GetMapping("/admin/studentquery")
    public String viewQueries(@RequestParam(required = false) String name,
                              @RequestParam(required = false) String batch,
                              @RequestParam(required = false) String section,
                              @RequestParam(required = false) String sort,
                              Model model) {

        // Convert empty strings to null
        name = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        batch = (batch != null && !batch.trim().isEmpty()) ? batch.trim() : null;
        section = (section != null && !section.trim().isEmpty()) ? section.trim() : null;
        sort = (sort != null && !sort.trim().isEmpty()) ? sort.trim() : null;

        List<StudentQuery> queries = queryService.getFilteredQueries(name, batch, section, sort);

        model.addAttribute("queries", queries);
        model.addAttribute("name", name);
        model.addAttribute("batch", batch);
        model.addAttribute("section", section);
        model.addAttribute("sort", sort);

        return "admin-queries";
    }

    // ✅ Update Query Status
    @PostMapping("/admin/query/updateStatus")
    public String updateStatus(@RequestParam("id") Long id,
                               @RequestParam("status") String status) {
        queryService.updateQueryStatus(id, status.trim().toUpperCase());
        return "redirect:/admin/studentquery";
    }

    // ✅ CSV Export Endpoint
    @GetMapping("/admin/studentquery/csv")
    public void downloadStudentQueryCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=student_queries.csv");

        List<StudentQuery> queries = queryService.getAllQueries();
        PrintWriter writer = response.getWriter();

        writer.println("Name,Enrollment,Batch,Section,Lecture Type,Lecture Name,Lecture Time,Description,Status,Submitted At");

        for (StudentQuery q : queries) {
            writer.println(String.join(",",
                q.getStudent().getName(),
                q.getStudent().getEnrollmentNumber(),
                q.getStudent().getBatch(),
                q.getStudent().getSection(),
                q.getType().toString(), // or .name() if it's an enum
                q.getLectureName(),
                q.getTime(),
                "\"" + q.getDescription().replace("\"", "'") + "\"", // avoid comma issues
                q.getStatus(),
                q.getSubmittedAt().toString()
            ));
        }

        writer.flush();
        writer.close();
    }

    // ✅ Excel Export Endpoint
    @GetMapping("/admin/studentquery/excel")
    public void downloadStudentQueryExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=student_queries.xlsx");

        List<StudentQuery> queries = queryService.getAllQueries();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Student Queries");

        String[] headers = {
            "Name", "Enrollment", "Batch", "Section",
            "Lecture Type", "Lecture Name", "Lecture Time",
            "Description", "Status", "Submitted At"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (StudentQuery q : queries) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(q.getStudent().getName());
            row.createCell(1).setCellValue(q.getStudent().getEnrollmentNumber());
            row.createCell(2).setCellValue(q.getStudent().getBatch());
            row.createCell(3).setCellValue(q.getStudent().getSection());
            row.createCell(4).setCellValue(q.getType().toString());
            row.createCell(5).setCellValue(q.getLectureName());
            row.createCell(6).setCellValue(q.getTime());
            row.createCell(7).setCellValue(q.getDescription());
            row.createCell(8).setCellValue(q.getStatus());
            row.createCell(9).setCellValue(q.getSubmittedAt().toString());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
