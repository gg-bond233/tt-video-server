package cn.ggbond.ttvideo.service.impl;

import cn.ggbond.ttvideo.constant.MessageConstant;
import cn.ggbond.ttvideo.enumeration.UserStatusEnum;
import cn.ggbond.ttvideo.mapper.UserMapper;
import cn.ggbond.ttvideo.model.dto.UserAddDTO;
import cn.ggbond.ttvideo.model.dto.UserDTO;
import cn.ggbond.ttvideo.model.dto.UserSearchDTO;
import cn.ggbond.ttvideo.model.entity.User;
import cn.ggbond.ttvideo.model.vo.UserManagementVO;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IAdminUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@CacheConfig(cacheNames = "userCache")
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<UserMapper, User> implements IAdminUserService {
    private final UserMapper userMapper;


    /**
     * 获取所有用户数量
     *
     * @return 用户数量
     */
    @Override
    public Result<Long> getAllUsersCount() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Long total = userMapper.selectCount(queryWrapper);
        return Result.success(total);
    }

    /**
     * 分页查询所有用户
     *
     * @param userSearchDTO 用户查询条件
     * @return 用户分页信息
     */
    @Override
    @Cacheable(key = "#userSearchDTO.pageNum + '-' + #userSearchDTO.pageSize + '-' +" +
            " #userSearchDTO.username + '-' + #userSearchDTO.phone + '-' + #userSearchDTO.userStatus")
    public Result<PageResult<UserManagementVO>> getAllUsers(UserSearchDTO userSearchDTO) {
        //分页查询
        Page<User> page = new Page<>(userSearchDTO.getPageNum(), userSearchDTO.getPageSize());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //构建查询条件
        if (userSearchDTO.getUsername() != null) {
            queryWrapper.like("username", userSearchDTO.getUsername());
        }
        if (userSearchDTO.getPhone() != null) {
            queryWrapper.like("phone", userSearchDTO.getPhone());
        }
        if (userSearchDTO.getUserStatus() != null) {
            queryWrapper.eq("userStatus", userSearchDTO.getUserStatus());
        }
        //倒序排序（最新创建的在前）
        queryWrapper.orderByDesc("create_time");
        IPage<User> userPage = userMapper.selectPage(page, queryWrapper);
        if(userPage.getRecords().size()==0){
            return Result.success(MessageConstant.DATA_NOT_FOUND, new PageResult<>(0L, null));
        }
        //转换为 UserManagementVO
        List<UserManagementVO> list = userPage.getRecords().stream().map(user -> {
            UserManagementVO userManagementVO = new UserManagementVO();
            BeanUtils.copyProperties(user, userManagementVO);
            return userManagementVO;
        }).toList();
        return Result.success(new PageResult<>(userPage.getTotal(), list));
    }


    /**
     * 添加用户
     *
     * @param userAddDTO 用户信息
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    public Result addUser(UserAddDTO userAddDTO) {
        //构建条件构造器
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userAddDTO.getUsername())
                .or()
                .eq("phone", userAddDTO.getPhone())
                .or()
                .eq("email", userAddDTO.getEmail());

        //查询用户是否已存在
        List<User> existingUsers = userMapper.selectList(queryWrapper);
        if(existingUsers!=null && !existingUsers.isEmpty()) {
            for (User user : existingUsers) {
                if (user.getUsername().equals(userAddDTO.getUsername())) {
                    return Result.error(MessageConstant.USERNAME + MessageConstant.ALREADY_EXISTS);
                }
                if (user.getPhone().equals(userAddDTO.getPhone())) {
                    return Result.error(MessageConstant.PHONE + MessageConstant.ALREADY_EXISTS);
                }
                if (user.getEmail().equals(userAddDTO.getEmail())) {
                    return Result.error(MessageConstant.EMAIL + MessageConstant.ALREADY_EXISTS);
                }
            }
        }
            //密码加密
            String password = DigestUtils.md5DigestAsHex(userAddDTO.getPassword().getBytes());
            User user = new User();
            user.setUsername(userAddDTO.getUsername()).setPassword(password).setPhone(userAddDTO.getPhone())
                    .setEmail(userAddDTO.getEmail()).setIntroduction(userAddDTO.getIntroduction())
                    .setCreateTime(LocalDateTime.now()).setUpdateTime(LocalDateTime.now());
        // 用户状态：0-启用，1-禁用
        if (userAddDTO.getUserStatus() == null) {
            user.setUserStatus(UserStatusEnum.ENABLE); // 默认启用
        } else {
            user.setUserStatus(userAddDTO.getUserStatus());
        }
            //插入新用户
        if (userMapper.insert(user) == 0) {
            return Result.error(MessageConstant.ADD + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.ADD + MessageConstant.SUCCESS);
    }

    /**
     * 更新用户
     *
     * @param userDTO 用户信息
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    public Result updateUser(UserDTO userDTO) {
        Long userId = userDTO.getUserId();

        //查询数据库用户信息
        User userByUsername = userMapper.selectOne(new QueryWrapper<User>().eq("username", userDTO.getUsername()));
        if (userByUsername!=null && !userByUsername.getUserId().equals(userId)){
            return Result.error(MessageConstant.USERNAME + MessageConstant.ALREADY_EXISTS);
        }

        User userByPhone = userMapper.selectOne(new QueryWrapper<User>().eq("phone", userDTO.getPhone()));
        if (userByPhone!=null && !userByPhone.getUserId().equals(userId)){
            return Result.error(MessageConstant.PHONE + MessageConstant.ALREADY_EXISTS);
        }
        User userByEmail = userMapper.selectOne(new QueryWrapper<User>().eq("email", userDTO.getEmail()));
        if (userByEmail!=null && !userByEmail.getUserId().equals(userId)){
            return Result.error(MessageConstant.EMAIL + MessageConstant.ALREADY_EXISTS);
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setUserId(userId);
        user.setUpdateTime(LocalDateTime.now());

        if (userMapper.updateById(user) == 0) {
            return Result.error(MessageConstant.UPDATE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS);
    }


    /**
     * 更新用户状态
     *
     * @param userId     用户id
     * @param userStatus 状态
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    public Result updateUserStatus(Long userId, Integer userStatus) {
        //类型转换
        UserStatusEnum statusEnum;
        if (userStatus==1) {
            statusEnum = UserStatusEnum.ENABLE;  //(前端代码1为启用，0为禁用)
        }else if (userStatus==0){
            statusEnum = UserStatusEnum.DISABLE;
        }else {
            return Result.error(MessageConstant.USER_STATUS_INVALID);
        }
        //设置用户状态
        User user=new User();
        user.setUserId(userId);
        user.setUserStatus(statusEnum);
        user.setUpdateTime(LocalDateTime.now());

        if (userMapper.updateById(user) == 0) {
            return Result.error(MessageConstant.UPDATE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS);
    }


    /**
     * 删除用户
     *
     * @param userId 用户id
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    public Result deleteUser(Long userId) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(MessageConstant.USER+MessageConstant.NOT_EXIST);
        }

        // 执行删除
        if (userMapper.deleteById(userId) == 0) {
            return Result.error(MessageConstant.DELETE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS);

    }


    /**
     * 批量删除用户
     *
     * @param userIds 用户id数组
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    public Result deleteUsers(List<Long> userIds) {
        // 1. 空列表校验
        if (userIds == null || userIds.isEmpty()) {
            return Result.error(MessageConstant.DATA_NOT_FOUND);
        }

        // 2. 检查是否存在
        Long existCount = userMapper.selectCount(new QueryWrapper<User>().in("id", userIds));
        if (existCount != userIds.size()) {
            return Result.error(MessageConstant.Delete_Error);
        }

        // 3. 执行删除
        if (userMapper.deleteByIds(userIds) == 0) {
            return Result.error(MessageConstant.DELETE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS);
    }


}
