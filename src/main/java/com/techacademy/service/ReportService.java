package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
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

        //登録重複チェック（表示中のレポートは除外）
        List<Report> listDate1 = reportRepository.findByEmployeeAndReportDateAndId(userDetail.getEmployee(),report.getReportDate(), report.getId());
//        System.out.println("listDate1.size() = " + listDate1.size());
        if (listDate1.size() == 1) {
            return ErrorKinds.CHECK_OK;
        }
//            List<Report> listDate = reportRepository.findByEmployeeAndReportDate(userDetail.getEmployee(),report.getReportDate());
//      System.out.println("listDate.size() = " + listDate.size());
//      System.out.println("size() = " + size());
//        if (listDate.size() != 0) {
//            return ErrorKinds.DATECHECK_ERROR;
//        }

        report.setEmployee(userDetail.getEmployee());

        report.setDeleteFlg(report.isDeleteFlg());

        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

//
//    // 従業員削除
//    @Transactional
//    public ErrorKinds delete(String code, UserDetail userDetail) {
//
//        // 自分を削除しようとした場合はエラーメッセージを表示
//        if (code.equals(userDetail.getEmployee().getCode())) {
//            return ErrorKinds.LOGINCHECK_ERROR;
//        }
//        Employee employee = findByCode(code);
//        LocalDateTime now = LocalDateTime.now();
//        employee.setUpdatedAt(now);
//        employee.setDeleteFlg(true);
//
//        return ErrorKinds.SUCCESS;
//    }
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
////    // 従業員コードで日報検索（）
//    public Report findByEmployeeReport(String EmployeeCode) {
//        // findByIdで検索
//        Optional<Report> option = reportRepository.findById(EmployeeCode);
//        // 取得できなかった場合はnullを返す
//        Report report = option.orElse(null);
//        return report;
//    }
//
//    // 従業員パスワードの半角英数字チェック処理
//    private boolean isHalfSizeCheckError(Employee employee) {
//
//        // 半角英数字チェック
//        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
//        Matcher matcher = pattern.matcher(employee.getPassword());
//        return !matcher.matches();
//    }
//
//    // 従業員パスワードの8文字～16文字チェック処理
//    public boolean isOutOfRangePassword(Employee employee) {
//
//        // 桁数チェック
//        int passwordLength = employee.getPassword().length();
//        return passwordLength < 8 || 16 < passwordLength;
//    }

}
