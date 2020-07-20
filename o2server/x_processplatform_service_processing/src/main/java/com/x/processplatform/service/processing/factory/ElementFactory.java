package com.x.processplatform.service.processing.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.script.CompiledScript;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Begin_;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Mapping;
import com.x.processplatform.core.entity.element.Mapping_;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Route_;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.Script_;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class ElementFactory extends AbstractFactory {

	private static Logger logger = LoggerFactory.getLogger(ElementFactory.class);

	public ElementFactory(Business business) throws Exception {
		super(business);
	}

	/* 取得属于指定Process 的设计元素 */
	@SuppressWarnings("unchecked")
	public <T extends JpaObject> List<T> listWithProcess(Class<T> clz, Process process) throws Exception {
		List<T> list = new ArrayList<>();
		Ehcache cache = ApplicationCache.instance().getCache(clz);
		String cacheKey = ApplicationCache.concreteCacheKey("listWithProcess", process.getId(), clz.getName());
		Element element = cache.get(cacheKey);
		if (null != element) {
			Object obj = element.getObjectValue();
			if (null != obj) {
				list = (List<T>) obj;
			}
		} else {
			EntityManager em = this.entityManagerContainer().get(clz);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(clz);
			Root<T> root = cq.from(clz);
			Predicate p = cb.equal(root.get(Agent.process_FIELDNAME), process.getId());
			cq.select(root).where(p);
			List<T> os = em.createQuery(cq).getResultList();
			for (T t : os) {
				em.detach(t);
				list.add(t);
			}
			/* 将object改为unmodifiable */
			list = Collections.unmodifiableList(list);
			cache.put(new Element(cacheKey, list));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T extends JpaObject> T get(String id, Class<T> clz) throws Exception {
		Ehcache cache = ApplicationCache.instance().getCache(clz);
		T t = null;
		String cacheKey = id;
		Element element = cache.get(cacheKey);
		if (null != element) {
			t = (T) element.getObjectValue();
		} else {
			t = this.entityManagerContainer().find(id, clz);
			if (null != t) {
				cache.put(new Element(cacheKey, t));
			}
		}
		return t;
	}

	public Activity getActivity(String id) throws Exception {
		Activity activity = null;
		activity = this.get(id, ActivityType.agent);
		if (null == activity) {
			activity = this.get(id, ActivityType.begin);
			if (null == activity) {
				activity = this.get(id, ActivityType.cancel);
				if (null == activity) {
					activity = this.get(id, ActivityType.choice);
					if (null == activity) {
						activity = this.get(id, ActivityType.delay);
						if (null == activity) {
							activity = this.get(id, ActivityType.embed);
							if (null == activity) {
								activity = this.get(id, ActivityType.end);
								if (null == activity) {
									activity = this.get(id, ActivityType.invoke);
									if (null == activity) {
										activity = this.get(id, ActivityType.manual);
										if (null == activity) {
											activity = this.get(id, ActivityType.merge);
											if (null == activity) {
												activity = this.get(id, ActivityType.message);
												if (null == activity) {
													activity = this.get(id, ActivityType.parallel);
													if (null == activity) {
														activity = this.get(id, ActivityType.service);
														if (null == activity) {
															activity = this.get(id, ActivityType.split);
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return activity;
	}

	public Activity get(String id, ActivityType activityType) throws Exception {
		switch (activityType) {
		case agent:
			return this.get(id, Agent.class);
		case begin:
			return this.get(id, Begin.class);
		case cancel:
			return this.get(id, Cancel.class);
		case choice:
			return this.get(id, Choice.class);
		case delay:
			return this.get(id, Delay.class);
		case embed:
			return this.get(id, Embed.class);
		case end:
			return this.get(id, End.class);
		case invoke:
			return this.get(id, Invoke.class);
		case manual:
			return this.get(id, Manual.class);
		case merge:
			return this.get(id, Merge.class);
		case message:
			return this.get(id, Message.class);
		case parallel:
			return this.get(id, Parallel.class);
		case service:
			return this.get(id, Service.class);
		case split:
			return this.get(id, Split.class);
		default:
			return null;
		}
	}

	/* 用Process的updateTime作为缓存值 */
	public Begin getBeginWithProcess(String id) throws Exception {
		Begin begin = null;
		Ehcache cache = ApplicationCache.instance().getCache(Begin.class);
		String cacheKey = id;
		Element element = cache.get(cacheKey);
		if (null != element) {
			begin = (Begin) element.getObjectValue();
		} else {
			EntityManager em = this.entityManagerContainer().get(Begin.class);
			Process process = this.get(id, Process.class);
			if (null != process) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Begin> cq = cb.createQuery(Begin.class);
				Root<Begin> root = cq.from(Begin.class);
				Predicate p = cb.equal(root.get(Begin_.process), process.getId());
				List<Begin> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
				if (!list.isEmpty()) {
					begin = list.get(0);
					cache.put(new Element(cacheKey, begin));
				}
			}
		}
		return begin;
	}

	@SuppressWarnings("unchecked")
	public List<Route> listRouteWithChoice(String id) throws Exception {
		List<Route> list = new ArrayList<>();
		Ehcache cache = ApplicationCache.instance().getCache(Route.class);
		String cacheKey = ApplicationCache.concreteCacheKey(id, Choice.class.getCanonicalName());
		Element element = cache.get(cacheKey);
		if (null != element) {
			list = (List<Route>) element.getObjectValue();
		} else {
			EntityManager em = this.entityManagerContainer().get(Route.class);
			Choice choice = this.get(id, Choice.class);
			if (null != choice) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Route> cq = cb.createQuery(Route.class);
				Root<Route> root = cq.from(Route.class);
				Predicate p = root.get(Route_.id).in(choice.getRouteList());
				list = em.createQuery(cq.where(p).orderBy(cb.asc(root.get(Route_.orderNumber)))).getResultList();
				if (!list.isEmpty()) {
					cache.put(new Element(cacheKey, list));
				}
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Route> listRouteWithManual(String id) throws Exception {
		List<Route> list = new ArrayList<>();
		Ehcache cache = ApplicationCache.instance().getCache(Manual.class);
		String cacheKey = ApplicationCache.concreteCacheKey(id, Manual.class.getCanonicalName());
		Element element = cache.get(cacheKey);
		if (null != element) {
			list = (List<Route>) element.getObjectValue();
		} else {
			EntityManager em = this.entityManagerContainer().get(Route.class);
			Manual manual = this.get(id, Manual.class);
			if (null != manual) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Route> cq = cb.createQuery(Route.class);
				Root<Route> root = cq.from(Route.class);
				Predicate p = root.get(Route_.id).in(manual.getRouteList());
				list = em.createQuery(cq.where(p).orderBy(cb.asc(root.get(Route_.orderNumber)))).getResultList();
				if (!list.isEmpty()) {
					cache.put(new Element(cacheKey, list));
				}
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Route> listRouteWithParallel(String id) throws Exception {
		List<Route> list = new ArrayList<>();
		Ehcache cache = ApplicationCache.instance().getCache(Parallel.class);
		String cacheKey = ApplicationCache.concreteCacheKey(id, Parallel.class.getCanonicalName());
		Element element = cache.get(cacheKey);
		if (null != element) {
			list = (List<Route>) element.getObjectValue();
		} else {
			EntityManager em = this.entityManagerContainer().get(Route.class);
			Parallel parallel = this.get(id, Parallel.class);
			if (null != parallel) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Route> cq = cb.createQuery(Route.class);
				Root<Route> root = cq.from(Route.class);
				Predicate p = root.get(Route_.id).in(parallel.getRouteList());
				list = em.createQuery(cq.where(p).orderBy(cb.asc(root.get(Route_.orderNumber)))).getResultList();
				if (!list.isEmpty()) {
					cache.put(new Element(cacheKey, list));
				}
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Script> listScriptNestedWithApplicationWithUniqueName(String applicationId, String uniqueName)
			throws Exception {
		List<Script> list = new ArrayList<>();
		Ehcache cache = ApplicationCache.instance().getCache(Script.class);
		String cacheKey = applicationId + "." + uniqueName;
		Element element = cache.get(cacheKey);
		if (null != element) {
			list = (List<Script>) element.getObjectValue();
		} else {
			List<String> names = new ArrayList<>();
			names.add(uniqueName);
			while (!names.isEmpty()) {
				List<String> loops = new ArrayList<>();
				for (String name : names) {
					Script o = this.getScriptWithApplicationWithUniqueName(applicationId, name);
					if ((null != o) && (!list.contains(o))) {
						list.add(o);
						loops.addAll(o.getDependScriptList());
					}
				}
				names = loops;
			}
			if (!list.isEmpty()) {
				Collections.reverse(list);
				cache.put(new Element(cacheKey, list));
			}
		}
		return list;
	}

	private Script getScriptWithApplicationWithUniqueName(String applicationId, String uniqueName) throws Exception {
		Script script = null;
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Script> cq = cb.createQuery(Script.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.name), uniqueName);
		p = cb.or(p, cb.equal(root.get(Script_.alias), uniqueName));
		p = cb.or(p, cb.equal(root.get(Script_.id), uniqueName));
		p = cb.and(p, cb.equal(root.get(Script_.application), applicationId));
		List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			script = list.get(0);
		}
		return script;
	}

	public List<Route> listRouteWithActvity(String id, ActivityType activityType) throws Exception {
		List<Route> list = new ArrayList<>();
		switch (activityType) {
		case agent:
			Agent agent = this.get(id, Agent.class);
			list.add(this.get(agent.getRoute(), Route.class));
			break;
		case begin:
			Begin begin = this.get(id, Begin.class);
			list.add(this.get(begin.getRoute(), Route.class));
			break;
		case cancel:
			break;
		case choice:
			Choice choice = this.get(id, Choice.class);
			for (String str : choice.getRouteList()) {
				list.add(this.get(str, Route.class));
			}
			break;
		case delay:
			Delay delay = this.get(id, Delay.class);
			list.add(this.get(delay.getRoute(), Route.class));
			break;
		case embed:
			Embed embed = this.get(id, Embed.class);
			list.add(this.get(embed.getRoute(), Route.class));
			break;
		case end:
			break;
		case invoke:
			Invoke invoke = this.get(id, Invoke.class);
			list.add(this.get(invoke.getRoute(), Route.class));
			break;
		case manual:
			Manual manual = this.get(id, Manual.class);
			for (String str : manual.getRouteList()) {
				list.add(this.get(str, Route.class));
			}
			break;
		case merge:
			Merge merge = this.get(id, Merge.class);
			list.add(this.get(merge.getRoute(), Route.class));
			break;
		case message:
			Message message = this.get(id, Message.class);
			list.add(this.get(message.getRoute(), Route.class));
			break;
		case parallel:
			Parallel parallel = this.get(id, Parallel.class);
			for (String str : parallel.getRouteList()) {
				list.add(this.get(str, Route.class));
			}
			break;
		case service:
			Service service = this.get(id, Service.class);
			list.add(this.get(service.getRoute(), Route.class));
			break;
		case split:
			Split split = this.get(id, Split.class);
			list.add(this.get(split.getRoute(), Route.class));
			break;
		default:
			break;
		}
		return ListTools.trim(list, true, true);
	}

	public List<String> listFormWithProcess(Process process) throws Exception {
		List<String> ids = new ArrayList<>();
		this.listWithProcess(Agent.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		this.listWithProcess(Begin.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		this.listWithProcess(Cancel.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		this.listWithProcess(Choice.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		this.listWithProcess(Delay.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		this.listWithProcess(Embed.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		this.listWithProcess(End.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		this.listWithProcess(Invoke.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		this.listWithProcess(Manual.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		this.listWithProcess(Merge.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		this.listWithProcess(Message.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		this.listWithProcess(Parallel.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		this.listWithProcess(Service.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		this.listWithProcess(Split.class, process).forEach(o -> {
			ids.add(o.getForm());
		});
		return ListTools.trim(ids, true, true);
	}

	public List<Mapping> listMappingEffectiveWithApplicationAndProcess(String application, String process)
			throws Exception {
		final List<Mapping> list = new ArrayList<>();
		Ehcache cache = ApplicationCache.instance().getCache(Mapping.class);
		String cacheKey = ApplicationCache.concreteCacheKey(application, process, Application.class.getName(),
				Process.class.getName());
		Element element = cache.get(cacheKey);
		if (null != element) {
			list.addAll((List<Mapping>) element.getObjectValue());
		} else {
			EntityManager em = this.entityManagerContainer().get(Mapping.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Mapping> cq = cb.createQuery(Mapping.class);
			Root<Mapping> root = cq.from(Mapping.class);
			Predicate p = cb.equal(root.get(Mapping_.enable), true);
			p = cb.and(p, cb.equal(root.get(Mapping_.application), application));
			p = cb.and(p, cb.or(cb.equal(root.get(Mapping_.process), process), cb.equal(root.get(Mapping_.process), ""),
					cb.isNull(root.get(Mapping_.process))));
			List<Mapping> os = em.createQuery(cq.where(p)).getResultList();
			os.stream().collect(Collectors.groupingBy(o -> {
				return o.getApplication() + o.getTableName();
			})).forEach((k, v) -> {
				list.add(v.stream().filter(i -> StringUtils.isNotEmpty(i.getProcess())).findFirst().orElse(v.get(0)));
			});
			if (!list.isEmpty()) {
				cache.put(new Element(cacheKey, list));
			}
		}
		return list;
	}

	public CompiledScript getCompiledScript(String applicationId, Activity o, String event) throws Exception {
		String cacheKey = ApplicationCache.concreteCacheKey(o.getId(), event);
		Ehcache cache = ApplicationCache.instance().getCache(o.getClass());
		Element element = cache.get(cacheKey);
		CompiledScript compiledScript = null;
		if (null != element && null != element.getObjectValue()) {
			compiledScript = (CompiledScript) element.getObjectValue();
		} else {
			String scriptName = null;
			String scriptText = null;
			switch (event) {
			case Business.EVENT_BEFOREARRIVE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.beforeArriveScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.beforeArriveScriptText_FIELDNAME));
				break;
			case Business.EVENT_AFTERARRIVE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.afterArriveScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.afterArriveScriptText_FIELDNAME));
				break;
			case Business.EVENT_BEFOREEXECUTE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.beforeExecuteScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.beforeExecuteScriptText_FIELDNAME));
				break;
			case Business.EVENT_AFTEREXECUTE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.afterExecuteScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.afterExecuteScriptText_FIELDNAME));
				break;
			case Business.EVENT_BEFOREINQUIRE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.beforeInquireScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.beforeInquireScriptText_FIELDNAME));
				break;
			case Business.EVENT_AFTERINQUIRE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.afterInquireScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.afterInquireScriptText_FIELDNAME));
				break;
			case Business.EVENT_MANUALTASKEXPIRE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.taskExpireScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.taskExpireScriptText_FIELDNAME));
				break;
			case Business.EVENT_MANUALTASK:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.taskScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.taskScriptText_FIELDNAME));
				break;
			case Business.EVENT_MANUALSTAY:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.manualStayScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.manualStayScriptText_FIELDNAME));
				break;
			case Business.EVENT_MANUALBEFORETASK:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.manualBeforeTaskScript_FIELDNAME));
				scriptText = Objects
						.toString(PropertyUtils.getProperty(o, Manual.manualBeforeTaskScriptText_FIELDNAME));
				break;
			case Business.EVENT_MANUALAFTERTASK:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.manualAfterTaskScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.manualAfterTaskScriptText_FIELDNAME));
				break;
			case Business.EVENT_INVOKEJAXWSPARAMETER:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxwsParameterScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxwsParameterScriptText_FIELDNAME));
				break;
			case Business.EVENT_INVOKEJAXRSPARAMETER:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsParameterScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsParameterScriptText_FIELDNAME));
				break;
			case Business.EVENT_INVOKEJAXWSRESPONSE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxwsResponseScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxwsResponseScriptText_FIELDNAME));
				break;
			case Business.EVENT_INVOKEJAXRSRESPONSE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsResponseScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsResponseScriptText_FIELDNAME));
				break;
			case Business.EVENT_INVOKEJAXRSBODY:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsBodyScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsBodyScriptText_FIELDNAME));
				break;
			case Business.EVENT_INVOKEJAXRSHEAD:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsHeadScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsHeadScriptText_FIELDNAME));
				break;
			case Business.EVENT_READ:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.readScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.readScriptText_FIELDNAME));
				break;
			case Business.EVENT_REVIEW:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.reviewScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.reviewScriptText_FIELDNAME));
				break;
			case Business.EVENT_AGENT:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Agent.script_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Agent.scriptText_FIELDNAME));
				break;
			case Business.EVENT_SERVICE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Service.script_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Service.scriptText_FIELDNAME));
				break;
			case Business.EVENT_AGENTINTERRUPT:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Agent.agentInterruptScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Agent.agentInterruptScriptText_FIELDNAME));
				break;
			case Business.EVENT_DELAY:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Delay.delayScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Delay.delayScriptText_FIELDNAME));
				break;
			case Business.EVENT_EMBEDTARGETASSIGNDATA:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Embed.targetAssginDataScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Embed.targetAssginDataScriptText_FIELDNAME));
				break;
			case Business.EVENT_EMBEDTARGETIDENTITY:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Embed.targetIdentityScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Embed.targetIdentityScriptText_FIELDNAME));
				break;
			case Business.EVENT_EMBEDTARGETTITLE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Embed.targetTitleScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Embed.targetTitleScriptText_FIELDNAME));
				break;
			case Business.EVENT_SPLIT:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Split.script_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Split.scriptText_FIELDNAME));
				break;
			default:
				break;
			}
			StringBuffer sb = new StringBuffer();
			try {
				sb.append("(function(){").append(System.lineSeparator());
				if (StringUtils.isNotEmpty(scriptName)) {
					List<Script> list = listScriptNestedWithApplicationWithUniqueName(applicationId, scriptName);
					for (Script script : list) {
						sb.append(script.getText()).append(System.lineSeparator());
					}
				}
				if (StringUtils.isNotEmpty(scriptText)) {
					sb.append(scriptText).append(System.lineSeparator());
				}
				sb.append("}).apply(bind);");
				compiledScript = ScriptFactory.compile(sb.toString());
				cache.put(new Element(cacheKey, compiledScript));
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return compiledScript;
	}

	public CompiledScript getCompiledScript(String applicationId, Route o, String event) throws Exception {
		String cacheKey = ApplicationCache.concreteCacheKey(o.getId(), event);
		Ehcache cache = ApplicationCache.instance().getCache(Route.class);
		Element element = cache.get(cacheKey);
		CompiledScript compiledScript = null;
		if (null != element && null != element.getObjectValue()) {
			compiledScript = (CompiledScript) element.getObjectValue();
		} else {
			String scriptName = null;
			String scriptText = null;
			switch (event) {
			case Business.EVENT_ROUTEAPPENDTASKIDENTITY:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Route.appendTaskIdentityScript_FIELDNAME));
				scriptText = Objects
						.toString(PropertyUtils.getProperty(o, Route.appendTaskIdentityScriptText_FIELDNAME));
				break;
			case Business.EVENT_ROUTE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Route.script_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Route.scriptText_FIELDNAME));
				break;
			default:
				break;
			}
			StringBuffer sb = new StringBuffer();
			try {
				sb.append("(function(){").append(System.lineSeparator());
				if (StringUtils.isNotEmpty(scriptName)) {
					List<Script> list = listScriptNestedWithApplicationWithUniqueName(applicationId, scriptName);
					for (Script script : list) {
						sb.append(script.getText()).append(System.lineSeparator());
					}
				}
				if (StringUtils.isNotEmpty(scriptText)) {
					sb.append(scriptText).append(System.lineSeparator());
				}
				sb.append("}).apply(bind);");
				compiledScript = ScriptFactory.compile(sb.toString());
				cache.put(new Element(cacheKey, compiledScript));
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return compiledScript;
	}

	public CompiledScript getCompiledScript(String applicationId, Process o, String event) throws Exception {
		String cacheKey = ApplicationCache.concreteCacheKey(o.getId(), event);
		Ehcache cache = ApplicationCache.instance().getCache(Process.class);
		Element element = cache.get(cacheKey);
		CompiledScript compiledScript = null;
		if (null != element && null != element.getObjectValue()) {
			compiledScript = (CompiledScript) element.getObjectValue();
		} else {
			String scriptName = null;
			String scriptText = null;
			switch (event) {
			case Business.EVENT_BEFOREARRIVE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.beforeArriveScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.beforeArriveScriptText_FIELDNAME));
				break;
			case Business.EVENT_AFTERARRIVE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.afterArriveScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.afterArriveScriptText_FIELDNAME));
				break;
			case Business.EVENT_BEFOREEXECUTE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.beforeExecuteScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.beforeExecuteScriptText_FIELDNAME));
				break;
			case Business.EVENT_AFTEREXECUTE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.afterExecuteScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.afterExecuteScriptText_FIELDNAME));
				break;
			case Business.EVENT_BEFOREINQUIRE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.beforeInquireScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.beforeInquireScriptText_FIELDNAME));
				break;
			case Business.EVENT_AFTERINQUIRE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.afterInquireScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.afterInquireScriptText_FIELDNAME));
				break;
			case Business.EVENT_PROCESSAFTERBEGIN:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.afterBeginScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.afterBeginScriptText_FIELDNAME));
				break;
			case Business.EVENT_PROCESSAFTEREND:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.afterEndScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.afterEndScriptText_FIELDNAME));
				break;
			case Business.EVENT_PROCESSEXPIRE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.expireScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.expireScriptText_FIELDNAME));
				break;
			default:
				break;
			}
			StringBuffer sb = new StringBuffer();
			try {
				sb.append("(function(){").append(System.lineSeparator());
				if (StringUtils.isNotEmpty(scriptName)) {
					List<Script> list = listScriptNestedWithApplicationWithUniqueName(applicationId, scriptName);
					for (Script script : list) {
						sb.append(script.getText()).append(System.lineSeparator());
					}
				}
				if (StringUtils.isNotEmpty(scriptText)) {
					sb.append(scriptText).append(System.lineSeparator());
				}
				sb.append("}).apply(bind);");
				compiledScript = ScriptFactory.compile(sb.toString());
				cache.put(new Element(cacheKey, compiledScript));
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return compiledScript;
	}

	public CompiledScript getCompiledScript(Activity activity, String event, String name, String code) {
		String cacheKey = ApplicationCache.concreteCacheKey(activity.getId(), event, name, code);
		Ehcache cache = ApplicationCache.instance().getCache(activity.getClass());
		Element element = cache.get(cacheKey);
		CompiledScript compiledScript = null;
		if (null != element && null != element.getObjectValue()) {
			compiledScript = (CompiledScript) element.getObjectValue();
		} else {
			StringBuffer sb = new StringBuffer();
			try {
				sb.append("(function(){").append(System.lineSeparator());
				if (StringUtils.isNotEmpty(code)) {
					sb.append(code).append(System.lineSeparator());
				}
				sb.append("}).apply(bind);");
				compiledScript = ScriptFactory.compile(sb.toString());
				cache.put(new Element(cacheKey, compiledScript));
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return compiledScript;
	}
}