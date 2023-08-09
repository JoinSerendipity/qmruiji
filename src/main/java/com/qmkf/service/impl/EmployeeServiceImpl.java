package com.qmkf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qmkf.dao.EmployeeDao;
import com.qmkf.domain.Employee;
import com.qmkf.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * Author：qm
 *
 * @Description：
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeDao, Employee> implements EmployeeService {
}
