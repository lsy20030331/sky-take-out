package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    public static final Integer PENDING_PAYMENT = 1;  // 待付款
    public static final Integer PENDING_ORDER = 2;  // 待接单
    public static final Integer RECEIVED_ORDER = 3; // 已结单
    public static final Integer OUT_OF_DELIVERY = 4; // 派送中
    public static final Integer COMPLETED = 5;  // 已完成
    public static final Integer CANCELED = 6;  // 已取消
    public static final Integer REFUND = 7;  // 退款

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    /**
     * 用户下单业务
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 处理业务异常(地址簿为空, 购物车数据为空)
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        if(shoppingCarts == null && shoppingCarts.size() == 0){
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        // 向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);  // 对象的属性copy
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getDetail());

        orderMapper.insert(orders);
        // 向订单明细表插入n条数据
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        // 批量插入数据
        orderDetailMapper.insertBatch(orderDetails);

        // 用户下单后清空购物车数据
        shoppingCartMapper.deleteByuserId(userId);

        // 封装VO对象用于方法的返回
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();

        return orderSubmitVO;
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception{
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        /*JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );*/

        JSONObject jsonObject = new JSONObject();
        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 用户历史订单调查
     * @param pageNum, pageSize, status
     * @return
     */
    @Override
    public PageResult page(int pageNum, int pageSize, Integer status) {
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setStatus(status);
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());

        PageHelper.startPage(pageNum,pageSize);
        // Page类是List类的子类所以也是List类型
        Page<Orders> ordersPage = orderMapper.pageQuery(ordersPageQueryDTO);

        // 创建用于存储VO对象的List
        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (ordersPage != null && ordersPage.getTotal() > 0) {
            for (Orders orders : ordersPage) {  // 遍历Page<Orders> ordersPage
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                // 将订单的数据拷贝给VO对象
                BeanUtils.copyProperties(orders, orderVO);
                // 每次遍历出一条数据后就将其赋给VO对象
                orderVO.setOrderDetailList(orderDetails);
                // 将赋好值的VO传入list对象中
                list.add(orderVO);
            }
        }
        return new PageResult(ordersPage.getTotal(), list);
    }

    /**
     * 调查订单明细
     * @param id
     * @return
     */
    @Override
    public OrderVO query(Long id) {
        OrderVO orderVO = new OrderVO();

        Orders orders = orderMapper.getById(id);
        BeanUtils.copyProperties(orders, orderVO);

        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

        orderVO.setOrderDetailList(orderDetails);

        return orderVO;
    }

    /**
     * 用户取消订单
     * @param id
     */
    @Override
    public void cancel(Integer id) {
         int status = CANCELED;
        orderMapper.cancleByOrderid(id, status);
    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    public void agagin(Long id) {
        // 调查当前订单的菜品数据
        List<OrderDetail> list = orderDetailMapper.getByOrderId(id);
        Orders orders = orderMapper.getById(id);

        // 将当前订单的菜品数据插入购物车中
        for (OrderDetail orderDetail : list){
            ShoppingCart shoppingCart = ShoppingCart.builder()
                            .name(orderDetail.getName())
                            .image(orderDetail.getImage())
                            .userId(orders.getUserId())
                            .setmealId(orderDetail.getSetmealId())
                            .dishFlavor(orderDetail.getDishFlavor())
                            .dishId(orderDetail.getDishId())
                            .amount(orderDetail.getAmount())
                            .number(orderDetail.getNumber())
                            .createTime(LocalDateTime.now())
                            .build();

            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 管理员搜索订单
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList<>();

        if (page != null && page.size() > 0){
            // 开始遍历page 读取出每一条Orders数据
            for(Orders orders : page){
                Long orderId = orders.getId();
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
                // 创建VO对象
                OrderVO orderVO = new OrderVO();
                // 获取菜品信息
                String orderDishes = getOrderDishesStr(orders);
                // 使用对象拷贝将orders与OrderDetail拷贝到VO中
                BeanUtils.copyProperties(orders, orderVO);
                BeanUtils.copyProperties(orderDetails, orderVO);
                orderVO.setOrderDishes(orderDishes);

                list.add(orderVO);

            }
        }
        return new PageResult(page.getTotal(), list);
    }

    // 查找订单菜品信息
    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());  // 将map对象转化为List

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }

    /**
     * 调查各个状态订单数量
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        //待接单数量
        Integer toBeConfirmed = orderMapper.countStatus(PENDING_ORDER);
        //待派送数量
        Integer confirmed = orderMapper.countStatus(RECEIVED_ORDER);
        //派送中数量
        Integer deliveryInProgress = orderMapper.countStatus(OUT_OF_DELIVERY);

        OrderStatisticsVO orderStatisticsVO = OrderStatisticsVO.builder()
                .toBeConfirmed(toBeConfirmed)
                .confirmed(confirmed)
                .deliveryInProgress(deliveryInProgress)
                .build();

        return orderStatisticsVO;
    }

    /**
     * 商家接单
     * @param ordersConfirmDTO
     */
    @Override
    public void orderconfirm(OrdersConfirmDTO ordersConfirmDTO) {
        Long id = ordersConfirmDTO.getId();
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(RECEIVED_ORDER);
        orderMapper.update(orders);
    }

    /**
     * 商家拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void orderRejection(OrdersRejectionDTO ordersRejectionDTO) {
        Long id = ordersRejectionDTO.getId();

        Orders orders = Orders.builder()
                        .id(id)
                        .cancelTime(LocalDateTime.now())
                        .rejectionReason(ordersRejectionDTO.getRejectionReason())
                        .status(CANCELED)
                        .build();

        orderMapper.update(orders);
    }

    /**
     * 商家取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void orderCancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orders.setStatus(CANCELED);

        orderMapper.update(orders);
    }

    /**
     * 商家派送订单
     * @param id
     */
    @Override
    public void orderDeliver(Long id) {

        Orders orders = Orders.builder()
                .id(id)
                .status(OUT_OF_DELIVERY)
                .build();

        orderMapper.update(orders);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void orderFinish(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .status(COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 用户催单
     * @param id
     */
    @Override
    public void reminder(Long id) {
        return;
    }


}
