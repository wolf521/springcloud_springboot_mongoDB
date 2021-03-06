package com.issmart.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.issmart.entity.BoothFeedBackEntity;
import com.issmart.entity.MemberFeedBackEntity;
import com.issmart.entity.MemberInfoEntity;
import com.issmart.entity.MemberPressEntity;
import com.issmart.entity.MemberStickEntity;
import com.issmart.entity.MemberVisitEntity;
import com.issmart.entity.ResponseResult;
import com.issmart.service.MemberService;
import com.issmart.service.RecommendMemberService;
import com.issmart.service.RecommendService;
import com.issmart.util.StringUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("member/manager")
@EnableAsync
@Api(value = "用户信息", description = "用户信息", tags = { "2" })
public class MemberManagerController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private RecommendService recommendService;
	
	@Autowired
	private RecommendMemberService recommendMemberService;

	/**
	 * 新建用户信息
	 * 
	 * @return
	 */
	@ApiOperation(value = "新建用户信息")
	@RequestMapping(value = "insert/member/info", method = RequestMethod.POST)
	public @ResponseBody ResponseResult<MemberInfoEntity> insert(@RequestBody MemberInfoEntity dataSourceEntity) {
		ResponseResult<MemberInfoEntity> responseResult = new ResponseResult<MemberInfoEntity>();
		MemberInfoEntity resultData = memberService.insert(dataSourceEntity);
		recommendMemberService.opeRecommendCollection(dataSourceEntity.getUnitId(), dataSourceEntity.getBeaconMac());
		responseResult.setData(resultData);
		return responseResult;
	}

	/**
	 * 新建用户访问数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ApiOperation(value = "新建用户访问信息")
	@RequestMapping(value = "insert/member/visit", method = RequestMethod.POST)
	public @ResponseBody ResponseResult<Integer> insertVisit(@RequestBody Map<String, Object> paramMap) {
		ResponseResult<Integer> responseResult = new ResponseResult<Integer>();
		Map<String, Object> param = JSON.parseObject(paramMap.get("message").toString(), Map.class);
		if (param.get("rssi") != null && Integer.parseInt(param.get("rssi").toString()) > 80) {
			return null;
		}
		MemberVisitEntity memberVisitEntity = new MemberVisitEntity(Long.parseLong(param.get("timestamp").toString()),
				param.get("unit").toString(), param.get("beacon_mac").toString(), param.get("device_mac").toString(),
				Long.parseLong(param.get("window_start").toString()), Long.parseLong(param.get("window_end").toString()));
		memberService.insertVisit(memberVisitEntity);
		responseResult.setData(1);
		return responseResult;
	}
	
	/**
	 * 新建用户反馈数据
	 * 
	 * @return
	 */
	@ApiOperation(value = "新建用户反馈数据")
	@RequestMapping(value = "insert/member/feedback", method = RequestMethod.POST)
	public @ResponseBody ResponseResult<Integer> insertMemberFeedBack(@RequestBody MemberFeedBackEntity memberFeedBackEntity) {
		ResponseResult<Integer> responseResult = new ResponseResult<Integer>();
		memberService.insertMemberFeedBack(memberFeedBackEntity);
		if(StringUtil.ON.equals(memberFeedBackEntity.getRefreshType())) {
			recommendMemberService.updateRecommendCollection(memberFeedBackEntity.getUnitId(), memberFeedBackEntity.getBeaconMac());
		}
		responseResult.setData(1);
		return responseResult;
	}
	
	/**
	 * 新建展台反馈数据
	 * 
	 * @return
	 */
	@ApiOperation(value = "新建展台反馈数据")
	@RequestMapping(value = "insert/booth/feedback", method = RequestMethod.POST)
	public @ResponseBody ResponseResult<Integer> insertBoothFeedBack(@RequestBody BoothFeedBackEntity boothFeedBackEntity) {
		ResponseResult<Integer> responseResult = new ResponseResult<Integer>();
		memberService.insertBoothFeedBack(boothFeedBackEntity);
		if(StringUtil.ON.equals(boothFeedBackEntity.getRefreshType())) {
			recommendService.updateRecommendCollection(boothFeedBackEntity.getUnitId(), boothFeedBackEntity.getBeaconMac());
		}
		responseResult.setData(1);
		return responseResult;
	}
	
	/**
	 * 新建用户按一按数据
	 * 
	 * @return
	 */
	@ApiOperation(value = "新建用户按一按数据")
	@RequestMapping(value = "insert/member/press", method = RequestMethod.POST)
	public @ResponseBody ResponseResult<Integer> insertPress(@RequestBody MemberPressEntity memberPressEntity) {
		ResponseResult<Integer> responseResult = new ResponseResult<Integer>();
		memberService.insertPress(memberPressEntity);
		recommendService.updateRecommendCollection(memberPressEntity.getUnitId(), memberPressEntity.getBeaconMac());
		responseResult.setData(1);
		return responseResult;
	}
	
	/**
	 * 新建用户贴一贴数据
	 * 
	 * @return
	 */
	@ApiOperation(value = "新建用户贴一贴数据")
	@RequestMapping(value = "insert/member/stick", method = RequestMethod.POST)
	public @ResponseBody ResponseResult<Integer> insertStick(@RequestBody MemberStickEntity memberStickEntity) {
		ResponseResult<Integer> responseResult = new ResponseResult<Integer>();
		memberService.insertStick(memberStickEntity);
		responseResult.setData(1);
		return responseResult;
	}
}
