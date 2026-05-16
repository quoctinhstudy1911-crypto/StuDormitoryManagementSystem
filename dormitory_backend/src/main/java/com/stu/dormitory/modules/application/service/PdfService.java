package com.stu.dormitory.modules.application.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import com.stu.dormitory.modules.application.entity.DormitoryApplication;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;

@Service
public class PdfService {

    public File generateApplicationPdf(
            DormitoryApplication application
    ) {

        try {

            String fileName =
                    application.getApplicationCode()
                            + ".pdf";

            File file =
                    new File(fileName);

            Document document =
                    new Document();

            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(file)
            );

            document.open();

            document.add(
                    new Paragraph(
                            "DORMITORY APPLICATION"
                    )
            );

            document.add(
                    new Paragraph(
                            "Application Code: "
                                    + application.getApplicationCode()
                    )
            );

            document.add(
                    new Paragraph(
                            "Full Name: "
                                    + application.getFullName()
                    )
            );

            document.add(
                    new Paragraph(
                            "CCCD: "
                                    + application.getCccd()
                    )
            );

            document.add(
                    new Paragraph(
                            "Email: "
                                    + application.getEmail()
                    )
            );

            document.add(
                    new Paragraph(
                            "Phone: "
                                    + application.getPhone()
                    )
            );

            document.close();

            return file;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Generate PDF failed"
            );
        }
    }
}