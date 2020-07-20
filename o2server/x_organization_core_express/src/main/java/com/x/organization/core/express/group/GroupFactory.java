package com.x.organization.core.express.group;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Group;
import com.x.base.core.project.tools.ListTools;

public class GroupFactory {

	public GroupFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 判断群组是否拥有指定角色中的一个或者多个 */
	public Boolean hasRole(String group, Collection<String> values) throws Exception {
		return ActionHasRole.execute(context, group, values);
	}

	/** 判断群组是否拥有指定角色中的一个或者多个 */
	public Boolean hasRole(String group, String... values) throws Exception {
		return ActionHasRole.execute(context, group, Arrays.asList(values));
	}

	/** 获取单个群组的distinguishedName */
	public String get(String value) throws Exception {
		List<String> os = ActionList.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	/** 批量获取群组的distinguishedName */
	public List<String> list(Collection<String> values) throws Exception {
		return ActionList.execute(context, values);
	}

	/** 批量获取群组的distinguishedName */
	public List<String> list(String... values) throws Exception {
		return ActionList.execute(context, Arrays.asList(values));
	}

	/** 获取群组对象 */
	public Group getObject(String value) throws Exception {
		List<? extends Group> os = ActionListObject.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return null;
		} else {
			return os.get(0);
		}
	}

	/** 批量获取群组对象 */
	public List<Group> listObject(Collection<String> values) throws Exception {
		List<? extends Group> os = ActionListObject.execute(context, values);
		return (List<Group>) os;
	}

	/** 批量获取群组对象 */
	public List<Group> listObject(String... values) throws Exception {
		List<? extends Group> os = ActionListObject.execute(context, Arrays.asList(values));
		return (List<Group>) os;
	}

	/** 根据群组获取群组的直接下级群组 */
	public List<String> listWithGroupSubDirect(Collection<String> values) throws Exception {
		return ActionListWithGroupSubDirect.execute(context, values);
	}

	/** 根据群组获取群组的直接下级群组 */
	public List<String> listWithGroupSubDirect(String... values) throws Exception {
		return ActionListWithGroupSubDirect.execute(context, Arrays.asList(values));
	}

	/** 根据群组获取群组的递归下级群组 */
	public List<String> listWithGroupSubNested(Collection<String> values) throws Exception {
		return ActionListWithGroupSubNested.execute(context, values);
	}

	/** 根据群组获取群组的递归下级群组 */
	public List<String> listWithGroupSubNested(String... values) throws Exception {
		return ActionListWithGroupSubNested.execute(context, Arrays.asList(values));
	}

	/** 根据群组获取群组的直接上级群组 */
	public List<String> listWithGroupSupDirect(Collection<String> values) throws Exception {
		return ActionListWithGroupSupDirect.execute(context, values);
	}

	/** 根据群组获取群组的直接上级群组 */
	public List<String> listWithGroupSupDirect(String... values) throws Exception {
		return ActionListWithGroupSupDirect.execute(context, Arrays.asList(values));
	}

	/** 根据群组获取群组的递归上级群组 */
	public List<String> listWithGroupSupNested(Collection<String> values) throws Exception {
		return ActionListWithGroupSupNested.execute(context, values);
	}

	/** 根据群组获取群组的递归上级群组 */
	public List<String> listWithGroupSupNested(String... values) throws Exception {
		return ActionListWithGroupSupNested.execute(context, Arrays.asList(values));
	}

	/** 查询人员所在的群组 */
	public List<String> listWithPerson(Collection<String> values) throws Exception {
		return ActionListWithPerson.execute(context, values);
	}

	/** 查询人员所在的群组 */
	public List<String> listWithPerson(String... values) throws Exception {
		return ActionListWithPerson.execute(context, Arrays.asList(values));
	}

	/** 查询人员所在的群组 */
	public List<String> listWithPerson(EffectivePerson effectivePerson) throws Exception {
		return ActionListWithPerson.execute(context, ListTools.toList(effectivePerson.getDistinguishedName()));
	}

}