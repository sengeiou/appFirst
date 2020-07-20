package com.x.attendance.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceEmployeeConfig.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceEmployeeConfig.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceEmployeeConfig extends SliceJpaObject {

	private static final long serialVersionUID = 2372078042298652744L;
	private static final String TABLE = PersistenceProperties.AttendanceEmployeeConfig.table;

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

	public void onPersist() throws Exception {
	}
	/*
	 * =============================================================================
	 * ===== 以上为 JpaObject 默认字段
	 * =============================================================================
	 * =====
	 */

	/*
	 * =============================================================================
	 * ===== 以下为具体不同的业务及数据表字段要求
	 * =============================================================================
	 * =====
	 */
	@FieldDescribe("顶层组织名称")
	@Column(name = "xtopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String topUnitName = "";

	@FieldDescribe("顶层组织编号")
	@Column(name = "xtopUnitOu", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String topUnitOu = "";

	@FieldDescribe("组织名称")
	@Column(name = "xunitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String unitName = "";

	@FieldDescribe("组织编号")
	@Column(name = "xunitOu", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String unitOu = "";

	@FieldDescribe("员工姓名")
	@Column(name = "xemployeeName", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = false)
	private String employeeName = "";

	@FieldDescribe("员工号")
	@Column(name = "xemployeeNumber", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String employeeNumber = "";

	@FieldDescribe("员工入职时间")
	@Column(name = "xempInTopUnitTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String empInTopUnitTime = "1900-01-01";

	@FieldDescribe("配置类型:REQUIRED（需要考勤）|NOTREQUIRED（不需要考勤）")
	@Column(name = "xconfigType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String configType = "";

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	public String getTopUnitOu() {
		return topUnitOu;
	}

	public void setTopUnitOu(String topUnitOu) {
		this.topUnitOu = topUnitOu;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getUnitOu() {
		return unitOu;
	}

	public void setUnitOu(String unitOu) {
		this.unitOu = unitOu;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}

	public String getEmpInTopUnitTime() {
		return empInTopUnitTime;
	}

	public void setEmpInTopUnitTime(String empInTopUnitTime) {
		this.empInTopUnitTime = empInTopUnitTime;
	}

}