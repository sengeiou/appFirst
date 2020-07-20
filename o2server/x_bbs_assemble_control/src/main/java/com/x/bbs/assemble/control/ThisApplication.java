package com.x.bbs.assemble.control;

import java.util.List;

import com.x.base.core.project.Context;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.queue.QueueNewReplyNotify;
import com.x.bbs.assemble.control.queue.QueueNewSubjectNotify;
import com.x.bbs.assemble.control.schedule.SubjectReplyTotalStatisticTask;
import com.x.bbs.assemble.control.schedule.SubjectTotalStatisticTask;
import com.x.bbs.assemble.control.schedule.UserSubjectReplyPermissionStatisticTask;
import com.x.bbs.assemble.control.service.BBSConfigSettingService;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ThisApplication {
	
	protected static Context context;
	public static final String BBSMANAGER = "BBSManager@CMSManagerSystemRole@R";
	public static QueueNewReplyNotify queueNewReplyNotify;
	public static QueueNewSubjectNotify queueNewSubjectNotify;

	public static Context context() {
		return context;
	}

	public static void init() throws Exception {
		try {
			initAllSystemConfig();
			queueNewReplyNotify = new QueueNewReplyNotify();
			queueNewSubjectNotify = new QueueNewSubjectNotify();

			MessageConnector.start(context());

			context().startQueue( queueNewReplyNotify );
			context().startQueue( queueNewSubjectNotify );

			context.schedule( SubjectTotalStatisticTask.class, "0 10 * * * ?");
			context.schedule( SubjectReplyTotalStatisticTask.class, "0 40 * * * ?");
			context.schedule( UserSubjectReplyPermissionStatisticTask.class, "0 0/30 * * * ?");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getRoleAndPermissionCacheKey( String personName ) {
		return "RoleAndPermission.withPerson." + personName;
	}
	
	private static void initAllSystemConfig() {
		BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
		BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
		BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
		BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
		BBSConfigSettingService configSettingService = new BBSConfigSettingService();
		List<BBSForumInfo> forumInfoList = null;
		List<BBSSectionInfo> sectionInfoList = null;

		try {
			configSettingService.initAllSystemConfig();
		} catch (Exception e) {
			LoggerFactory.getLogger(ThisApplication.class)
					.warn("BBS system check all config setting got an exception.");
			LoggerFactory.getLogger(ThisApplication.class).error(e);
		}

		try {
			forumInfoList = forumInfoServiceAdv.listAll();
			if (forumInfoList != null) {
				for (BBSForumInfo forumInfo : forumInfoList) {
					permissionInfoService.createForumPermission(forumInfo);
					roleInfoService.createForumRole( null, forumInfo);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(ThisApplication.class)
					.warn("BBS system check all forum permission and role got an exception.");
			LoggerFactory.getLogger(ThisApplication.class).error(e);
		}

		try {
			sectionInfoList = sectionInfoServiceAdv.listAll();
			if (sectionInfoList != null) {
				for (BBSSectionInfo sectionInfo : sectionInfoList) {
					permissionInfoService.createSectionPermission(sectionInfo);
					roleInfoService.createSectionRole( "System", sectionInfo);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(ThisApplication.class)
					.warn("BBS system check all section permission and role got an exception.");
			LoggerFactory.getLogger(ThisApplication.class).error(e);
		}
	}
	
	/**
	 * 判断用户是否有BBS系统管理权限
	 * 1、系统管理员Manager或者xadmin
	 * 2、拥有BBSManager角色的人员
	 * @param effectivePerson
	 * @return
	 */
	public static Boolean isBBSManager( EffectivePerson effectivePerson ) {
		UserManagerService userManagerService = new UserManagerService();
		try {
			if ( userManagerService.isHasPlatformRole( effectivePerson.getDistinguishedName(), ThisApplication.BBSMANAGER  )
				|| effectivePerson.isManager()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 判断用户是否有权限对指定的论坛进行管理
	 * 1、系统管理员、BBS管理员
	 * 2、指定论坛设置的管理员
	 * @param effectivePerson
	 * @param forumInfo
	 * @return
	 */
	public static Boolean isForumManager( EffectivePerson effectivePerson, BBSForumInfo forumInfo ) {
		if( isBBSManager( effectivePerson ) ) {
			return true;
		}
		if ( forumInfo != null && ListTools.isNotEmpty( forumInfo.getForumManagerList() ) ) {
			for (String name : forumInfo.getForumManagerList()) {
				if ( effectivePerson.getDistinguishedName().equals( name ) ) {
					return true;
				}
			}
		}
		return false;
	}
	
}
