package com.x.program.center.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.script.AbstractResources;
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.tools.CronTools;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import com.x.program.center.Business;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.Agent;
import com.x.program.center.core.entity.Agent_;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class TriggerAgent extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(TriggerAgent.class);

	private static final CopyOnWriteArrayList<String> LOCK = new CopyOnWriteArrayList<>();

	private static Ehcache cache = ApplicationCache.instance().getCache(Agent.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (pirmaryCenter()) {
				List<Pair> list = new ArrayList<>();
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					list = this.list(business);
				}
				list.stream().forEach(p -> {
					this.trigger(p);
				});
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void trigger(Pair pair) {
		try {
			if (StringUtils.isEmpty(pair.getCron())) {
				return;
			}
			Date date = CronTools.next(pair.getCron(), pair.getLastStartTime());
			if (date.before(new Date())) {
				if (LOCK.contains(pair.getId())) {
					throw new ExceptionAgentLastNotEnd(pair);
				} else {
					Agent agent = null;
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						agent = emc.find(pair.getId(), Agent.class);
						if (null != agent) {
							emc.beginTransaction(Agent.class);
							agent.setLastStartTime(new Date());
							emc.commit();
						}
					}
					if (null != agent && agent.getEnable()) {
						logger.info("trigger agent : {}, name :{}, cron: {}, last start time: {}.", pair.getId(),
								pair.getName(), pair.getCron(),
								(pair.getLastStartTime() == null ? "" : DateTools.format(pair.getLastStartTime())));
						ExecuteThread thread = new ExecuteThread(agent);
						thread.start();
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private List<Pair> list(Business business) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Agent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Agent> root = cq.from(Agent.class);
		Path<String> path_id = root.get(Agent_.id);
		Path<String> path_name = root.get(Agent_.name);
		Path<String> path_cron = root.get(Agent_.cron);
		Path<Date> path_lastEndTime = root.get(Agent_.lastEndTime);
		Path<Date> path_lastStartTime = root.get(Agent_.lastStartTime);
		Predicate p = cb.equal(root.get(Agent_.enable), true);
		List<Tuple> list = em
				.createQuery(
						cq.multiselect(path_id, path_name, path_cron, path_lastEndTime, path_lastStartTime).where(p))
				.getResultList();
		List<Pair> pairs = list.stream().map(o -> {
			return new Pair(o.get(path_id), o.get(path_name), o.get(path_cron), o.get(path_lastStartTime));
		}).distinct().collect(Collectors.toList());
		return pairs;
	}

	class Pair {
		Pair(String id, String name, String cron, Date lastStartTime) {
			this.id = id;
			this.name = name;
			this.cron = cron;
			this.lastStartTime = lastStartTime;
		}

		private String id;

		private String name;

		private String cron;

		private Date lastStartTime;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getCron() {
			return cron;
		}

		public void setCron(String cron) {
			this.cron = cron;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getLastStartTime() {
			return lastStartTime;
		}

		public void setLastStartTime(Date lastStartTime) {
			this.lastStartTime = lastStartTime;
		}

	}

	public class ExecuteThread extends Thread {

		private Agent agent;

		public ExecuteThread(Agent agent) {
			this.agent = agent;
		}

		public void run() {
			if (StringUtils.isNotEmpty(agent.getText())) {
				try {
					LOCK.add(agent.getId());
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						String cacheKey = ApplicationCache.concreteCacheKey(TriggerAgent.class, agent.getId());
						Element element = cache.get(cacheKey);
						CompiledScript compiledScript = null;
						if ((null != element) && (null != element.getObjectValue())) {
							compiledScript = (CompiledScript) element.getObjectValue();
						} else {
							compiledScript = ScriptFactory.compile(ScriptFactory.functionalization(agent.getText()));
							cache.put(new Element(cacheKey, compiledScript));
						}
						ScriptContext scriptContext = new SimpleScriptContext();
						Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
						Resources resources = new Resources();
						resources.setEntityManagerContainer(emc);
						resources.setContext(ThisApplication.context());
						resources.setOrganization(new Organization(ThisApplication.context()));
						resources.setApplications(ThisApplication.context().applications());
						resources.setWebservicesClient(new WebservicesClient());
						bindings.put(ScriptFactory.BINDING_NAME_RESOURCES, resources);
						try {
							ScriptFactory.initialServiceScriptText().eval(scriptContext);
							compiledScript.eval(scriptContext);
						} catch (Exception e) {
							throw new ExceptionAgentEval(e, e.getMessage(), agent.getId(), agent.getName(),
									agent.getAlias(), agent.getText());
						}
					} catch (Exception e) {
						logger.error(e);
					}

					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						Agent o = emc.find(agent.getId(), Agent.class);
						if (null != o) {
							emc.beginTransaction(Agent.class);
							o.setLastEndTime(new Date());
							emc.commit();
						}
					} catch (Exception e) {
						logger.error(e);
					}
				} finally {
					LOCK.remove(agent.getId());
				}
			}
		}
	}

	public static class Resources extends AbstractResources {
		private Organization organization;

		public Organization getOrganization() {
			return organization;
		}

		public void setOrganization(Organization organization) {
			this.organization = organization;
		}

	}

}