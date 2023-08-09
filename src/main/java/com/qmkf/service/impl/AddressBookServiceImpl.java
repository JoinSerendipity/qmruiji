package com.qmkf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qmkf.dao.AddressBookDao;
import com.qmkf.domain.AddressBook;
import com.qmkf.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * Author：qm
 *
 * @Description：
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao, AddressBook> implements AddressBookService {
}
