package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */

    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 新增员工的操作
     * @param employee
     */
    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user, status) " +
            "values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    @AutoFill(value = OperationType.INSERT)  // 拦截到此方法进行公共字段的填充
    void insert(Employee employee);

    /**
     * 员工的分页调查操作
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据主键来动态修改
     * @param employee
     */
    @AutoFill(value = OperationType.UPDATE)  // 拦截到此方法进行公共字段的填充
    void update(Employee employee);

    /**
     * 根据id来调查员工信息
     * @return
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);

    @Delete("delete from employee where id = #{id}")
    void deleteById(Long id);
}
