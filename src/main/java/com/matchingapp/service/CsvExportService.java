package com.matchingapp.service;

import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Service
public class CsvExportService {

    public void writeUsersToCsv(PrintWriter writer, List<com.matchingapp.entity.User> users) {
        writer.append("ID,氏名,メールアドレス,役割\n");
        for (com.matchingapp.entity.User user : users) {
            writer.append(String.format("%d,%s,%s,%s\n",
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole()
            ));
        }
    }

    public void writeNurseriesToCsv(PrintWriter writer, List<com.matchingapp.entity.Nursery> nurseries) {
        writer.append("ID,園名,メールアドレス,所在地,保育方針\n");
        for (com.matchingapp.entity.Nursery nursery : nurseries) {
            writer.append(String.format("%d,%s,%s,%s,%s\n",
                    nursery.getId(),
                    nursery.getName(),
                    nursery.getEmail(),
                    nursery.getAddress() != null ? nursery.getAddress() : "",
                    nursery.getPhilosophy() != null ? nursery.getPhilosophy() : ""
            ));
        }
    }

    public void writeJobPostingsToCsv(PrintWriter writer, List<com.matchingapp.entity.JobPosting> jobPostings) {
        writer.append("ID,園名,タイトル,勤務地,給与,勤務時間,ステータス\n");
        for (com.matchingapp.entity.JobPosting jp : jobPostings) {
            writer.append(String.format("%d,%s,%s,%s,%d,%s,%s\n",
                    jp.getId(),
                    jp.getNursery().getName(),
                    jp.getTitle(),
                    jp.getArea() != null ? jp.getArea() : "",
                    jp.getSalary() != null ? jp.getSalary() : 0,
                    jp.getWorkTime() != null ? jp.getWorkTime() : "",
                    jp.getStatus()
            ));
        }
    }

    public void writeInterviewsToCsv(PrintWriter writer, List<com.matchingapp.entity.Interview> interviews) {
        writer.append("ID,保育園名,保育士名,面談希望日,ステータス,メッセージ\n");
        for (com.matchingapp.entity.Interview interview : interviews) {
            writer.append(String.format("%d,%s,%s,%s,%s,%s\n",
                    interview.getId(),
                    interview.getNursery().getName(),
                    interview.getUser().getName(),
                    interview.getInterviewDate() != null ? interview.getInterviewDate().toString() : "",
                    interview.getStatus(),
                    interview.getMessage() != null ? interview.getMessage() : ""
            ));
        }
    }

    public void writeStatisticsToCsv(PrintWriter writer, Map<String, Long> data, String title) {
        writer.append(title + "\n");
        data.forEach((key, value) -> writer.append(key + "," + value + "\n"));
        writer.append("\n");
    }
}
