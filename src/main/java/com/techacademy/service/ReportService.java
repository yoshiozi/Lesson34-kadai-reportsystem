package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ReportService(ReportRepository reportRepository, PasswordEncoder passwordEncoder) {
        this.reportRepository = reportRepository;
        this.passwordEncoder = passwordEncoder;
    }

//
//    // 日報新規登録内容の保存
    @Transactional
    public ErrorKinds save(Report report, UserDetail userDetail) {

        //日報（作成者＋作成日）重複チェック
        List<Report> listDate = reportRepository.findByEmployeeAndReportDate(userDetail.getEmployee(),report.getReportDate());
//        System.out.println("listDate.size() = " + listDate.size());
        if (listDate.size() != 0) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        report.setEmployee(userDetail.getEmployee());

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報更新保存
    @Transactional
    public ErrorKinds update(Report report, String id, UserDetail userDetail) {

        //登録日重複チェック（表示中のレポートは除外）
        List<Report> listDate1 = reportRepository.findByEmployeeAndReportDateAndId(userDetail.getEmployee(),report.getReportDate(), report.getId());
//      System.out.println("listDate1.size() = " + listDate1.size());
      if (listDate1.size() == 1) {
          return reportSave(report, id, userDetail);
          }

    // 重複チェック
    ErrorKinds result = reportDateCheck(report, id, userDetail);
    if (ErrorKinds.CHECK_OK != result) {
        return result;
    }
        return ErrorKinds.SUCCESS;
    }

//
    // 日報削除
    @Transactional
    public ErrorKinds delete(String id) {

//        // 自分を削除しようとした場合はエラーメッセージを表示
//        if (id.equals(userDetail.getReport().getId())) {
//            return ErrorKinds.LOGINCHECK_ERROR;
//        }
        Report report = findByReport(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }
//
    // 登録日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

//    // 従業員の日報詳細検索
    public Report findByReport(String id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

//
//    // 対象従業員の日報リストを取得（）
    public List<Report> findByEmployee(Employee employee) {
        // findByIdで検索
        return reportRepository.findByEmployee(employee);
    }
//
//    // 登録日重複チェック
    public ErrorKinds reportDateCheck(Report report, String id, UserDetail userDetail) {
    List<Report> listDate = reportRepository.findByEmployeeAndReportDate(userDetail.getEmployee(),report.getReportDate());
//  System.out.println("listDate.size() = " + listDate.size());
//  System.out.println("size() = " + size());
    if (listDate.size() != 0) {
        return ErrorKinds.DATECHECK_ERROR;
    }
    return reportSave(report, id, userDetail);
    }
//
//        // レポート更新
 public ErrorKinds reportSave(Report report, String id, UserDetail userDetail) {
    Report report_before = reportRepository.findById(id).get();

    report.setEmployee(userDetail.getEmployee());

    report.setDeleteFlg(report_before.isDeleteFlg());

    LocalDateTime now = LocalDateTime.now();
    report.setCreatedAt(report_before.getCreatedAt());
    report.setUpdatedAt(now);

    reportRepository.save(report);
    return ErrorKinds.SUCCESS;
 }
//
//    // 従業員パスワードの8文字～16文字チェック処理
//    public boolean isOutOfRangePassword(Employee employee) {
//
//        // 桁数チェック
//        int passwordLength = employee.getPassword().length();
//        return passwordLength < 8 || 16 < passwordLength;
//    }

}
