package com.pinyougou.user.service.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.dubbo.common.json.ParseException;
import com.pinyougou.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.pojo.TbUserExample;
import com.pinyougou.pojo.TbUserExample.Criteria;


import entity.PageResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {
		user.setCreated(new Date());//创建时间
		user.setUpdated(new Date());//修改时间
		String password = DigestUtils.md5Hex(user.getPassword());//密码用MD5加密
		user.setPassword(password);
		userMapper.insert(user);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){
		userMapper.updateByPrimaryKey(user);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		if(user!=null){			
						if(user.getUsername()!=null && user.getUsername().length()>0){
				criteria.andUsernameLike("%"+user.getUsername()+"%");
			}
			if(user.getPassword()!=null && user.getPassword().length()>0){
				criteria.andPasswordLike("%"+user.getPassword()+"%");
			}
			if(user.getPhone()!=null && user.getPhone().length()>0){
				criteria.andPhoneLike("%"+user.getPhone()+"%");
			}
			if(user.getEmail()!=null && user.getEmail().length()>0){
				criteria.andEmailLike("%"+user.getEmail()+"%");
			}
			if(user.getSourceType()!=null && user.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}
			if(user.getNickName()!=null && user.getNickName().length()>0){
				criteria.andNickNameLike("%"+user.getNickName()+"%");
			}
			if(user.getName()!=null && user.getName().length()>0){
				criteria.andNameLike("%"+user.getName()+"%");
			}
			if(user.getStatus()!=null && user.getStatus().length()>0){
				criteria.andStatusLike("%"+user.getStatus()+"%");
			}
			if(user.getHeadPic()!=null && user.getHeadPic().length()>0){
				criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}
			if(user.getQq()!=null && user.getQq().length()>0){
				criteria.andQqLike("%"+user.getQq()+"%");
			}
			if(user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0){
				criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}
			if(user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0){
				criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}
			if(user.getSex()!=null && user.getSex().length()>0){
				criteria.andSexLike("%"+user.getSex()+"%");
			}
	
		}
		
		Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


	/**
	 * 随机生成6位数 短信验证码
	 * @param phone
	 */
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private JmsTemplate jmsTemplate;//发送消息
	@Autowired
	private Destination smsDestination;//发送短信验证码

	@Value("${sign_name}")
	private String sign_name;//签名

	@Value("${template_code}")
	private String template_code;


	@Override
	public void creatSmsCode(final String phone) {

		//随机生成6位数，使用 RandomStringUtils 工具类
		//* 创建一个随机字符串，其长度是指定的字符数，将从数字字符集中选择字符。
		//* count:生成随机数的长度
		final String code = RandomStringUtils.randomNumeric(6);
		System.out.println("code:"+code);

		//将验证码 存储到redis中,使用字符串键值对存储
		redisTemplate.boundValueOps("smsCode").set(code);
		//发送到activeMQ
		jmsTemplate.send(smsDestination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();//获取要发送的信息对象
				//设置参数
				mapMessage.setString("mobile",phone);//电话
				mapMessage.setString("sign_name",sign_name);//签名
				mapMessage.setString("template_code",template_code);//模板编号
				//"{\"code\":\"123\"}"

				Map map=new HashMap();
				map.put("code",code);
				String s = JSON.toJSONString(map);

				mapMessage.setString("param",s);//参数 验证码

				return mapMessage;//返回消息
			}
		});


	}

	/**
	 * 判断验证码是否正确
	 * @param phone
	 * @param code
	 * @return
	 */
	@Override
	public Boolean checkSmsCode(String phone, String code) {
		//从redis中获取的验证码
		String smsCode = (String) redisTemplate.boundValueOps("smsCode").get();
		//判断验证码是否存在
		if (smsCode==null){//不存在
			return false;
		}
		//存在时，判断与页面传来的是否一样
		if (!code.equals(smsCode)){//不同
			return false;
		}
		//存在且相同
		return true;
	}
}
