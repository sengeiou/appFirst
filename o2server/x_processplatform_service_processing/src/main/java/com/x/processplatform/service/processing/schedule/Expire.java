package com.x.processplatform.service.processing.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.service.processing.ThisApplication;

public class Expire extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(Expire.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			TimeStamp stamp = new TimeStamp();
			String sequence = null;
			List<Task> targets = new ArrayList<>();
			AtomicInteger count = new AtomicInteger();
			do {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					targets = this.list(emc, sequence);
				}
				if (!targets.isEmpty()) {
					sequence = targets.get(targets.size() - 1).getSequence();
					for (Task task : targets) {
						try {
							try {
								ThisApplication.context().applications()
										.getQuery(x_processplatform_service_processing.class,
												Applications.joinQueryUri("task", task.getId(), "expire"),
												task.getJob())
										.getData(WoId.class);
								count.incrementAndGet();
							} catch (Exception e) {
								throw new ExceptionExpire(e, task.getId(), task.getTitle(), task.getSequence());
							}
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}
			} while (!targets.isEmpty());
			logger.print("标识{}个过期待办, 耗时:{}.", count.intValue(), stamp.consumingMilliseconds());
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private List<Task> list(EntityManagerContainer emc, String sequence) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Task> root = cq.from(Task.class);
		Path<String> id_path = root.get(Task_.id);
		Path<String> job_path = root.get(Task_.job);
		Path<String> sequence_path = root.get(Task_.sequence);
		Predicate p = cb.or(cb.isNull(root.get(Task_.expired)), cb.equal(root.get(Task_.expired), false));
		p = cb.and(p, cb.isNotNull(root.get(Task_.expireTime)));
		p = cb.and(p, cb.lessThanOrEqualTo(root.get(Task_.expireTime), new Date()));
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequence_path, sequence));
		}
		cq.multiselect(id_path, job_path, sequence_path).where(p).orderBy(cb.asc(sequence_path));
		List<Tuple> os = em.createQuery(cq).setMaxResults(200).getResultList();
		List<Task> list = new ArrayList<>();
		for (Tuple o : os) {
			Task task = new Task();
			task.setId(o.get(id_path));
			task.setJob(o.get(job_path));
			task.setSequence(o.get(sequence_path));
			list.add(task);
		}
		return list;
	}

}