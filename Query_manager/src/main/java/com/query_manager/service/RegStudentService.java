package com.query_manager.service;

import com.query_manager.entity.RegStudent;
import com.query_manager.repository.RegStudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class RegStudentService {

    @Autowired
    private RegStudentRepository studentRepo;

    public void saveStudentsFromExcel(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            List<RegStudent> studentList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                RegStudent student = new RegStudent();
                student.setFullName(row.getCell(0).getStringCellValue());
                student.setEmail(row.getCell(1).getStringCellValue());
                student.setEnrollmentNo(row.getCell(2).getStringCellValue());
                student.setBatch(row.getCell(3).getStringCellValue());
                student.setSection(row.getCell(4).getStringCellValue());
                student.setPassword(row.getCell(5).getStringCellValue());  // Optional: hash if needed
                student.setActive(false); // or true depending on your logic

                studentList.add(student);
            }

            studentRepo.saveAll(studentList);

            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload: " + e.getMessage());
        }
    }
}
