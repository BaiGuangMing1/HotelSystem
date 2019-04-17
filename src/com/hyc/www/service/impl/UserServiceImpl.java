/*
 * Copyright (c) 2019.  黄钰朝
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hyc.www.service.impl;

import com.hyc.www.dao.inter.UserDao;
import com.hyc.www.po.User;
import com.hyc.www.service.constant.ServeConsts.Status;
import com.hyc.www.service.inter.UserService;
import com.hyc.www.util.BeanFactory;
import com.hyc.www.util.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.hyc.www.service.constant.ServeConsts.Status.*;
import static com.hyc.www.util.Md5Utils.getDigest;
import static com.hyc.www.util.ServiceUtils.*;
import static com.hyc.www.util.UUIDUtils.getUUID;

/**
 * @author <a href="mailto:kobe524348@gmail.com">黄钰朝</a>
 * @program XHotel
 * @description 负责用户相关的业务逻辑，包括用户注册登陆的功能
 * @date 2019-04-13 18:24
 */
public class UserServiceImpl implements UserService {

    private UserDao dao = (UserDao) BeanFactory.getBean(BeanFactory.DaoType.UserDao);

    /**
     * 负责用户的注册功能
     *
     * @name regist
     * @notice none
     * @author <a href="mailto:kobe524348@gmail.com">黄钰朝</a>
     * @date 2019/4/13
     */
    @Override
    public Status regist(HttpServletRequest req, HttpServletResponse resp) {
        User user = (User) BeanUtils.toObject(req.getParameterMap(), User.class);
        //TODO
        System.out.println(user.getUserName());
        if (dao.isExist(user.getUserName())) {
            return ACCOUNT_ALREADY_EXIST;
        }
        if (!isValidRegist(user)) {
            return DATA_ILLEGAL;
        }
        user.setPassword(getDigest(user.getPassword()));
        user.setId(getUUID());
        if (dao.addUser(user)) {
            return REGIST_SUCCESS;
        }
        return ERROR;
    }

    /**
     * 负责用户的登陆功能
     *
     * @name login
     * @author <a href="mailto:kobe524348@gmail.com">黄钰朝</a>
     * @date 2019/4/13
     */
    @Override
    public Status login(HttpServletRequest req, HttpServletResponse resp) {
        User user = (User) BeanUtils.toObject(req.getParameterMap(), User.class);
        if (!dao.isExist(user.getUserName())) {
            return ACCOUNT_NOT_FOUNT;
        }
        if (!dao.getPassword(user.getUserName()).equals(getDigest(user.getPassword()))) {
            return PASSWORD_INCORRECT;
        }
        return LOGIN_SUCCESS;
    }

    /**
     * 负责返回用户的个人信息
     *
     * @param req
     * @param resp
     * @name myInfo
     * @notice none
     * @author <a href="mailto:kobe524348@gmail.com">黄钰朝</a>
     * @date 2019/4/16
     */
    @Override
    public Status myInfo(HttpServletRequest req, HttpServletResponse resp) {
        User user = (User) BeanUtils.toObject(req.getParameterMap(), User.class);

        user = dao.getUser(user.getUserName());
        if (user == null) {
            return ACCOUNT_NOT_FOUNT;
        }
        //TODO 上传用户信息
        return SUCCESS;
    }

    /**
     * 负责更新用户信息的功能
     *
     * @name updateInfo
     * @notice none
     * @author <a href="mailto:kobe524348@gmail.com">黄钰朝</a>
     * @date 2019/4/13
     */
    @Override
    public Status updateInfo(HttpServletRequest req, HttpServletResponse resp) {
        User user = (User) BeanUtils.toObject(req.getParameterMap(), User.class);
        if (!isValidUserInfo(user)) {
            return DATA_ILLEGAL;
        }
        if (dao.update(user)) {
            return UPDATE_SUCCESS;
        }
        return ERROR;
    }

    /**
     * 负责用户更新登陆密码的功能
     *
     * @name updatePwd
     * @notice none
     * @author <a href="mailto:kobe524348@gmail.com">黄钰朝</a>
     * @date 2019/4/13
     */
    @Override
    public Status updatePwd(HttpServletRequest req, HttpServletResponse resp) {
        User user = (User) BeanUtils.toObject(req.getParameterMap(), User.class);
        String newPwd = req.getParameter("newPwd");

        if (!isValidPwd(newPwd)) {
            return DATA_ILLEGAL;
        }
        if (!dao.getPassword(user.getUserName()).equals(getDigest(user.getPassword()))) {
            return PASSWORD_INCORRECT;
        }
        user = dao.getUser(user.getUserName());
        if (user == null) {
            return ACCOUNT_NOT_FOUNT;
        }
        user.setPassword(getDigest(newPwd));
        if (dao.updateAll(user)) {
            return UPDATE_SUCCESS;
        }
        return ERROR;
    }

    /**
     * 负责用户更新支付密码的功能
     *
     * @name updatePayPwd
     * @notice none
     * @author <a href="mailto:kobe524348@gmail.com">黄钰朝</a>
     * @date 2019/4/13
     */
    @Override
    public Status updatePayPwd(HttpServletRequest req, HttpServletResponse resp) {
        User user = (User) BeanUtils.toObject(req.getParameterMap(), User.class);
        String newPwd = req.getParameter("newPwd");
        String oldPwd = user.getPayPwd();
        user = dao.getUser(user.getUserName());
        if (user == null) {
            return ACCOUNT_NOT_FOUNT;
        }
        if (!isValidPwd(newPwd)) {
            return DATA_ILLEGAL;
        }
        if (!user.getPayPwd().equals(getDigest(oldPwd))) {
            return PASSWORD_INCORRECT;
        }
        user.setPayPwd(getDigest(newPwd));
        if (dao.updateAll(user)) {
            return UPDATE_SUCCESS;
        }
        return ERROR;
    }


}