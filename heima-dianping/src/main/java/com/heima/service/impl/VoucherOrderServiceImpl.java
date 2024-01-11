package com.heima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.dto.Result;
import com.heima.entity.SeckillVoucher;
import com.heima.entity.VoucherOrder;
import com.heima.mapper.VoucherOrderMapper;
import com.heima.service.ISeckillVoucherService;
import com.heima.service.IVoucherOrderService;
import com.heima.utils.RedisIDWorker;
import com.heima.utils.UserHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private RedisIDWorker redisIDWorker;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static
    {
        SECKILL_SCRIPT=new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }
    @Override
    public Result seckillVoucher(Long voucherId) {
        Long userId=UserHolder.getUser().getId();
        //1.执行lua脚本
        Long result = stringRedisTemplate.execute(SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(),
                userId.toString());
        //2.判断结果是否为0
        int r=result.intValue();
        if(r!=0)
        {
            return Result.fail(r==1?"库存不足":"不能重复下单");
        }
        return null;


        //3.如果正确返回

        //1.查询优惠券
//        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
//        //2.判断秒杀是否开始
//        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
//            return Result.fail("尚未开始");
//        }
//        //3.判断是否结束
//        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
//            return Result.fail("秒杀结束");
//        }
//        //4.判断库存是否充足
//        if (voucher.getStock()<1) {
//            return Result.fail("库存不足");
//        }
//        Long userId=UserHolder.getUser().getId();
//        SimpleRedisLock simpleRedisLock=new SimpleRedisLock("order"+userId,stringRedisTemplate);
//        boolean isLock = simpleRedisLock.tryLock(10);
//        if(!isLock)
//        {
//            return Result.fail("一人最多一单");
//        }
//
//        IVoucherOrderService proxy;
//        try {
//            proxy = (IVoucherOrderService) AopContext.currentProxy();
//            return proxy.createVoucherOrder(voucherId, voucher);
//        } finally {
//            simpleRedisLock.unlock();
//        }

    }

    @Transactional
    public Result createVoucherOrder(Long voucherId, SeckillVoucher voucher) {
        Long userId=UserHolder.getUser().getId();
        int count=query().eq("user_id", userId.intValue()).eq("voucher_id", voucherId).count().intValue();
        if(count>0)
        {
            return Result.fail("仅能购买一单");
        }
        //5.扣减库存
        boolean success=seckillVoucherService.update()
                .setSql("stock=stock-1").eq("voucher_id", voucherId).eq("stock", voucher.getStock())
                .update();
        if(!success)
        {
            return Result.fail("库存不足");
        }
        //6.创建订单
        VoucherOrder voucherOrder=new VoucherOrder();
        long seckill_order_id = redisIDWorker.nextId("seckill_order");
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setId(seckill_order_id);
        voucherOrder.setUserId(UserHolder.getUser().getId());
        save(voucherOrder);
        return Result.ok(seckill_order_id);

    }
}
