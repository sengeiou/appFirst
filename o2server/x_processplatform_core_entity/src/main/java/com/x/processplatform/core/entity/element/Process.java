package com.x.processplatform.core.entity.element;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.annotation.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.Process.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.Process.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Process extends SliceJpaObject {

	private static final long serialVersionUID = 3241184900530625402L;
	private static final String TABLE = PersistenceProperties.Element.Process.table;

	public static final String SERIALPHASE_ARRIVE = "arrive";
	public static final String SERIALPHASE_INQUIRE = "inquire";

	public static final String DEFAULTSTARTMODE_DRAFT = "draft";
	public static final String DEFAULTSTARTMODE_INSTANCE = "instance";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	public void onPersist() throws Exception {
		/* 默认流程名称作为意见为'是' */
		if (this.routeNameAsOpinion == null) {
			this.routeNameAsOpinion = true;
		}
		if (StringUtils.isEmpty(this.edition)) {
			this.edition = this.id;
			this.editionEnable = true;
			this.editionNumber = 1.0;
			this.editionName = this.name + "_V" + this.editionNumber;
		}
	}

	public Boolean getProjectionFully() {
		return BooleanUtils.isTrue(this.projectionFully);
	}

	public String getBeforeArriveScript() {
		return (null == beforeArriveScript) ? "" : this.beforeArriveScript;
	}

	public String getBeforeArriveScriptText() {
		return (null == beforeArriveScriptText) ? "" : this.beforeArriveScriptText;
	}

	public String getAfterArriveScript() {
		return (null == afterArriveScript) ? "" : this.afterArriveScript;
	}

	public String getAfterArriveScriptText() {
		return (null == afterArriveScriptText) ? "" : this.afterArriveScriptText;
	}

	public String getBeforeExecuteScript() {
		return (null == beforeExecuteScript) ? "" : this.beforeExecuteScript;
	}

	public String getBeforeExecuteScriptText() {
		return (null == beforeExecuteScriptText) ? "" : this.beforeExecuteScriptText;
	}

	public String getAfterExecuteScript() {
		return (null == afterExecuteScript) ? "" : this.afterExecuteScript;
	}

	public String getAfterExecuteScriptText() {
		return (null == afterExecuteScriptText) ? "" : this.afterExecuteScriptText;
	}

	public String getBeforeInquireScript() {
		return (null == beforeInquireScript) ? "" : this.beforeInquireScript;
	}

	public String getBeforeInquireScriptText() {
		return (null == beforeInquireScriptText) ? "" : this.beforeInquireScriptText;
	}

	public String getAfterInquireScript() {
		return (null == afterInquireScript) ? "" : this.afterInquireScript;
	}

	public String getAfterInquireScriptText() {
		return (null == afterInquireScriptText) ? "" : this.afterInquireScriptText;
	}

	public Boolean getRouteNameAsOpinion() {
		return BooleanUtils.isFalse(routeNameAsOpinion) ? false : true;
	}

	public static final String name_FIELDNAME = "name";
	@RestrictFlag
	@FieldDescribe("名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false, citationNotExists =
	/* 同一个应用下不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Process.class, equals = @Equal(property = "application", field = "application"), notEquals = @NotEqual(property = "edition", field = "edition")))
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false, citationNotExists =
	/* 同一个应用下不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Process.class, equals = @Equal(property = "application", field = "application"), notEquals = @NotEqual(property = "edition", field = "edition")))
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("流程创建者.")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorPerson;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("最后的编辑者.")
	@Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后的编辑时间.")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String application_FIELDNAME = "application";
	@IdReference(Application.class)
	@FieldDescribe("流程所属应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = @CitationExist(type = Application.class))
	private String application;

	public static final String controllerList_FIELDNAME = "controllerList";
	@FieldDescribe("流程管理者.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + controllerList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + controllerList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + controllerList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + controllerList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> controllerList;

	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = ColumnNamePrefix + icon_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String icon;

	public static final String beforeBeginScript_FIELDNAME = "beforeBeginScript";
	@IdReference(Script.class)
	@FieldDescribe("流程启动前事件脚本.")
	/** 脚本可能使用名称,所以长度为255 */
	@Column(length = length_255B, name = ColumnNamePrefix + beforeBeginScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeBeginScript;

	public static final String beforeBeginScriptText_FIELDNAME = "beforeBeginScriptText";
	@FieldDescribe("流程启动前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeBeginScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeBeginScriptText;

	public static final String afterBeginScript_FIELDNAME = "afterBeginScript";
	@IdReference(Script.class)
	/** 脚本可能使用名称,所以长度为255 */
	@FieldDescribe("流程启动前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterBeginScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterBeginScript;

	public static final String afterBeginScriptText_FIELDNAME = "afterBeginScriptText";
	@FieldDescribe("流程启动前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterBeginScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterBeginScriptText;

	public static final String beforeEndScript_FIELDNAME = "beforeEndScript";
	@IdReference(Script.class)
	/** 脚本可能使用名称,所以长度为255 */
	@FieldDescribe("流程结束后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeEndScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeEndScript;

	public static final String beforeEndScriptText_FIELDNAME = "beforeEndScriptText";
	@FieldDescribe("流程结束后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeEndScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeEndScriptText;

	public static final String afterEndScript_FIELDNAME = "afterEndScript";
	@IdReference(Script.class)
	/** 脚本可能使用名称,所以长度为255 */
	@FieldDescribe("流程结束后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterEndScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterEndScript;

	public static final String afterEndScriptText_FIELDNAME = "afterEndScriptText";
	@FieldDescribe("流程结束后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterEndScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterEndScriptText;

	public static final String startableIdentityList_FIELDNAME = "startableIdentityList";
	@FieldDescribe("在指定启动时候,允许新建Work的用户.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ startableIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ startableIdentityList_FIELDNAME))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + startableIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + startableIdentityList_FIELDNAME + JoinIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> startableIdentityList;

	public static final String startableUnitList_FIELDNAME = "startableUnitList";
	@FieldDescribe("在指定启动时候,允许新建Work的组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ startableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ startableUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + startableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + startableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> startableUnitList;

	public static final String serialTexture_FIELDNAME = "serialTexture";
	@FieldDescribe("编号定义.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + serialTexture_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serialTexture;

	public static final String serialActivity_FIELDNAME = "serialActivity";
	@IdReference({ Agent.class, Begin.class, Cancel.class, Choice.class, Choice.class, Delay.class, Embed.class,
			End.class, Invoke.class, Manual.class, Merge.class, Message.class, Parallel.class, Service.class,
			Split.class })
	@FieldDescribe("编号活动ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + serialActivity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serialActivity;

	public static final String serialPhase_FIELDNAME = "serialPhase";
	@FieldDescribe("编号活动阶段可以选择arrive或者inquire,默认情况下为空为arrive")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + serialPhase_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serialPhase;

	public static final String expireType_FIELDNAME = "expireType";
	@FieldDescribe("过期方式.可选值never,appoint,script")
	@Enumerated(EnumType.STRING)
	@Column(length = ExpireType.length, name = ColumnNamePrefix + expireType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ExpireType expireType = ExpireType.never;

	public static final String expireDay_FIELDNAME = "expireDay";
	@FieldDescribe("过期日期.")
	@Column(name = ColumnNamePrefix + expireDay_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer expireDay;

	public static final String expireHour_FIELDNAME = "expireHour";
	@FieldDescribe("过期小时.")
	@Column(name = ColumnNamePrefix + expireHour_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer expireHour;

	public static final String expireWorkTime_FIELDNAME = "expireWorkTime";
	@FieldDescribe("过期是否是工作时间.")
	@Column(name = ColumnNamePrefix + expireWorkTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean expireWorkTime;

	public static final String expireScript_FIELDNAME = "expireScript";
	@IdReference(Script.class)
	/** 脚本可能使用名称,所以长度为255 */
	@FieldDescribe("过期时间设定脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + expireScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String expireScript;

	public static final String expireScriptText_FIELDNAME = "expireScriptText";
	@FieldDescribe("过期时间设定脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + expireScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String expireScriptText;

	public static final String checkDraft_FIELDNAME = "checkDraft";
	@FieldDescribe("是否进行无内容的草稿删除校验.")
	@Column(name = ColumnNamePrefix + checkDraft_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean checkDraft;

	public static final String projection_FIELDNAME = "projection";
	@FieldDescribe("字段映射配置.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + projection_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String projection;

	public static final String projectionFully_FIELDNAME = "projectionFully";
	@FieldDescribe("执行完全映射,在每次流转时会将所有的工作,待办,已办,待阅,已阅,参阅执行全部字段映射,默认false")
	@Column(name = ColumnNamePrefix + projectionFully_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean projectionFully;

	public static final String routeNameAsOpinion_FIELDNAME = "routeNameAsOpinion";
	@FieldDescribe("如果没有默认意见那么将路由名称作为默认意见.")
	@Column(name = ColumnNamePrefix + routeNameAsOpinion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean routeNameAsOpinion;

	public static final String beforeArriveScript_FIELDNAME = "beforeArriveScript";
	@IdReference(Script.class)
	@FieldDescribe("统一活动到达前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeArriveScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeArriveScript;

	public static final String beforeArriveScriptText_FIELDNAME = "beforeArriveScriptText";
	@FieldDescribe("统一活动到达前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeArriveScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeArriveScriptText;

	public static final String afterArriveScript_FIELDNAME = "afterArriveScript";
	@IdReference(Script.class)
	@FieldDescribe("统一活动到达后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterArriveScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterArriveScript;

	public static final String afterArriveScriptText_FIELDNAME = "afterArriveScriptText";
	@FieldDescribe("统一活动到达后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterArriveScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterArriveScriptText;

	public static final String beforeExecuteScript_FIELDNAME = "beforeExecuteScript";
	@IdReference(Script.class)
	@FieldDescribe("统一活动执行前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeExecuteScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeExecuteScript;

	public static final String beforeExecuteScriptText_FIELDNAME = "beforeExecuteScriptText";
	@FieldDescribe("统一活动执行前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeExecuteScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeExecuteScriptText;

	public static final String afterExecuteScript_FIELDNAME = "afterExecuteScript";
	@IdReference(Script.class)
	@FieldDescribe("统一活动执行后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterExecuteScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterExecuteScript;

	public static final String afterExecuteScriptText_FIELDNAME = "afterExecuteScriptText";
	@FieldDescribe("统一活动执行后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterExecuteScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterExecuteScriptText;

	public static final String beforeInquireScript_FIELDNAME = "beforeInquireScript";
	@IdReference(Script.class)
	@FieldDescribe("统一路由查询前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeInquireScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeInquireScript;

	public static final String beforeInquireScriptText_FIELDNAME = "beforeInquireScriptText";
	@FieldDescribe("统一路由查询前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeInquireScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeInquireScriptText;

	public static final String afterInquireScript_FIELDNAME = "afterInquireScript";
	@IdReference(Script.class)
	@FieldDescribe("统一路由查询后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterInquireScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterInquireScript;

	public static final String afterInquireScriptText_FIELDNAME = "afterInquireScriptText";
	@FieldDescribe("统一路由查询后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterInquireScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterInquireScriptText;

	public static final String edition_FIELDNAME = "edition";
	@FieldDescribe("版本编码,不同版本的流程编码需相同.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + edition_FIELDNAME)
	private String edition;

	public static final String editionName_FIELDNAME = "editionName";
	@FieldDescribe("版本名")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + editionName_FIELDNAME)
	private String editionName;

	public static final String editionEnable_FIELDNAME = "editionEnable";
	@FieldDescribe("启用版本")
	@Column(name = ColumnNamePrefix + editionEnable_FIELDNAME)
	private Boolean editionEnable;

	public static final String editionNumber_FIELDNAME = "editionNumber";
	@FieldDescribe("版本号")
	@Column(name = ColumnNamePrefix + editionNumber_FIELDNAME)
	private Double editionNumber;

	public static final String editionDes_FIELDNAME = "editionDes";
	@FieldDescribe("版本描述.")
	@Column(length = length_255B, name = ColumnNamePrefix + editionDes_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String editionDes;

	public static final String defaultStartMode_FIELDNAME = "defaultStartMode";
	@FieldDescribe("默认启动方式,draft,instance")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + defaultStartMode_FIELDNAME)
	private String defaultStartMode;

	/* flag标志位 */

	public String getName() {
		return name;
	}

	public void setProjectionFully(Boolean projectionFully) {
		this.projectionFully = projectionFully;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getBeforeBeginScript() {
		return beforeBeginScript;
	}

	public void setBeforeBeginScript(String beforeBeginScript) {
		this.beforeBeginScript = beforeBeginScript;
	}

	public String getAfterEndScript() {
		return afterEndScript;
	}

	public void setAfterEndScript(String afterEndScript) {
		this.afterEndScript = afterEndScript;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public List<String> getStartableIdentityList() {
		return startableIdentityList;
	}

	public void setStartableIdentityList(List<String> startableIdentityList) {
		this.startableIdentityList = startableIdentityList;
	}

	public List<String> getStartableUnitList() {
		return startableUnitList;
	}

	public void setStartableUnitList(List<String> startableUnitList) {
		this.startableUnitList = startableUnitList;
	}

	public String getBeforeBeginScriptText() {
		return beforeBeginScriptText;
	}

	public void setBeforeBeginScriptText(String beforeBeginScriptText) {
		this.beforeBeginScriptText = beforeBeginScriptText;
	}

	public String getAfterBeginScript() {
		return afterBeginScript;
	}

	public void setAfterBeginScript(String afterBeginScript) {
		this.afterBeginScript = afterBeginScript;
	}

	public String getAfterBeginScriptText() {
		return afterBeginScriptText;
	}

	public void setAfterBeginScriptText(String afterBeginScriptText) {
		this.afterBeginScriptText = afterBeginScriptText;
	}

	public String getBeforeEndScript() {
		return beforeEndScript;
	}

	public void setBeforeEndScript(String beforeEndScript) {
		this.beforeEndScript = beforeEndScript;
	}

	public String getBeforeEndScriptText() {
		return beforeEndScriptText;
	}

	public void setBeforeEndScriptText(String beforeEndScriptText) {
		this.beforeEndScriptText = beforeEndScriptText;
	}

	public String getAfterEndScriptText() {
		return afterEndScriptText;
	}

	public void setAfterEndScriptText(String afterEndScriptText) {
		this.afterEndScriptText = afterEndScriptText;
	}

	public String getProjection() {
		return projection;
	}

	public void setProjection(String projection) {
		this.projection = projection;
	}

	public String getSerialTexture() {
		return serialTexture;
	}

	public void setSerialTexture(String serialTexture) {
		this.serialTexture = serialTexture;
	}

	public String getSerialActivity() {
		return serialActivity;
	}

	public void setSerialActivity(String serialActivity) {
		this.serialActivity = serialActivity;
	}

	public ExpireType getExpireType() {
		return expireType;
	}

	public void setExpireType(ExpireType expireType) {
		this.expireType = expireType;
	}

	public Integer getExpireDay() {
		return expireDay;
	}

	public void setExpireDay(Integer expireDay) {
		this.expireDay = expireDay;
	}

	public Integer getExpireHour() {
		return expireHour;
	}

	public void setExpireHour(Integer expireHour) {
		this.expireHour = expireHour;
	}

	public Boolean getExpireWorkTime() {
		return expireWorkTime;
	}

	public void setExpireWorkTime(Boolean expireWorkTime) {
		this.expireWorkTime = expireWorkTime;
	}

	public String getExpireScript() {
		return expireScript;
	}

	public void setExpireScript(String expireScript) {
		this.expireScript = expireScript;
	}

	public String getExpireScriptText() {
		return expireScriptText;
	}

	public void setExpireScriptText(String expireScriptText) {
		this.expireScriptText = expireScriptText;
	}

	public List<String> getControllerList() {
		return controllerList;
	}

	public void setControllerList(List<String> controllerList) {
		this.controllerList = controllerList;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Boolean getCheckDraft() {
		return checkDraft;
	}

	public void setCheckDraft(Boolean checkDraft) {
		this.checkDraft = checkDraft;
	}

	public String getSerialPhase() {
		return serialPhase;
	}

	public void setSerialPhase(String serialPhase) {
		this.serialPhase = serialPhase;
	}

	public void setRouteNameAsOpinion(Boolean routeNameAsOpinion) {
		this.routeNameAsOpinion = routeNameAsOpinion;
	}

	public void setBeforeArriveScript(String beforeArriveScript) {
		this.beforeArriveScript = beforeArriveScript;
	}

	public void setBeforeArriveScriptText(String beforeArriveScriptText) {
		this.beforeArriveScriptText = beforeArriveScriptText;
	}

	public void setAfterArriveScript(String afterArriveScript) {
		this.afterArriveScript = afterArriveScript;
	}

	public void setAfterArriveScriptText(String afterArriveScriptText) {
		this.afterArriveScriptText = afterArriveScriptText;
	}

	public void setBeforeExecuteScript(String beforeExecuteScript) {
		this.beforeExecuteScript = beforeExecuteScript;
	}

	public void setBeforeExecuteScriptText(String beforeExecuteScriptText) {
		this.beforeExecuteScriptText = beforeExecuteScriptText;
	}

	public void setAfterExecuteScript(String afterExecuteScript) {
		this.afterExecuteScript = afterExecuteScript;
	}

	public void setAfterExecuteScriptText(String afterExecuteScriptText) {
		this.afterExecuteScriptText = afterExecuteScriptText;
	}

	public void setBeforeInquireScript(String beforeInquireScript) {
		this.beforeInquireScript = beforeInquireScript;
	}

	public void setBeforeInquireScriptText(String beforeInquireScriptText) {
		this.beforeInquireScriptText = beforeInquireScriptText;
	}

	public void setAfterInquireScript(String afterInquireScript) {
		this.afterInquireScript = afterInquireScript;
	}

	public void setAfterInquireScriptText(String afterInquireScriptText) {
		this.afterInquireScriptText = afterInquireScriptText;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getEditionName() {
		return editionName;
	}

	public void setEditionName(String editionName) {
		this.editionName = editionName;
	}

	public Boolean getEditionEnable() {
		return editionEnable;
	}

	public void setEditionEnable(Boolean editionEnable) {
		this.editionEnable = editionEnable;
	}

	public Double getEditionNumber() {
		return editionNumber;
	}

	public void setEditionNumber(Double editionNumber) {
		this.editionNumber = editionNumber;
	}

	public String getEditionDes() {
		return editionDes;
	}

	public void setEditionDes(String editionDes) {
		this.editionDes = editionDes;
	}

	public String getDefaultStartMode() {
		return defaultStartMode;
	}

	public void setDefaultStartMode(String defaultStartMode) {
		this.defaultStartMode = defaultStartMode;
	}

}