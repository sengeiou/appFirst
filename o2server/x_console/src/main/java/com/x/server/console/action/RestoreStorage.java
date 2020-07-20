package com.x.server.console.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.factory.PersistenceXmlHelper;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.config.StorageMappings;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;

/**
 * @author zhourui
 */

public class RestoreStorage {

	private static Logger logger = LoggerFactory.getLogger(RestoreStorage.class);

	private Date start = new Date();

	private File dir;

	private DumpRestoreStorageCatalog catalog;

	private Gson pureGsonDateFormated = XGsonBuilder.instance();

	public boolean execute(final String path) throws Exception {
		if (StringUtils.isEmpty(path)) {
			logger.print("path is empty.");
		}
		if (BooleanUtils.isTrue(DateTools.isCompactDateTime(path))) {
			this.dir = new File(Config.base(), "local/dump/dumpStorage_" + path);
			this.catalog = BaseTools.readConfigObject("local/dump/dumpStorage_" + path + "/catalog.json",
					DumpRestoreStorageCatalog.class);
		} else {
			this.dir = new File(path);
			if (!(this.dir.exists() && this.dir.isDirectory())) {
				logger.print("dir not exist: {}.", path);
				return false;
			} else if (StringUtils.startsWith(dir.getAbsolutePath(), Config.base())) {
				logger.print("path can not in base directory.");
				return false;
			}
			this.catalog = XGsonBuilder.instance()
					.fromJson(FileUtils.readFileToString(
							new File(dir.getAbsolutePath() + File.separator + "catalog.json"),
							DefaultCharset.charset_utf_8), DumpRestoreStorageCatalog.class);
		}
		return this.execute();
	}

	private boolean execute() throws Exception {
		final List<String> storageContainerEntityNames = new ArrayList<>();
		storageContainerEntityNames.addAll((List<String>) Config.resource(Config.RESOURCE_STORAGECONTAINERENTITYNAMES));
		List<String> classNames = new ArrayList<>();
		classNames.addAll(this.catalog.keySet());
		classNames = ListTools.includesExcludesWildcard(classNames, Config.dumpRestoreStorage().getIncludes(),
				Config.dumpRestoreStorage().getExcludes());
		logger.print("restore storage find {} to restore.", classNames.size());
		final File persistence = new File(Config.dir_local_temp_classes(), DateTools.compact(this.start) + "_dump.xml");
		PersistenceXmlHelper.write(persistence.getAbsolutePath(), classNames);
		final StorageMappings storageMappings = Config.storageMappings();
		int count = 0;
		for (int i = 0; i < classNames.size(); i++) {
			final Class<StorageObject> cls = (Class<StorageObject>) Class.forName(classNames.get(i));
			final EntityManagerFactory emf = OpenJPAPersistence.createEntityManagerFactory(cls.getName(),
					persistence.getName(), PersistenceXmlHelper.properties(cls.getName(), Config.slice().getEnable()));
			final EntityManager em = emf.createEntityManager();
			em.setFlushMode(FlushModeType.COMMIT);
			try {
				final DumpRestoreStorageCatalogItem item = this.catalog.get(cls.getName());
				logger.print(
						"restore storage({}/{}): {}, count: {}, normal: {} will be restore, invalidStorage: {} and empty: {} will be ignore, size: {}M.",
						(i + 1), classNames.size(), cls.getName(), item.getCount(), item.getNormal(),
						item.getInvalidStorage(), item.getEmpty(), (item.getSize() / 1024 / 1024));
				count += this.store(cls, em, storageMappings);
			} finally {
				em.close();
				emf.close();
			}
			logger.print("restore storage completed, total count: {}, elapsed: {} minutes.", count,
					(System.currentTimeMillis() - start.getTime()) / 1000 / 60);
		}
		return false;
	}

	private <T extends StorageObject> long store(final Class<T> cls, final EntityManager em,
			final StorageMappings storageMappings) throws Exception {
		final File classDirectory = new File(this.dir, cls.getName());
		if ((!classDirectory.exists()) || (!classDirectory.isDirectory())) {
			throw new Exception("can not find directory: " + classDirectory.getAbsolutePath() + ".");
		}
		long count = 0;
		final List<File> files = new ArrayList<File>(
				FileUtils.listFiles(classDirectory, new String[] { "json" }, false));
		/** 对文件进行排序,和dump的时候的顺序保持一直 */
		Collections.sort(files, new Comparator<File>() {
			public int compare(final File o1, final File o2) {
				final String n1 = FilenameUtils.getBaseName(o1.getName());
				final String n2 = FilenameUtils.getBaseName(o2.getName());
				final Integer i1 = Integer.parseInt(n1);
				final Integer i2 = Integer.parseInt(n2);
				return i1.compareTo(i2);
			}
		});
		/** 尽量将删除的工作放在后面 */
		this.clean(cls, em, storageMappings);
		StorageMapping mapping = null;
		File file = null;
		for (int i = 0; i < files.size(); i++) {
			file = files.get(i);
			/** 必须先转换成 jsonElement 不能直接转成泛型T,如果直接转会有类型不匹配比如Integer变成了Double */
			logger.print("restoring " + (i + 1) + "/" + files.size() + " part of storage: " + cls.getName() + ".");
			final JsonArray raws = this.convert(file);
			if (null != raws) {
				em.getTransaction().begin();
				for (final JsonElement o : raws) {
					final T t = pureGsonDateFormated.fromJson(o, cls);
					if (Config.dumpRestoreStorage().getRedistribute()) {
						mapping = storageMappings.random(cls);
					} else {
						mapping = storageMappings.get(cls, t.getStorage());
					}
					if (null == mapping) {
						throw new Exception(
								"can not find storageMapping class: " + cls.getName() + ", name:" + t.getName());
					}
					final File source = new File(classDirectory, FilenameUtils.getBaseName(file.getName())
							+ StorageObject.PATHSEPARATOR + FilenameUtils.getName(t.path()));
					try (FileInputStream input = new FileInputStream(source)) {
						t.saveContent(mapping, input, t.getName());
					}
					em.persist(t);
					count++;
				}
				em.getTransaction().commit();
				em.clear();
				Runtime.getRuntime().gc();
			}
		}
		return count;
	}

	private JsonArray convert(final File file) throws IOException {
		/** 这里不进行判断,因为格式是严格约定的,出现意外应该先报错停止 */
		final String json = FileUtils.readFileToString(file, DefaultCharset.charset);
		final JsonElement jsonElement = pureGsonDateFormated.fromJson(json, JsonElement.class);
		final JsonObject jsonObject = jsonElement.getAsJsonObject();
		return jsonObject.get("normals").getAsJsonArray();
	}

	private <T extends StorageObject> void clean(final Class<T> cls, final EntityManager em,
			final StorageMappings storageMappings) throws Exception {
		List<T> list = null;
		StorageMapping mapping = null;
		do {
			if (ListTools.isNotEmpty(list)) {
				em.getTransaction().begin();
				for (final T t : list) {
					mapping = storageMappings.get(cls, t.getStorage());
					if (null != mapping) {
						t.deleteContent(mapping);
					}
					em.remove(t);
				}
				em.getTransaction().commit();
			}
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final CriteriaQuery<T> cq = cb.createQuery(cls);
			final Root<T> root = cq.from(cls);
			cq.select(root);
			list = em.createQuery(cq).setMaxResults(Config.dumpRestoreData().getBatchSize()).getResultList();
		} while (ListTools.isNotEmpty(list));
	}

}