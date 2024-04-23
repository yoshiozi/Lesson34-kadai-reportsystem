package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.EmployeeRepository;
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
//    // 従業員保存
    @Transactional
    public ErrorKinds save(Report report, Model model, @AuthenticationPrincipal UserDetail userDetail ) {

        // 日報（作成者＋作成日）重複チェック
        if (ReportRepository.findByEmployeeAndReportDate(userDetail.getEmployee(), report.getReportDate()) != null) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }
//
//    // 従業員更新保存
//    @Transactional
//    public ErrorKinds update(Employee employee, String code) {
//        Employee oldEmployee = findByCode(code);
//        if ("".equals(employee.getPassword())) {
//            employee.setPassword(oldEmployee.getPassword());
//        }
//        else {
//            // パスワードチェック
//            ErrorKinds result = employeePasswordCheck(employee);
//            if (ErrorKinds.CHECK_OK != result) {
//                return result;
//            }
//        }
//        employee.setDeleteFlg(oldEmployee.isDeleteFlg());
//
//        LocalDateTime now = LocalDateTime.now();
//        employee.setCreatedAt(oldEmployee.getCreatedAt());
//        employee.setUpdatedAt(now);
//
//        employeeRepository.save(employee);
//        return ErrorKinds.SUCCESS;
//    }
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
    // 従業員一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

//    // 従業員の日報検索
//    public Report findByReport() {
//        // findByIdで検索
//        Optional<Report> option = reportRepository.findByID();
//        // 取得できなかった場合はnullを返す
//        Report report = option.orElse(null);
//        return report;
//    }
//
//    // 従業員パスワードチェック
//    private ErrorKinds employeePasswordCheck(Employee employee) {
//
//        // 従業員パスワードの半角英数字チェック処理
//        if (isHalfSizeCheckError(employee)) {
//
//            return ErrorKinds.HALFSIZE_ERROR;
//        }
//
//        // 従業員パスワードの8文字～16文字チェック処理
//        if (isOutOfRangePassword(employee)) {
//
//            return ErrorKinds.RANGECHECK_ERROR;
//        }
//
//        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
//
//        return ErrorKinds.CHECK_OK;
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
